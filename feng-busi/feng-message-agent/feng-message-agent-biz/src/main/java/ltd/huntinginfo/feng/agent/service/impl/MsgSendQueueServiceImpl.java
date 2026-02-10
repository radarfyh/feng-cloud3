package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.agent.api.entity.MsgSendQueue;
import ltd.huntinginfo.feng.agent.api.entity.CodeApplyRequest;
import ltd.huntinginfo.feng.agent.api.entity.CodeApplyResponse;
import ltd.huntinginfo.feng.agent.api.entity.MessageSendRequest;
import ltd.huntinginfo.feng.agent.api.entity.MessageSendResponse;
import ltd.huntinginfo.feng.agent.mapper.MsgSendQueueMapper;
import ltd.huntinginfo.feng.agent.config.MsgSendQueueConfig;
import ltd.huntinginfo.feng.agent.service.MessageCenterClientService;
import ltd.huntinginfo.feng.agent.service.MsgSendQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class MsgSendQueueServiceImpl extends ServiceImpl<MsgSendQueueMapper, MsgSendQueue> implements MsgSendQueueService {

    private final MsgSendQueueMapper msgSendQueueMapper;
    private final MsgSendQueueConfig queueConfig;
    private final MessageCenterClientService messageCenterClientService;
    
    // 线程控制
    private ScheduledExecutorService scheduler;
    private ExecutorService taskExecutor;
    private volatile boolean running = false;
    private final ReentrantLock startStopLock = new ReentrantLock();
    
    // 统计信息
    private final AtomicInteger activeTaskCount = new AtomicInteger(0);
    private final AtomicLong totalProcessedCount = new AtomicLong(0);
    private final AtomicLong successProcessedCount = new AtomicLong(0);
    private final AtomicLong failedProcessedCount = new AtomicLong(0);
    private volatile Date lastProcessTime;
    private volatile Date serviceStartTime;
    
    // 服务状态
    private String serviceStatus = "INITIALIZING";
    
    // 任务去重缓存（防止短时间内重复处理相同任务）
    private final Map<String, Long> taskDeduplicationCache = new ConcurrentHashMap<>();

    @Override
    public MsgSendQueue getById(String id) {
        try {
            MsgSendQueue result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的队列记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询队列详情失败: id={}", id, e);
            throw new RuntimeException("查询队列详情失败", e);
        }
    }

    @Override
    public IPage<MsgSendQueue> page(IPage<MsgSendQueue> page, MsgSendQueue msgSendQueue) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = buildQueryWrapper(msgSendQueue);
            wrapper.orderByAsc(MsgSendQueue::getExecuteTime); // 按执行时间正序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询队列列表失败", e);
            throw new RuntimeException("分页查询队列列表失败", e);
        }
    }

    @Override
    public List<MsgSendQueue> list(MsgSendQueue msgSendQueue) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = buildQueryWrapper(msgSendQueue);
            wrapper.orderByAsc(MsgSendQueue::getExecuteTime); // 按执行时间正序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询队列列表失败", e);
            throw new RuntimeException("查询队列列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgSendQueue msgSendQueue) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgSendQueue.getId())) {
            	msgSendQueue.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgSendQueue.getCreateTime() == null) {
                msgSendQueue.setCreateTime(new Date());
            }
            
            // 设置默认值
            if (msgSendQueue.getQueueStatus() == null) {
                msgSendQueue.setQueueStatus("PENDING");
            }
            
            if (msgSendQueue.getPriority() == null) {
                msgSendQueue.setPriority(5);
            }
            
            if (msgSendQueue.getMaxRetry() == null) {
                msgSendQueue.setMaxRetry(3);
            }
            
            if (msgSendQueue.getCurrentRetry() == null) {
                msgSendQueue.setCurrentRetry(0);
            }
            
            // 如果没有设置执行时间，则使用当前时间
            if (msgSendQueue.getExecuteTime() == null) {
                msgSendQueue.setExecuteTime(new Date());
            }
            
            boolean result = super.save(msgSendQueue);
            if (result) {
                log.debug("保存队列记录成功: id={}, appKey={}, queueType={}", 
                        msgSendQueue.getId(), msgSendQueue.getAppKey(), msgSendQueue.getQueueType());
            } else {
                log.error("保存队列记录失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存队列记录失败", e);
            throw new RuntimeException("保存队列记录失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgSendQueue msgSendQueue) {
        try {
            // 验证队列记录是否存在
            MsgSendQueue existingQueue = super.getById(msgSendQueue.getId());
            if (existingQueue == null) {
                log.warn("更新队列记录失败，记录不存在: id={}", msgSendQueue.getId());
                return false;
            }
            
            boolean result = super.updateById(msgSendQueue);
            if (result) {
                log.debug("更新队列记录成功: id={}, appKey={}, queueType={}", 
                        msgSendQueue.getId(), msgSendQueue.getAppKey(), msgSendQueue.getQueueType());
            } else {
                log.warn("更新队列记录失败: id={}", msgSendQueue.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新队列记录失败: id={}", msgSendQueue.getId(), e);
            throw new RuntimeException("更新队列记录失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgSendQueue existingQueue = super.getById(id);
            if (existingQueue == null) {
                log.warn("删除队列记录失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除队列记录成功: id={}, appKey={}, queueType={}", 
                        id, existingQueue.getAppKey(), existingQueue.getQueueType());
            } else {
                log.error("删除队列记录失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除队列记录失败: id={}", id, e);
            throw new RuntimeException("删除队列记录失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgSendQueue> buildQueryWrapper(MsgSendQueue msgSendQueue) {
        LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
        
        if (msgSendQueue != null) {
            // 按ID查询
            if (msgSendQueue.getId() != null && !msgSendQueue.getId().isEmpty()) {
                wrapper.eq(MsgSendQueue::getId, msgSendQueue.getId());
            }
            
            // 按应用标识查询
            if (msgSendQueue.getAppKey() != null && !msgSendQueue.getAppKey().isEmpty()) {
                wrapper.eq(MsgSendQueue::getAppKey, msgSendQueue.getAppKey());
            }
            
            // 按队列类型查询
            if (msgSendQueue.getQueueType() != null && !msgSendQueue.getQueueType().isEmpty()) {
                wrapper.eq(MsgSendQueue::getQueueType, msgSendQueue.getQueueType());
            }
            
            // 按队列状态查询
            if (msgSendQueue.getQueueStatus() != null && !msgSendQueue.getQueueStatus().isEmpty()) {
                wrapper.eq(MsgSendQueue::getQueueStatus, msgSendQueue.getQueueStatus());
            }
            
            // 按消息ID查询
            if (msgSendQueue.getMsgId() != null && !msgSendQueue.getMsgId().isEmpty()) {
                wrapper.eq(MsgSendQueue::getMsgId, msgSendQueue.getMsgId());
            }
            
            // 按优先级查询
            if (msgSendQueue.getPriority() != null) {
                wrapper.eq(MsgSendQueue::getPriority, msgSendQueue.getPriority());
            }
            
            // 按执行时间范围查询
            // if (msgSendQueue.getExecuteTime() != null) {
            //     wrapper.ge(MsgSendQueue::getExecuteTime, startTime);
            //     wrapper.le(MsgSendQueue::getExecuteTime, endTime);
            // }
            
            // 按执行结果代码查询
            if (msgSendQueue.getResultCode() != null && !msgSendQueue.getResultCode().isEmpty()) {
                wrapper.eq(MsgSendQueue::getResultCode, msgSendQueue.getResultCode());
            }
            
            // 按重试次数查询
            if (msgSendQueue.getCurrentRetry() != null) {
                wrapper.ge(MsgSendQueue::getCurrentRetry, msgSendQueue.getCurrentRetry());
            }
        }
        
        return wrapper;
    }

    /**
     * 获取待处理的任务列表
     */
    @Override
    public List<MsgSendQueue> getPendingTasks(int limit) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getQueueStatus, "PENDING");
            wrapper.le(MsgSendQueue::getExecuteTime, new Date());
            
            // 启用优先级时，高优先级任务优先
            if (queueConfig.isEnablePriority()) {
                wrapper.orderByAsc(MsgSendQueue::getPriority);
            }
            
            wrapper.orderByAsc(MsgSendQueue::getExecuteTime);
            wrapper.orderByAsc(MsgSendQueue::getCreateTime);
            wrapper.last("LIMIT " + limit);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取待处理任务列表失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取处理中的任务列表
     */
    public List<MsgSendQueue> getProcessingTasks() {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getQueueStatus, "PROCESSING");
            wrapper.orderByAsc(MsgSendQueue::getExecuteStartTime);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取处理中任务列表失败", e);
            throw new RuntimeException("获取处理中任务列表失败", e);
        }
    }

    /**
     * 获取待重试的任务列表（失败后重新进入待处理状态的任务）
     */
    public List<MsgSendQueue> getRetryTasks(int limit) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getQueueStatus, "PENDING");
            wrapper.apply("current_retry < max_retry"); // 使用原生SQL条件
            wrapper.le(MsgSendQueue::getExecuteTime, new Date()); // 执行时间已到
            wrapper.orderByAsc(MsgSendQueue::getExecuteTime);
            wrapper.last("LIMIT " + limit);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取重试任务列表失败", e);
            throw new RuntimeException("获取重试任务列表失败", e);
        }
    }

    /**
     * 获取指定应用的所有队列任务
     */
    public List<MsgSendQueue> getTasksByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getAppKey, appKey);
            wrapper.orderByDesc(MsgSendQueue::getCreateTime);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取应用队列任务失败: appKey={}", appKey, e);
            throw new RuntimeException("获取队列任务失败", e);
        }
    }
    
    /**
     * 获取最终失败的任务列表（已达到最大重试次数）
     */
    public List<MsgSendQueue> getFinalFailedTasks(int limit) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getQueueStatus, "FAILED");
            wrapper.orderByDesc(MsgSendQueue::getExecuteEndTime);
            wrapper.last("LIMIT " + limit);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取最终失败任务列表失败", e);
            throw new RuntimeException("获取最终失败任务列表失败", e);
        }
    }

    /**
     * 获取指定消息ID的所有队列任务
     */
    public List<MsgSendQueue> getTasksByMsgId(String msgId) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getMsgId, msgId);
            wrapper.orderByAsc(MsgSendQueue::getCreateTime);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取消息队列任务失败: msgId={}", msgId, e);
            throw new RuntimeException("获取队列任务失败", e);
        }
    }

    /**
     * 创建发送任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgSendQueue createSendTask(String appKey, String msgId, Date executeTime, int priority) {
        try {
            MsgSendQueue queue = new MsgSendQueue();
            queue.setId(UUID.randomUUID().toString().replace("-", ""));
            queue.setAppKey(appKey);
            queue.setMsgId(msgId);
            queue.setQueueType("SEND");
            queue.setQueueStatus("PENDING");
            queue.setPriority(priority != 0 ? priority : 5);
            queue.setExecuteTime(executeTime != null ? executeTime : new Date());
            queue.setMaxRetry(queueConfig.getMaxRetry());
            queue.setCurrentRetry(0);
            
            boolean result = this.save(queue);
            if (result) {
                if (queueConfig.isDetailedLogging()) {
                    log.info("创建发送任务成功: id={}, appKey={}, msgId={}", 
                            queue.getId(), appKey, msgId);
                }
                return queue;
            }
            return null;
            
        } catch (Exception e) {
            log.error("创建发送任务失败: appKey={}, msgId={}", appKey, msgId, e);
            throw new RuntimeException("创建发送任务失败", e);
        }
    }
    
    /**
     * 创建回调任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgSendQueue createCallbackTask(String appKey, String msgId, Date executeTime) {
        try {
            MsgSendQueue queue = new MsgSendQueue();
            queue.setAppKey(appKey);
            queue.setMsgId(msgId);
            queue.setQueueType("CALLBACK");
            queue.setQueueStatus("PENDING");
            queue.setPriority(3); // 回调任务默认中等优先级
            queue.setExecuteTime(executeTime != null ? executeTime : new Date());
            queue.setMaxRetry(3);
            queue.setCurrentRetry(0);
            
            boolean result = this.save(queue);
            if (result) {
                log.debug("创建回调任务成功: id={}, appKey={}, msgId={}", 
                        queue.getId(), appKey, msgId);
                return queue;
            } else {
                log.error("创建回调任务失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建回调任务失败", e);
            throw new RuntimeException("创建回调任务失败", e);
        }
    }

    /**
     * 创建重试任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgSendQueue createRetryTask(String appKey, String msgId, Date executeTime, int currentRetry) {
        try {
            MsgSendQueue queue = new MsgSendQueue();
            queue.setAppKey(appKey);
            queue.setMsgId(msgId);
            queue.setQueueType("RETRY");
            queue.setQueueStatus("PENDING");
            queue.setPriority(5); // 重试任务默认低优先级
            queue.setExecuteTime(executeTime != null ? executeTime : new Date());
            queue.setMaxRetry(3);
            queue.setCurrentRetry(currentRetry);
            
            boolean result = this.save(queue);
            if (result) {
                log.debug("创建重试任务成功: id={}, appKey={}, msgId={}, retry={}", 
                        queue.getId(), appKey, msgId, currentRetry);
                return queue;
            } else {
                log.error("创建重试任务失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建重试任务失败", e);
            throw new RuntimeException("创建重试任务失败", e);
        }
    }

    /**
     * 标记任务为处理中
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessing(String id) {
        try {
            LambdaUpdateWrapper<MsgSendQueue> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgSendQueue::getId, id);
            wrapper.eq(MsgSendQueue::getQueueStatus, "PENDING"); // 只有待处理状态才能标记为处理中
            wrapper.set(MsgSendQueue::getQueueStatus, "PROCESSING");
            wrapper.set(MsgSendQueue::getExecuteStartTime, new Date());
            wrapper.set(MsgSendQueue::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("标记任务为处理中成功: id={}", id);
            } else {
                log.warn("标记任务为处理中失败，任务不存在或状态不正确: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("标记任务为处理中失败: id={}", id, e);
            throw new RuntimeException("标记任务为处理中失败", e);
        }
    }

    /**
     * 标记任务为成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSuccess(String id, String resultCode, String resultMessage) {
        try {
            LambdaUpdateWrapper<MsgSendQueue> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgSendQueue::getId, id);
            wrapper.in(MsgSendQueue::getQueueStatus, "PENDING", "PROCESSING", "FAILED"); // 多种状态都可以标记为成功
            wrapper.set(MsgSendQueue::getQueueStatus, "SUCCESS");
            wrapper.set(MsgSendQueue::getResultCode, resultCode);
            wrapper.set(MsgSendQueue::getResultMessage, resultMessage);
            wrapper.set(MsgSendQueue::getExecuteEndTime, new Date());
            wrapper.set(MsgSendQueue::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("标记任务为成功: id={}, resultCode={}", id, resultCode);
            } else {
                log.warn("标记任务为成功失败，任务不存在或状态不正确: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("标记任务为成功失败: id={}", id, e);
            throw new RuntimeException("标记任务为成功失败", e);
        }
    }

    /**
     * 标记任务为失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsFailed(String id, String resultCode, String resultMessage) {
        try {
            // 先获取当前任务信息
            MsgSendQueue task = super.getById(id);
            if (task == null) {
                log.warn("标记任务为失败失败，任务不存在: id={}", id);
                return false;
            }
            
            LambdaUpdateWrapper<MsgSendQueue> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgSendQueue::getId, id);
            wrapper.in(MsgSendQueue::getQueueStatus, "PENDING", "PROCESSING"); // 只有这些状态可以标记为失败
            
            // 增加重试次数
            int newRetryCount = task.getCurrentRetry() + 1;
            
            if (newRetryCount >= task.getMaxRetry()) {
                // 达到最大重试次数，标记为最终失败
                wrapper.set(MsgSendQueue::getQueueStatus, "FAILED");
            } else {
                // 还可以重试，计算下次执行时间（延迟重试）
                wrapper.set(MsgSendQueue::getQueueStatus, "PENDING");
                wrapper.set(MsgSendQueue::getExecuteTime, calculateNextRetryTime(newRetryCount));
            }
            
            wrapper.set(MsgSendQueue::getCurrentRetry, newRetryCount);
            wrapper.set(MsgSendQueue::getResultCode, resultCode);
            wrapper.set(MsgSendQueue::getResultMessage, resultMessage);
            wrapper.set(MsgSendQueue::getExecuteEndTime, new Date());
            wrapper.set(MsgSendQueue::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("标记任务为失败: id={}, retryCount={}, resultCode={}", 
                        id, newRetryCount, resultCode);
            } else {
                log.warn("标记任务为失败失败，任务不存在或状态不正确: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("标记任务为失败失败: id={}", id, e);
            throw new RuntimeException("标记任务为失败失败", e);
        }
    }

    /**
     * 计算下次重试时间（指数退避算法）
     */
    private Date calculateNextRetryTime(int retryCount) {
        // 基础延迟时间（秒）
        long baseDelay = 5L;
        // 指数退避：2^retryCount * baseDelay 秒
        long delaySeconds = (long) (Math.pow(2, retryCount) * baseDelay);
        // 最大延迟时间限制（300秒=5分钟）
        delaySeconds = Math.min(delaySeconds, 300L);
        
        return new Date(System.currentTimeMillis() + delaySeconds * 1000);
    }

    /**
     * 更新任务执行结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskResult(String id, String queueStatus, String resultCode, String resultMessage) {
        try {
            LambdaUpdateWrapper<MsgSendQueue> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgSendQueue::getId, id);
            wrapper.set(MsgSendQueue::getQueueStatus, queueStatus);
            wrapper.set(MsgSendQueue::getResultCode, resultCode);
            wrapper.set(MsgSendQueue::getResultMessage, resultMessage);
            wrapper.set(MsgSendQueue::getExecuteEndTime, new Date());
            wrapper.set(MsgSendQueue::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新任务结果成功: id={}, status={}, code={}", 
                        id, queueStatus, resultCode);
            } else {
                log.warn("更新任务结果失败，任务不存在: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("更新任务结果失败: id={}", id, e);
            throw new RuntimeException("更新任务结果失败", e);
        }
    }

    /**
     * 批量删除已完成的任务（成功或最终失败）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long cleanCompletedTasks(Date beforeDate) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgSendQueue::getQueueStatus, "SUCCESS", "FAILED");
            wrapper.le(MsgSendQueue::getExecuteEndTime, beforeDate);
            
            Long count = this.count(wrapper);
            if (count > 0) {
                boolean result = this.remove(wrapper);
                if (result) {
                    log.info("清理已完成任务成功: 清理条数={}, 完成时间<={}", count, beforeDate);
                    return count;
                }
            }
            return 0L;
        } catch (Exception e) {
            log.error("清理已完成任务失败", e);
            throw new RuntimeException("清理已完成任务失败", e);
        }
    }

    /**
     * 统计各种状态的任务数量
     */
    @Override
    public long countByStatus(String queueStatus) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getQueueStatus, queueStatus);
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计任务数量失败: status={}", queueStatus, e);
            throw new RuntimeException("统计任务数量失败", e);
        }
    }

    /**
     * 统计应用的各种状态任务数量
     */
    public long countByAppKeyAndStatus(String appKey, String queueStatus) {
        try {
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgSendQueue::getAppKey, appKey);
            wrapper.eq(MsgSendQueue::getQueueStatus, queueStatus);
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计应用任务数量失败: appKey={}, status={}", appKey, queueStatus, e);
            throw new RuntimeException("统计任务数量失败", e);
        }
    }
    
    @PostConstruct
    @Override
    public void init() {
        try {
            serviceStatus = "INITIALIZING";
            log.info("开始初始化消息队列服务...");
            
            if (!queueConfig.isEnabled()) {
                serviceStatus = "DISABLED";
                log.info("消息队列服务已禁用");
                return;
            }
            
            // 1. 初始化线程池
            initThreadPools();
            
            // 2. 启动队列处理
            start();
            
            // 3. 清理过期任务
            if (queueConfig.isAutoCleanCompleted()) {
                cleanCompletedTasks();
            }
            
            log.info("消息队列服务初始化完成，配置: {}", queueConfig);
            
        } catch (Exception e) {
            serviceStatus = "INITIALIZATION_FAILED";
            log.error("消息队列服务初始化失败", e);
            throw new RuntimeException("消息队列服务初始化失败", e);
        }
    }
    
    /**
     * 初始化线程池
     */
    private void initThreadPools() {
        // 初始化任务执行线程池
        taskExecutor = new ThreadPoolExecutor(
            queueConfig.getCorePoolSize(),
            queueConfig.getMaxPoolSize(),
            queueConfig.getKeepAliveSeconds(),
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueConfig.getQueueCapacity()),
            r -> {
                Thread t = new Thread(r, "msg-queue-worker-" + 
                    UUID.randomUUID().toString().substring(0, 8));
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            },
            new ThreadPoolExecutor.AbortPolicy() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    // 队列满时，由调用者线程执行
                    log.warn("线程池队列已满，由调用者线程执行任务");
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                }
            }
        );
        
        // 初始化调度器
        scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "msg-queue-scheduler");
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        });
        
        log.debug("线程池初始化完成: corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                queueConfig.getCorePoolSize(), queueConfig.getMaxPoolSize(), 
                queueConfig.getQueueCapacity());
    }
    
    /**
     * 启动队列处理
     */
    @Override
    public synchronized void start() {
        startStopLock.lock();
        try {
            if (running) {
                log.warn("队列服务已经在运行中");
                return;
            }
            
            if (!queueConfig.isEnabled()) {
                log.warn("队列服务在配置中已禁用，无法启动");
                return;
            }
            
            serviceStatus = "STARTING";
            running = true;
            serviceStartTime = new Date();
            
            // 立即执行一次处理，然后按固定间隔执行
            scheduler.scheduleWithFixedDelay(() -> {
                if (running) {
                    try {
                        processQueueTasks();
                    } catch (Exception e) {
                        log.error("队列处理任务执行异常", e);
                    }
                }
            }, queueConfig.getInitialDelay(), queueConfig.getProcessInterval(), TimeUnit.SECONDS);
            
            serviceStatus = "RUNNING";
            log.info("消息队列服务已启动，处理间隔: {}秒，线程池: {}/{}", 
                    queueConfig.getProcessInterval(),
                    queueConfig.getCorePoolSize(),
                    queueConfig.getMaxPoolSize());
            
        } finally {
            startStopLock.unlock();
        }
    }
    
    /**
     * 停止队列处理
     */
    @Override
    public synchronized void stop() {
        startStopLock.lock();
        try {
            if (!running) {
                log.warn("队列服务已经停止");
                return;
            }
            
            serviceStatus = "STOPPING";
            running = false;
            
            log.info("正在停止消息队列服务...");
            
        } finally {
            startStopLock.unlock();
        }
    }
    
    /**
     * 重启队列服务
     */
    @Override
    public void restart() {
        startStopLock.lock();
        try {
            log.info("重启消息队列服务...");
            
            // 先停止
            stop();
            
            // 等待一段时间确保所有任务完成
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 清理资源
            shutdownThreadPools();
            
            // 重新初始化
            initThreadPools();
            
            // 重新启动
            start();
            
            log.info("消息队列服务重启完成");
            
        } catch (Exception e) {
            serviceStatus = "RESTART_FAILED";
            log.error("重启队列服务失败", e);
            throw new RuntimeException("重启队列服务失败", e);
        } finally {
            startStopLock.unlock();
        }
    }
    
    /**
     * 处理队列任务
     */
    private void processQueueTasks() {
        try {
            lastProcessTime = new Date();
            
            // 1. 获取待处理的任务
            List<MsgSendQueue> pendingTasks = getPendingTasks(queueConfig.getBatchSize());
            if (pendingTasks.isEmpty()) {
                if (queueConfig.isDetailedLogging()) {
                    log.debug("没有待处理的队列任务");
                }
                return;
            }
            
            if (queueConfig.isDetailedLogging()) {
                log.info("开始处理 {} 个队列任务", pendingTasks.size());
            }
            
            // 2. 根据配置决定处理方式
            if (queueConfig.isAsyncProcessing()) {
                processTasksAsync(pendingTasks);
            } else {
                processTasksSync(pendingTasks);
            }
            
            if (queueConfig.isDetailedLogging()) {
                log.info("队列任务处理完成，总处理: {}，成功: {}，失败: {}", 
                        totalProcessedCount.get(), 
                        successProcessedCount.get(), 
                        failedProcessedCount.get());
            }
            
        } catch (Exception e) {
            log.error("队列任务处理异常", e);
        }
    }
    
    /**
     * 异步处理任务
     */
    private void processTasksAsync(List<MsgSendQueue> tasks) {
        // 根据优先级分组
        Map<Integer, List<MsgSendQueue>> priorityGroups = new HashMap<>();
        for (MsgSendQueue task : tasks) {
            int priority = task.getPriority() != null ? task.getPriority() : 5;
            priorityGroups.computeIfAbsent(priority, k -> new ArrayList<>()).add(task);
        }
        
        // 按优先级从高到低处理
        priorityGroups.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                List<MsgSendQueue> priorityTasks = entry.getValue();
                priorityTasks.forEach(this::processTaskAsync);
            });
    }
    
    /**
     * 同步处理任务
     */
    private void processTasksSync(List<MsgSendQueue> tasks) {
        tasks.forEach(this::processTaskSync);
    }
    
    /**
     * 异步处理单个任务
     */
    private void processTaskAsync(MsgSendQueue task) {
        taskExecutor.submit(() -> {
            try {
                processSingleTask(task);
            } catch (Exception e) {
                log.error("异步处理任务异常: taskId={}", task.getId(), e);
            }
        });
    }
    
    /**
     * 同步处理单个任务
     */
    private void processTaskSync(MsgSendQueue task) {
        try {
            processSingleTask(task);
        } catch (Exception e) {
            log.error("同步处理任务异常: taskId={}", task.getId(), e);
        }
    }
    
    /**
     * 处理单个任务
     */
    private void processSingleTask(MsgSendQueue task) {
        String taskId = task.getId();
        
        try {
            // 任务去重检查
            if (queueConfig.isEnableDeduplication() && isDuplicateTask(taskId)) {
                log.debug("跳过重复任务: taskId={}", taskId);
                return;
            }
            
            // 标记为处理中
            if (!markAsProcessing(taskId)) {
                log.warn("任务已被其他线程处理: taskId={}", taskId);
                return;
            }
            
            activeTaskCount.incrementAndGet();
            totalProcessedCount.incrementAndGet();
            
            // 执行任务
            long startTime = System.currentTimeMillis();
            boolean success = executeTask(task);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 检查是否超时
            if (duration > queueConfig.getMaxProcessTime() * 1000L) {
                log.warn("任务处理超时: taskId={}, duration={}ms", taskId, duration);
            }
            
            // 更新统计
            if (success) {
                successProcessedCount.incrementAndGet();
            } else {
                failedProcessedCount.incrementAndGet();
            }
            
            if (queueConfig.isDetailedLogging()) {
                log.debug("任务处理完成: taskId={}, success={}, duration={}ms", 
                        taskId, success, duration);
            }
            
        } catch (Exception e) {
            log.error("处理任务异常: taskId={}", taskId, e);
            failedProcessedCount.incrementAndGet();
            
            // 标记为失败
            markAsFailed(taskId, "PROCESS_ERROR", e.getMessage());
            
        } finally {
            activeTaskCount.decrementAndGet();
            
            // 清理去重缓存
            if (queueConfig.isEnableDeduplication()) {
                taskDeduplicationCache.remove(taskId);
            }
        }
    }
    
    /**
     * 检查是否为重复任务
     */
    private boolean isDuplicateTask(String taskId) {
        Long lastProcessTime = taskDeduplicationCache.get(taskId);
        if (lastProcessTime == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long cacheTime = queueConfig.getDeduplicationCacheMinutes() * 60 * 1000L;
        
        if (currentTime - lastProcessTime < cacheTime) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 执行任务逻辑
     */
    private boolean executeTask(MsgSendQueue task) {
        try {
            String taskType = task.getQueueType();
            String msgId = task.getMsgId();
            
            if (queueConfig.isDetailedLogging()) {
                log.debug("执行任务: taskId={}, type={}, msgId={}", 
                        task.getId(), taskType, msgId);
            }
            
            // 根据任务类型执行不同的处理逻辑
            switch (taskType) {
                case "SEND":
                    return processSendTask(task);
                case "CALLBACK":
                    return processCallbackTask(task);
                case "RETRY":
                    return processRetryTask(task);
                case "STATUS_UPDATE":
                    return processStatusUpdateTask(task);
                default:
                    log.warn("未知的任务类型: {}", taskType);
                    markAsFailed(task.getId(), "UNKNOWN_TYPE", "未知的任务类型");
                    return false;
            }
            
        } catch (Exception e) {
            log.error("执行任务异常: taskId={}", task.getId(), e);
            markAsFailed(task.getId(), "EXECUTE_ERROR", e.getMessage());
            return false;
        }
    }
    
    /**
     * 处理发送任务
     */
    private boolean processSendTask(MsgSendQueue task) {
    	 // TODO: 实现发送的逻辑
        log.debug("处理发送任务: taskId={}, msgId={}", task.getId(), task.getMsgId());
        
        // 模拟处理成功
        return markAsSuccess(task.getId(), "200", "发送成功");
    }
    
    /**
     * 处理回调任务
     */
    private boolean processCallbackTask(MsgSendQueue task) {
        // TODO: 实现回调到业务系统的逻辑
        log.debug("处理回调任务: taskId={}, msgId={}", task.getId(), task.getMsgId());
        
        // 模拟处理成功
        return markAsSuccess(task.getId(), "200", "回调成功");
    }
    
    /**
     * 处理重试任务
     */
    private boolean processRetryTask(MsgSendQueue task) {
        log.debug("处理重试任务: taskId={}, msgId={}", task.getId(), task.getMsgId());
        
        // TODO: 根据具体业务逻辑实现重试
        // 这里可以调用相应的服务进行重试
        
        return markAsSuccess(task.getId(), "200", "重试成功");
    }
    
    /**
     * 处理状态更新任务
     */
    private boolean processStatusUpdateTask(MsgSendQueue task) {
        log.debug("处理状态更新任务: taskId={}, msgId={}", task.getId(), task.getMsgId());
        
        // TODO: 实现状态更新逻辑
        
        return markAsSuccess(task.getId(), "200", "状态更新成功");
    }
    
    /**
     * 清理已完成的任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void cleanCompletedTasks() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -queueConfig.getCompletedRetentionDays());
            Date beforeDate = calendar.getTime();
            
            LambdaQueryWrapper<MsgSendQueue> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgSendQueue::getQueueStatus, "SUCCESS", "FAILED");
            wrapper.le(MsgSendQueue::getExecuteEndTime, beforeDate);
            
            Long count = this.count(wrapper);
            if (count > 0) {
                boolean result = this.remove(wrapper);
                if (result) {
                    log.info("清理已完成任务成功: 清理条数={}, 保留天数={}", 
                            count, queueConfig.getCompletedRetentionDays());
                }
            }
        } catch (Exception e) {
            log.error("清理已完成任务失败", e);
        }
    }
    
    /**
     * 获取队列统计信息
     */
    @Override
    public Map<String, Object> getQueueStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("serviceStatus", serviceStatus);
        stats.put("running", running);
        stats.put("serviceStartTime", serviceStartTime);
        stats.put("lastProcessTime", lastProcessTime);
        stats.put("activeTaskCount", activeTaskCount.get());
        stats.put("totalProcessedCount", totalProcessedCount.get());
        stats.put("successProcessedCount", successProcessedCount.get());
        stats.put("failedProcessedCount", failedProcessedCount.get());
        stats.put("deduplicationCacheSize", taskDeduplicationCache.size());
        
        // 配置信息
        stats.put("config", queueConfig);
        
        // 线程池状态
        if (taskExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) taskExecutor;
            Map<String, Object> executorStats = new HashMap<>();
            executorStats.put("activeCount", executor.getActiveCount());
            executorStats.put("poolSize", executor.getPoolSize());
            executorStats.put("corePoolSize", executor.getCorePoolSize());
            executorStats.put("maxPoolSize", executor.getMaximumPoolSize());
            executorStats.put("queueSize", executor.getQueue().size());
            executorStats.put("completedTaskCount", executor.getCompletedTaskCount());
            executorStats.put("largestPoolSize", executor.getLargestPoolSize());
            stats.put("executorStatistics", executorStats);
        }
        
        // 获取各种状态的队列数量
        try {
            stats.put("pendingCount", countByStatus("PENDING"));
            stats.put("processingCount", countByStatus("PROCESSING"));
            stats.put("successCount", countByStatus("SUCCESS"));
            stats.put("failedCount", countByStatus("FAILED"));
        } catch (Exception e) {
            log.error("获取队列统计信息失败", e);
        }
        
        return stats;
    }
    

    
    /**
     * 定时监控队列状态
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void monitorQueueStatus() {
        if (!queueConfig.isEnabled() || !queueConfig.isHealthCheckEnabled()) {
            return;
        }
        
        try {
            long pendingCount = countByStatus("PENDING");
            long processingCount = countByStatus("PROCESSING");
            
            // 告警检查
            if (pendingCount > queueConfig.getQueueCapacity() * 0.8) {
                log.warn("待处理队列任务过多: {} 个，超过队列容量的80%", pendingCount);
            }
            
            if (processingCount > queueConfig.getMaxPoolSize() * 2) {
                log.warn("处理中任务过多: {} 个，超过最大线程数的2倍", processingCount);
            }
            
            // 线程池健康检查
            if (taskExecutor instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) taskExecutor;
                if (executor.isShutdown() || executor.isTerminating() || executor.isTerminated()) {
                    log.error("线程池异常，正在重启队列服务");
                    restart();
                }
            }
            
            if (queueConfig.isDetailedLogging()) {
                log.debug("队列监控: 待处理={}, 处理中={}, 活跃线程={}", 
                        pendingCount, processingCount, activeTaskCount.get());
            }
            
        } catch (Exception e) {
            log.error("队列监控任务异常", e);
        }
    }
    
    /**
     * 关闭线程池
     */
    private void shutdownThreadPools() {
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
        
        if (taskExecutor != null) {
            taskExecutor.shutdown();
            try {
                if (!taskExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                taskExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 清理资源
     */
    @PreDestroy
    @Override
    public void destroy() {
        log.info("开始清理消息队列服务资源...");
        
        try {
            serviceStatus = "DESTROYING";
            
            // 停止服务
            stop();
            
            // 关闭线程池
            shutdownThreadPools();
            
            // 清理缓存
            taskDeduplicationCache.clear();
            
            serviceStatus = "DESTROYED";
            log.info("消息队列服务资源清理完成");
            
        } catch (Exception e) {
            serviceStatus = "DESTROY_FAILED";
            log.error("清理消息队列服务资源失败", e);
            throw new RuntimeException("清理消息队列服务资源失败", e);
        }
    }
}