package work.metanet.feng.gateway.param;

import lombok.Builder;
import lombok.Data;

/**
 * @author edison
 */
@Builder
@Data
public class HutoolCaptchaValidateParam {
    /**
     * 图形验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid = "";

}
