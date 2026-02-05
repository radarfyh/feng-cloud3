package work.metanet.feng.common.security.component;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * FengSecurityMessageSourceConfiguration 用于配置自定义的错误信息资源。
 * <p>
 * 该配置类在 Web 应用环境中生效，并注入一个用于处理错误信息的 {@link MessageSource} Bean。
 * 通过 {@link ReloadableResourceBundleMessageSource}，可以支持从外部资源文件加载国际化的错误信息，
 * 以便为用户提供更友好的错误提示信息。
 * </p>
 */
@ConditionalOnWebApplication(type = SERVLET)
public class FengSecurityMessageSourceConfiguration implements WebMvcConfigurer {

    /**
     * 配置自定义的 MessageSource，用于加载错误消息资源。
     * 
     * @return 返回配置好的 MessageSource 实例
     * @see ReloadableResourceBundleMessageSource
     */
    @Bean
    public MessageSource securityMessageSource() {
        // 使用 ReloadableResourceBundleMessageSource 加载自定义的错误消息资源
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // 设置错误消息文件的路径
        messageSource.addBasenames("classpath:org/springframework/security/messages");
        // 返回配置好的 messageSource
        return messageSource;
    }
}
