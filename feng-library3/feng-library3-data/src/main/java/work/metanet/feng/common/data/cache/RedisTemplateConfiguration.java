package work.metanet.feng.common.data.cache;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RedisTemplate 配置类
 * <p>
 * 该配置类负责初始化 RedisTemplate，以便在应用中通过 Redis 进行缓存操作。它设置了适当的序列化方式，
 * 使得 RedisTemplate 能够存储和获取对象及其键值对。
 * </p>
 */
@Slf4j
@EnableCaching
@Configuration
@AutoConfigureBefore(name = { "org.redisson.spring.starter.RedissonAutoConfiguration", 
                              "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration" })
public class RedisTemplateConfiguration {

    /**
     * 创建并配置 RedisTemplate 实例
     * <p>
     * 本方法配置了 RedisTemplate 的键和值的序列化器。键使用 StringRedisSerializer 序列化，
     * 值使用 JdkSerializationRedisSerializer 序列化。该配置支持缓存数据存储和提取。
     * </p>
     * 
     * @param redisConnectionFactory Redis 连接工厂，自动注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 检查 RedisConnectionFactory 是否为 null
        if (redisConnectionFactory == null) {
            throw new IllegalArgumentException("RedisConnectionFactory must not be null");
        }

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 配置键的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 配置哈希键的序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 配置值的序列化方式
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

        // 配置哈希值的序列化方式
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());

        // 设置 Redis 连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 输出日志，确认配置成功
        log.info("RedisTemplate has been initialized with StringRedisSerializer for keys and JdkSerializationRedisSerializer for values.");

        return redisTemplate;
    }
}
