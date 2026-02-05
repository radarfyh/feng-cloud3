package work.metanet.feng.common.data.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RedisMessageConfiguration 类用于配置 Redis 消息信道相关设置。
 * <p>
 * 该配置类负责创建并配置一个 RedisMessageListenerContainer，监听 Redis 消息并处理相关的消息事件。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class RedisMessageConfiguration {

    /**
     * 创建并配置 RedisMessageListenerContainer Bean
     * <p>
     * 如果 Spring 容器中没有已配置的 RedisMessageListenerContainer Bean，系统将自动创建并配置一个容器，
     * 该容器用于监听 Redis 消息并处理相关事件。
     * </p>
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 配置好的 RedisMessageListenerContainer 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        // 检查 RedisConnectionFactory 是否为空，避免潜在的空指针异常
        if (redisConnectionFactory == null) {
            throw new IllegalArgumentException("RedisConnectionFactory must not be null");
        }

        // 创建 RedisMessageListenerContainer 实例
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        
        // 设置连接工厂
        container.setConnectionFactory(redisConnectionFactory);
        
        // 输出日志，确认容器已创建
        log.info("RedisMessageListenerContainer has been initialized with the given connection factory.");

        return container;
    }
}
