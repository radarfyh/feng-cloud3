package ltd.huntinginfo.feng.Indicator;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.RedisServer;

@Component
public class RedisHealthIndicator implements HealthIndicator {
    
    private final RedisServer redisServer;
    private final JedisPool jedisPool;

    public RedisHealthIndicator(RedisServer redisServer, JedisPool jedisPool) {
        this.redisServer = redisServer;
        this.jedisPool = jedisPool;
    }

    @Override
    public Health health() {
        if (!redisServer.isActive()) {
            return Health.down()
                .withDetail("reason", "Redis process not running")
                .build();
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            String ping = jedis.ping();
            return "PONG".equals(ping) 
                ? Health.up().withDetail("response", ping).build()
                : Health.down().withDetail("response", ping).build();
        } catch (Exception e) {
            return Health.down(e)
                .withDetail("port", redisServer.ports().get(0))
                .build();
        }
    }
}
