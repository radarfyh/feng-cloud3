package ltd.huntinginfo.feng.common.core.mq.producer;

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
	<T> void sendMessageReceived(T payload, String businessType);

	/**
	 * 发送【消息已分发】事件（对应状态：DISTRIBUTED）
	 */
	<T> void sendMessageDistributed(T payload, String businessType);

	/**
	 * 发送【消息已发送】事件（对应状态：SENT）
	 */
	<T> void sendMessageSent(T payload, String businessType);

	/**
	 * 发送【消息已读】事件（对应状态：READ）
	 */
	<T> void sendMessageRead(T payload, String businessType);

	/**
	 * 发送【消息已过期】事件（对应状态：EXPIRED）
	 */
	<T> void sendMessageExpired(T payload, String businessType);

	/**
	 * 发送【消息失败】事件（对应状态：FAILED）
	 */
	<T> void sendMessageFailed(T payload, String businessType);

	/**
	 * 异步发送“发送任务”
	 */
	<T> void sendSendTask(T payload);

	/**
	 * 异步发送“回调任务”
	 */
	<T> void sendCallbackTask(T payload);

	/**
	 * 异步发送“重试任务”
	 */
	<T> void sendRetryTask(T payload);

	/**
	 * 异步发送“广播分发任务”
	 */
	<T> void sendBroadcastDispatchTask(T payload);

	/**
	 * 发送延迟的【消息发送】任务
	 */
	<T> void sendDelayedSendTask(T payload, long delayMillis);

	/**
	 * 发送延迟的【消息过期】处理任务
	 */
	<T> void sendDelayedExpireTask(T payload, long delayMillis);

}