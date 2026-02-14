package ltd.huntinginfo.feng.center.service.processor;

import ltd.huntinginfo.feng.admin.api.feign.RemoteAreaService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteOrgService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUserService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.service.*;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 消息分发处理器
 * <p>
 * 职责：
 * 1. 处理消息状态变更后的业务逻辑（如状态更新、任务创建、统计等），主要业务逻辑参见数据库表脚本和UmpMsgQueueService的注释
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

    // ==================== 业务常量（私有） ====================

	/** 分页查询大小（避免一次性获取过多数据） */
    private static final int BATCH_QUERY_SIZE = 1000;

    /** 工作者ID前缀 */
    private static final String WORKER_PREFIX = "distribution-processor-";

    /** 最大分页限制（防止无限循环） */
    private static final int MAX_PAGE_LIMIT = 100;

    // ==================== 依赖注入 ====================
    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgQueueService umpMsgQueueService;
    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;
    // 状态机
    private final MessageStateMachine messageStateMachine;
    // 推送处理器
    private final MessagePushProcessor messagePushProcessor;

    // Feign客户端
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteOrgService remoteOrgService;
    private final RemoteAreaService remoteAreaService;

    // ==================== 消息状态事件处理 ====================
    
    /**
     * 处理【消息已接收】事件
     * 业务逻辑：创建消息发送队列任务，存到ump_msg_queue表中
     */    
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageReceived(String messageId, Map<String, Object> payload) {
        log.info("处理消息已接收事件，创建分发任务，消息ID: {}", messageId);

        if (StrUtil.isBlank(messageId)) {
            log.error("消息不存在，消息ID: {}", messageId);
            return;
        }
        
        String title = payload.get(MqMessageEventConstants.TaskDataKeys.TITLE).toString();
        if (StrUtil.isBlank(title)) {
            log.error("消息标题为空，消息ID: {}", messageId);
            return;
        }

        String status = payload.get(MqMessageEventConstants.TaskDataKeys.STATUS).toString();
        String oldStatus = payload.get(MqMessageEventConstants.TaskDataKeys.OLD_STATUS).toString();
        if (!MqMessageEventConstants.EventTypes.RECEIVED.equals(status)) {
            log.warn("消息状态不是RECEIVED，跳过创建分发任务，消息ID: {}, 状态: {}",
                    messageId, status);
            return;
        }

        // 1. 状态机：进入分发中
        messageStateMachine.onDistributeStart(messageId);

        // 2. 创建分发队列任务（DISTRIBUTE）

        umpMsgQueueService.createQueueTask(
                MqMessageEventConstants.QueueTaskTypes.DISTRIBUTE,
                MqMessageEventConstants.QueueNames.MESSAGE_DISTRIBUTE_QUEUE,
                messageId,
                payload,
                MqMessageEventConstants.TaskPriorities.DEFAULT,
                LocalDateTime.now(),
                MqMessageEventConstants.RetryDefaults.MAX_RETRY
        );

        log.info("分发队列任务创建成功，消息ID: {}, 队列任务ID已生成", messageId);
    }

    /**
     * 处理【消息已分发】事件
     * 业务逻辑：更新消息分发时间，触发后续流程（如推送任务创建）
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageDistributed(String messageId, Map<String, Object> payload) {
        log.info("处理消息已分发事件，消息ID: {}", messageId);
        // 状态已在 processDistributeTask 中设置为 DISTRIBUTED
        // 此方法仅用于扩展（如统计、通知等）
    }


    /**
     * 处理【消息已读】事件
     * 业务逻辑：更新收件箱阅读状态、更新消息主表已读计数
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageRead(String messageId, Map<String, Object> payload) {
        log.info("处理消息已读事件，消息ID: {}", messageId);

        String receiverId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.RECEIVER_ID);
        String receiverType = (String) payload.getOrDefault(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE,
                MqMessageEventConstants.ReceiverTypes.USER);
        if (receiverId == null) {
            log.warn("消息已读事件缺少 receiverId，消息ID: {}", messageId);
            return;
        }

        if (MqMessageEventConstants.ReceiverTypes.USER.equals(receiverType)) {
            // 收件箱已读
            umpMsgInboxService.lambdaUpdate()
                    .eq(UmpMsgInbox::getMsgId, messageId)
                    .eq(UmpMsgInbox::getReceiverId, receiverId)
                    .set(UmpMsgInbox::getReadStatus, 1)
                    .set(UmpMsgInbox::getReadTime, LocalDateTime.now())
                    .update();
            updateMessageReadCount(messageId);
        } else {
            // 广播已读，需从payload或关联查询获取broadcastId
            String broadcastId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.BROADCAST_ID);
            if (broadcastId == null) {
            	BroadcastDetailVO broadcast = umpMsgBroadcastService.getBroadcastByMsgId(messageId);
                if (broadcast != null) {
                    broadcastId = broadcast.getId();
                    broadcast.setReadCount(broadcast.getReadCount() + 1);
                    umpMsgBroadcastService.incrementReadCount(broadcastId);
                }
            }
            if (broadcastId != null) {
                umpBroadcastReceiveRecordService.markAsRead(broadcastId, receiverId, receiverType);
                
            }
        }

        // 状态机：将消息主表状态置为 READ
        messageStateMachine.onRead(messageId);
    }

    /**
     * 处理【消息已过期】事件
     * 业务逻辑：将消息状态标记为过期，可进行资源清理
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageExpired(String messageId, Map<String, Object> payload) {
        log.info("处理消息已过期事件，消息ID: {}", messageId);
        // 根据状态图，过期时状态为PULL_FAILED
        messageStateMachine.onPullFailed(messageId); 
    }


    /**
     * 处理【消息分发中】事件（DISTRIBUTING）
     * 通常由分发任务创建后发出，用于记录状态开始
     */
    @Transactional
    public void handleMessageDistributing(String messageId, Map<String, Object> payload) {
        log.info("处理消息分发中事件，消息ID: {}", messageId);
        // 状态已在创建分发任务时由状态机设置为 DISTRIBUTING，此方法仅用于日志或扩展
        // 无需重复操作状态机
    }

    /**
     * 处理【消息分发失败】事件（DIST_FAILED）
     */
    @Transactional
    public void handleMessageDistFailed(String messageId, Map<String, Object> payload) {
        log.info("处理消息分发失败事件，消息ID: {}", messageId);
        // 状态已由队列重试逻辑在超限时通过状态机设置，此处仅做补充处理（如告警）
        // 状态机已在队列服务中调用 onDistributeFailed
    }

    /**
     * 处理【消息已推送】事件（PUSHED）
     */
    @Transactional
    public void handleMessagePushed(String messageId, Map<String, Object> payload) {
        log.info("处理消息已推送事件，消息ID: {}", messageId);
        // 由推送任务执行时调用状态机 onPushed
    }

    /**
     * 处理【消息推送失败】事件（PUSH_FAILED）
     */
    @Transactional
    public void handleMessagePushFailed(String messageId, Map<String, Object> payload) {
        log.info("处理消息推送失败事件，消息ID: {}", messageId);
        // 状态已由队列重试逻辑设置，此处可记录失败原因
    }

    /**
     * 处理【业务系统已接收】事件（BIZ_RECEIVED）
     */
    @Transactional
    public void handleMessageBizReceived(String messageId, Map<String, Object> payload) {
        log.info("处理业务系统已接收事件，消息ID: {}", messageId);
        // 由推送处理器在收到业务成功响应时调用状态机 onBusinessReceived
    }

    /**
     * 处理【待拉取】事件（POLL）
     */
    @Transactional
    public void handleMessagePoll(String messageId, Map<String, Object> payload) {
        log.info("处理消息待拉取事件，消息ID: {}", messageId);
        // 由分发任务在写扩散后调用状态机 onPollReady
    }

    /**
     * 处理【业务系统已拉取】事件（BIZ_PULLED）
     */
    @Transactional
    public void handleMessageBizPolled(String messageId, Map<String, Object> payload) {
        log.info("处理业务系统已拉取事件，消息ID: {}", messageId);
        // 由拉取接口在业务系统成功拉取时调用状态机 onBusinessPulled
    }

    /**
     * 处理【拉取失败】事件（POLL_FAILED）
     */
    @Transactional
    public void handleMessagePollFailed(String messageId, Map<String, Object> payload) {
        log.info("处理消息拉取失败事件，消息ID: {}", messageId);
        // 由定时任务扫描超时消息时调用状态机 onPollFailed
    }
    
    // ==================== 队列任务处理 ====================

    /**
     * 处理分发队列任务（写扩散/读扩散）
     * 此方法由队列消费者调用，payload 中包含任务数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDistributeTask(Map<String, Object> payload) {
        String taskId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.TASK_ID);
        String messageId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID);

        // 查询任务实体
        UmpMsgQueue queueTask = umpMsgQueueService.getById(taskId);
        if (queueTask == null) {
            log.error("分发任务不存在，taskId: {}", taskId);
            return;
        }

        String workerId = WORKER_PREFIX + Thread.currentThread().getId();

        log.info("开始处理分发队列任务，任务ID: {}, 消息ID: {}", taskId, messageId);

        try {
            umpMsgQueueService.markAsProcessing(taskId, workerId);

            UmpMsgMain message = umpMsgMainService.getById(messageId);
            if (message == null) {
                throw new RuntimeException("消息不存在: " + messageId);
            }

            List<Map<String, Object>> receiverList = resolveReceivers(message);
            updateTaskEstimate(queueTask, receiverList.size());

            boolean isPushMode = MqMessageEventConstants.PushModes.PUSH.equals(message.getPushMode());
            boolean isUserType = MqMessageEventConstants.ReceiverTypes.USER.equals(message.getReceiverType());

            if (receiverList.size() <= MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                // 写扩散 - 收件箱
                umpMsgInboxService.batchCreateInboxRecords(messageId, receiverList,
                        MqMessageEventConstants.DistributeModes.INBOX);
                messageStateMachine.onDistributed(messageId, receiverList.size());

                if (isPushMode && isUserType) {
                    // 逐人推送 → 创建 PUSH 任务
                    createPushTasks(messageId, receiverList);
                }
            } else {
                // 读扩散 - 广播筒
                String broadcastType = determineBroadcastType(message.getReceiverType(), receiverList.size());
                String broadcastId = umpMsgBroadcastService.createBroadcast(
                        messageId,
                        broadcastType,
                        message.getReceiverScope(),
                        generateTargetDescription(message.getReceiverType(), message.getReceiverScope())
                );
                umpBroadcastReceiveRecordService.batchUpsertReceiveRecords(broadcastId, receiverList);
                umpMsgBroadcastService.updateReceivedCount(broadcastId, receiverList.size());
                messageStateMachine.onDistributed(messageId, receiverList.size());

                if (isPushMode && !isUserType) {
                    // 单次回调 → 创建 PUSH 任务（类型仍为 PUSH，但任务数据中携带 broadcastId 和 callbackUrl）
                	createCallbackPushTask(messageId, broadcastId, message.getCallbackUrl());
                } else if (!isPushMode) {
                    // POLL 模式：状态已为 DISTRIBUTED，无需额外动作
                    // 但需将消息置为待拉取状态？状态图中 DISTRIBUTED -> POLL 需要显式触发
                    messageStateMachine.onPollReady(messageId);
                }
            }

            umpMsgQueueService.markAsSuccess(taskId, workerId,
                    String.format("分发成功，接收者数量: %d", receiverList.size()));

        } catch (Exception e) {
            log.error("处理分发队列任务失败，任务ID: {}, 消息ID: {}", taskId, messageId, e);
            // 重试逻辑由队列服务处理，这里只记录失败
            umpMsgQueueService.markAsFailed(taskId, workerId, e.getMessage(), e.toString()); 
            throw e;
        }
    }

    /**
     * 处理重试任务
     */
    @Transactional
    public void processRetryTask(Map<String, Object> payload) {
        String taskId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.TASK_ID);
        log.info("处理重试任务，taskId: {}", taskId);
        // 重试逻辑已由 umpMsgQueueService 在 markAsFailed 中实现延迟重试
        // 此方法仅用于接收重试事件，无需额外操作
    }
    
    /**
     * 处理推送任务（PUSH）
     * 适用于：收件箱逐人推送 + 广播单次回调
     */
    @Transactional
    public void processPushTask(Map<String, Object> payload) {
        String taskId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.TASK_ID);
        String messageId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID);
        String inboxId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.INBOX_ID);
        String broadcastId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.BROADCAST_ID);
        String callbackUrl = (String) payload.get(MqMessageEventConstants.TaskDataKeys.CALLBACK_URL);

        log.info("处理推送任务，taskId: {}, messageId: {}", taskId, messageId);

        // 实际推送逻辑委托给 MessagePushProcessor

        boolean success;
        if (inboxId != null) {
            UmpMsgInbox inbox = umpMsgInboxService.getById(inboxId);
            success = messagePushProcessor.pushMessageToReceiver(inbox);
        } else if (broadcastId != null) {
            success = messagePushProcessor.callbackBusinessSystem(messageId, broadcastId, callbackUrl);
        } else {
            log.error("推送任务缺少 inboxId 或 broadcastId，taskId: {}", taskId);
            umpMsgQueueService.markAsFailed(taskId, "push-worker", "无效的任务数据", "");
            return;
        }

        if (success) {
            umpMsgQueueService.markAsSuccess(taskId, "push-worker", "推送成功");
        } else {
            // 失败由队列服务重试，这里标记失败并抛异常触发重试
            umpMsgQueueService.markAsFailed(taskId, "push-worker", "推送失败", "");
            throw new RuntimeException("推送失败");
        }
    }

    /**
     * 处理延迟发送任务
     */
    @Transactional
    public void processDelayedSend(Map<String, Object> payload) {
        log.info("处理延迟发送任务，payload: {}", payload);
        String messageId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID);
        // 延迟发送本质是创建 PUSH 任务
        Map<String, Object> pushTaskData = new HashMap<>();
        pushTaskData.put(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID, messageId);
        pushTaskData.put(MqMessageEventConstants.TaskDataKeys.DELAYED, true);
        // 需要根据原消息的接收者类型等补充任务数据，简化处理
        umpMsgQueueService.createQueueTask(
                MqMessageEventConstants.QueueTaskTypes.PUSH,
                MqMessageEventConstants.QueueNames.MESSAGE_PUSH,
                messageId,
                pushTaskData,
                MqMessageEventConstants.TaskPriorities.LOW,
                LocalDateTime.now(),
                MqMessageEventConstants.RetryDefaults.MAX_RETRY
        );
    }

    /**
     * 处理延迟过期任务
     */
    @Transactional
    public void processDelayedExpire(Map<String, Object> payload) {
        log.info("处理延迟过期任务，payload: {}", payload);
        String messageId = (String) payload.get(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID);
        messageStateMachine.onPollFailed(messageId);
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
                case MqMessageEventConstants.ReceiverTypes.USER:
                    receiverList = resolveUserReceivers(receiverScope);
                    break;
                case MqMessageEventConstants.ReceiverTypes.DEPT:
                    receiverList = resolveDepartmentReceivers(receiverScope);
                    break;
                case MqMessageEventConstants.ReceiverTypes.ORG:
                    receiverList = resolveOrganizationReceivers(receiverScope);
                    break;
                case MqMessageEventConstants.ReceiverTypes.AREA:
                    receiverList = resolveAreaReceivers(receiverScope);
                    break;
                case MqMessageEventConstants.ReceiverTypes.ALL:
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
            if (receiverScope.containsKey("loginIds") && receiverScope.get("loginIds") instanceof List) {
                List<String> userIds = (List<String>) receiverScope.get("loginIds");
                
                // 批量获取用户信息
                if (!CollectionUtils.isEmpty(userIds)) {
                    // 分批处理，避免单次请求数据量过大
                    for (int i = 0; i < userIds.size(); i += BATCH_QUERY_SIZE) {
                        int end = Math.min(i + BATCH_QUERY_SIZE, userIds.size());
                        List<String> batchIds = userIds.subList(i, end);
                        
                        // 调用Feign服务获取用户信息
                        R<List<Map<String, Object>>> result = remoteUserService.listUsersByIds(batchIds);
                        if (result != null && result.getData() != null) {
                            result.getData().forEach(user -> {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) receivers.add(receiver);
                            });
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
                    result.getData().forEach(user -> {
                        Map<String, Object> receiver = createUserReceiver(user);
                        if (receiver != null) receivers.add(receiver);
                    });
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
                    // 部门信息用于日志，不一定需要
                    for (String deptId : deptIds) {
                        Map<String, Object> query = new HashMap<>();
                        query.put("deptId", deptId);
                        query.put("includeSubDepts", true);
                        R<List<Map<String, Object>>> userResult = remoteUserService.listUsersByDept(query);
                        if (userResult != null && userResult.getData() != null) {
                            userResult.getData().forEach(user -> {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) receivers.add(receiver);
                            });
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
                            userResult.getData().forEach(user -> {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) receivers.add(receiver);
                            });
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
            if (receiverScope.containsKey("orgCodes") && receiverScope.get("orgCodes") instanceof List) {
                List<String> orgCodes = (List<String>) receiverScope.get("orgCodes");
                if (!CollectionUtils.isEmpty(orgCodes)) {
                    for (String orgCode : orgCodes) {
                        Map<String, Object> query = new HashMap<>();
                        query.put("orgCode", orgCode);
                        R<List<Map<String, Object>>> userResult = remoteOrgService.listUsersByOrg(query);
                        if (userResult != null && userResult.getData() != null) {
                            userResult.getData().forEach(user -> {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) receivers.add(receiver);
                            });
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
            if (receiverScope.containsKey("divisionCodes") && receiverScope.get("divisionCodes") instanceof List) {
                List<String> areaCodes = (List<String>) receiverScope.get("divisionCodes");
                if (!CollectionUtils.isEmpty(areaCodes)) {
                    for (String areaCode : areaCodes) {
                        Map<String, Object> query = new HashMap<>();
                        query.put("divisionCode", areaCode);
                        query.put("includeSubAreas", true);
                        R<List<Map<String, Object>>> userResult = remoteAreaService.listUsersByArea(query);
                        if (userResult != null && userResult.getData() != null) {
                            userResult.getData().forEach(user -> {
                                Map<String, Object> receiver = createUserReceiver(user);
                                if (receiver != null) receivers.add(receiver);
                            });
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
            int page = 1;
            int pageSize = BATCH_QUERY_SIZE;
            boolean hasMore = true;

            while (hasMore) {
                Map<String, Object> query = new HashMap<>();
                query.put("page", page);
                query.put("size", pageSize);
                query.put("status", "ACTIVE");

                R<List<Map<String, Object>>> result = remoteUserService.listUsersByDept(query);
                if (result != null && result.getData() != null) {
                    List<Map<String, Object>> userList = result.getData();
                    if (userList.isEmpty()) {
                        hasMore = false;
                    } else {
                        userList.forEach(user -> {
                            Map<String, Object> receiver = createUserReceiver(user);
                            if (receiver != null) receivers.add(receiver);
                        });
                        page++;
                    }
                } else {
                    hasMore = false;
                }

                if (page > MAX_PAGE_LIMIT) {
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
        int createdCount = umpMsgInboxService.batchCreateInboxRecords(
                msgId, receiverList, MqMessageEventConstants.DistributeModes.INBOX);
        log.info("消息分发到收件箱成功，消息ID: {}, 接收者数量: {}, 成功创建: {}",
                msgId, receiverList.size(), createdCount);
        umpMsgMainService.updateReceiverCount(msgId, receiverList.size(), 0, 0);
    }
    
    /**
     * 分发到广播信息筒（读扩散）
     */
    private void distributeToBroadcast(UmpMsgMain message, List<Map<String, Object>> receiverList) {
        String msgId = message.getId();
        String receiverType = message.getReceiverType();
        Map<String, Object> receiverScope = message.getReceiverScope();

        Map<String, Object> targetScope = new HashMap<>();
        targetScope.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE, receiverType);
        targetScope.put("receiverScope", receiverScope);
        targetScope.put(MqMessageEventConstants.TaskDataKeys.ESTIMATED_COUNT, receiverList.size());

        String broadcastType = determineBroadcastType(receiverType, receiverList.size());
        String targetDescription = generateTargetDescription(receiverType, receiverScope);

        String broadcastId = umpMsgBroadcastService.createBroadcast(
                msgId, broadcastType, targetScope, targetDescription);

        umpMsgBroadcastService.updateBroadcastStatistics(broadcastId, 0, 0, 0);
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

        for (Map<String, Object> receiver : receiverList) {
            String receiverId = (String) receiver.get(MqMessageEventConstants.TaskDataKeys.RECEIVER_ID);
            String receiverType = (String) receiver.get(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE);
            // 需要先创建收件箱记录才能获取 inboxId，但收件箱记录已在 distributeToInbox 中批量创建
            // 此处需要查询对应收件箱ID，简化处理：假设可以通过消息ID和接收者ID查询
            UmpMsgInbox inbox = umpMsgInboxService.getByMsgAndReceiver(msgId, receiverId, receiverType);
            if (inbox == null) {
                log.warn("收件箱记录不存在，无法创建推送任务，msgId: {}, receiverId: {}", msgId, receiverId);
                continue;
            }

            Map<String, Object> taskData = new HashMap<>();
            taskData.put(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID, msgId);
            taskData.put(MqMessageEventConstants.TaskDataKeys.INBOX_ID, inbox.getId());
            taskData.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_ID, receiverId);
            taskData.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE, receiverType);
            taskData.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_NAME, receiver.get("receiverName"));
            // 从应用凭证获取回调地址，此处简化
            taskData.put(MqMessageEventConstants.TaskDataKeys.CALLBACK_URL, getCallbackUrl(msgId));

            umpMsgQueueService.createQueueTask(
                    MqMessageEventConstants.QueueTaskTypes.PUSH,
                    MqMessageEventConstants.QueueNames.MESSAGE_PUSH,
                    msgId,
                    taskData,
                    MqMessageEventConstants.TaskPriorities.LOW,
                    LocalDateTime.now(),
                    MqMessageEventConstants.RetryDefaults.MAX_RETRY
            );
        }
        log.info("创建推送任务成功，消息ID: {}, 任务数量: {}", msgId, receiverList.size());
    }
    
    private String getCallbackUrl(String msgId) {
        // TODO: 根据消息ID查询发送方应用的回调地址
        // 可从 ump_msg_main.callback_url 或 ump_app_credential.callback_url 获取
        return "";
    }
    
    private void createCallbackPushTask(String msgId, String broadcastId, String callbackUrl) {
        Map<String, Object> taskData = new HashMap<>();
        taskData.put(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID, msgId);
        taskData.put(MqMessageEventConstants.TaskDataKeys.BROADCAST_ID, broadcastId);
        taskData.put(MqMessageEventConstants.TaskDataKeys.CALLBACK_URL, callbackUrl);

        umpMsgQueueService.createQueueTask(
                MqMessageEventConstants.QueueTaskTypes.PUSH,
                MqMessageEventConstants.QueueNames.MESSAGE_PUSH,
                msgId,
                taskData,
                MqMessageEventConstants.TaskPriorities.LOW,
                LocalDateTime.now(),
                MqMessageEventConstants.RetryDefaults.MAX_RETRY
        );
        log.info("创建回调推送任务成功，消息ID: {}, 广播ID: {}", msgId, broadcastId);
    }
    
    /**
     * 更新任务预估数量
     */
    private void updateTaskEstimate(UmpMsgQueue queueTask, int estimatedCount) {
        Map<String, Object> taskData = queueTask.getTaskData();
        if (taskData == null) {
            taskData = new HashMap<>();
        }
        taskData.put(MqMessageEventConstants.TaskDataKeys.ESTIMATED_COUNT, estimatedCount);
        // 若UmpMsgQueueService未提供更新任务数据方法，此处仅记录日志
        log.debug("更新任务预估数量，任务ID: {}, 预估数量: {}", queueTask.getId(), estimatedCount);
    }

    // ==================== 辅助方法 ====================

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
        receiver.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_ID, userId);
        receiver.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE, MqMessageEventConstants.ReceiverTypes.USER);
        receiver.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_NAME, StringUtils.hasText(username) ? username : userId);
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
            if (node.containsKey("children") && node.get("children") instanceof List) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
                extractDeptIdsRecursive(children, deptIds);
            }
        }
    }

    private String determineBroadcastType(String receiverType, int receiverCount) {
        if (MqMessageEventConstants.ReceiverTypes.ALL.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.ALL;
        } else if (MqMessageEventConstants.ReceiverTypes.AREA.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.AREA;
        } else if (MqMessageEventConstants.ReceiverTypes.ORG.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.ORG;
        } else if (MqMessageEventConstants.ReceiverTypes.DEPT.equals(receiverType)) {
            return receiverCount > MqMessageEventConstants.Thresholds.DEPT_MASS_THRESHOLD ?
                    MqMessageEventConstants.BroadcastTypes.MASS_DEPT :
                    MqMessageEventConstants.BroadcastTypes.DEPT;
        } else {
            return MqMessageEventConstants.BroadcastTypes.CUSTOM;
        }
    }

    private String generateTargetDescription(String receiverType, Map<String, Object> receiverScope) {
        if (receiverScope == null) {
            return "自定义接收者";
        }
        switch (receiverType) {
            case MqMessageEventConstants.ReceiverTypes.USER:
                if (receiverScope.containsKey("userIds") && receiverScope.get("userIds") instanceof List) {
                    List<String> userIds = (List<String>) receiverScope.get("userIds");
                    return "指定用户: " + userIds.size() + "人";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.DEPT:
                if (receiverScope.containsKey("deptIds") && receiverScope.get("deptIds") instanceof List) {
                    List<String> deptIds = (List<String>) receiverScope.get("deptIds");
                    return "指定部门: " + deptIds.size() + "个";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.ORG:
                if (receiverScope.containsKey("orgIds") && receiverScope.get("orgIds") instanceof List) {
                    List<String> orgIds = (List<String>) receiverScope.get("orgIds");
                    return "指定组织: " + orgIds.size() + "个";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.AREA:
                if (receiverScope.containsKey("areaCodes") && receiverScope.get("areaCodes") instanceof List) {
                    List<String> areaCodes = (List<String>) receiverScope.get("areaCodes");
                    return "指定区域: " + areaCodes.size() + "个";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.ALL:
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