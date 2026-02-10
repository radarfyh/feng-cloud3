package ltd.huntinginfo.feng.agent.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.agent.strategy.IMqProducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
// 核心：当配置文件中 feng.mq.type = rabbitmq 时，加载此 Bean
@ConditionalOnProperty(prefix = "feng.mq", name = "type", havingValue = "rabbitmq", matchIfMissing = true)
public class RabbitProducerImpl implements IMqProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMsg(String topic, Map<String, Object> payload) {
        log.info("【RabbitMQ】发送消息, Topic: {}, Payload: {}", topic, payload);
        // 这里简单将 topic 既当作 Exchange 也当作 RoutingKey，具体逻辑可根据业务细化
        rabbitTemplate.convertAndSend(topic, topic, payload);
    }
}