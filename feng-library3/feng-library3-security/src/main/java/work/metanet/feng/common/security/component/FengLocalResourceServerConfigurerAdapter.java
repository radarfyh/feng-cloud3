package work.metanet.feng.common.security.component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 支持本地模式（不经过认证中心 CheckToken）的资源服务器配置。
 * <p>
 * 本类配置了 OAuth2 资源服务器的基本认证功能，支持在不依赖认证中心的情况下，直接通过本地方式进行令牌验证。
 * 主要功能：
 * - 配置允许的接口（无需认证）
 * - 配置认证失败时的处理方式
 * - 配置本地令牌提取和验证服务
 * </p>
 */
@Slf4j
public class FengLocalResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Autowired
    protected AuthenticationEntryPoint resourceAuthExceptionEntryPoint;

    @Autowired
    private PermitAllUrlResolver permitAllUrlResolver;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    /**
     * 配置 HTTP 安全策略，指定哪些 URL 无需认证，哪些需要认证。
     * 允许通过 iframe 嵌套以避免 swagger-ui 无法加载。
     * <p>
     * 该方法配置了允许访问的 URL（通过 PermitAllUrlResolver 解析），
     * 以及针对其它请求的认证规则，禁用 CSRF 防护，并设置跨域资源共享。
     * </p>
     * 
     * @param httpSecurity HTTP 安全配置
     */
    @Override
    @SneakyThrows
    public void configure(HttpSecurity httpSecurity) {
        // 允许使用 iframe 嵌套，解决 Swagger UI 加载问题
        httpSecurity.headers().frameOptions().disable();

        // 配置 URL 请求权限
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();

        // 配置无需认证的接口
        permitAllUrlResolver.registry(registry);

        // 默认配置：任何请求都需要认证
        registry.anyRequest().authenticated()
                .and()
                .csrf().disable();  // 禁用 CSRF 防护
    }

    /**
     * 配置资源服务器的认证和令牌验证
     * <p>
     * 该方法配置了资源服务器的认证入口、令牌提取器以及资源服务器令牌服务。
     * </p>
     * 
     * @param resources 资源服务器配置
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
                .tokenExtractor(tokenExtractor)
                .tokenServices(resourceServerTokenServices);
    }
}
