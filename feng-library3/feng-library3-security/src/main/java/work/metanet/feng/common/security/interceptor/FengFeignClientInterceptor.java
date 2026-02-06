// package work.metanet.feng.common.security.interceptor;

// import cn.hutool.core.collection.CollUtil;
// import work.metanet.feng.common.core.constant.SecurityConstants;
// import feign.RequestInterceptor;
// import feign.RequestTemplate;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// import org.springframework.stereotype.Component;

// import java.util.Collection;

// @Slf4j
// @Component
// public class FengFeignClientInterceptor implements RequestInterceptor {

//     @Override
//     public void apply(RequestTemplate template) {
//         // 1. 保留原有的FROM头检查逻辑
//         Collection<String> fromHeader = template.headers().get(SecurityConstants.FROM);
//         if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_IN)) {
//             log.debug("Skipping token propagation as 'FROM' header contains '{}'", SecurityConstants.FROM_IN);
//             return;
//         }

//         // 2. 使用Spring Security 6新API获取当前认证信息
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
//         if (authentication instanceof JwtAuthenticationToken) {
//             Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
//             String tokenValue = jwt.getTokenValue();
            
//             // 3. 将令牌添加到Feign请求头
//             template.header("Authorization", "Bearer " + tokenValue);
//             log.debug("Successfully propagated OAuth2 Token to Feign request");
//         } else {
//             // 4. 根据您的架构，这里可能是其他类型的认证（如服务间认证）
//             log.warn("Current authentication is not a JWT. No token will be propagated for Feign call.");
//         }
//     }
// }