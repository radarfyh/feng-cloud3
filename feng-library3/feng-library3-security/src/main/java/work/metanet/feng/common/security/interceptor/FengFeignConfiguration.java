package work.metanet.feng.common.security.interceptor;

import feign.Feign;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.commons.security.AccessTokenContextRelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * <p>
 * FengFeignConfiguration 类提供了对 Feign 客户端的配置增强，
 * 它会在 Feign 请求中自动添加 OAuth2 认证信息（如 token）。
 * </p>
 * 
 * <p>
 * 该类在 Feign 可用时加载，并且依赖于 OAuth2 的相关配置。
 * </p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(Feign.class)  // 当 Feign 类存在时加载该配置
public class FengFeignConfiguration {

    /**
     * 配置 OAuth2 Feign 请求拦截器。
     * <p>
     * 当配置了 OAuth2 客户端的相关信息（如 client-id）时，初始化 FengFeignClientInterceptor 拦截器，
     * 它将会在 Feign 请求中自动插入 OAuth2 token 进行认证。
     * </p>
     * 
     * @param oAuth2ClientContext OAuth2 客户端上下文，包含访问令牌等信息
     * @param resource OAuth2 受保护资源的配置
     * @param accessTokenContextRelay 用于复制 OAuth2 token 的上下文
     * @return RequestInterceptor 请求拦截器
     */
    @Bean
    @ConditionalOnProperty("security.oauth2.client.client-id")  // 当配置了 OAuth2 客户端 id 时加载该配置
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
                                                             OAuth2ProtectedResourceDetails resource,
                                                             AccessTokenContextRelay accessTokenContextRelay) {
        log.debug("Creating OAuth2 Feign Request Interceptor for client: {}", resource.getClientId());
        
        // 创建并返回 Feign 请求拦截器
        FengFeignClientInterceptor interceptor = new FengFeignClientInterceptor(oAuth2ClientContext, resource, accessTokenContextRelay);
        log.info("OAuth2 Feign Request Interceptor created successfully for client: {}", resource.getClientId());
        
        return interceptor;
    }
}
