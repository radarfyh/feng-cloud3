package work.metanet.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "redis.embedded")
public class EmbeddedRedisProperties {
    
    @NotNull
    private Boolean enabled = true;
    
    @Min(1024)
    private int port = 6379;
    
    @NotBlank
    private String maxHeap = "64MB";
    
    private String password;
    
    private Boolean requirepass = false;
    
    private String ip = "127.0.0.1";
}