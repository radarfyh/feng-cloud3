package work.metanet.feng.common.file;

import work.metanet.feng.common.file.core.FileProperties;
import work.metanet.feng.common.file.local.LocalFileAutoConfiguration;
import work.metanet.feng.common.file.oss.OssAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * aws 自动配置类
 *
 * @author edison
 */
@Import({OssAutoConfiguration.class, LocalFileAutoConfiguration.class})
@EnableConfigurationProperties({FileProperties.class})
public class FileAutoConfiguration {

}
