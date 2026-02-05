package work.metanet.feng.gateway.service.impl;

import java.awt.Font;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.HutoolCaptchaTypeEnums;
import work.metanet.feng.gateway.config.CaptchaCacheServiceProvider;
import work.metanet.feng.gateway.config.property.HutoolCaptchaProperties;
import work.metanet.feng.gateway.service.HutoolCaptchaCacheService;
import work.metanet.feng.gateway.service.HutoolCaptchaService;
import work.metanet.feng.gateway.vo.HutoolCaptchaResult;

/**
 * 图片验证码
 *
 * @author edison
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HutoolCaptchaServiceImpl implements HutoolCaptchaService {

    private final HutoolCaptchaProperties captchaProperties;
    private final CodeGenerator codeGenerator;
    private final Font captchaFont;
    private final HutoolCaptchaCacheService captchaCacheService;

    @Override
    public Mono<Boolean> validate(String uuid, String code) {
        // 从redis中获取验证码
        String redisCode = captchaCacheService.get(CacheConstants.DEFAULT_CODE_KEY + uuid);
        
        return Mono.just(codeGenerator.verify(redisCode, code));
    }


    @Override
    public HutoolCaptchaResult getCaptcha() {

        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if (HutoolCaptchaTypeEnums.CIRCLE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if (HutoolCaptchaTypeEnums.GIF.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else if (HutoolCaptchaTypeEnums.LINE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        } else if (HutoolCaptchaTypeEnums.SHEAR.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        }
        captcha.setGenerator(codeGenerator);
        captcha.setTextAlpha(captchaProperties.getTextAlpha());
        captcha.setFont(captchaFont);

        String code = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        // 验证码文本缓存至Redis，用于登录校验
        String uuid = IdUtil.fastSimpleUUID();

        // 将验证码存到redis中 并设置5分钟过期
        String key = CacheConstants.DEFAULT_CODE_KEY + uuid;
        captchaCacheService.set(key, code, 5 * 60);

        return HutoolCaptchaResult.builder().img(imageBase64Data).uuid(uuid).build();
    }

    @Override
    public Mono<HutoolCaptchaResult> getCaptcha(ServerWebExchange exchange) {
        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if ("circle".equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if ("gif".equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        }

        captcha.setGenerator(codeGenerator);
        String code = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        // 验证码文本缓存至Redis，用于登录校验
        String uuid = IdUtil.fastSimpleUUID();
        
        // 将验证码存到redis中 并设置5分钟过期
        String key = CacheConstants.DEFAULT_CODE_KEY + uuid;
        captchaCacheService.set(key, code, 5 * 60);

        // 保存到 ServerHttpSession
//        WebSession session = exchange.getSession().block();
//        session.getAttributes().put("CAPTCHA_CODE", code);
//        session.getAttributes().put("CAPTCHA_UUID", uuid);
//
//        return Mono.just(HutoolCaptchaResult.builder().img(imageBase64Data).uuid(uuid).build());
        return exchange.getSession()
                .doOnTerminate(() -> log.info("Session terminated"))
                .map(session -> {
                    session.getAttributes().put("CAPTCHA_CODE", code);
                    session.getAttributes().put("CAPTCHA_UUID", uuid);
                    return HutoolCaptchaResult.builder().img(imageBase64Data).uuid(uuid).build();
                });
    }

    @Override
    public Mono<Boolean> validate(String uuid, String code, ServerWebExchange exchange) {
        // 从session获取验证码
//    	WebSession session = exchange.getSession().block();
//        String captchaCode = (String) session.getAttribute("CAPTCHA_CODE");
//        return Mono.just(codeGenerator.verify(captchaCode, code));
//        
        return exchange.getSession()
                .map(session -> {
                    // 通过 session 获取数据，并返回 Mono
                    String captchaCode = (String) session.getAttributes().get("CAPTCHA_CODE");                    
                    return codeGenerator.verify(captchaCode, code);
                });
    }
}
