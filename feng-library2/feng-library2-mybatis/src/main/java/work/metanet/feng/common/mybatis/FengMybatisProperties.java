package work.metanet.feng.common.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Mybatis 配置
 *
 * @author edison
 * @date 2021/6/3
 */
@Data
@RefreshScope
@ConfigurationProperties("feng.mybatis")
public class FengMybatisProperties {

	/**
	 * 是否打印可执行 sql
	 */
	private boolean showSql = true;

}
