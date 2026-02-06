package work.metanet.feng.common.security.component;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FengBearerTokenResolver implements BearerTokenResolver {
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final PermitAllUrlResolver permitAllUrlResolver;
    private final DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();
    
    @Override
    public String resolve(HttpServletRequest request) {
        // 检查是否在公开URL列表中
        for (String pattern : permitAllUrlResolver.getIgnoreUrls()) {
            if (pathMatcher.match(pattern, request.getRequestURI())) {
                return null; // 公开URL，不需要token
            }
        }
        return defaultResolver.resolve(request);
    }
}