package work.metanet.feng.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.gateway.service.HutoolCaptchaService;
import work.metanet.feng.gateway.param.HutoolCaptchaValidateParam;
import work.metanet.feng.gateway.vo.HutoolCaptchaResult;

@Slf4j
@RestController
@RequestMapping("/ht/captcha")
@RequiredArgsConstructor
//@Tag(name = "验证码工具类", description = "验证码工具类")
public class HutoolCaptchaController {

    private final HutoolCaptchaService sysCaptchaService;

    /**
     * 生成验证码
     */
    @GetMapping("/get")
    public Mono<HutoolCaptchaResult> getCaptcha(ServerWebExchange exchange) {
        return sysCaptchaService.getCaptcha(exchange);  // 通过 exchange 获取 ServerHttpSession
    }

    /**
     * 验证码校验
     *
     * @param param 验证码参数
     */
    @PostMapping("/check")
    public Mono<Boolean> checkCaptcha(@RequestBody HutoolCaptchaValidateParam param, ServerWebExchange exchange) {
        String uuid = param.getUuid();
        String captcha = param.getCode();
        if (StrUtil.isEmpty(uuid) || StrUtil.isEmpty(captcha)) {
            log.error("验证码参数不能为空");
            return Mono.just(false);
        }
        log.info("接收到验证码: uuId:{}, 验证码:{}", uuid, captcha);
        // 参数校验
        return sysCaptchaService.validate(uuid, captcha, exchange);  // 通过 exchange 获取 ServerHttpSession
    }
}
