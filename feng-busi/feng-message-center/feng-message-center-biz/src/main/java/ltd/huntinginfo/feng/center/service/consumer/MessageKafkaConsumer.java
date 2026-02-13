package ltd.huntinginfo.feng.center.service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.consumer.MqMessageConsumer;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.center.service.processor.MessageDistributionProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Kafka 消息消费者
 * <p>
 * 与 RabbitMQ 的 {@code MessageRabbitConsumer} 完全对称：
 * - 实现 {@link MqMessageConsumer} 接口
 * - 每个处理方法标注 {@link KafkaListener}，监听对应的 Topic
 * - 业务逻辑委托给 {@link MessageDistributionProcessor}
 * - 通过 {@code @ConditionalOnProperty} 控制仅在 mc.mq.type=kafka 时加载
 * </p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
@RequiredArgsConstructor
public class MessageKafkaConsumer implements MqMessageConsumer {

    private final MessageDistributionProcessor messageDistributionProcessor;

    // ==================== 消息状态事件监听（与 MqMessageEventConstants.Queues 一一对应）====================

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_RECEIVED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageReceived(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息已接收事件，消息ID: {}, 重试次数: {}", messageId, message.getRetryCount());
        messageDistributionProcessor.handleMessageReceived(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTING,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageDistributing(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息分发中事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistributing(messageId, message.getPayload());
    }

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageDistributed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息已分发事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistributed(messageId, message.getPayload());
    }

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_DIST_FAILED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageDistFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息分发失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageDistFailed(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_PUSHED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessagePushed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息已推送事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePushed(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_PUSH_FAILED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessagePushFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息推送失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePushFailed(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_BIZ_RECEIVED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageBizReceived(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息业务已接收事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageBizReceived(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_POLL,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessagePoll(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息待拉取事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePoll(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_BIZ_PULLED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageBizPolled(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息业务已拉取事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageBizPolled(messageId, message.getPayload());
    }
    
    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_POLL_FAILED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessagePollFailed(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息拉取失败事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessagePollFailed(messageId, message.getPayload());
    }

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_READ,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageRead(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息已读事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageRead(messageId, message.getPayload());
    }

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_EXPIRED,
                   groupId = MqMessageEventConstants.ConsumerGroups.MESSAGE_STATE)
    public void handleMessageExpired(MqMessage<Map<String, Object>> message) {
        String messageId = extractMessageId(message);
        log.info("Kafka 接收到消息已过期事件，消息ID: {}", messageId);
        messageDistributionProcessor.handleMessageExpired(messageId, message.getPayload());
    }

    // ==================== 异步任务监听 ====================

    /**
     * 监听分发任务（DISTRIBUTE）
     */
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTE_TASK,
                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
    public void handleDistributeTask(MqMessage<Map<String, Object>> message) {
        log.info("Kafka 接收到分发任务");
        messageDistributionProcessor.processDistributeTask(message.getPayload());
    }

    /**
     * 监听推送任务（PUSH）
     */
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_PUSH_TASK,
                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
    public void handlePushTask(MqMessage<Map<String, Object>> message) {
        log.info("Kafka 接收到推送任务");
        messageDistributionProcessor.processPushTask(message.getPayload());
    }

    /**
     * 监听重试任务（RETRY）
     */
    @KafkaListener(topics = MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK,
                   groupId = MqMessageEventConstants.ConsumerGroups.TASK)
    public void handleRetryTask(MqMessage<Map<String, Object>> message) {
        log.info("Kafka 接收到重试任务");
        messageDistributionProcessor.processRetryTask(message.getPayload());
    }
    
    // ==================== 延迟任务监听（Kafka 无原生延迟，但仍可接收外部投递的消息）====================

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.DELAYED_SEND,
                   groupId = MqMessageEventConstants.ConsumerGroups.DELAYED)
    public void handleDelayedSendTask(MqMessage<Map<String, Object>> message) {
        log.info("Kafka 接收到延迟发送任务");
        messageDistributionProcessor.processDelayedSend(message.getPayload());
    }

    @Override
    @KafkaListener(topics = MqMessageEventConstants.Queues.DELAYED_EXPIRE,
                   groupId = MqMessageEventConstants.ConsumerGroups.DELAYED)
    public void handleDelayedExpireTask(MqMessage<Map<String, Object>> message) {
        log.info("Kafka 接收到延迟过期任务");
        messageDistributionProcessor.processDelayedExpire(message.getPayload());
    }

    // ==================== 私有辅助方法 ====================

    private String extractMessageId(MqMessage<Map<String, Object>> message) {
        Map<String, Object> payload = message.getPayload();
        if (payload != null && payload.containsKey("messageId")) {
            return (String) payload.get("messageId");
        }
        return message.getMessageId();
    }
}