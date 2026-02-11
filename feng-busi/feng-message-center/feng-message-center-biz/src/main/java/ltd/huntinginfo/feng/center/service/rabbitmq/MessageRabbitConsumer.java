package ltd.huntinginfo.feng.center.service.rabbitmq;

import ltd.huntinginfo.feng.common.rabbitmq.constant.RabbitMessageEvent;
import ltd.huntinginfo.feng.common.rabbitmq.dto.RabbitMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RabbitMQ消息消费者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRabbitConsumer {
    
    /**
     * 监听消息创建事件
     */
    @RabbitListener(queues = RabbitMessageEvent.QUEUE_MESSAGE_CREATED)
    public void handleMessageCreated(RabbitMessage<Map<String, Object>> rabbitMessage) {
        try {
            Map<String, Object> messageData = rabbitMessage.getPayload();
            String messageId = (String) messageData.get("messageId");
            String msgCode = (String) messageData.get("msgCode");
            
            log.info("接收到消息创建事件，消息ID: {}, 消息编码: {}, 重试次数: {}", 
                    messageId, msgCode, rabbitMessage.getRetryCount());
            
            // 处理消息创建后的业务逻辑
            processMessageCreated(messageData);
            
        } catch (Exception e) {
            log.error("处理消息创建事件失败", e);
            // 这里可以实现重试逻辑或死信队列处理
            throw e; // 抛出异常让RabbitMQ重试
        }
    }
    
    /**
     * 监听消息状态更新事件
     */
    @RabbitListener(queues = RabbitMessageEvent.QUEUE_MESSAGE_STATUS)
    public void handleMessageStatusUpdated(RabbitMessage<Map<String, Object>> rabbitMessage) {
        try {
            Map<String, Object> statusData = rabbitMessage.getPayload();
            String messageId = (String) statusData.get("messageId");
            String oldStatus = (String) statusData.get("oldStatus");
            String newStatus = (String) statusData.get("newStatus");
            
            log.info("接收到消息状态更新事件，消息ID: {}, 状态: {} -> {}, 重试次数: {}", 
                    messageId, oldStatus, newStatus, rabbitMessage.getRetryCount());
            
            // 处理消息状态更新后的业务逻辑
            processMessageStatusUpdated(statusData);
            
        } catch (Exception e) {
            log.error("处理消息状态更新事件失败", e);
            throw e; // 抛出异常让RabbitMQ重试
        }
    }
    
    /**
     * 监听消息分发事件
     */
    @RabbitListener(queues = RabbitMessageEvent.QUEUE_MESSAGE_DISTRIBUTE)
    public void handleMessageDistributed(RabbitMessage<Map<String, Object>> rabbitMessage) {
        try {
            Map<String, Object> distributeData = rabbitMessage.getPayload();
            String messageId = (String) distributeData.get("messageId");
            Integer receiverCount = (Integer) distributeData.get("receiverCount");
            
            log.info("接收到消息分发事件，消息ID: {}, 接收者数量: {}, 重试次数: {}", 
                    messageId, receiverCount, rabbitMessage.getRetryCount());
            
            // 处理消息分发后的业务逻辑
            processMessageDistributed(distributeData);
            
        } catch (Exception e) {
            log.error("处理消息分发事件失败", e);
            throw e; // 抛出异常让RabbitMQ重试
        }
    }
    
    /**
     * 监听消息过期事件
     */
    @RabbitListener(queues = RabbitMessageEvent.QUEUE_MESSAGE_EXPIRE)
    public void handleMessageExpired(RabbitMessage<Map<String, Object>> rabbitMessage) {
        try {
            log.info("接收到消息过期事件，重试次数: {}", rabbitMessage.getRetryCount());
            
            // 处理消息过期后的业务逻辑
            processMessageExpired(rabbitMessage.getPayload());
            
        } catch (Exception e) {
            log.error("处理消息过期事件失败", e);
            // 过期消息不重试，直接记录日志
            log.error("消息过期事件处理失败，数据: {}", rabbitMessage.getPayload(), e);
        }
    }
    
    // ============ 私有方法 ============
    
    private void processMessageCreated(Map<String, Object> messageData) {
        // 这里实现消息创建后的业务逻辑
        // 例如：消息验证、消息路由、消息分发等
        
        String messageId = (String) messageData.get("messageId");
        String pushMode = (String) messageData.get("pushMode");
        
        log.debug("处理消息创建业务逻辑，消息ID: {}, 推送模式: {}", messageId, pushMode);
        
        // 根据推送模式处理
        if ("PUSH".equals(pushMode)) {
            // 主动推送模式
            handlePushModeMessage(messageData);
        } else if ("POLL".equals(pushMode)) {
            // 轮询模式
            handlePollModeMessage(messageData);
        }
    }
    
    private void processMessageStatusUpdated(Map<String, Object> statusData) {
        // 这里实现消息状态更新后的业务逻辑
        // 例如：状态同步、通知发送者、触发下一步流程等
        
        String messageId = (String) statusData.get("messageId");
        String newStatus = (String) statusData.get("newStatus");
        
        log.debug("处理消息状态更新业务逻辑，消息ID: {}, 新状态: {}", messageId, newStatus);
        
        switch (newStatus) {
            case "SENT":
                handleMessageSent(messageId, statusData);
                break;
            case "DISTRIBUTED":
                handleMessageDistributed(messageId, statusData);
                break;
            case "READ":
                handleMessageRead(messageId, statusData);
                break;
            case "FAILED":
                handleMessageFailed(messageId, statusData);
                break;
        }
    }
    
    private void processMessageDistributed(Map<String, Object> distributeData) {
        // 这里实现消息分发后的业务逻辑
        // 例如：生成收件箱记录、通知接收者等
        
        String messageId = (String) distributeData.get("messageId");
        Integer receiverCount = (Integer) distributeData.get("receiverCount");
        String receiverType = (String) distributeData.get("receiverType");
        
        log.debug("处理消息分发业务逻辑，消息ID: {}, 接收者类型: {}, 数量: {}", 
                messageId, receiverType, receiverCount);
        
        // 根据接收者类型处理分发
        if ("USER".equals(receiverType)) {
            handleUserMessageDistribution(messageId, distributeData);
        } else if ("DEPT".equals(receiverType)) {
            handleDeptMessageDistribution(messageId, distributeData);
        } else if ("ALL".equals(receiverType)) {
            handleAllMessageDistribution(messageId, distributeData);
        }
    }
    
    private void processMessageExpired(Object payload) {
        // 这里实现消息过期后的业务逻辑
        // 例如：清理相关数据、记录日志、通知管理员等
        
        log.debug("处理消息过期业务逻辑");
        
        // 如果是List类型，说明是批量过期
        if (payload instanceof java.util.List) {
            java.util.List<?> expiredList = (java.util.List<?>) payload;
            log.info("批量处理过期消息，数量: {}", expiredList.size());
        } else {
            log.info("处理单个消息过期");
        }
    }
    
    private void handlePushModeMessage(Map<String, Object> messageData) {
        // 实现主动推送模式的业务逻辑
        String messageId = (String) messageData.get("messageId");
        log.debug("处理主动推送消息，消息ID: {}", messageId);
        
        // TODO: 实现具体的推送逻辑
    }
    
    private void handlePollModeMessage(Map<String, Object> messageData) {
        // 实现轮询模式的业务逻辑
        String messageId = (String) messageData.get("messageId");
        log.debug("处理轮询模式消息，消息ID: {}", messageId);
        
        // TODO: 实现具体的轮询逻辑
    }
    
    private void handleMessageSent(String messageId, Map<String, Object> statusData) {
        log.debug("处理消息已发送状态，消息ID: {}", messageId);
        // TODO: 实现消息已发送后的处理逻辑
    }
    
    private void handleMessageDistributed(String messageId, Map<String, Object> statusData) {
        log.debug("处理消息已分发状态，消息ID: {}", messageId);
        // TODO: 实现消息已分发后的处理逻辑
    }
    
    private void handleMessageRead(String messageId, Map<String, Object> statusData) {
        log.debug("处理消息已读状态，消息ID: {}", messageId);
        // TODO: 实现消息已读后的处理逻辑
    }
    
    private void handleMessageFailed(String messageId, Map<String, Object> statusData) {
        log.debug("处理消息失败状态，消息ID: {}", messageId);
        // TODO: 实现消息失败后的处理逻辑
    }
    
    private void handleUserMessageDistribution(String messageId, Map<String, Object> distributeData) {
        log.debug("处理个人消息分发，消息ID: {}", messageId);
        // TODO: 实现个人消息分发的具体逻辑
    }
    
    private void handleDeptMessageDistribution(String messageId, Map<String, Object> distributeData) {
        log.debug("处理部门消息分发，消息ID: {}", messageId);
        // TODO: 实现部门消息分发的具体逻辑
    }
    
    private void handleAllMessageDistribution(String messageId, Map<String, Object> distributeData) {
        log.debug("处理全体消息分发，消息ID: {}", messageId);
        // TODO: 实现全体消息分发的具体逻辑
    }
}