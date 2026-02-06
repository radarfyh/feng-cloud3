package work.metanet.feng.gateway.service;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import work.metanet.feng.gateway.vo.HutoolCaptchaResult;
/**
 * 图片验证码
 *
 * @author edison
 */
public interface HutoolCaptchaService {
    /**
     * 验证码效验
     *
     * @param uuid uuid
     * @param code 验证码
     * @return true：成功 false：失败
     */
	Mono<Boolean> validate(String uuid, String code);

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    HutoolCaptchaResult getCaptcha();

    /**
     * 获取验证码
     *
     * @param session
     * @return
     */
    Mono<HutoolCaptchaResult> getCaptcha(ServerWebExchange exchange);

    /**
     * 验证码效验
     *
     * @param uuid
     * @param code
     * @param session
     * @return
     */
    Mono<Boolean> validate(String uuid, String code, ServerWebExchange exchange);
}
