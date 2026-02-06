package work.metanet.feng.common.security.handler;

import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.web.util.UriComponentsBuilder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 增强认证成功回调处理，增加租户上下文信息，避免在某些极端情况下丢失。
 * 继承自SimpleUrlAuthenticationSuccessHandler，重写onAuthenticationSuccess方法。
 * 
 * 主要逻辑：
 * 1. 如果有保存的请求，跳转至保存的目标URL。
 * 2. 如果没有保存的请求，跳转到默认目标URL。
 * 3. 添加租户信息（如tenantId）到目标URL。
 */
@Slf4j
public class TenantSavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    public TenantSavedRequestAwareAuthenticationSuccessHandler() {
    }

    /**
     * 认证成功后的回调处理。
     * 1. 检查是否有保存的请求，若存在，则跳转至该请求的URL。
     * 2. 若没有保存的请求，则跳转至默认URL，并在URL中附加租户信息。
     * 
     * @param request 当前的HttpServletRequest
     * @param response 当前的HttpServletResponse
     * @param authentication 当前的Authentication对象
     * @throws ServletException 如果处理请求时发生Servlet异常
     * @throws IOException 如果发生IO异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws ServletException, IOException {

        // 获取保存的请求
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        
        if (savedRequest == null) {
            log.info("No saved request found, redirecting to default target URL.");
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 如果设置了总是使用默认目标URL，则忽略保存的请求，直接跳转到默认目标URL
        if (isAlwaysUseDefaultTargetUrl()) {
            log.info("Redirecting to default target URL.");
            this.requestCache.removeRequest(request, response);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 清除认证信息
        this.clearAuthenticationAttributes(request);

        // 获取保存请求的重定向URL
        String targetUrl = savedRequest.getRedirectUrl();

        // 获取租户信息，并将其安全地附加到目标URL
        try {
            KeyStrResolver keyStrResolver = SpringContextHolder.getBean(KeyStrResolver.class);
            String tenantId = keyStrResolver.key();

            // 使用 UriComponentsBuilder 安全地构建URL（自动处理参数分隔和编码）
            String finalRedirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam(CommonConstants.TENANT_ID, tenantId)
                    .build()
                    .toUriString();

            log.info("Redirecting to URL: {}", finalRedirectUrl);
            this.getRedirectStrategy().sendRedirect(request, response, finalRedirectUrl);
        } catch (Exception e) {
            log.error("Error while adding tenant information to redirect URL.", e);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
