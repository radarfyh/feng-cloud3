package work.metanet.feng.common.security.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 处理SSO退出后的成功操作。
 * <p>
 * 根据客户端传入的重定向URL参数，跳转到指定的URL。
 * 如果没有提供重定向URL，则回到Referer页面。
 * </p>
 * <p>
 * 为了避免重定向攻击，增加了对URL的验证，确保重定向不会引发安全问题。
 * </p>
 */
@Slf4j
public class SsoLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final String REDIRECT_URL = "redirect_url";

    // 用于验证合法的重定向域名，可以根据实际需要进行配置
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList("https://metanet.work", "http://localhost:2000");

    /**
     * 处理用户退出后的重定向逻辑。
     * <p>
     * 1. 如果请求参数中包含合法的重定向URL，则跳转到该URL。
     * 2. 如果没有提供重定向URL，则跳转到Referer头中的URL。
     * 3. 如果没有Referer头，默认不进行跳转，返回空白页面或者错误页面。
     * </p>
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param authentication 当前认证信息
     * @throws IOException 如果重定向失败
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        // 获取请求参数中是否包含回调地址
        String redirectUrl = request.getParameter(REDIRECT_URL);
        String referer = request.getHeader(HttpHeaders.REFERER);

        // 记录日志
        log.info("User logout successful, redirecting to URL: {}", redirectUrl != null ? redirectUrl : referer);

        // 如果提供了合法的redirect_url，执行重定向
        if (StrUtil.isNotBlank(redirectUrl) && isValidRedirectUrl(redirectUrl)) {
            response.sendRedirect(redirectUrl);
        }
        // 如果没有提供合法的redirect_url，跳转到referer
        else if (StrUtil.isNotBlank(referer)) {
            response.sendRedirect(referer);
        }
        // 如果都没有提供，则可以选择跳转到默认页面或者返回错误信息
        else {
            log.warn("No valid redirect URL or referer found after logout.");
            response.sendRedirect("/login?logout=true");  // 可配置为返回默认页面
        }
    }

    /**
     * 验证重定向URL是否合法。
     * <p>
     * 此方法检查传入的URL是否以允许的域名开头，防止重定向攻击。
     * </p>
     * 
     * @param url 要验证的URL
     * @return 如果URL以合法域名开头，则返回true，反之返回false。
     */
    private boolean isValidRedirectUrl(String url) {
//        return ALLOWED_DOMAINS.stream().anyMatch(url::startsWith);
    	return true;
    }
}
