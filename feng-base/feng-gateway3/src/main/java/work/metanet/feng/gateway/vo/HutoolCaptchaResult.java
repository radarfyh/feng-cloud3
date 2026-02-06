package work.metanet.feng.gateway.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author edison
 */

@Builder
@Data
public class HutoolCaptchaResult {
	/**
	 * 验证码缓存key
	 */
	private String uuid;
	/**
	 * 验证码图片Base64字符串
	 */
	private String img;

}
