package ltd.huntinginfo.feng.common.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 消息生产者实现
 * <p>
 * 完全依赖 Spring Boot 自动配置的 KafkaTemplate。
 * - exchange 参数被忽略
 * - routingKey 直接作为 Topic（使用 Queues 常量）
 * - 延迟消息立即发送，记录警告
 * </p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaProducer implements MqMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // -------------------------------------------------------------------------
    // 核心发送方法
    // -------------------------------------------------------------------------

    @Override
    public <T> void send(String exchange, String routingKey, MqMessage<T> message) {
        // 确保消息ID、发送时间、重试次数不为空
        if (message.getMessageId() == null) {
            message.setMessageId(java.util.UUID.randomUUID().toString());
        }
        if (message.getSendTime() == null) {
            message.setSendTime(java.time.LocalDateTime.now());
        }
        if (message.getRetryCount() == null) {
            message.setRetryCount(0);
        }

        String topic = routingKey;
        String key = message.getMessageId();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Kafka 消息发送成功，topic: {}, partition: {}, offset: {}, messageId: {}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), message.getMessageId());
            } else {
                log.error("Kafka 消息发送失败，topic: {}, messageId: {}", topic, message.getMessageId(), ex);
            }
        });
    }

    @Override
    public <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis) {
        log.warn("Kafka 不支持原生延迟消息，delayMillis={} 参数被忽略，立即发送。topic={}, messageId={}",
                delayMillis, routingKey, message.getMessageId());
        send(exchange, routingKey, message);
    }

    // -------------------------------------------------------------------------
    // 消息状态事件（与数据库状态一一对应）
    // -------------------------------------------------------------------------

    @Override
    public <T> void sendMessageReceived(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.RECEIVED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_RECEIVED,
                message);
    }

    @Override
    public <T> void sendMessageDistributed(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.DISTRIBUTED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED,
                message);
    }

    @Override
    public <T> void sendMessageSent(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.SENT,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_SENT,
                message);
    }

    @Override
    public <T> void sendMessageRead(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.READ,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_READ,
                message);
    }

    @Override
    public <T> void sendMessageExpired(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.EXPIRED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_EXPIRED,
                message);
    }

    @Override
    public <T> void sendMessageFailed(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.FAILED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_FAILED,
                message);
    }

    // -------------------------------------------------------------------------
    // 异步任务
    // -------------------------------------------------------------------------

    @Override
    public <T> void sendSendTask(T payload) {
        MqMessage<T> message = MqMessage.create("SEND_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_SEND_TASK,
                message);
    }

    @Override
    public <T> void sendCallbackTask(T payload) {
        MqMessage<T> message = MqMessage.create("CALLBACK_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_CALLBACK_TASK,
                message);
    }

    @Override
    public <T> void sendRetryTask(T payload) {
        MqMessage<T> message = MqMessage.create("RETRY_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK,
                message);
    }

    @Override
    public <T> void sendBroadcastDispatchTask(T payload) {
        MqMessage<T> message = MqMessage.create("BROADCAST_DISPATCH_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.BROADCAST_DISPATCH_TASK,
                message);
    }

    // -------------------------------------------------------------------------
    // 延迟任务
    // -------------------------------------------------------------------------

    @Override
    public <T> void sendDelayedSendTask(T payload, long delayMillis) {
        log.warn("Kafka 不支持原生延迟消息，sendDelayedSendTask 将立即发送。delayMillis={}", delayMillis);
        sendSendTask(payload);
    }

    @Override
    public <T> void sendDelayedExpireTask(T payload, long delayMillis) {
        log.warn("Kafka 不支持原生延迟消息，sendDelayedExpireTask 将立即发送。delayMillis={}", delayMillis);
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.EXPIRED,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.Queues.DELAYED_EXPIRE,
                message);
    }
}