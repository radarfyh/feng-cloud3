package ltd.huntinginfo.feng.common.core.mq.consumer;

import java.util.Map;

import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;

/**
 * 消息消费者统一接口
 * <p>
 * 业务模块需实现该接口，并通过 {@code @Component} 注册为 Bean。
 * RabbitMQ / Kafka 模块通过监听器调用此接口方法，将消息传递给业务层。
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
public interface MqMessageConsumer {

    // ---------- 消息状态事件 ----------
    void handleMessageReceived(MqMessage<Map<String, Object>> message);

    void handleMessageDistributed(MqMessage<Map<String, Object>> message);

    void handleMessageSent(MqMessage<Map<String, Object>> message);

    void handleMessageRead(MqMessage<Map<String, Object>> message);

    void handleMessageExpired(MqMessage<Map<String, Object>> message);

    void handleMessageFailed(MqMessage<Map<String, Object>> message);

    // ---------- 异步任务 ----------
    void handleSendTask(MqMessage<Map<String, Object>> message);

    void handleCallbackTask(MqMessage<Map<String, Object>> message);

    void handleRetryTask(MqMessage<Map<String, Object>> message);

    void handleBroadcastDispatchTask(MqMessage<Map<String, Object>> message);

    // ---------- 延迟任务 ----------
    void handleDelayedSendTask(MqMessage<Map<String, Object>> message);

    void handleDelayedExpireTask(MqMessage<Map<String, Object>> message);
}