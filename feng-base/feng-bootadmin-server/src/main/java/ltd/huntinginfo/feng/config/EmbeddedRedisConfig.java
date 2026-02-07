package ltd.huntinginfo.feng.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true")
@Slf4j
public class EmbeddedRedisConfig {
    
    private final EmbeddedRedisProperties properties;
    private RedisServer redisServer;
    
    public EmbeddedRedisConfig(EmbeddedRedisProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisServer redisServer() throws IOException {
        if (!properties.getEnabled()) {
            return null;
        }

        RedisServerBuilder builder = RedisServer.builder()
                .port(properties.getPort())
                .setting("maxheap " + properties.getMaxHeap())
                .setting("heapdir " + System.getProperty("java.io.tmpdir"));
        // Windows 系统特殊配置
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            builder.setting("bind 127.0.0.1");
            log.info("os.name contain win");
        }
        else {
        	builder.setting("bind " + (properties.getIp() != null ? properties.getIp() : "127.0.0.1"));
        }
        if (properties.getRequirepass() && properties.getPassword() != null) {
            builder.setting("requirepass " + properties.getPassword());
        }
            
        this.redisServer = builder.build();
        return redisServer;
    }
    
    @PostConstruct
    public void startRedis() throws IOException {
        if (properties.getEnabled() && redisServer != null) {
            try {
                if (!redisServer.isActive()) {
                    // 添加重试逻辑
                    int maxAttempts = 3;
                    int attempt = 0;
                    while (attempt < maxAttempts) {
                        try {
                            redisServer.start();
                            break;
                        } catch (RuntimeException e) {
                            attempt++;
                            if (attempt >= maxAttempts) {
                                throw new IllegalStateException(
                                    "Failed to start embedded Redis server after " + maxAttempts + " attempts", e);
                            }
                            // 等待后重试
                            Thread.sleep(1000);
                            // 尝试更换端口
                            changeRedisPort();
                        }
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to start embedded Redis server on port " 
                    + properties.getPort(), e);
            }
        }
    }

    private void changeRedisPort() throws IOException {
        int newPort = properties.getPort() + 1;
        log.warn("Changing Redis port from {} to {}", properties.getPort(), newPort);
        properties.setPort(newPort);
        this.redisServer = redisServer();
    }
    
    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            try {
                redisServer.stop();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to stop embedded Redis server", e);
            }
        }
    }
    
    public RedisServer getRedisServer() {
        return redisServer;
    }
}