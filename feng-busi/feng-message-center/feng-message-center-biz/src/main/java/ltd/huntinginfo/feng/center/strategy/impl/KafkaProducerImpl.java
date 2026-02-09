package ltd.huntinginfo.feng.center.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.strategy.IMqProducer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
// 核心：当配置文件中 feng.mq.type = kafka 时，加载此 Bean
@ConditionalOnProperty(prefix = "feng.mq", name = "type", havingValue = "kafka")
public class KafkaProducerImpl implements IMqProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMsg(String topic, Map<String, Object> payload) {
        log.info("【Kafka】发送消息, Topic: {}, Payload: {}", topic, payload);
        // 注意：Kafka Topic 需要提前创建，或者配置 auto-create-topics
        kafkaTemplate.send(topic, payload);
    }
}
