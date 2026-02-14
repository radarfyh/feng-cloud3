package ltd.huntinginfo.feng.center.service.consumer;

import ltd.huntinginfo.feng.center.service.processor.MessageDistributionProcessor;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.consumer.MqMessageConsumer;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RabbitMQ 消息消费者
 * <p>
 * 职责：监听各业务队列，将消息转换为业务对象，调用对应的业务处理器。
 * 不包含任何业务逻辑，所有业务处理委托给 {@link MessageDistributionProcessor}。
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@RequiredArgsConstructor
public class MessageRabbitConsumer implements MqMessageConsumer {

    private final MessageDistributionProcessor messageDistributionProcessor;

    // ==================== 消息状态事件监听（与 ump_msg_main.status 一一对应） ====================

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_RECEIVED)
    public void handleMessageDistributeStart(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息已接收事件，消息ID: {}, 重试次数: {}", messageId, message.getRetryCount());
        messageDistributionProcessor.handleMessageReceived(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTING)
    public void handleMessageDistributing(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息分发中事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistributing(messageId, message.getPayload());
    }

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED)
    public void handleMessageDistributed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息已分发事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistributed(messageId, message.getPayload());
    }

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DIST_FAILED)
    public void handleMessageDistributeFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息分发失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistFailed(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PUSHED)
    public void handleMessagePushed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息已推送事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePushed(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PUSH_FAILED)
    public void handleMessagePushFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息推送失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePushFailed(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_BIZ_RECEIVED)
    public void handleMessageBusinessReceived(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息业务已接收事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageBizReceived(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PULL)
    public void handleMessagePullReady(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息待拉取事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePoll(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_BIZ_PULLED)
    public void handleMessageBusinessPulled(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息业务已拉取事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageBizPolled(messageId, message.getPayload());
    }
    
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PULL_FAILED)
    public void handleMessagePullFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息拉取失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePollFailed(messageId, message.getPayload());
    }

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_READ)
    public void handleMessageRead(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息已读事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageRead(messageId, message.getPayload());
    }

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_EXPIRED)
    public void handleMessageExpired(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("RabbitMQ 接收到消息已过期事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageExpired(messageId, message.getPayload());
    }

    // ==================== 异步任务监听 ====================

//    /**
//     * 监听分发任务（DISTRIBUTE）
//     */
//    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTE_TASK)
//    public void handleDistributeTask(MqMessage<Map<String, Object>> message) {
//        log.info("RabbitMQ 接收到分发任务");
//        messageDistributionProcessor.processDistributeTask(message.getPayload());
//    }
//
//    /**
//     * 监听推送任务（PUSH）
//     */
//    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PUSH_TASK)
//    public void handlePushTask(MqMessage<Map<String, Object>> message) {
//        log.info("RabbitMQ 接收到推送任务");
//        messageDistributionProcessor.processPushTask(message.getPayload());
//    }
//
//    /**
//     * 监听重试任务（RETRY）
//     */
//    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK)
//    public void handleRetryTask(MqMessage<Map<String, Object>> message) {
//        log.info("RabbitMQ 接收到重试任务");
//        messageDistributionProcessor.processRetryTask(message.getPayload());
//    }

    // ==================== 延迟队列监听 ====================
//    @Override
//    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DELAYED_SEND)
//    public void handleDelayedSendTask(MqMessage<Map<String, Object>> MqMessage) {
//        log.info("RabbitMQ 接收到延迟发送任务，重试次数: {}", MqMessage.getRetryCount());
//        messageDistributionProcessor.processDelayedSend(MqMessage.getPayload());
//    }
//
//    @Override
//    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DELAYED_EXPIRE)
//    public void handleDelayedExpireTask(MqMessage<Map<String, Object>> MqMessage) {
//        log.info("RabbitMQ 接收到延迟过期处理任务，重试次数: {}", MqMessage.getRetryCount());
//        messageDistributionProcessor.processDelayedExpire(MqMessage.getPayload());
//    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从 MqMessage 中提取消息ID
     * 优先从 payload 中获取 messageId，若不存在则使用 MqMessage 自身的 messageId
     */
    private String extractMessageId(MqMessage<Map<String, Object>> MqMessage) {
        Map<String, Object> payload = MqMessage.getPayload();
        if (payload != null && payload.containsKey("messageId")) {
            return (String) payload.get("messageId");
        }
        return MqMessage.getMessageId();
    }
}