package work.metanet.feng.common.data.tenant;

import feign.RequestInterceptor;
import work.metanet.feng.common.core.util.JasyptUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * 租户信息拦截配置类
 * <p>
 * 该类包含了为 Feign 客户端和 Http 请求添加租户信息拦截器的配置。
 * <p>
 * 1. FengFeignTenantInterceptor 用于为 Feign 请求添加租户信息。
 * 2. TenantRequestInterceptor 用于为普通 Http 请求添加租户信息。
 * </p>
 * <p>
 * 此配置类用于在系统的每个请求中自动注入租户标识符（如租户ID），实现多租户的支持。
 * </p>
 *
 * @author edison
 * @date 2020/4/29
 */
@Configuration
public class FengTenantConfiguration {
    /**
     * 为 Feign 客户端配置租户信息拦截器
     * <p>
     * 该拦截器会在每次 Feign 请求时，自动在请求头中注入租户信息。
     * </p>
     *
     * @return FengFeignTenantInterceptor
     */
    @Bean
    public RequestInterceptor fengFeignTenantInterceptor() {
        return new FengFeignTenantInterceptor();
    }

    /**
     * 为普通的 HTTP 请求配置租户信息拦截器
     * <p>
     * 该拦截器会在每次 HTTP 请求时，自动注入租户信息。
     * </p>
     *
     * @return TenantRequestInterceptor
     */
    @Bean
    public ClientHttpRequestInterceptor fengTenantRequestInterceptor() {
        return new TenantRequestInterceptor();
    }
}
