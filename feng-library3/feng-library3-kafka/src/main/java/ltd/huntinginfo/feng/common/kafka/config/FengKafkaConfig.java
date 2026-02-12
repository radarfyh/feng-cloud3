package ltd.huntinginfo.feng.common.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka 配置类（仅保留条件化装配和可选的 ObjectMapper 定制）
 * <p>
 * 所有连接参数、序列化器均由 Spring Boot 自动配置，通过 application.yml 控制。
 * 本类仅在 mc.mq.type = kafka 时加载，确保与其他 MQ 实现隔离。
 * </p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
public class FengKafkaConfig {

    /**
     * 定制 ObjectMapper，支持 Java 8 时间类型
     * 此 Bean 会被自动配置的 JsonSerializer/JsonDeserializer 使用
     */
    @Bean
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}