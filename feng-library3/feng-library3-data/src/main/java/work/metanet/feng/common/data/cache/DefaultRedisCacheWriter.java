package work.metanet.feng.common.data.cache;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * DefaultRedisCacheWriter 是一个 RedisCacheWriter 实现类，用于与 Redis 进行缓存操作。
 * 它支持诸如 put、get、remove 和缓存清理等操作，并提供可选的锁机制以确保在缓存更新期间的线程安全。
 * <p>
 * 该类使用 RedisConnectionFactory 来建立 Redis 连接，并支持缓存锁定和过期。
 * </p>
 */
class DefaultRedisCacheWriter implements RedisCacheWriter {

    private final RedisConnectionFactory connectionFactory;
    private final Duration sleepTime;

    /**
     * 使用给定的 RedisConnectionFactory 构造 DefaultRedisCacheWriter。
     * 
     * @param connectionFactory Redis 连接工厂
     */
    DefaultRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, Duration.ZERO);
    }

    /**
     * 使用给定的 RedisConnectionFactory 和每次锁请求之间的睡眠时间构造 DefaultRedisCacheWriter。
     * 
     * @param connectionFactory Redis 连接工厂
     * @param sleepTime 锁请求之间的睡眠时间。使用 Duration.ZERO 来禁用锁定。
     */
    private DefaultRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration sleepTime) {
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");
        Assert.notNull(sleepTime, "SleepTime must not be null!");

        this.connectionFactory = connectionFactory;
        this.sleepTime = sleepTime;
    }

    /**
     * 将值放入与给定名称和键相关联的缓存中。
     * 可选地，可以提供过期时间。
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 缓存项的生存时间（可选）
     */
    @Override
    public void put(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");

        execute(name, connection -> {
            if (shouldExpireWithin(ttl)) {
                connection.set(key, value, Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS), SetOption.upsert());
            } else {
                connection.set(key, value);
            }
            return "OK";
        });
    }

    /**
     * 从缓存中检索与给定名称和键相关联的值。
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @return 缓存的值
     */
    @Override
    public byte[] get(String name, byte[] key) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");

        return execute(name, connection -> connection.get(key));
    }

    /**
     * 如果缓存中不存在值，则将值放入缓存。
     * 可选地，可以提供过期时间。
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 缓存项的生存时间（可选）
     * @return 如果键已存在，则返回现有值，否则返回 null
     */
    @Override
    public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");

        return execute(name, connection -> {
            if (isLockingCacheWriter()) {
                doLock(name, connection);
            }
            try {
                if (connection.setNX(key, value)) {
                    if (shouldExpireWithin(ttl)) {
                        connection.pExpire(key, ttl.toMillis());
                    }
                    return null;
                }
                return connection.get(key);
            } finally {
                if (isLockingCacheWriter()) {
                    doUnlock(name, connection);
                }
            }
        });
    }

    /**
     * 移除与给定名称和键相关联的缓存项。
     * 
     * @param name 缓存名称
     * @param key 缓存键
     */
    @Override
    public void remove(String name, byte[] key) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");

        execute(name, connection -> connection.del(key));
    }

    /**
     * 清除匹配给定模式的缓存项。
     * 
     * @param name 缓存名称
     * @param pattern 匹配缓存键的模式
     */
    @Override
    public void clean(String name, byte[] pattern) {
        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(pattern, "Pattern must not be null!");

        execute(name, connection -> {
            boolean wasLocked = false;
            try {
                if (isLockingCacheWriter()) {
                    doLock(name, connection);
                    wasLocked = true;
                }

                byte[][] keys = Optional.ofNullable(connection.keys(pattern)).orElse(Collections.emptySet()).toArray(new byte[0][]);

                if (keys.length > 0) {
                    connection.del(keys);
                }
            } finally {
                if (wasLocked && isLockingCacheWriter()) {
                    doUnlock(name, connection);
                }
            }
            return "OK";
        });
    }

	@Override
	public void clearStatistics(String s) {

	}

	@Override
	public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
		return null;
	}
	
    /**
     * 显式地锁定给定缓存名称的缓存。
     * 
     * @param name 缓存名称
     */
    void lock(String name) {
        execute(name, connection -> doLock(name, connection));
    }

    /**
     * 显式地解锁给定缓存名称的缓存。
     * 
     * @param name 缓存名称
     */
    void unlock(String name) {
        executeLockFree(connection -> doUnlock(name, connection));
    }

    /**
     * 在给定的缓存名称上执行锁定操作。
     * 
     * @param name 缓存名称
     * @param connection Redis 连接
     * @return 如果锁定操作成功，则返回 true
     */
    private Boolean doLock(String name, RedisConnection connection) {
        return connection.setNX(createCacheLockKey(name), new byte[0]);
    }

    /**
     * 在给定的缓存名称上执行解锁操作。
     * 
     * @param name 缓存名称
     * @param connection Redis 连接
     * @return 被删除的键的数量
     */
    private Long doUnlock(String name, RedisConnection connection) {
        return connection.del(createCacheLockKey(name));
    }

    /**
     * 检查缓存是否已锁定。
     * 
     * @param name 缓存名称
     * @param connection Redis 连接
     * @return 如果缓存已锁定，则返回 true
     */
    boolean doCheckLock(String name, RedisConnection connection) {
        return connection.exists(createCacheLockKey(name));
    }

    /**
     * @return 如果 RedisCacheWriter 使用锁，则返回 true。
     */
    private boolean isLockingCacheWriter() {
        return !sleepTime.isZero() && !sleepTime.isNegative();
    }
    /**
     * 执行 Redis 操作，使用给定的名称和回调函数。
     * 
     * @param name 缓存名称
     * @param callback 操作回调
     * @param <T> 操作的返回类型
     * @return 操作的结果
     */
    private <T> T execute(String name, Function<RedisConnection, T> callback) {
        RedisConnection connection = connectionFactory.getConnection();
        try {
            checkAndPotentiallyWaitUntilUnlocked(name, connection);
            return callback.apply(connection);
        } finally {
            connection.close();
        }
    }

    /**
     * 执行 Redis 操作，不进行任何锁定行为。
     * 
     * @param callback 操作回调
     */
    private void executeLockFree(Consumer<RedisConnection> callback) {
        RedisConnection connection = connectionFactory.getConnection();
        try {
            callback.accept(connection);
        } finally {
            connection.close();
        }
    }

    /**
     * 检查是否使用锁定，并在缓存解锁之前等待。
     * 
     * @param name 缓存名称
     * @param connection Redis 连接
     */
    private void checkAndPotentiallyWaitUntilUnlocked(String name, RedisConnection connection) {
        if (!isLockingCacheWriter()) {
            return;
        }

        try {
            while (doCheckLock(name, connection)) {
                Thread.sleep(sleepTime.toMillis());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PessimisticLockingFailureException(
                    String.format("Interrupted while waiting to unlock cache %s", name), ex);
        }
    }

    /**
     * 检查是否需要根据给定的 TTL 设置过期时间。
     * 
     * @param ttl TTL（过期时间）
     * @return 如果需要设置过期时间，则返回 true；否则返回 false
     */
    private static boolean shouldExpireWithin(@Nullable Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }
    
    /**
     * 创建给定缓存名称的锁定键。
     * 
     * @param name 缓存名称
     * @return 锁定键
     */
    private static byte[] createCacheLockKey(String name) {
        return (name + "~lock").getBytes(StandardCharsets.UTF_8);
    }
    


    @Override
    public CacheStatistics getCacheStatistics(String s) {
        return null;
    }


}
