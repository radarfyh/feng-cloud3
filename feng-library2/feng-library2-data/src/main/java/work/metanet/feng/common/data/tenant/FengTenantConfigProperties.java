package work.metanet.feng.common.data.tenant;

import lombok.Data;
import work.metanet.feng.common.data.tenant.TenantBroker.TenantBrokerExceptionWrapper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * 多租户配置
 * <p>
 * 该配置类用于管理多租户相关的配置项，如机构列名称和涉及多租户的数据表集合。
 * 该配置类是 Spring Boot 配置的一部分，支持从外部配置源（如 application.properties 或 YAML 文件）加载数据。
 * </p>
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "feng.tenant")
public class FengTenantConfigProperties {

    /**
     * 维护机构列名称
     * <p>
     * 指定多租户环境中，租户的标识列名，通常用于区分不同租户的数据。
     * 默认值为 "tenant_id"，但可以根据实际情况进行配置。
     * </p>
     */
    private String column = "tenant_id";

    /**
     * 多机构的数据表集合
     * <p>
     * 指定哪些数据表包含多租户数据。表中的数据将根据租户列（如 "tenant_id"）进行过滤。
     * 例如，配置 "user", "order" 表示这些表是多租户表，需要根据机构信息进行隔离。
     * </p>
     */
    @NotEmpty(message = "The tables list cannot be empty")
    private List<String> tables = new ArrayList<>();

    /**
     * 校验配置项的合法性
     * <p>
     * 确保所有的必要配置项都被正确设置，避免启动时出现意外的配置错误。
     * 可以在此方法中抛出 RuntimeException 或使用 @Validated 结合 @NotEmpty 来完成基本校验。
     * </p>
     */
    public void validateConfig() {
        if (tables == null || tables.isEmpty()) {
            throw new TenantBrokerExceptionWrapper("The 'tables' configuration must not be empty");
        }
    }
}
