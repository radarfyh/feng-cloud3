package work.metanet.feng.gateway.config;

import java.awt.Font;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.gateway.config.property.HutoolCaptchaProperties;

/**
 * 验证码自动装配配置
 *
 * @author edison
 * @since 2023/11/24
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class HutoolCaptchaConfig {

    private final HutoolCaptchaProperties captchaProperties;

    /**
     * 验证码文字生成器
     *
     * @return CodeGenerator
     */
    @Bean
    public CodeGenerator codeGenerator() {
    	log.debug("Enter codeGenerator");
        String codeType = captchaProperties.getCode().getType();
        int codeLength = captchaProperties.getCode().getLength();
        log.debug("codeType:{}, codeLength:{}", codeType, codeLength);
        
        CodeGenerator codeGen = null;
        if ("math".equalsIgnoreCase(codeType)) {
        	codeGen = new MathGenerator(codeLength);
        } else if ("random".equalsIgnoreCase(codeType)) {
        	codeGen = new RandomGenerator(codeLength);
        } else {
            throw new IllegalArgumentException("Invalid captcha codegen type: " + codeType);
        }
        
        log.debug("codeGen: {}", codeGen);
        return codeGen;
    }

    /**
     * 验证码字体
     */
    @Bean
    public Font captchaFont() {
        String fontName = captchaProperties.getFont().getName();
        int fontSize = captchaProperties.getFont().getSize();
        int fontWight = captchaProperties.getFont().getWeight();
        return new Font(fontName, fontWight, fontSize);
    }

}

