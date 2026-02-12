package ltd.huntinginfo.feng.common.rabbitmq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消息发送服务（实现 MqMessageProducer 接口）
 * <p>
 * 核心改进：
 * 1. 不再手动转换 Message，直接调用 rabbitTemplate.convertAndSend(Object)
 * 2. 延迟消息通过 MessagePostProcessor 设置 x-delay 头
 * 3. 移除冗余的消息头设置（消息体已包含全部业务信息）
 * 4. 与重构后的常量体系完全对齐
 * 5. 废弃专用延迟交换机方法，统一使用主交换机 + x-delay
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@RequiredArgsConstructor
public class RabbitMqService implements MqMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T> void send(String exchange, String routingKey, MqMessage<T> message) {
        try {
            // 确保消息ID存在（构造时已生成）
            if (message.getMessageId() == null) {
                message.setMessageId(java.util.UUID.randomUUID().toString());
            }
            // 确保发送时间存在
            if (message.getSendTime() == null) {
                message.setSendTime(java.time.LocalDateTime.now());
            }
            // 确保重试次数不为空
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }

            rabbitTemplate.convertAndSend(exchange, routingKey, message);

            log.debug("RabbitMQ 消息发送成功，exchange: {}, routingKey: {}, messageId: {}, type: {}",
                    exchange, routingKey, message.getMessageId(), message.getMessageType());
        } catch (AmqpException e) {
            log.error("RabbitMQ 消息发送失败，exchange: {}, routingKey: {}, messageId: {}",
                    exchange, routingKey, message.getMessageId(), e);
            throw new RuntimeException("RabbitMQ 消息发送失败", e);
        }
    }

    @Override
    public <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis) {
        try {
            if (message.getMessageId() == null) {
                message.setMessageId(java.util.UUID.randomUUID().toString());
            }
            if (message.getSendTime() == null) {
                message.setSendTime(java.time.LocalDateTime.now());
            }
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }

            // 使用 MessagePostProcessor 设置延迟头
            rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
                msg.getMessageProperties().setHeader("x-delay", delayMillis);
                return msg;
            });

            log.debug("RabbitMQ 延迟消息发送成功，exchange: {}, routingKey: {}, delay: {}ms, messageId: {}",
                    exchange, routingKey, delayMillis, message.getMessageId());
        } catch (AmqpException e) {
            log.error("RabbitMQ 延迟消息发送失败，exchange: {}, routingKey: {}, messageId: {}",
                    exchange, routingKey, message.getMessageId(), e);
            throw new RuntimeException("RabbitMQ 延迟消息发送失败", e);
        }
    }

    // ========== 以下为业务便捷方法，直接使用重构后的常量 ==========

    /**
     * 发送【消息已接收】事件（对应状态：RECEIVED）
     */
    @Override
    public <T> void sendMessageReceived(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.RECEIVED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_RECEIVED,
                message);
    }

    /**
     * 发送【消息已分发】事件（对应状态：DISTRIBUTED）
     */
    @Override
    public <T> void sendMessageDistributed(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.DISTRIBUTED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_DISTRIBUTED,
                message);
    }

    /**
     * 发送【消息已发送】事件（对应状态：SENT）
     */
    @Override
    public <T> void sendMessageSent(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.SENT,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_SENT,
                message);
    }

    /**
     * 发送【消息已读】事件（对应状态：READ）
     */
    @Override
    public <T> void sendMessageRead(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.READ,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_READ,
                message);
    }

    /**
     * 发送【消息已过期】事件（对应状态：EXPIRED）
     */
    @Override
    public <T> void sendMessageExpired(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.EXPIRED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED,
                message);
    }

    /**
     * 发送【消息失败】事件（对应状态：FAILED）
     */
    @Override
    public <T> void sendMessageFailed(T payload, String businessType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.FAILED,
                businessType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.MESSAGE_FAILED,
                message);
    }

    // ---------- 异步任务发送方法 ----------
    
    /**
     * 异步发送“发送任务”
     */
    @Override
    public <T> void sendSendTask(T payload) {
        MqMessage<T> message = MqMessage.create("SEND_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.TASK_SEND,
                message);
    }

    /**
     * 异步发送“回调任务”
     */
    @Override
    public <T> void sendCallbackTask(T payload) {
        MqMessage<T> message = MqMessage.create("CALLBACK_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.TASK_CALLBACK,
                message);
    }

    /**
     * 异步发送“重试任务”
     */
    @Override
    public <T> void sendRetryTask(T payload) {
        MqMessage<T> message = MqMessage.create("RETRY_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.TASK_RETRY,
                message);
    }

    /**
     * 异步发送“广播分发任务”
     */
    @Override
    public <T> void sendBroadcastDispatchTask(T payload) {
        MqMessage<T> message = MqMessage.create("BROADCAST_DISPATCH_TASK", payload);
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.TASK_BROADCAST_DISPATCH,
                message);
    }

    // ---------- 延迟消息业务方法 ----------
    /**
     * 发送延迟的【消息发送】任务
     */
    @Override
    public <T> void sendDelayedSendTask(T payload, long delayMillis) {
        MqMessage<T> message = MqMessage.create("DELAYED_SEND_TASK", payload);
        sendDelayed(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.DELAYED_SEND,
                message,
                delayMillis);
    }

    /**
     * 发送延迟的【消息过期】处理任务
     */
    @Override
    public <T> void sendDelayedExpireTask(T payload, long delayMillis) {
        MqMessage<T> message = MqMessage.create("DELAYED_EXPIRE_TASK", payload);
        sendDelayed(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.DELAYED_EXPIRE,
                message,
                delayMillis);
    }
}