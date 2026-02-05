package work.metanet.feng.common.data.cache;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * RedisCacheManagerConfiguration 配置类，负责创建并配置 CacheManagerCustomizers。
 * <p>
 * 该类会在没有配置 `CacheManagerCustomizers` Bean 时，自动创建一个 CacheManagerCustomizers 实例。
 * 用于扩展和定制缓存管理器的行为。
 * </p>
 */

@Configuration
@ConditionalOnMissingBean(CacheManagerCustomizers.class)
public class RedisCacheManagerConfiguration {
	/**
	 * 创建并返回一个 `CacheManagerCustomizers` 实例。
	 * <p>
	 * 如果存在定制的 `CacheManagerCustomizer` Bean，则通过 `ObjectProvider` 获取并传递给 `CacheManagerCustomizers`。
	 * 如果没有提供定制器，则使用默认的构造方式。
	 * </p>
	 *
	 * @param customizers 可选的 `CacheManagerCustomizer` 列表，用于定制缓存管理器的行为
	 * @return 配置好的 `CacheManagerCustomizers` 实例
	 */
	@Bean
	public CacheManagerCustomizers cacheManagerCustomizers(
			ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
	    // 如果没有提供 CacheManagerCustomizer，则返回空的定制器列表
	    List<CacheManagerCustomizer<?>> customizerList = customizers.getIfAvailable(() -> Collections.emptyList());
	    return new CacheManagerCustomizers(customizerList);
	}

}
