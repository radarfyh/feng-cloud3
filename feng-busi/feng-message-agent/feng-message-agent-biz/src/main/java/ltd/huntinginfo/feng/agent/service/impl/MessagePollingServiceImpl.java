package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.agent.api.dto.*;
import ltd.huntinginfo.feng.agent.api.dto.MsgCallbackDTO.CenterReceiverDTO;
import ltd.huntinginfo.feng.agent.api.dto.MsgCallbackDTO.CenterSenderDTO;
import ltd.huntinginfo.feng.agent.api.dto.MsgCallbackDTO.MessageInfoDTO;
import ltd.huntinginfo.feng.agent.api.entity.*;
import ltd.huntinginfo.feng.agent.api.entity.MsgAppCredential;
import ltd.huntinginfo.feng.agent.api.entity.*;
import ltd.huntinginfo.feng.agent.api.vo.*;
import ltd.huntinginfo.feng.agent.config.MessagePollingConfig;
import ltd.huntinginfo.feng.agent.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePollingServiceImpl implements MessagePollingService {
    
    // 依赖注入
    private final MessageCenterClientService messageCenterClientService;
    private final MsgAppCredentialService msgAppCredentialService;
    private final MsgAgentMappingService msgAgentMappingService;
    private final MsgPollCursorService msgPollCursorService;
    private final MsgAgentLogService msgAgentLogService;
    private final MsgReceiverMappingService msgReceiverMappingService;
    private final MessagePollingConfig pollingConfig;
    private final TransactionTemplate transactionTemplate;
    
    // 线程控制
    private ScheduledExecutorService scheduler;
    private ExecutorService callbackExecutor;
    private volatile boolean running = false;
    
    // 统计信息
    private final AtomicLong totalPollCount = new AtomicLong(0);
    private final AtomicLong totalMessageCount = new AtomicLong(0);
    private final AtomicLong successCallbackCount = new AtomicLong(0);
    private final AtomicLong failedCallbackCount = new AtomicLong(0);
    
    // 服务状态
    private LocalDateTime lastStartTime;
    private LocalDateTime lastStopTime;
    private String serviceStatus = "INITIALIZED";
    
    @PostConstruct
    @Override
    public void init() {
        if (!pollingConfig.isEnabled()) {
            log.info("消息轮询服务已禁用");
            serviceStatus = "DISABLED";
            return;
        }
        
        // 初始化线程池
        scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "msg-polling-scheduler");
            t.setDaemon(true);
            return t;
        });
        
        // 初始化回调线程池
        callbackExecutor = new ThreadPoolExecutor(
            pollingConfig.getCorePoolSize(),
            pollingConfig.getMaxPoolSize(),
            pollingConfig.getKeepAliveSeconds(),
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(pollingConfig.getQueueCapacity()),
            r -> {
                Thread t = new Thread(r, "msg-callback-" + IdUtil.fastSimpleUUID());
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        log.info("消息轮询服务初始化完成，配置: {}", pollingConfig);
        
        // 启动轮询服务
        start();
    }
    
    /**
     * 启动轮询服务
     */
    @Override
    public synchronized void start() {
        if (running) {
            log.warn("轮询服务已经在运行中");
            return;
        }
        
        if (!pollingConfig.isEnabled()) {
            log.warn("轮询服务在配置中已禁用，无法启动");
            return;
        }
        
        running = true;
        serviceStatus = "RUNNING";
        lastStartTime = LocalDateTime.now();
        
        // 延迟指定时间后开始执行，然后按固定间隔执行
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                try {
                    pollAllApplications();
                } catch (Exception e) {
                    log.error("轮询任务执行异常", e);
                }
            }
        }, pollingConfig.getInitialDelay(), pollingConfig.getIntervalSeconds(), TimeUnit.SECONDS);
        
        log.info("消息轮询服务已启动，轮询间隔: {}秒，主题: {}，游标键: {}", 
                pollingConfig.getIntervalSeconds(), 
                pollingConfig.getTopic(), 
                pollingConfig.getCursorKey());
    }
    
    /**
     * 停止轮询服务
     */
    @Override
    public synchronized void stop() {
        running = false;
        serviceStatus = "STOPPED";
        lastStopTime = LocalDateTime.now();
        log.info("消息轮询服务已停止");
    }
    
    /**
     * 重启轮询服务
     */
    @Override
    public synchronized void restart() {
        log.info("重启消息轮询服务...");
        stop();
        try {
            Thread.sleep(1000); // 等待1秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
        log.info("消息轮询服务重启完成");
    }
    
    /**
     * 为所有启用的应用执行轮询
     */
    private void pollAllApplications() {
        try {
            List<MsgAppCredentialVO> activeApps = msgAppCredentialService.list(
                MsgAppCredentialDTO.builder()
                    .status(1)
                    .delFlag("0")
                    .build()
            );
            
            if (CollUtil.isEmpty(activeApps)) {
                log.debug("没有启用的应用需要轮询");
                return;
            }
            
            // 限制并发数
            int concurrentLimit = Math.min(activeApps.size(), pollingConfig.getMaxConcurrentPolling());
            List<MsgAppCredentialVO> limitedApps = activeApps.subList(0, Math.min(concurrentLimit, activeApps.size()));
            
            totalPollCount.incrementAndGet();
            if (pollingConfig.isDetailedLogging()) {
                log.debug("开始轮询，应用数量: {}，并发限制: {}", activeApps.size(), concurrentLimit);
            }
            
            // 并行处理每个应用的轮询
            limitedApps.parallelStream().forEach(this::pollForApplication);
            
        } catch (Exception e) {
            log.error("轮询所有应用时发生异常", e);
        }
    }
    
    /**
     * 为指定应用执行轮询
     */
    private void pollForApplication(MsgAppCredentialVO app) {
        String appKey = app.getAppKey();
        
        try {
            // 1. 获取游标
            MsgPollCursor cursor = msgPollCursorService.getByAppKey(appKey);
            if (cursor == null) {
                cursor = msgPollCursorService.getOrCreateCursor(
                    appKey, 
                    pollingConfig.getCursorKey(), 
                    pollingConfig.getIntervalSeconds()
                );
            }
            
            // 检查是否需要轮询
            if (!shouldPoll(cursor)) {
                return;
            }
            
            // 2. 构建轮询请求
            MessageReceiveRequest request = new MessageReceiveRequest();
            request.setZtbm(pollingConfig.getTopic());
            request.setYbid(StrUtil.trimToEmpty(cursor.getYbid()));
            
            // 3. 调用消息中心接口
            MessageReceiveResponse response = messageCenterClientService.receiveMessages(
                convertToEntity(app), request);
            
            // 4. 处理响应
            if (response == null || !"00000".equals(response.getCode())) {
                handlePollError(appKey, cursor, response);
                return;
            }
            
            // 5. 处理接收到的消息
            List<MessageRecord> messages = response.getXxjl();
            if (CollUtil.isNotEmpty(messages)) {
                processReceivedMessages(messages, app, cursor);
                if (pollingConfig.isDetailedLogging()) {
                    log.info("应用 {} 接收到 {} 条新消息", appKey, messages.size());
                }
            }
            
            // 6. 更新游标
            updateCursorAfterSuccess(cursor, response.getYbid(), messages.size());
            
        } catch (Exception e) {
            log.error("应用 {} 轮询发生异常", appKey, e);
            handlePollException(appKey, e);
        }
    }
    
    /**
     * 检查是否需要轮询
     */
    private boolean shouldPoll(MsgPollCursor cursor) {
        if (cursor == null || cursor.getStatus() != 1) {
            return false;
        }
        
        // 检查错误次数
        if (cursor.getErrorCount() != null && cursor.getErrorCount() >= pollingConfig.getMaxRetry()) {
            log.warn("应用 {} 错误次数过多，暂停轮询", cursor.getAppKey());
            
            // 自动重置游标
            if (pollingConfig.isAutoResetCursor() && 
                cursor.getErrorCount() >= pollingConfig.getAutoResetErrorThreshold()) {
                log.info("自动重置游标: appKey={}", cursor.getAppKey());
                msgPollCursorService.resetCursor(cursor.getAppKey(), pollingConfig.getCursorKey());
                return true;
            }
            
            return false;
        }
        
        // 检查上次轮询时间
        if (cursor.getLastPollTime() != null) {
            long elapsed = System.currentTimeMillis() - cursor.getLastPollTime().getTime();
            long interval = cursor.getPollInterval() != null ? 
                cursor.getPollInterval() * 1000L : pollingConfig.getIntervalSeconds() * 1000L;
            
            if (elapsed < interval) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 处理接收到的消息
     */
    @Transactional(rollbackFor = Exception.class)
    protected void processReceivedMessages(List<MessageRecord> messages, 
                                         MsgAppCredentialVO app, 
                                         MsgPollCursor cursor) {
        String appKey = app.getAppKey();
        
        // 根据配置决定是否批量处理
        if (pollingConfig.isAsyncProcessing() && messages.size() > pollingConfig.getBatchSize()) {
            processMessagesAsync(messages, app, cursor);
        } else {
            processMessagesSync(messages, app, cursor);
        }
    }
    
    /**
     * 同步处理消息
     */
    private void processMessagesSync(List<MessageRecord> messages, 
                                    MsgAppCredentialVO app, 
                                    MsgPollCursor cursor) {
        String appKey = app.getAppKey();
        
        for (MessageRecord message : messages) {
            try {
                // 1. 查找接收者映射
                MsgReceiverMapping receiverMapping = findReceiverMapping(message, appKey);
                if (receiverMapping == null) {
                    log.warn("未找到接收者映射: jsdwdm={}, jsrzjhm={}, appKey={}", 
                            message.getJsdwdm(), message.getJsrzjhm(), appKey);
                    continue;
                }
                
                // 2. 保存消息映射
                MsgAgentMapping mapping = saveMessageMapping(message, app, receiverMapping);
                
                // 3. 异步发送回调
                if (pollingConfig.isAsyncProcessing()) {
                    callbackExecutor.submit(() -> sendCallback(mapping, app));
                } else {
                    sendCallback(mapping, app);
                }
                
                totalMessageCount.incrementAndGet();
                
                // 4. 记录日志
                if (pollingConfig.isDetailedLogging()) {
                    msgAgentLogService.logPoll(
                        mapping.getId(), 
                        appKey, 
                        "RECEIVE_MESSAGE",
                        String.format("接收到新消息: %s, %s", mapping.getXxbm(), message.getXxbt()),
                        message
                    );
                }
                
            } catch (Exception e) {
                log.error("处理消息失败: xxbm={}, appKey={}", message.getXxbm(), appKey, e);
                msgAgentLogService.logError(
                    null, appKey, 
                    "PROCESS_MESSAGE_ERROR",
                    String.format("处理消息失败: %s, %s", message.getXxbm(), e.getMessage()),
                    message
                );
            }
        }
    }
    
    /**
     * 异步处理消息
     */
    private void processMessagesAsync(List<MessageRecord> messages, 
                                     MsgAppCredentialVO app, 
                                     MsgPollCursor cursor) {
        String appKey = app.getAppKey();
        
        // 分批处理
        List<List<MessageRecord>> batches = CollUtil.split(messages, pollingConfig.getBatchSize());
        
        for (List<MessageRecord> batch : batches) {
            callbackExecutor.submit(() -> {
                try {
                    processMessagesSync(batch, app, cursor);
                } catch (Exception e) {
                    log.error("异步处理消息批次失败: appKey={}, batchSize={}", appKey, batch.size(), e);
                }
            });
        }
    }
    
    /**
     * 查找接收者映射
     */
    private MsgReceiverMapping findReceiverMapping(MessageRecord message, String appKey) {
        // 优先按个人接收者查找
        if (StrUtil.isNotBlank(message.getJsrzjhm())) {
            MsgReceiverMapping mapping = msgReceiverMappingService.getByCenterPerson(
                message.getJsrzjhm());
            if (mapping != null && mapping.getAppKey().equals(appKey)) {
                return mapping;
            }
        }
        
        // 按单位接收者查找
        if (StrUtil.isNotBlank(message.getJsdwdm())) {
            return msgReceiverMappingService.getByCenterUnit(message.getJsdwdm());
        }
        
        return null;
    }
    
    /**
     * 保存消息映射
     */
    private MsgAgentMapping saveMessageMapping(MessageRecord message, 
                                             MsgAppCredentialVO app,
                                             MsgReceiverMapping receiverMapping) {
        MsgAgentMapping mapping = new MsgAgentMapping();
        mapping.setId(IdUtil.fastSimpleUUID());
        mapping.setAppKey(app.getAppKey());
        mapping.setXxbm(message.getXxbm());
        mapping.setCenterMsgId(message.getXxbm());
        
        // 消息内容
        mapping.setMsgType(message.getXxlx());
        mapping.setMsgTitle(message.getXxbt());
        mapping.setContent(message.getXxnr());
        mapping.setPriority(convertPriority(message.getJjcd()));
        
        // 发送方信息
        mapping.setCenterFsdw(message.getFsdw());
        mapping.setCenterFsdwdm(message.getFsdwdm());
        mapping.setCenterFsr(message.getFsr());
        mapping.setCenterFsrzjhm(message.getFsrzjhm());
        mapping.setCenterFssj(parseDateTime(message.getFssj()));
        
        // 接收方信息
        mapping.setCenterJsdw(message.getJsdw());
        mapping.setCenterJsdwdm(message.getJsdwdm());
        mapping.setCenterJsr(message.getJsr());
        mapping.setCenterJsrzjhm(message.getJsrzjhm());
        mapping.setCenterClzt(message.getClzt());
        
        // 业务接收者信息
        mapping.setReceiverIdcard(receiverMapping.getBizReceiverId());
        mapping.setReceiverName(receiverMapping.getBizReceiverName());
        mapping.setReceiverType(receiverMapping.getBizReceiverType());
        
        // 消息详情
        mapping.setCenterXxlx(message.getXxlx());
        mapping.setCenterXxbt(message.getXxbt());
        mapping.setCenterXxnr(message.getXxnr());
        mapping.setCenterCldz(message.getCldz());
        mapping.setCenterJjcd(message.getJjcd());
        mapping.setCenterYwcs(message.getYwcs());
        mapping.setCenterTb(message.getTb());
        
        // 状态
        mapping.setStatus("RECEIVED");
        mapping.setStatusCode("2000");
        mapping.setSendTime(new Date());
        mapping.setCenterReceiveTime(new Date());
        
        // 回调配置
        mapping.setCallbackUrl(app.getCallbackUrl());
        mapping.setCallbackMethod("POST");
        mapping.setCallbackAuthMode(app.getCallbackAuthMode());
        
        // 保存到数据库
        msgAgentMappingService.save(mapping);
        return mapping;
    }
    
    /**
     * 发送回调到业务系统
     */
    private void sendCallback(MsgAgentMapping mapping, MsgAppCredentialVO app) {
        String appKey = app.getAppKey();
        String callbackUrl = mapping.getCallbackUrl();
        
        if (StrUtil.isBlank(callbackUrl)) {
            log.warn("应用 {} 未配置回调地址，跳过回调", appKey);
            return;
        }
        
        int retryCount = 0;
        boolean success = false;
        
        while (retryCount < pollingConfig.getCallbackMaxRetry() && !success) {
            try {
                if (retryCount > 0) {
                    log.info("重试回调第 {} 次: msgId={}, appKey={}", 
                            retryCount, mapping.getId(), appKey);
                    Thread.sleep(pollingConfig.getCallbackRetryInterval());
                }
                
                // 1. 构建回调请求
                MsgCallbackDTO callbackDTO = buildCallbackDTO(mapping);
                
                // 2. 生成签名头部
                Map<String, String> headers = generateCallbackHeaders(app);
                
                // 3. 发送回调请求
                success = sendCallbackRequest(callbackUrl, headers, callbackDTO);
                
                if (success) {
                    successCallbackCount.incrementAndGet();
                    if (pollingConfig.isDetailedLogging()) {
                        log.info("回调成功: msgId={}, appKey={}", mapping.getId(), appKey);
                    }
                } else {
                    failedCallbackCount.incrementAndGet();
                    log.warn("回调失败: msgId={}, appKey={}", mapping.getId(), appKey);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("回调异常: msgId={}, appKey={}, retry={}", 
                        mapping.getId(), appKey, retryCount, e);
                failedCallbackCount.incrementAndGet();
            }
            
            retryCount++;
        }
        
        // 更新状态
        updateCallbackStatus(mapping, success, retryCount);
    }
    
    /**
     * 构建回调DTO
     */
    private MsgCallbackDTO buildCallbackDTO(MsgAgentMapping mapping) {
        MsgCallbackDTO dto = new MsgCallbackDTO();
        dto.setEventType("MESSAGE_RECEIVED");
        dto.setEventTime(new Date());
        dto.setMsgId(mapping.getId());
        
        // 消息内容
        MessageInfoDTO message = new MessageInfoDTO();
        message.setXxbm(mapping.getXxbm());
        message.setXxlx(mapping.getCenterXxlx());
        message.setXxbt(mapping.getCenterXxbt());
        message.setXxnr(mapping.getCenterXxnr());
        message.setCldz(mapping.getCenterCldz());
        message.setJjcd(mapping.getCenterJjcd());
        message.setYwcs(mapping.getCenterYwcs());
        message.setTb(mapping.getCenterTb());
        dto.setMessage(message);
        
        // 发送方
        CenterSenderDTO sender = new CenterSenderDTO();
        sender.setFsdw(mapping.getCenterFsdw());
        sender.setFsdwdm(mapping.getCenterFsdwdm());
        sender.setFsr(mapping.getCenterFsr());
        sender.setFsrzjhm(mapping.getCenterFsrzjhm());
        sender.setFssj(mapping.getCenterFssj());
        dto.setSender(sender);
        
        // 接收方
        CenterReceiverDTO receiver = new CenterReceiverDTO();
        receiver.setJsdw(mapping.getCenterJsdw());
        receiver.setJsdwdm(mapping.getCenterJsdwdm());
        receiver.setJsr(mapping.getCenterJsr());
        receiver.setJsrzjhm(mapping.getCenterJsrzjhm());
        dto.setReceiver(receiver);
        
        dto.setClzt(mapping.getCenterClzt());
        
        return dto;
    }
    
    /**
     * 生成回调头部
     */
    private Map<String, String> generateCallbackHeaders(MsgAppCredentialVO app) {
        long timestamp = System.currentTimeMillis();
        String nonce = IdUtil.fastSimpleUUID();
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Callback-App-Key", app.getAppKey());
        headers.put("X-Callback-Timestamp", String.valueOf(timestamp));
        headers.put("X-Callback-Nonce", nonce);
        
        // 生成签名
        String appSecret = msgAppCredentialService.getAppSecret(app.getAppKey());
        String signature = generateSignature(appSecret, timestamp, nonce);
        headers.put("X-Callback-Signature", signature);
        
        return headers;
    }
    
    /**
     * 生成签名
     */
    private String generateSignature(String appSecret, long timestamp, String nonce) {
        try {
            String data = appSecret + "|" + timestamp + "|" + nonce;
            HMac hmac = new HMac(HmacAlgorithm.HmacSHA256, appSecret.getBytes());
            return hmac.digestHex(data);
        } catch (Exception e) {
            log.error("生成签名失败", e);
            return "";
        }
    }
    
    /**
     * 发送回调请求
     */
    private boolean sendCallbackRequest(String callbackUrl, 
                                      Map<String, String> headers, 
                                      MsgCallbackDTO body) {
        try {
            HttpRequest request = HttpRequest.post(callbackUrl)
                .timeout(pollingConfig.getCallbackTimeout());
            
            headers.forEach(request::header);
            request.body(JSONUtil.toJsonStr(body));
            
            HttpResponse response = request.execute();
            
            if (response.isOk()) {
                String responseBody = response.body();
                R<?> result = JSONUtil.toBean(responseBody, R.class);
                return result != null && "0".equals(result.getCode());
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("回调请求异常", e);
            return false;
        }
    }
    
    /**
     * 更新回调状态
     */
    private void updateCallbackStatus(MsgAgentMapping mapping, boolean success, int retryCount) {
        transactionTemplate.execute(status -> {
            try {
                MsgAgentMapping entity = msgAgentMappingService.getById(mapping.getId());
                if (entity != null) {
                    entity.setStatus(success ? "CALLBACK_SENT" : "CALLBACK_FAILED");
                    entity.setStatusCode(success ? "1005" : "1009");
                    entity.setCallbackTime(new Date());
                    entity.setRetryCount(retryCount);
                    msgAgentMappingService.updateById(entity);
                }
                return true;
            } catch (Exception e) {
                log.error("更新回调状态失败", e);
                status.setRollbackOnly();
                return false;
            }
        });
    }
    
    /**
     * 处理轮询错误
     */
    private void handlePollError(String appKey, MsgPollCursor cursor, MessageReceiveResponse response) {
        String errorMsg = response != null ? response.getInfo() : "响应为空";
        log.warn("应用 {} 轮询失败: {}", appKey, errorMsg);
        
        msgPollCursorService.recordPollError(appKey, pollingConfig.getCursorKey(), errorMsg);
        msgAgentLogService.logError(
            null, appKey, 
            "POLL_ERROR",
            String.format("轮询失败: %s", errorMsg),
            response
        );
    }
    
    /**
     * 处理轮询异常
     */
    private void handlePollException(String appKey, Exception e) {
        msgPollCursorService.recordPollError(appKey, pollingConfig.getCursorKey(), e.getMessage());
        msgAgentLogService.logError(
            null, appKey, 
            "POLL_EXCEPTION",
            String.format("轮询异常: %s", e.getMessage()),
            e
        );
    }
    
    /**
     * 成功轮询后更新游标
     */
    private void updateCursorAfterSuccess(MsgPollCursor cursor, String newYbid, int messageCount) {
        msgPollCursorService.recordPollSuccess(
            cursor.getAppKey(), 
            pollingConfig.getCursorKey(), 
            newYbid, 
            messageCount
        );
    }
    
    /**
     * 转换紧急程度为优先级
     */
    private Integer convertPriority(String jjcd) {
        if (StrUtil.isBlank(jjcd)) return 3;
        
        try {
            int level = Integer.parseInt(jjcd);
            if (level >= 8) return 1;
            if (level >= 6) return 2;
            if (level >= 4) return 3;
            if (level >= 2) return 4;
            return 5;
        } catch (Exception e) {
            return 3;
        }
    }
    
    /**
     * 解析日期时间
     */
    private Date parseDateTime(String dateTimeStr) {
        if (StrUtil.isBlank(dateTimeStr)) return null;
        
        try {
            return DateUtil.parse(dateTimeStr, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * VO转Entity
     */
    private MsgAppCredential convertToEntity(MsgAppCredentialVO vo) {
        MsgAppCredential entity = new MsgAppCredential();
        BeanUtil.copyProperties(vo, entity);
        return entity;
    }
    
    /**
     * 获取轮询统计信息
     */
    @Override
    public Map<String, Object> getPollingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("serviceStatus", serviceStatus);
        stats.put("running", running);
        stats.put("totalPollCount", totalPollCount.get());
        stats.put("totalMessageCount", totalMessageCount.get());
        stats.put("successCallbackCount", successCallbackCount.get());
        stats.put("failedCallbackCount", failedCallbackCount.get());
        stats.put("lastStartTime", lastStartTime);
        stats.put("lastStopTime", lastStopTime);
        
        // 线程池状态
        if (callbackExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) callbackExecutor;
            Map<String, Object> executorStats = new HashMap<>();
            executorStats.put("activeCount", executor.getActiveCount());
            executorStats.put("poolSize", executor.getPoolSize());
            executorStats.put("corePoolSize", executor.getCorePoolSize());
            executorStats.put("maxPoolSize", executor.getMaximumPoolSize());
            executorStats.put("queueSize", executor.getQueue().size());
            executorStats.put("completedTaskCount", executor.getCompletedTaskCount());
            stats.put("executorStatistics", executorStats);
        }
        
        // 配置信息
        stats.put("config", pollingConfig);
        
        // 获取所有游标状态
        List<MsgPollCursor> cursors = msgPollCursorService.getRunningCursors();
        List<Map<String, Object>> cursorStats = cursors.stream()
            .map(cursor -> {
                Map<String, Object> cs = new HashMap<>();
                cs.put("appKey", cursor.getAppKey());
                cs.put("ybid", StrUtil.subPre(cursor.getYbid(), 20));
                cs.put("pollCount", cursor.getPollCount());
                cs.put("messageCount", cursor.getMessageCount());
                cs.put("errorCount", cursor.getErrorCount());
                cs.put("lastPollTime", cursor.getLastPollTime());
                cs.put("lastSuccessTime", cursor.getLastSuccessTime());
                cs.put("status", cursor.getStatus());
                return cs;
            })
            .collect(Collectors.toList());
        
        stats.put("cursorStatistics", cursorStats);
        return stats;
    }
    
    /**
     * 手动触发轮询
     */
    @Override
    public boolean triggerPolling(String appKey) {
        MsgAppCredentialVO app = msgAppCredentialService.getByAppKey(appKey);
        if (app == null) {
            log.error("未找到应用: {}", appKey);
            return false;
        }
        
        try {
            pollForApplication(app);
            return true;
        } catch (Exception e) {
            log.error("手动触发轮询失败: {}", appKey, e);
            return false;
        }
    }
    
    /**
     * 重置游标
     */
    @Override
    public boolean resetCursor(String appKey) {
        return msgPollCursorService.resetCursor(appKey, pollingConfig.getCursorKey());
    }
    
    /**
     * 监控任务 - 定时检查状态
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void monitorPollingService() {
        if (!pollingConfig.isEnabled() || !pollingConfig.isHealthCheckEnabled()) {
            return;
        }
        
        try {
            // 检查是否有游标长时间未更新
            List<MsgPollCursor> cursors = msgPollCursorService.getRunningCursors();
            for (MsgPollCursor cursor : cursors) {
                if (cursor.getLastPollTime() != null) {
                    long elapsed = System.currentTimeMillis() - cursor.getLastPollTime().getTime();
                    if (elapsed > pollingConfig.getHealthCheckInterval() * 60 * 1000) {
                        log.warn("游标长时间未更新: appKey={}, 最后轮询时间={}, 已过去{}分钟", 
                                cursor.getAppKey(), cursor.getLastPollTime(), 
                                elapsed / (60 * 1000));
                    }
                }
            }
            
            // 记录监控日志
            if (pollingConfig.isDetailedLogging()) {
                log.debug("轮询服务监控: 状态={}, 运行中={}, 总轮询次数={}, 总消息数={}", 
                        serviceStatus, running, totalPollCount.get(), totalMessageCount.get());
            }
            
        } catch (Exception e) {
            log.error("轮询服务监控任务异常", e);
        }
    }
    
    /**
     * 清理资源
     */
    @PreDestroy
    @Override
    public void destroy() {
        log.info("开始清理轮询服务资源");
        
        stop();
        serviceStatus = "DESTROYING";
        
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (callbackExecutor != null) {
            callbackExecutor.shutdown();
            try {
                if (!callbackExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    callbackExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                callbackExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        serviceStatus = "DESTROYED";
        log.info("轮询服务资源清理完成");
    }
}