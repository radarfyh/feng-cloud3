package work.metanet.feng.common.security.component;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.cloud.commons.security.AccessTokenContextRelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ClientConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;

import java.lang.annotation.*;

/**
 * 自动配置类，解决 Feign 请求时传递 OAuth2 令牌为空的问题。
 * <p>
 * 该配置类会在应用启动时自动创建并注入 {@link AccessTokenContextRelay}，该组件会将 OAuth2 认证信息
 * 从客户端上下文中传递到请求上下文，确保在 Feign 调用中传递正确的 OAuth2 令牌。
 * </p>
 */
@Configuration
@AutoConfigureAfter(OAuth2AutoConfiguration.class)  // 确保 OAuth2 自动配置后执行
@ConditionalOnWebApplication  // 仅在 Web 应用程序环境中启用
@ConditionalOnProperty("security.oauth2.client.client-id")  // 仅在配置了 client-id 的情况下启用
public class FengResourceServerTokenRelayAutoConfiguration {

    /**
     * 创建并注入 {@link AccessTokenContextRelay} 实例，该实例将 OAuth2 令牌从客户端上下文
     * 传递到请求上下文，解决 Feign 调用中令牌丢失的问题。
     * 
     * @param context OAuth2ClientContext，提供 OAuth2 客户端的上下文信息。
     * @return 返回 {@link AccessTokenContextRelay} 实例。
     */
    @Bean
    public AccessTokenContextRelay accessTokenContextRelay(OAuth2ClientContext context) {
        return new AccessTokenContextRelay(context);
    }

    /**
     * 自定义条件注解，仅在满足特定条件时启用相关配置。
     * 该注解用于在客户端和资源服务器配置都可用时，启用相关的配置。
     */
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Conditional(OAuth2OnClientInResourceServerCondition.class)
    @interface ConditionalOnOAuth2ClientInResourceServer {
    }

    /**
     * 条件判断类，判断 OAuth2 客户端和资源服务器的配置是否同时存在。
     * 仅当这两个配置都存在时，相关的 Bean 才会被注册。
     */
    private static class OAuth2OnClientInResourceServerCondition extends AllNestedConditions {

        public OAuth2OnClientInResourceServerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);  // 在注册 Bean 时进行条件判断
        }

        /**
         * 判断是否存在资源服务器配置
         */
        @ConditionalOnBean(ResourceServerConfiguration.class)
        static class Server {
        }

        /**
         * 判断是否存在 OAuth2 客户端配置
         */
        @ConditionalOnBean(OAuth2ClientConfiguration.class)
        static class Client {
        }

    }

}
