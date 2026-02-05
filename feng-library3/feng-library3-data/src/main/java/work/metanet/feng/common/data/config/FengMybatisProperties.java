package work.metanet.feng.common.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * FengMybatisProperties 配置类
 * <p>
 * 该类用于配置 Mybatis 相关的属性。在 Spring Boot 应用启动时，Mybatis 配置将从 
 * "feng.mybatis" 配置前缀中加载并自动注入到该类实例中。它支持动态刷新功能，以便在
 * 配置更新时实时应用更改。
 * </p>
 */
@Data
@RefreshScope // 支持 Spring Cloud Config 动态刷新配置
@ConfigurationProperties("feng.mybatis") // 从配置文件中加载 "feng.mybatis" 前缀的配置项
public class FengMybatisProperties {

    /**
     * 是否打印可执行 SQL
     * <p>
     * 该属性用于控制 Mybatis 是否输出 SQL 到日志中。默认情况下，SQL 会被打印，
     * 但可以根据需要在配置文件中禁用。
     * </p>
     * 默认值为 true，表示打印 SQL。
     */
    private boolean showSql = true;

    /**
     * 可以扩展更多的 Mybatis 配置项
     * <p>
     * 将来如果有新的配置项，可以在这里添加更多的属性并通过配置文件传递。
     * </p>
     */
}
