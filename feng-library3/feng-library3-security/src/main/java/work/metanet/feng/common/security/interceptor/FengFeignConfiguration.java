// package work.metanet.feng.common.security.interceptor;

// import feign.Feign;
// import feign.RequestInterceptor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// // 注意：这里可能还需要注入其他Spring Security 6的管理器，如OAuth2AuthorizedClientManager

// /**
//  * 新架构下的Feign配置，用于传播JWT令牌。
//  */
// @Slf4j
// @Configuration
// @ConditionalOnClass(Feign.class)
// public class FengFeignConfiguration {

//     /**
//      * 配置一个全局的Feign请求拦截器，用于传播当前的安全上下文（JWT令牌）。
//      * 仅当当前认证类型为JWT（即资源服务器模式）时，此拦截器才生效。
//      */
//     @Bean
//     @ConditionalOnBean(JwtAuthenticationToken.class) // 示例性条件，可根据实际情况调整
//     public RequestInterceptor jwtFeignRequestInterceptor() {
//         log.info("Creating JWT Feign Request Interceptor for microservice calls.");
//         // 返回一个基于Spring Security 6新API的自定义拦截器
//         return new FengFeignClientInterceptor(); 
//     }
// }