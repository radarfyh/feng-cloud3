package work.metanet.feng.common.security.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
public class FengLocalResourceServerConfigurerAdapter {

    @Autowired
    protected AuthenticationEntryPoint resourceAuthExceptionEntryPoint;

    @Autowired
    private PermitAllUrlResolver permitAllUrlResolver;


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
    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        // 允许使用 iframe 嵌套，解决 Swagger UI 加载问题
        httpSecurity.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));


        // 配置 URL 请求权限
        httpSecurity.authorizeHttpRequests(registry -> {
            // 配置无需认证的接口
            permitAllUrlResolver.registry(registry);
            // 默认配置：任何请求都需要认证
            registry.anyRequest().authenticated();
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable); // 禁用 CSRF 防护
        
       
        return httpSecurity.build();
    }

}
