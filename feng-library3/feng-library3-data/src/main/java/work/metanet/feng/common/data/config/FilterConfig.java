package work.metanet.feng.common.data.config;

//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import work.metanet.feng.common.data.tenant.WebRequestLoggingFilter;

/**
 * FilterConfig 配置类
 * <p>
 * 该类用于注册 Web 请求日志记录过滤器，拦截所有传入的请求并记录日志。
 * </p>
 * <p>
 * 通过使用 Spring Boot 的 FilterRegistrationBean 注册过滤器，可以灵活地配置过滤器的 URL 模式和顺序。
 * </p>
 * 全部注释，以便于解决SSE消息无法实时发送给客户端的问题，因为本配置类注册了 WebRequestLoggingFilter，并将其应用于所有 URL（/*），对 SSE 的响应流进行了处理，导致 SSE 无法正常工作 2025.3.19
 * 
 * @author edison
 */
//@Configuration  
public class FilterConfig {  
  
    /**
     * 注册 WebRequestLoggingFilter 过滤器
     * <p>
     * 此方法将 WebRequestLoggingFilter 注册为 Spring Bean，并指定拦截所有请求 URL（"/*"）。
     * </p>
     * <p>
     * 该过滤器用于记录 Web 请求的相关信息，有助于进行请求日志的分析。
     * </p>
     * 
     * @return FilterRegistrationBean 配置了 WebRequestLoggingFilter 的过滤器注册器
     */
	/*    @Bean  
    public FilterRegistrationBean<WebRequestLoggingFilter> timingFilterRegistration() {  
        FilterRegistrationBean<WebRequestLoggingFilter> registration = new FilterRegistrationBean<>();  
        registration.setFilter(new WebRequestLoggingFilter());  
        registration.addUrlPatterns("/*"); // 设置过滤器拦截的 URL 模式
        registration.setOrder(1); // 可选: 设置过滤器的执行顺序，数值越小，优先级越高
        return registration;  
    } */ 
}  
