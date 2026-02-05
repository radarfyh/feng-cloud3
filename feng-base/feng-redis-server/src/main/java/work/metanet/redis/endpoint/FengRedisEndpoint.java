package work.metanet.redis.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "redis")
public class FengRedisEndpoint {
    
    private final RedisServer redisServer;
    private final JedisPool jedisPool;
    
    public FengRedisEndpoint(RedisServer redisServer, 
    		JedisPool jedisPool) {
		this.redisServer = redisServer;
		this.jedisPool = jedisPool;
//		redisServer.start();
	}

    // 状态检查
    @ReadOperation
    public Map<String, Object> status() {
        return Map.of(
                "status", redisServer.isActive() ? "UP" : "DOWN",
                "details", Map.of(
                    "port", redisServer.ports().get(0),
                    "active", redisServer.isActive()
                )
            );
    }

    // 统一操作入口
    @WriteOperation
    public Map<String, String> execute(@Selector String action) throws IOException {
        Map<String, String> response = new HashMap<>();
        try {
            switch (action.toLowerCase()) {
                case "start":
                    if (!redisServer.isActive()) {
                        redisServer.start();
                    }
                    response.put("status", "STARTED");
                    break;
                case "stop":
                    if (redisServer.isActive()) {
                        redisServer.stop();
                    }
                    response.put("status", "STOPPED");
                    break;
                default:
                    throw new IllegalArgumentException("無效指令，請使用start或者stop");
            }
            response.put("success", "true");
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("success", "false");
        }
        return response;
    }
    
    private Jedis createJedisConnection() {
        return jedisPool.getResource();
    }
}