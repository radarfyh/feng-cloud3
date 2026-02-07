package ltd.huntinginfo.feng.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
    
    @Value("${redis.embedded.port:6379}")
    private int port;
    
    @Value("${redis.embedded.password:}")
    private String password;
    
    @Bean
    @ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true")
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        
        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, "localhost", port, 3000, password);
        }
        return new JedisPool(poolConfig, "localhost", port, 3000);
    }
}
