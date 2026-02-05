package work.metanet.feng.common.security.interceptor;

import cn.hutool.core.collection.CollUtil;
import work.metanet.feng.common.core.constant.SecurityConstants;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.security.AccessTokenContextRelay;
import org.springframework.cloud.openfeign.security.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import java.util.Collection;

/**
 * 该拦截器扩展了 OAuth2FeignRequestInterceptor，主要用于在 Feign 请求中注入 OAuth2 token。
 * <p>
 * 它会检查请求头中的 "FROM" 字段，并基于当前的 OAuth2ClientContext 和 AccessTokenContextRelay 
 * 对请求模板进行适当的调整，确保请求携带正确的 OAuth2 令牌。
 * </p>
 */
@Slf4j
public class FengFeignClientInterceptor extends OAuth2FeignRequestInterceptor {

    private final OAuth2ClientContext oAuth2ClientContext;
    private final AccessTokenContextRelay accessTokenContextRelay;

    /**
     * 构造器，初始化 OAuth2 客户端上下文和 AccessTokenContextRelay
     * 
     * @param oAuth2ClientContext 提供的 OAuth2 客户端上下文
     * @param resource 需要访问的资源类型
     * @param accessTokenContextRelay 用于复制 token 的上下文
     */
    public FengFeignClientInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource,
                                      AccessTokenContextRelay accessTokenContextRelay) {
        super(oAuth2ClientContext, resource);
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.accessTokenContextRelay = accessTokenContextRelay;
    }

    /**
     * 拦截请求，检查 "FROM" 请求头并根据需要复制 OAuth2 令牌到请求头中。
     * 
     * @param template Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate template) {
        // 获取请求头中的 "FROM" 字段
        Collection<String> fromHeader = template.headers().get(SecurityConstants.FROM);
        
        // 如果 header 包含 FROM_IN，跳过处理
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_IN)) {
            log.debug("Skipping OAuth2 token application as 'FROM' header contains '{}'", SecurityConstants.FROM_IN);
            return;
        }

        // 复制 token 信息
        accessTokenContextRelay.copyToken();
        
        // 如果 OAuth2ClientContext 中存在有效的 AccessToken，执行 token 注入
        if (oAuth2ClientContext != null && oAuth2ClientContext.getAccessToken() != null) {
            log.debug("Applying OAuth2 token to the request template");
            super.apply(template);
        } else {
            log.warn("No valid OAuth2 token found in the OAuth2ClientContext");
        }
    }
}
