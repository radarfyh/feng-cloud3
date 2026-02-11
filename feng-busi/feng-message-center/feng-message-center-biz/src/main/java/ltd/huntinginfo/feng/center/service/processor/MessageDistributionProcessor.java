package ltd.huntinginfo.feng.center.service.processor;

import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.user3.service.UserService;
import ltd.huntinginfo.feng.user3.service.DepartmentService;
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
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
 * 负责处理RabbitMQ中的消息事件，将消息分发到收件箱或广播信息筒
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDistributionProcessor {
    
    // 分发策略阈值：接收者数量超过此阈值使用广播模式
    private static final int BROADCAST_THRESHOLD = 1000;
    
    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpAppCredentialService umpAppCredentialService;
    private final UserService userService;
    private final DepartmentService departmentService;
    
    /**
     * 处理消息创建事件
     */
    @Transactional(rollbackFor = Exception.class)
    public void processMessageCreated(Map<String, Object> messageData) {
        String messageId = (String) messageData.get("messageId");
        if (!StringUtils.hasText(messageId)) {
            log.error("消息ID为空，无法处理消息创建事件");
            return;
        }
        
        try {
            // 1. 获取消息详情
            UmpMsgMain message = umpMsgMainService.getById(messageId);
            if (message == null) {
                log.error("消息不存在，消息ID: {}", messageId);
                return;
            }
            
            // 2. 验证消息状态
            if (!"RECEIVED".equals(message.getStatus())) {
                log.warn("消息状态不是RECEIVED，跳过分发，消息ID: {}, 状态: {}", messageId, message.getStatus());
                return;
            }
            
            // 3. 获取接收者列表
            List<Map<String, Object>> receiverList = resolveReceivers(message);
            if (CollectionUtils.isEmpty(receiverList)) {
                log.warn("消息没有接收者，消息ID: {}, 标题: {}", messageId, message.getTitle());
                // 标记为已分发（没有接收者）
                umpMsgMainService.updateMessageStatus(messageId, "DISTRIBUTED");
                return;
            }
            
            // 4. 根据接收者数量选择分发策略
            if (receiverList.size() <= BROADCAST_THRESHOLD) {
                // 写扩散：使用收件箱表
                distributeToInbox(message, receiverList);
            } else {
                // 读扩散：使用广播信息筒
                distributeToBroadcast(message, receiverList);
            }
            
            // 5. 更新消息状态为已分发
            boolean success = umpMsgMainService.updateMessageStatus(messageId, "DISTRIBUTED");
            if (success) {
                log.info("消息分发完成，消息ID: {}, 接收者数量: {}, 分发模式: {}", 
                        messageId, receiverList.size(), 
                        receiverList.size() <= BROADCAST_THRESHOLD ? "INBOX" : "BROADCAST");
            }
            
        } catch (Exception e) {
            log.error("处理消息创建事件失败，消息ID: {}", messageId, e);
            // 更新消息状态为失败
            umpMsgMainService.updateMessageStatus(messageId, "FAILED");
            throw e;
        }
    }
    
    /**
     * 解析接收者列表
     */
    private List<Map<String, Object>> resolveReceivers(UmpMsgMain message) {
        String receiverType = message.getReceiverType();
        Map<String, Object> receiverScope = message.getReceiverScope();
        List<Map<String, Object>> receiverList = new ArrayList<>();
        
        try {
            switch (receiverType) {
                case "USER":
                    // 个人接收者
                    receiverList = resolveUserReceivers(receiverScope);
                    break;
                case "DEPT":
                    // 部门接收者
                    receiverList = resolveDepartmentReceivers(receiverScope);
                    break;
                case "ORG":
                    // 组织接收者
                    receiverList = resolveOrganizationReceivers(receiverScope);
                    break;
                case "AREA":
                    // 区域接收者
                    receiverList = resolveAreaReceivers(receiverScope);
                    break;
                case "ALL":
                    // 全体接收者
                    receiverList = resolveAllReceivers(receiverScope);
                    break;
                default:
                    log.warn("未知的接收者类型: {}", receiverType);
                    break;
            }
        } catch (Exception e) {
            log.error("解析接收者失败，消息ID: {}, 接收者类型: {}", message.getId(), receiverType, e);
        }
        
        return receiverList;
    }
    
    /**
     * 解析个人接收者
     */
    private List<Map<String, Object>> resolveUserReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope != null) {
            // 方式1：直接指定用户ID列表
            if (receiverScope.containsKey("userIds") && receiverScope.get("userIds") instanceof List) {
                List<String> userIds = (List<String>) receiverScope.get("userIds");
                for (String userId : userIds) {
                    Map<String, Object> receiver = new HashMap<>();
                    receiver.put("receiverId", userId);
                    receiver.put("receiverType", "USER");
                    receiver.put("receiverName", getUsernameById(userId));
                    receivers.add(receiver);
                }
            }
            
            // 方式2：通过角色获取用户
            if (receiverScope.containsKey("roleIds") && receiverScope.get("roleIds") instanceof List) {
                List<String> roleIds = (List<String>) receiverScope.get("roleIds");
                List<String> userIds = getUserIdsByRoles(roleIds);
                for (String userId : userIds) {
                    Map<String, Object> receiver = new HashMap<>();
                    receiver.put("receiverId", userId);
                    receiver.put("receiverType", "USER");
                    receiver.put("receiverName", getUsernameById(userId));
                    receivers.add(receiver);
                }
            }
        }
        
        return receivers;
    }
    
    /**
     * 解析部门接收者
     */
    private List<Map<String, Object>> resolveDepartmentReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope != null) {
            // 方式1：直接指定部门
            if (receiverScope.containsKey("deptIds") && receiverScope.get("deptIds") instanceof List) {
                List<String> deptIds = (List<String>) receiverScope.get("deptIds");
                for (String deptId : deptIds) {
                    // 获取部门下所有用户
                    List<String> userIds = getUserIdsByDepartment(deptId);
                    for (String userId : userIds) {
                        Map<String, Object> receiver = new HashMap<>();
                        receiver.put("receiverId", userId);
                        receiver.put("receiverType", "USER");
                        receiver.put("receiverName", getUsernameById(userId));
                        receivers.add(receiver);
                    }
                }
            }
            
            // 方式2：指定部门下的角色
            if (receiverScope.containsKey("deptRoles") && receiverScope.get("deptRoles") instanceof Map) {
                Map<String, List<String>> deptRoles = (Map<String, List<String>>) receiverScope.get("deptRoles");
                for (Map.Entry<String, List<String>> entry : deptRoles.entrySet()) {
                    String deptId = entry.getKey();
                    List<String> roleIds = entry.getValue();
                    List<String> userIds = getUserIdsByDeptAndRoles(deptId, roleIds);
                    for (String userId : userIds) {
                        Map<String, Object> receiver = new HashMap<>();
                        receiver.put("receiverId", userId);
                        receiver.put("receiverType", "USER");
                        receiver.put("receiverName", getUsernameById(userId));
                        receivers.add(receiver);
                    }
                }
            }
        }
        
        return receivers;
    }
    
    /**
     * 解析组织接收者
     */
    private List<Map<String, Object>> resolveOrganizationReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope != null) {
            // 方式1：直接指定组织ID
            if (receiverScope.containsKey("orgId")) {
                String orgId = (String) receiverScope.get("orgId");
                // 获取组织下所有用户（包括子部门）
                List<String> userIds = getUserIdsByOrganization(orgId);
                for (String userId : userIds) {
                    Map<String, Object> receiver = new HashMap<>();
                    receiver.put("receiverId", userId);
                    receiver.put("receiverType", "USER");
                    receiver.put("receiverName", getUsernameById(userId));
                    receivers.add(receiver);
                }
            }
        }
        
        return receivers;
    }
    
    /**
     * 解析区域接收者
     */
    private List<Map<String, Object>> resolveAreaReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        if (receiverScope != null) {
            // 方式1：指定行政区划代码
            if (receiverScope.containsKey("areaCodes") && receiverScope.get("areaCodes") instanceof List) {
                List<String> areaCodes = (List<String>) receiverScope.get("areaCodes");
                for (String areaCode : areaCodes) {
                    // 根据行政区划获取用户（消防系统特殊逻辑）
                    List<String> userIds = getUserIdsByAreaCode(areaCode);
                    for (String userId : userIds) {
                        Map<String, Object> receiver = new HashMap<>();
                        receiver.put("receiverId", userId);
                        receiver.put("receiverType", "USER");
                        receiver.put("receiverName", getUsernameById(userId));
                        receivers.add(receiver);
                    }
                }
            }
        }
        
        return receivers;
    }
    
    /**
     * 解析全体接收者
     */
    private List<Map<String, Object>> resolveAllReceivers(Map<String, Object> receiverScope) {
        List<Map<String, Object>> receivers = new ArrayList<>();
        
        // 获取所有有效用户
        // 注意：实际生产环境需要分页查询，避免内存溢出
        List<String> userIds = getAllActiveUserIds();
        for (String userId : userIds) {
            Map<String, Object> receiver = new HashMap<>();
            receiver.put("receiverId", userId);
            receiver.put("receiverType", "USER");
            receiver.put("receiverName", getUsernameById(userId));
            receivers.add(receiver);
        }
        
        return receivers;
    }
    
    /**
     * 分发到收件箱（写扩散）
     */
    private void distributeToInbox(UmpMsgMain message, List<Map<String, Object>> receiverList) {
        String msgId = message.getId();
        String pushMode = message.getPushMode();
        
        // 批量创建收件箱记录
        int createdCount = umpMsgInboxService.batchCreateInboxRecords(msgId, receiverList, "INBOX");
        
        log.info("消息分发到收件箱成功，消息ID: {}, 接收者数量: {}, 成功创建: {}", 
                msgId, receiverList.size(), createdCount);
        
        // 如果是主动推送模式，标记为待推送
        if ("PUSH".equals(pushMode)) {
            // 这里可以触发推送任务
            triggerPushTask(msgId);
        }
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
                broadcastId, 0, 0, 0); // 初始都为0
        
        log.info("消息分发到广播信息筒成功，消息ID: {}, 广播ID: {}, 预估接收者: {}", 
                msgId, broadcastId, receiverList.size());
        
        // 3. 触发广播分发任务（异步处理）
        triggerBroadcastDistributeTask(broadcastId);
    }
    
    /**
     * 根据接收者类型和数量确定广播类型
     */
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
    
    /**
     * 生成目标描述
     */
    private String generateTargetDescription(String receiverType, Map<String, Object> receiverScope) {
        switch (receiverType) {
            case "USER":
                if (receiverScope != null && receiverScope.containsKey("userIds")) {
                    List<String> userIds = (List<String>) receiverScope.get("userIds");
                    return "指定用户: " + userIds.size() + "人";
                }
                break;
            case "DEPT":
                if (receiverScope != null && receiverScope.containsKey("deptIds")) {
                    List<String> deptIds = (List<String>) receiverScope.get("deptIds");
                    return "指定部门: " + deptIds.size() + "个";
                }
                break;
            case "ALL":
                return "全体用户";
            case "AREA":
                if (receiverScope != null && receiverScope.containsKey("areaCodes")) {
                    List<String> areaCodes = (List<String>) receiverScope.get("areaCodes");
                    return "指定区域: " + areaCodes.size() + "个";
                }
                break;
        }
        return "自定义接收者";
    }
    
    // ============ 辅助方法（需要根据实际业务实现） ============
    
    private String getUsernameById(String userId) {
        // TODO: 调用用户服务获取用户名
        try {
            // return userService.getUsernameById(userId);
            return "用户" + userId;
        } catch (Exception e) {
            log.warn("获取用户名失败，用户ID: {}", userId, e);
            return "未知用户";
        }
    }
    
    private List<String> getUserIdsByRoles(List<String> roleIds) {
        // TODO: 根据角色ID获取用户ID列表
        List<String> userIds = new ArrayList<>();
        try {
            // userIds = userService.getUserIdsByRoleIds(roleIds);
        } catch (Exception e) {
            log.error("根据角色获取用户失败，角色IDs: {}", roleIds, e);
        }
        return userIds;
    }
    
    private List<String> getUserIdsByDepartment(String deptId) {
        // TODO: 根据部门ID获取用户ID列表
        List<String> userIds = new ArrayList<>();
        try {
            // userIds = departmentService.getUserIdsByDeptId(deptId);
        } catch (Exception e) {
            log.error("根据部门获取用户失败，部门ID: {}", deptId, e);
        }
        return userIds;
    }
    
    private List<String> getUserIdsByDeptAndRoles(String deptId, List<String> roleIds) {
        // TODO: 根据部门和角色获取用户ID列表
        List<String> userIds = new ArrayList<>();
        try {
            // userIds = userService.getUserIdsByDeptAndRoles(deptId, roleIds);
        } catch (Exception e) {
            log.error("根据部门和角色获取用户失败，部门ID: {}, 角色IDs: {}", deptId, roleIds, e);
        }
        return userIds;
    }
    
    private List<String> getUserIdsByOrganization(String orgId) {
        // TODO: 根据组织ID获取用户ID列表
        List<String> userIds = new ArrayList<>();
        try {
            // userIds = userService.getUserIdsByOrgId(orgId);
        } catch (Exception e) {
            log.error("根据组织获取用户失败，组织ID: {}", orgId, e);
        }
        return userIds;
    }
    
    private List<String> getUserIdsByAreaCode(String areaCode) {
        // TODO: 根据行政区划代码获取用户ID列表（消防系统特殊逻辑）
        List<String> userIds = new ArrayList<>();
        try {
            // userIds = userService.getUserIdsByAreaCode(areaCode);
        } catch (Exception e) {
            log.error("根据行政区划获取用户失败，区划代码: {}", areaCode, e);
        }
        return userIds;
    }
    
    private List<String> getAllActiveUserIds() {
        // TODO: 获取所有活跃用户ID列表（需要分页）
        List<String> userIds = new ArrayList<>();
        try {
            // 实际应该分页查询，这里简化处理
            // userIds = userService.getAllActiveUserIds();
        } catch (Exception e) {
            log.error("获取所有活跃用户失败", e);
        }
        return userIds;
    }
    
    private void triggerPushTask(String msgId) {
        // TODO: 触发消息推送任务
        log.debug("触发消息推送任务，消息ID: {}", msgId);
        // 可以将推送任务放入队列，由推送服务处理
    }
    
    private void triggerBroadcastDistributeTask(String broadcastId) {
        // TODO: 触发广播分发任务
        log.debug("触发广播分发任务，广播ID: {}", broadcastId);
        // 可以将广播分发任务放入队列，由分发服务处理
    }
}
