package work.metanet.feng.common.data.cache;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RedisCacheAutoConfiguration 是一个 Spring Boot 配置类，扩展了 Redis 缓存的自动配置功能。
 * <p>
 * 该类支持通过缓存名称自动化配置缓存的超时时间（TTL）。当缓存名称包含TTL参数（例如："cacheName#60"），
 * TTL将被解析并应用于该缓存。此外，还对缓存配置进行了定制化，提供了多种缓存设置（如序列化方式、键前缀等）。
 * </p>
 */
@Configuration
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@ConditionalOnBean({ RedisConnectionFactory.class })
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheAutoConfiguration {

	private final CacheProperties cacheProperties;

	private final CacheManagerCustomizers customizerInvoker;

	@Nullable
	private final RedisCacheConfiguration redisCacheConfiguration;

	/**
	 * 构造函数用于初始化 RedisCacheAutoConfiguration 类的依赖项。
	 * <p>
	 * 该构造函数会注入 Redis 缓存的基本配置、缓存定制化工具和 Redis 缓存配置（如果有）。
	 * </p>
	 * 
	 * @param cacheProperties 缓存相关配置，包含缓存名称、TTL等设置
	 * @param customizerInvoker 缓存定制化工具，允许定制缓存管理器的行为
	 * @param redisCacheConfiguration Redis 缓存配置，可以提供自定义配置
	 */
	RedisCacheAutoConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizerInvoker,
			ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
		this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
	}

	/**
	 * 创建并返回自定义的 RedisCacheManager 实例。
	 * <p>
	 * 该方法根据缓存的基本配置、缓存名称及其TTL（通过配置类或默认值）创建 RedisCacheManager。
	 * 然后通过定制化工具执行额外的定制操作，最后返回一个已定制的缓存管理器实例。
	 * </p>
	 * 
	 * @param connectionFactory Redis 连接工厂，用于与 Redis 服务器进行连接
	 * @param resourceLoader 资源加载器，用于加载相关资源
	 * @return 配置好的 RedisCacheManager 实例
	 */
	@Bean
	@Primary
	public RedisCacheManager cacheFengManager(RedisConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
		DefaultRedisCacheWriter redisCacheWriter = new DefaultRedisCacheWriter(connectionFactory);
		RedisCacheConfiguration cacheConfiguration = this.determineConfiguration(resourceLoader.getClassLoader());
		List<String> cacheNames = this.cacheProperties.getCacheNames();
		Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
		if (!cacheNames.isEmpty()) {
			Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
			cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
			initialCaches.putAll(cacheConfigMap);
		}
		RedisAutoCacheManager cacheManager = new RedisAutoCacheManager(redisCacheWriter, cacheConfiguration,
				initialCaches, true);
		cacheManager.setTransactionAware(false);
		return this.customizerInvoker.customize(cacheManager);
	}
	
	/**
	 * 根据当前的 Redis 缓存配置返回对应的 RedisCacheConfiguration。
	 * <p>
	 * 如果提供了自定义的 Redis 缓存配置，则返回该配置；否则使用默认的配置并根据配置文件中的设置定制（如TTL、键前缀等）。
	 * </p>
	 *
	 * @param classLoader 类加载器，用于设置序列化方式
	 * @return 配置好的 RedisCacheConfiguration 实例
	 */
	private RedisCacheConfiguration determineConfiguration(ClassLoader classLoader) {
		if (this.redisCacheConfiguration != null) {
			return this.redisCacheConfiguration;
		}
		else {
			CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
			RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
			config = config.serializeValuesWith(RedisSerializationContext.SerializationPair
					.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
			if (redisProperties.getTimeToLive() != null) {
				config = config.entryTtl(redisProperties.getTimeToLive());
			}

			if (redisProperties.getKeyPrefix() != null) {
				config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
			}

			if (!redisProperties.isCacheNullValues()) {
				config = config.disableCachingNullValues();
			}

			if (!redisProperties.isUseKeyPrefix()) {
				config = config.disableKeyPrefix();
			}

			return config;
		}
	}

}
