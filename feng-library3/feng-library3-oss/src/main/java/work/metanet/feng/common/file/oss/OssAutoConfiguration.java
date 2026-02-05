package work.metanet.feng.common.file.oss;

import work.metanet.feng.common.file.core.FileProperties;
import work.metanet.feng.common.file.core.FileTemplate;
import work.metanet.feng.common.file.local.LocalFileProperties;
import work.metanet.feng.common.file.oss.http.OssEndpoint;
import work.metanet.feng.common.file.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * aws 自动配置类
 *
 * @author edison
 */
@AllArgsConstructor
@EnableConfigurationProperties({OssProperties.class, LocalFileProperties.class})
public class OssAutoConfiguration {

    private final FileProperties properties;

    @Bean
    @Primary
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnProperty(name = "file.oss.enable", havingValue = "true")
    public FileTemplate ossTemplate() {
        return new OssTemplate(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "file.oss.info", havingValue = "true")
    public OssEndpoint ossEndpoint(OssTemplate template) {
        return new OssEndpoint(template);
    }

}
