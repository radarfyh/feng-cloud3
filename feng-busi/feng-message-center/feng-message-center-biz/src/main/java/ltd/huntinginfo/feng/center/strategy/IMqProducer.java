package ltd.huntinginfo.feng.center.strategy;

import java.util.Map;

/**
 * 统一消息发送接口 (屏蔽底层 MQ 差异)
 */
public interface IMqProducer {
    /**
     * 发送消息
     * @param topic   主题 (RabbitMQ中对应 Exchange 或 RoutingKey)
     * @param payload 消息体
     */
    void sendMsg(String topic, Map<String, Object> payload);
}
