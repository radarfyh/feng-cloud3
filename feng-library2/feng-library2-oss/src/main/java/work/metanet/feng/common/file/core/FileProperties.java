package work.metanet.feng.common.file.core;

import work.metanet.feng.common.file.oss.OssProperties;
import work.metanet.feng.common.file.local.LocalFileProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件 配置信息
 *
 * @author edison
 * <p>
 * bucket 设置公共读权限
 */
@Data
@ConfigurationProperties(prefix = "file")
public class FileProperties {

	/**
	 * 默认的存储桶名称
	 */
	private String bucketName = "feng_user2";

	/**
	 * 本地文件配置信息
	 */
	private LocalFileProperties local;

	/**
	 * oss 文件配置信息
	 */
	private OssProperties oss;

}
