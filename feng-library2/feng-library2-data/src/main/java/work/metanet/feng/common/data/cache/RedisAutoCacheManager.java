package work.metanet.feng.common.data.cache;

import cn.hutool.core.util.StrUtil;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.data.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * RedisAutoCacheManager 扩展了 RedisCacheManager，提供了缓存名称自动化配置功能。
 * <p>
 * 缓存名称通过分隔符 '#' 来区分。第一个部分是缓存名称，第二部分是 TTL（过期时间）。
 * 如果缓存名称包含 TTL（例如："cacheName#60"），则 TTL 将被解析并应用于该缓存。
 * </p>
 */
@Slf4j
public class RedisAutoCacheManager extends RedisCacheManager {

    /**
     * 缓存名称和 TTL 之间的分隔符
     */
    private static final String SPLIT_FLAG = "#";

    /**
     * 缓存名称和 TTL 分隔后的数组长度
     */
    private static final int CACHE_LENGTH = 2;

    /**
     * 构造函数，初始化 RedisAutoCacheManager
     *
     * @param cacheWriter                   Redis 缓存写入器
     * @param defaultCacheConfiguration     默认缓存配置
     * @param initialCacheConfigurations    初始缓存配置
     * @param allowInFlightCacheCreation   是否允许动态创建缓存
     */
    RedisAutoCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
    }

    /**
     * 重写 createRedisCache 方法，根据缓存名称判断是否包含 TTL 配置。
     * 如果缓存名称包含 "#"，则解析 TTL 并设置相应的过期时间。
     *
     * @param name        缓存名称
     * @param cacheConfig 缓存配置
     * @return RedisCache 实例
     */
    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        // 如果缓存名称为空或不包含分隔符，则直接调用父类的方法
        if (StrUtil.isBlank(name) || !name.contains(SPLIT_FLAG)) {
            return super.createRedisCache(name, cacheConfig);
        }

        // 将缓存名称按分隔符拆分成数组
        String[] cacheArray = name.split(SPLIT_FLAG);
        // 如果拆分后数组长度小于缓存长度，则直接调用父类的方法
        if (cacheArray.length < CACHE_LENGTH) {
            return super.createRedisCache(name, cacheConfig);
        }

        // 如果缓存配置不为空，则设置过期时间
        if (cacheConfig != null) {
            // 解析 TTL（以秒为单位）
            Duration duration = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
            // 设置缓存的 TTL
            cacheConfig = cacheConfig.entryTtl(duration);
        }
        // 返回基于解析后的缓存名称和配置创建的 RedisCache 实例
        return super.createRedisCache(cacheArray[0], cacheConfig);
    }

    /**
     * 重写 getCache 方法，根据缓存名称自动为不同的租户配置缓存名称。
     * 如果缓存名称以 CacheConstants.GLOBALLY 开头，则直接返回缓存；否则，根据租户信息添加前缀。
     *
     * @param name 缓存名称
     * @return Cache 实例
     */
    @Override
    public Cache getCache(String name) {
        // 如果缓存名称是全局缓存，则直接调用父类的 getCache 方法
        if (name.startsWith(CacheConstants.GLOBALLY)) {
            return super.getCache(name);
        }
        // 否则，基于租户信息添加前缀
        return super.getCache(CacheConstants.FENG_TENANT + StrUtil.COLON + name);
    }
}
