package ltd.huntinginfo.feng.center.service.processor;

import ltd.huntinginfo.feng.admin.api.feign.RemoteAreaService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteOrgService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUserService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.service.*;
import ltd.huntinginfo.feng.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 消息分发处理器
 * <p>
 * 职责：
 * 1. 处理消息状态变更后的业务逻辑（如状态更新、任务创建、统计等）
 * 2. 执行分发队列任务（写扩散/读扩散）
 * 3. 调用 Feign 接口获取用户/部门/组织/区域信息
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDistributionProcessor {
    
    // 分发策略阈值：接收者数量超过此阈值使用广播模式
    private static final int BROADCAST_THRESHOLD = 1000;
    
    // 分页查询大小（避免一次性获取过多数据）
    private static final int BATCH_QUERY_SIZE = 1000;
    
    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgQueueService umpMsgQueueService;
    
    // Feign客户端
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteOrgService remoteOrgService;
    private final RemoteAreaService remoteAreaService;
    
    // ==================== 消息状态事件处理（由消费者直接调用） ====================

    /**
     * 处理【消息已接收】事件
     * 业务逻辑：创建消息分发队列任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageReceived(String messageId, Map<String, Object> payload) {
        log.info("处理消息已接收事件，创建分发队列任务，消息ID: {}", messageId);

        UmpMsgMain message = umpMsgMainService.getById(messageId);
        if (message == null) {
            log.error("消息不存在，消息ID: {}", messageId);
            return;
        }

        if (!"RECEIVED".equals(message.getStatus())) {
            log.warn("消息状态不是RECEIVED，跳过创建分发任务，消息ID: {}, 状态: {}",
                    messageId, message.getStatus());
            return;
        }

        // 创建消息分发队列任务
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("messageId", messageId);
        taskData.put("receiverType", message.getReceiverType());
        taskData.put("receiverScope", message.getReceiverScope());
        taskData.put("estimatedCount", 0);

        String queueTaskId = umpMsgQueueService.createQueueTask(
                "DISTRIBUTE",
                "message_distribute",
                messageId,
                taskData,
                5,
                LocalDateTime.now(),
                3
        );

        log.info("消息分发队列任务创建成功，消息ID: {}, 队列任务ID: {}", messageId, queueTaskId);
    }

    /**
     * 处理【消息已分发】事件
     * 业务逻辑：更新消息分发时间，触发后续流程（如推送任务创建）
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageDistributed(String messageId, Map<String, Object> payload) {
        log.info("处理消息已分发事件，消息ID: {}", messageId);

        // 1. 更新消息分发时间
        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getDistributeTime, LocalDateTime.now())
                .update();

        // 2. 获取消息详情，判断是否需要创建推送任务
        UmpMsgMain message = umpMsgMainService.getById(messageId);
        if (message != null && "PUSH".equals(message.getPushMode())) {
            // 异步触发推送任务创建（通过队列）
            Map<String, Object> pushTaskData = new HashMap<>();
            pushTaskData.put("messageId", messageId);
            pushTaskData.put("pushMode", message.getPushMode());

            umpMsgQueueService.createQueueTask(
                    "PUSH",
                    "message_push",
                    messageId,
                    pushTaskData,
                    7,
                    LocalDateTime.now(),
                    3
            );
            log.info("已触发推送任务创建，消息ID: {}", messageId);
        }
    }

    /**
     * 处理【消息已发送】事件
     * 业务逻辑：更新发送时间，记录发送成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageSent(String messageId, Map<String, Object> payload) {
        log.info("处理消息已发送事件，消息ID: {}", messageId);

        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getSendTime, LocalDateTime.now())
                .set(UmpMsgMain::getStatus, "SENT")
                .update();

        // 可在此处扩展：通知发送方、记录审计日志等
    }

    /**
     * 处理【消息已读】事件
     * 业务逻辑：更新收件箱阅读状态、更新消息主表已读计数
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageRead(String messageId, Map<String, Object> payload) {
        log.info("处理消息已读事件，消息ID: {}", messageId);

        String receiverId = (String) payload.get("receiverId");
        if (receiverId == null) {
            log.warn("消息已读事件缺少 receiverId，消息ID: {}", messageId);
            return;
        }

        // 更新收件箱阅读状态
        umpMsgInboxService.lambdaUpdate()
                .eq(UmpMsgInbox::getMsgId, messageId)
                .eq(UmpMsgInbox::getReceiverId, receiverId)
                .set(UmpMsgInbox::getReadStatus, 1)
                .set(UmpMsgInbox::getReadTime, LocalDateTime.now())
                .update();

        // 重新统计已读数量并更新消息主表
        updateMessageReadCount(messageId);
    }

    /**
     * 处理【消息已过期】事件
     * 业务逻辑：将消息状态标记为过期，可进行资源清理
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageExpired(String messageId, Map<String, Object> payload) {
        log.info("处理消息已过期事件，消息ID: {}", messageId);

        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getStatus, "EXPIRED")
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();

        // 可选：清理未发送的推送任务、回调任务等
    }

    /**
     * 处理【消息失败】事件
     * 业务逻辑：标记失败状态，记录失败原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageFailed(String messageId, Map<String, Object> payload) {
        String reason = (String) payload.getOrDefault("reason", "未知错误");
        log.info("处理消息失败事件，消息ID: {}, 原因: {}", messageId, reason);

        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getStatus, "FAILED")
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();
    }
    // ==================== 队列任务处理 ====================

    /**
     * 处理分发队列任务（写扩散/读扩散）
     * 此方法由队列消费者调用，payload 中包含任务数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDistributeTask(Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        String messageId = (String) payload.get("messageId");

        // 查询任务实体
        UmpMsgQueue queueTask = umpMsgQueueService.getById(taskId);
        if (queueTask == null) {
            log.error("分发任务不存在，taskId: {}", taskId);
            return;
        }

        // 调用原处理逻辑
        processDistributeTask(queueTask);
    }

    /**
     * 处理分发队列任务（实体版本）
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDistributeTask(UmpMsgQueue queueTask) {
        String taskId = queueTask.getId();
        String messageId = queueTask.getMsgId();
        String workerId = "distribution-processor-" + Thread.currentThread().getId();

        log.info("开始处理分发队列任务，任务ID: {}, 消息ID: {}", taskId, messageId);

        try {
            // 1. 标记任务为处理中
            umpMsgQueueService.markAsProcessing(taskId, workerId);

            // 2. 获取消息详情
            UmpMsgMain message = umpMsgMainService.getById(messageId);
            if (message == null) {
                throw new RuntimeException("消息不存在: " + messageId);
            }

            // 3. 解析接收者列表
            List<Map<String, Object>> receiverList = resolveReceivers(message);

            // 4. 更新任务预估数量
            updateTaskEstimate(queueTask, receiverList.size());

            // 5. 根据接收者数量选择分发策略
            if (receiverList.size() <= BROADCAST_THRESHOLD) {
                distributeToInbox(message, receiverList);
                if ("PUSH".equals(message.getPushMode())) {
                    createPushTasks(messageId, receiverList);
                }
            } else {
                distributeToBroadcast(message, receiverList);
                createBroadcastDistributeTask(messageId, receiverList.size());
            }

            // 6. 更新消息状态为已分发
            umpMsgMainService.updateMessageStatus(messageId, "DISTRIBUTED");

            // 7. 标记任务为成功
            umpMsgQueueService.markAsSuccess(taskId, workerId,
                    String.format("成功分发消息，接收者数量: %d", receiverList.size()));

            log.info("分发队列任务处理成功，任务ID: {}, 消息ID: {}, 接收者数量: {}",
                    taskId, messageId, receiverList.size());

        } catch (Exception e) {
            log.error("处理分发队列任务失败，任务ID: {}, 消息ID: {}", taskId, messageId, e);
            umpMsgQueueService.markAsFailed(taskId, workerId, e.getMessage(), e.toString());

            // 重试判断
            if (queueTask.getCurrentRetry() >= queueTask.getMaxRetry()) {
                umpMsgMainService.updateMessageStatus(messageId, "FAILED");
                log.error("分发任务达到最大重试次数，消息ID: {}", messageId);
            }
            throw e;
        }
    }

    /**
     * 处理推送任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processSendTask(Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        log.info("处理推送任务，taskId: {}", taskId);
        // TODO: 调用实际推送逻辑（HTTP/WebSocket等）
        umpMsgQueueService.markAsSuccess(taskId, "push-worker", "推送成功");
    }

    /**
     * 处理回调任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processCallbackTask(Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        log.info("处理回调任务，taskId: {}", taskId);
        // TODO: 调用业务系统回调接口
        umpMsgQueueService.markAsSuccess(taskId, "callback-worker", "回调成功");
    }

    /**
     * 处理重试任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processRetryTask(Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        log.info("处理重试任务，taskId: {}", taskId);
        // TODO: 根据原任务类型重新入队
    }

    /**
     * 处理广播分发任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processBroadcastDispatchTask(Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        log.info("处理广播分发任务，taskId: {}", taskId);
        // TODO: 实现广播消息的分页拉取与推送
        umpMsgQueueService.markAsSuccess(taskId, "broadcast-worker", "广播分发完成");
    }

    /**
     * 处理延迟发送任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDelayedSend(Map<String, Object> payload) {
        log.info("处理延迟发送任务，payload: {}", payload);
        // 将延迟任务转为普通推送任务
        String messageId = (String) payload.get("messageId");
        Map<String, Object> sendTaskData = new HashMap<>();
        sendTaskData.put("messageId", messageId);
        sendTaskData.put("delayed", true);
        umpMsgQueueService.createQueueTask("PUSH", "message_push", messageId, sendTaskData, 7, LocalDateTime.now(), 3);
    }

    /**
     * 处理延迟过期任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDelayedExpire(Map<String, Object> payload) {
        log.info("处理延迟过期任务，payload: {}", payload);
        String messageId = (String) payload.get("messageId");
        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getStatus, "EXPIRED")
                .update();
    }
    
    // ==================== 接收者解析、分发逻辑 ====================
    
    /**
     * 解析接收者列表（使用Feign调用）
     */
    private List<Map<String, Object>> resolveReceivers(UmpMsgMain message) {
        String receiverType = message.getReceiverType();
        Map<String, Object> receiverScope = message.getReceiverScope();
        List<Map<String, Object>> receiverList = new ArrayList<>();
        
        try {
            switch (receiverType) {
                case "USER":
                    receiverList = resolveUserReceivers(receiverScope);
                    break;
                case "DEPT":
                    receiverList = resolveDepartmentReceivers(receiverScope);
                    break;
                case "ORG":
                    receiverList = resolveOrganizationReceivers(receiverScope);
                    break;
                case "AREA":
                    receiverList = resolveAreaReceivers(receiverScope);
                    break;
                case "ALL":
                    receiverList = resolveAllReceivers();
                    break;
                default:
                    log.warn("未知的接收者类型: {}", receiverType);
                    break;
            }
        } catch (Exception e) {
            log.error("解析接收者失败，消息ID: {}, 接收者类型: {}", message.getId(), receiverType, e);
            throw new RuntimeException("解析接收者失败: " + e.getMessage(), e);
        }
        
        log.info("解析接收者完成，消息ID: {}, 接收者类型: {}, 数量: {}", 
                message.getId(), receiverType, receiverList.size());
        
        return receiverList;
    }
    
    /**
     * 解析个人接收者
     */
    private List<Map<String, Object>> resolveUserReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope == null || receiverScope.isEmpty()) {
            return receivers;
        }
        
        try {
            // 方式1：直接指定用户ID列表
            if (receiverScope.containsKey("userIds") && receiverScope.get("userIds") instanceof List) {
                List<String> userIds = (List<String>) receiverScope.get("userIds");
                
                // 批量获取用户信息
                if (!CollectionUtils.isEmpty(userIds)) {
                    // 分批处理，避免单次请求数据量过大
                    for (int i = 0; i < userIds.size(); i += BATCH_QUERY_SIZE) {
                        int end = Math.min(i + BATCH_QUERY_SIZE, userIds.size());
                        List<String> batchIds = userIds.subList(i, end);
                        
                        // 调用Feign服务获取用户信息
                        R<List<Map<String, Object>>> result = remoteUserService.listUsersByIds(batchIds);
                        
                        if (result != null && result.getData() != null) {
                            List<Map<String, Object>> userList = result.getData();
                            for (Map<String, Object> user : userList) {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) {
                                    receivers.add(receiver);
                                }
                            }
                        }
                    }
                }
            }
            
            // 方式2：通过其他条件查询用户（如角色、部门等）
            if (receiverScope.containsKey("query") && receiverScope.get("query") instanceof Map) {
                Map<String, Object> query = (Map<String, Object>) receiverScope.get("query");
                
                // 调用Feign服务查询用户
                R<List<Map<String, Object>>> result = remoteUserService.listUsersByDept(query);
                
                if (result != null && result.getData() != null) {
                    List<Map<String, Object>> userList = result.getData();
                    for (Map<String, Object> user : userList) {
                        Map<String, Object> receiver = createUserReceiver(user);
                        if (receiver != null) {
                            receivers.add(receiver);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("解析个人接收者失败", e);
            throw new RuntimeException("解析个人接收者失败: " + e.getMessage(), e);
        }
        
        return receivers;
    }
    
    /**
     * 解析部门接收者
     */
    private List<Map<String, Object>> resolveDepartmentReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope == null || receiverScope.isEmpty()) {
            return receivers;
        }
        
        try {
            // 方式1：直接指定部门ID列表
            if (receiverScope.containsKey("deptIds") && receiverScope.get("deptIds") instanceof List) {
                List<String> deptIds = (List<String>) receiverScope.get("deptIds");
                
                if (!CollectionUtils.isEmpty(deptIds)) {
                    // 获取部门信息
                    R<List<Map<String, Object>>> deptResult = remoteDeptService.listDeptsByIds(deptIds);
                    
                    if (deptResult != null && deptResult.getData() != null) {
                        // 获取每个部门下的用户
                        for (String deptId : deptIds) {
                            Map<String, Object> query = new HashMap<>();
                            query.put("deptId", deptId);
                            query.put("includeSubDepts", true); // 默认包含子部门
                            
                            R<List<Map<String, Object>>> userResult = remoteUserService.listUsersByDept(query);
                            
                            if (userResult != null && userResult.getData() != null) {
                                List<Map<String, Object>> userList = userResult.getData();
                                for (Map<String, Object> user : userList) {
                                    Map<String, Object> receiver = createUserReceiver(user);
                                    if (receiver != null) {
                                        receivers.add(receiver);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 方式2：通过部门树查询
            if (receiverScope.containsKey("deptTree") && receiverScope.get("deptTree") instanceof Map) {
                Map<String, Object> deptTreeQuery = (Map<String, Object>) receiverScope.get("deptTree");
                
                // 获取部门树
                String deptName = (String) deptTreeQuery.get("deptName");
                R<List<Map<String, Object>>> deptTreeResult = remoteDeptService.getDeptTree(deptName);
                
                if (deptTreeResult != null && deptTreeResult.getData() != null) {
                    // 从部门树中获取所有部门ID，然后查询用户
                    List<String> allDeptIds = extractDeptIdsFromTree(deptTreeResult.getData());
                    
                    // 批量获取用户（这里简化处理，实际需要分批次）
                    for (String deptId : allDeptIds) {
                        Map<String, Object> query = new HashMap<>();
                        query.put("deptId", deptId);
                        
                        R<List<Map<String, Object>>> userResult = remoteUserService.listUsersByDept(query);
                        
                        if (userResult != null && userResult.getData() != null) {
                            List<Map<String, Object>> userList = userResult.getData();
                            for (Map<String, Object> user : userList) {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) {
                                    receivers.add(receiver);
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("解析部门接收者失败", e);
            throw new RuntimeException("解析部门接收者失败: " + e.getMessage(), e);
        }
        
        return receivers;
    }
    
    /**
     * 解析组织接收者
     */
    private List<Map<String, Object>> resolveOrganizationReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope == null || receiverScope.isEmpty()) {
            return receivers;
        }
        
        try {
            // 方式1：直接指定组织ID列表
            if (receiverScope.containsKey("orgIds") && receiverScope.get("orgIds") instanceof List) {
                List<String> orgIds = (List<String>) receiverScope.get("orgIds");
                
                if (!CollectionUtils.isEmpty(orgIds)) {
                    // 获取组织信息
                    R<List<Map<String, Object>>> orgResult = remoteOrgService.listOrgsByIds(orgIds);
                    
                    if (orgResult != null && orgResult.getData() != null) {
                        // 获取每个组织下的用户
                        for (String orgId : orgIds) {
                            Map<String, Object> query = new HashMap<>();
                            query.put("orgId", orgId);
                            
                            R<List<Map<String, Object>>> userResult = remoteOrgService.listUsersByOrg(query);
                            
                            if (userResult != null && userResult.getData() != null) {
                                List<Map<String, Object>> userList = userResult.getData();
                                for (Map<String, Object> user : userList) {
                                    Map<String, Object> receiver = createUserReceiver(user);
                                    if (receiver != null) {
                                        receivers.add(receiver);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("解析组织接收者失败", e);
            throw new RuntimeException("解析组织接收者失败: " + e.getMessage(), e);
        }
        
        return receivers;
    }
    
    /**
     * 解析区域接收者
     */
    private List<Map<String, Object>> resolveAreaReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope == null || receiverScope.isEmpty()) {
            return receivers;
        }
        
        try {
            // 方式1：指定行政区划代码
            if (receiverScope.containsKey("areaCodes") && receiverScope.get("areaCodes") instanceof List) {
                List<String> areaCodes = (List<String>) receiverScope.get("areaCodes");
                
                if (!CollectionUtils.isEmpty(areaCodes)) {
                    // 获取区域信息
                    R<List<Map<String, Object>>> areaResult = remoteAreaService.listAreasByCodes(areaCodes);
                    
                    if (areaResult != null && areaResult.getData() != null) {
                        // 获取每个区域的用户
                        for (String areaCode : areaCodes) {
                            Map<String, Object> query = new HashMap<>();
                            query.put("areaCode", areaCode);
                            query.put("includeSubAreas", true); // 默认包含子区域
                            
                            R<List<Map<String, Object>>> userResult = remoteAreaService.listUsersByArea(query);
                            
                            if (userResult != null && userResult.getData() != null) {
                                List<Map<String, Object>> userList = userResult.getData();
                                for (Map<String, Object> user : userList) {
                                    Map<String, Object> receiver = createUserReceiver(user);
                                    if (receiver != null) {
                                        receivers.add(receiver);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("解析区域接收者失败", e);
            throw new RuntimeException("解析区域接收者失败: " + e.getMessage(), e);
        }
        
        return receivers;
    }
    
    /**
     * 解析全体接收者
     */
    private List<Map<String, Object>> resolveAllReceivers() {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        try {
            // 这里需要实现获取所有活跃用户的逻辑
            // 由于用户数量可能很大，需要分页查询
            
            int page = 1;
            int pageSize = 1000;
            boolean hasMore = true;
            
            while (hasMore) {
                Map<String, Object> query = new HashMap<>();
                query.put("page", page);
                query.put("size", pageSize);
                query.put("status", "ACTIVE"); // 只获取活跃用户
                
                // 注意：feng-user3项目可能需要添加分页查询接口
                // 这里假设有相应的接口
                R<List<Map<String, Object>>> result = remoteUserService.listUsersByDept(query);
                
                if (result != null && result.getData() != null) {
                    List<Map<String, Object>> userList = result.getData();
                    
                    if (userList.isEmpty()) {
                        hasMore = false;
                    } else {
                        for (Map<String, Object> user : userList) {
                            Map<String, Object> receiver = createUserReceiver(user);
                            if (receiver != null) {
                                receivers.add(receiver);
                            }
                        }
                        page++;
                    }
                } else {
                    hasMore = false;
                }
                
                // 避免无限循环
                if (page > 100) {
                    log.warn("获取全体接收者时达到最大分页数限制");
                    break;
                }
            }
            
        } catch (Exception e) {
            log.error("解析全体接收者失败", e);
            throw new RuntimeException("解析全体接收者失败: " + e.getMessage(), e);
        }
        
        return receivers;
    }
    
    /**
     * 分发到收件箱（写扩散）
     */
    private void distributeToInbox(UmpMsgMain message, List<Map<String, Object>> receiverList) {
        String msgId = message.getId();
        
        // 批量创建收件箱记录
        int createdCount = umpMsgInboxService.batchCreateInboxRecords(msgId, receiverList, "INBOX");
        
        log.info("消息分发到收件箱成功，消息ID: {}, 接收者数量: {}, 成功创建: {}", 
                msgId, receiverList.size(), createdCount);
        
        // 更新消息的接收者数量
        umpMsgMainService.updateReceiverCount(msgId, receiverList.size(), 0, 0);
    }
    
    /**
     * 分发到广播信息筒（读扩散）
     */
    private void distributeToBroadcast(UmpMsgMain message, List<Map<String, Object>> receiverList) {
        String msgId = message.getId();
        String receiverType = message.getReceiverType();
        Map<String, Object> receiverScope = message.getReceiverScope();
        
        // 1. 创建广播记录
        Map<String, Object> targetScope = new HashMap<>();
        targetScope.put("receiverType", receiverType);
        targetScope.put("receiverScope", receiverScope);
        targetScope.put("estimatedCount", receiverList.size());
        
        String broadcastType = determineBroadcastType(receiverType, receiverList.size());
        String targetDescription = generateTargetDescription(receiverType, receiverScope);
        
        String broadcastId = umpMsgBroadcastService.createBroadcast(
                msgId, broadcastType, targetScope, targetDescription);
        
        // 2. 更新广播统计信息（预估接收者数量）
        umpMsgBroadcastService.updateBroadcastStatistics(
                broadcastId, 0, 0, 0);
        
        // 3. 更新消息的接收者数量
        umpMsgMainService.updateReceiverCount(msgId, receiverList.size(), 0, 0);
        
        log.info("消息分发到广播信息筒成功，消息ID: {}, 广播ID: {}, 预估接收者: {}", 
                msgId, broadcastId, receiverList.size());
    }
    
    /**
     * 创建推送队列任务
     */
    private void createPushTasks(String msgId, List<Map<String, Object>> receiverList) {
        if (CollectionUtils.isEmpty(receiverList)) {
            return;
        }
        
        // 为每个接收者创建推送任务
        for (Map<String, Object> receiver : receiverList) {
            String receiverId = (String) receiver.get("receiverId");
            String receiverType = (String) receiver.get("receiverType");
            
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("msgId", msgId);
            taskData.put("receiverId", receiverId);
            taskData.put("receiverType", receiverType);
            taskData.put("receiverName", receiver.get("receiverName"));
            
            umpMsgQueueService.createQueueTask(
                    "PUSH",                      // 队列类型
                    "message_push",             // 队列名称
                    msgId,                      // 消息ID
                    taskData,                   // 任务数据
                    7,                          // 优先级（较低）
                    LocalDateTime.now(),        // 立即执行
                    3                           // 最大重试次数
            );
        }
        
        log.info("创建推送队列任务成功，消息ID: {}, 任务数量: {}", msgId, receiverList.size());
    }
    
    /**
     * 创建广播分发队列任务
     */
    private void createBroadcastDistributeTask(String msgId, int receiverCount) {
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("msgId", msgId);
        taskData.put("receiverCount", receiverCount);
        taskData.put("status", "PENDING");
        
        umpMsgQueueService.createQueueTask(
                "BROADCAST_DISTRIBUTE",        // 队列类型
                "broadcast_distribute",        // 队列名称
                msgId,                         // 消息ID
                taskData,                      // 任务数据
                5,                             // 优先级
                LocalDateTime.now(),           // 立即执行
                3                              // 最大重试次数
        );
        
        log.info("创建广播分发队列任务成功，消息ID: {}, 接收者数量: {}", msgId, receiverCount);
    }
    
    /**
     * 更新任务预估数量
     */
    private void updateTaskEstimate(UmpMsgQueue queueTask, int estimatedCount) {
        Map<String, Object> taskData = queueTask.getTaskData();
        if (taskData == null) {
            taskData = new HashMap<>();
        }
        
        taskData.put("estimatedCount", estimatedCount);
        
        // 这里需要更新队列任务的数据，但由于UmpMsgQueueService没有提供更新任务数据的方法，
        // 我们可以通过直接更新实体或扩展服务来实现
        // 简化处理：这里只记录日志
        log.debug("更新任务预估数量，任务ID: {}, 预估数量: {}", queueTask.getId(), estimatedCount);
    }
    
    // ============ 辅助方法 ============
    
    private Map<String, Object> createUserReceiver(Map<String, Object> user) {
        if (user == null || user.isEmpty()) {
            return null;
        }
        
        String userId = (String) user.get("id");
        String username = (String) user.get("username");
        
        if (!StringUtils.hasText(userId)) {
            log.warn("用户信息缺少ID字段: {}", user);
            return null;
        }
        
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("receiverId", userId);
        receiver.put("receiverType", "USER");
        receiver.put("receiverName", StringUtils.hasText(username) ? username : userId);
        
        // 可以添加其他信息，如部门、组织等
        if (user.containsKey("deptId")) {
            receiver.put("deptId", user.get("deptId"));
        }
        if (user.containsKey("deptName")) {
            receiver.put("deptName", user.get("deptName"));
        }
        
        return receiver;
    }
    
    private List<String> extractDeptIdsFromTree(List<Map<String, Object>> deptTree) {
        List<String> deptIds = new ArrayList<>();
        extractDeptIdsRecursive(deptTree, deptIds);
        return deptIds;
    }
    
    private void extractDeptIdsRecursive(List<Map<String, Object>> nodes, List<String> deptIds) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        
        for (Map<String, Object> node : nodes) {
            String deptId = (String) node.get("id");
            if (StringUtils.hasText(deptId)) {
                deptIds.add(deptId);
            }
            
            // 递归处理子节点
            if (node.containsKey("children") && node.get("children") instanceof List) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
                extractDeptIdsRecursive(children, deptIds);
            }
        }
    }
    
    private String determineBroadcastType(String receiverType, int receiverCount) {
        switch (receiverType) {
            case "ALL":
                return "ALL";
            case "AREA":
                return "AREA";
            case "ORG":
                return "ORG";
            case "DEPT":
                return receiverCount > 5000 ? "MASS_DEPT" : "DEPT";
            default:
                return "CUSTOM";
        }
    }
    
    private String generateTargetDescription(String receiverType, Map<String, Object> receiverScope) {
        if (receiverScope == null) {
            return "自定义接收者";
        }
        
        switch (receiverType) {
            case "USER":
                if (receiverScope.containsKey("userIds") && receiverScope.get("userIds") instanceof List) {
                    List<String> userIds = (List<String>) receiverScope.get("userIds");
                    return "指定用户: " + userIds.size() + "人";
                }
                break;
            case "DEPT":
                if (receiverScope.containsKey("deptIds") && receiverScope.get("deptIds") instanceof List) {
                    List<String> deptIds = (List<String>) receiverScope.get("deptIds");
                    return "指定部门: " + deptIds.size() + "个";
                }
                break;
            case "ORG":
                if (receiverScope.containsKey("orgIds") && receiverScope.get("orgIds") instanceof List) {
                    List<String> orgIds = (List<String>) receiverScope.get("orgIds");
                    return "指定组织: " + orgIds.size() + "个";
                }
                break;
            case "AREA":
                if (receiverScope.containsKey("areaCodes") && receiverScope.get("areaCodes") instanceof List) {
                    List<String> areaCodes = (List<String>) receiverScope.get("areaCodes");
                    return "指定区域: " + areaCodes.size() + "个";
                }
                break;
            case "ALL":
                return "全体用户";
        }
        
        return "自定义接收者";
    }
    
    /**
     * 更新消息已读计数
     */
    private void updateMessageReadCount(String messageId) {
        Long readCount = umpMsgInboxService.lambdaQuery()
                .eq(UmpMsgInbox::getMsgId, messageId)
                .eq(UmpMsgInbox::getReadStatus, 1)
                .count();
        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getReadCount, readCount)
                .update();
    }
}