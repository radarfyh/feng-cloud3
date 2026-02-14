package ltd.huntinginfo.feng.common.core.mq.producer;

import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;

/**
 * 消息队列生产者抽象接口
 * <p>
 * 统一消息平台可基于此接口实现 RabbitMQ、Kafka 等多种实现，
 * 业务模块通过 {@code @ConditionalOnProperty} 注入具体实现，实现 MQ 切换。
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
public interface MqMessageProducer {

    /**
     * 发送普通消息
     *
     * @param exchange   交换机（RabbitMQ）/ Topic（Kafka）
     * @param routingKey 路由键（RabbitMQ）/ 分区键（Kafka）
     * @param message    消息体（已包含业务泛型）
     * @param <T>        业务数据类型
     */
    <T> void send(String exchange, String routingKey, MqMessage<T> message);

    /**
     * 发送延迟消息
     *
     * @param exchange    交换机（RabbitMQ）/ Topic（Kafka）
     * @param routingKey  路由键（RabbitMQ）/ 分区键（Kafka）
     * @param message     消息体
     * @param delayMillis 延迟毫秒数（RabbitMQ 需安装 x-delay 插件）
     * @param <T>         业务数据类型
     */
    <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis);

    /**
     * 发送【消息已接收】事件（对应状态：RECEIVED）
     */
    default <T> void sendMessageDistributeStart(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.RECEIVED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RECEIVED,
                message);
    }

    /**
     * 发送【消息分发中】事件（对应状态：DISTRIBUTING）
     */
    default <T> void sendMessageDistributing(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.DISTRIBUTING,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTING,
                message);
    }

    /**
     * 发送【消息已分发】事件（对应状态：DISTRIBUTED）
     */
    default <T> void sendMessageDistributed(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.DISTRIBUTED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTED,
                message);
    }
    
    /**
     * 发送【消息分发失败】事件（对应状态：DIST_FAILED）
     */
    default <T> void sendMessageDistributeFailed(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.DIST_FAILED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DIST_FAILED,
                message);
    }
    
    /**
     * 发送【消息已推送】事件（对应状态：PUSHED）
     */
    default <T> void sendMessagePushed(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.PUSHED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSHED,
                message);
    }
    
    /**
     * 发送【消息推送失败】事件（对应状态：PUSH_FAILED）
     */
    default <T> void sendMessagePushFailed(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.PUSH_FAILED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSH_FAILED,
                message);
    }

    /**
     * 发送【消息业务已接收】事件（对应状态：BIZ_RECEIVED）
     */
    default <T> void sendMessageBusinessReceived(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.BIZ_RECEIVED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_RECEIVED,
                message);
    }
    
    /**
     * 发送【消息业务待拉取】事件（对应状态：PULL）
     */
    default <T> void sendMessagePullReady(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.PULL,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL,
                message);
    }
    
    /**
     * 发送【消息业务已拉取】事件（对应状态：BIZ_PULLED）
     */
    default <T> void sendMessageBusinessPulled(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.BIZ_PULLED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_PULLED,
                message);
    }
    
    /**
     * 发送【消息业务拉取失败】事件（对应状态：PULL_FAILED）
     */
    default <T> void sendMessagePullFailed(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.PULL_FAILED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL_FAILED,
                message);
    }

    /**
     * 发送【消息已读】事件（对应状态：READ）
     */
    default <T> void sendMessageRead(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.READ,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_READ,
                message);
    }

    /**
     * 发送【消息已过期】事件（对应状态：EXPIRED）
     */
    default <T> void sendMessageExpired(T payload, String eventType) {
        MqMessage<T> message = MqMessage.create(
                MqMessageEventConstants.EventTypes.EXPIRED,
                eventType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED,
                message);
    }

    // ---------- 异步任务发送方法 ----------
    
//    /**
//     * 异步发送“发送任务”
//     */
//    default <T> void sendPushTask(T payload) {
//        MqMessage<T> message = MqMessage.create("SEND_TASK", payload);
//        send(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.TASK_SEND,
//                message);
//    }
//
//    /**
//     * 异步发送“回调任务”
//     */
//    default <T> void sendCallbackTask(T payload) {
//        MqMessage<T> message = MqMessage.create("CALLBACK_TASK", payload);
//        send(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.TASK_CALLBACK,
//                message);
//    }
//
//    /**
//     * 异步发送“重试任务”
//     */
//    default <T> void sendRetryTask(T payload) {
//        MqMessage<T> message = MqMessage.create("RETRY_TASK", payload);
//        send(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.TASK_RETRY,
//                message);
//    }
//
//    /**
//     * 异步发送“广播分发任务”
//     */
//    default <T> void sendBroadcastDispatchTask(T payload) {
//        MqMessage<T> message = MqMessage.create("BROADCAST_DISPATCH_TASK", payload);
//        send(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.TASK_BROADCAST_DISPATCH,
//                message);
//    }
//
//    // ---------- 延迟消息业务方法 ----------
//    /**
//     * 发送延迟的【消息发送】任务
//     */
//    default <T> void sendDelayedSendTask(T payload, long delayMillis) {
//        MqMessage<T> message = MqMessage.create("DELAYED_SEND_TASK", payload);
//        sendDelayed(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.DELAYED_SEND,
//                message,
//                delayMillis);
//    }
//
//    /**
//     * 发送延迟的【消息过期】处理任务
//     */
//    default <T> void sendDelayedExpireTask(T payload, long delayMillis) {
//        MqMessage<T> message = MqMessage.create("DELAYED_EXPIRE_TASK", payload);
//        sendDelayed(MqMessageEventConstants.Exchanges.MESSAGE,
//                MqMessageEventConstants.RoutingKeys.DELAYED_EXPIRE,
//                message,
//                delayMillis);
//    }

}