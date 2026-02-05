package work.metanet.feng.common.security.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 自定义 Bearer Token 提取器，用于跳过公开权限的请求进行校验
 * <p>
 * 该类继承自 {@link BearerTokenExtractor}，并扩展了 Bearer Token 提取的逻辑。
 * 对于配置为公开权限的 URL（由 {@link PermitAllUrlResolver} 提供），
 * 如果请求的路径和方法匹配该 URL，则跳过 Bearer Token 的提取。
 * 否则，调用父类的 {@code extract} 方法来提取 Token。
 * </p>
 * <p>
 * 主要用于在某些公开接口（如静态资源或公共 API）上跳过 Bearer Token 校验，避免不必要的身份验证。
 * </p>
 */
@Component
//@RequiredArgsConstructor
public class FengBearerTokenExtractor extends BearerTokenExtractor {

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final PermitAllUrlResolver permitAllUrlResolver;
    
    public FengBearerTokenExtractor(PermitAllUrlResolver permitAllUrlResolver) {
    	this.permitAllUrlResolver = permitAllUrlResolver;
    }

    /**
     * 提取请求中的 Bearer Token
     * <p>
     * 如果请求匹配配置中的公开 URL，直接跳过 Bearer Token 提取，
     * 否则，使用父类的 {@code extract} 方法进行 Bearer Token 的提取。
     * </p>
     *
     * @param request 当前 HTTP 请求
     * @return 如果匹配公开 URL，则返回 {@code null}，表示不需要提取 Token；
     *         否则，返回提取到的 Authentication 对象
     */
    @Override
    public Authentication extract(HttpServletRequest request) {
        // 1. 判断请求路径和方法是否匹配公开 URL 列表
        boolean result = permitAllUrlResolver.getIgnoreUrls().stream().anyMatch(url -> {
            // 解析 URL 和方法
            List<String> urlList = StrUtil.split(url, "|");
            // 判断请求路径是否匹配
            boolean match = pathMatcher.match(urlList.get(0), request.getRequestURI());
            // 判断请求方法是否匹配（如果 URL 配置中有方法）
            if (urlList.size() == 2) {
                List<String> methods = StrUtil.split(urlList.get(1), StrUtil.COMMA);
                return CollUtil.contains(methods, request.getMethod()) && match;
            }
            return match;
        });
        
        // 如果匹配公开 URL，跳过 Bearer Token 提取
        return result ? null : super.extract(request);
    }
}
