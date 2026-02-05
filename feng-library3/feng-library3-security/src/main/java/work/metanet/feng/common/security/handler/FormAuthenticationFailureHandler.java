package work.metanet.feng.common.security.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import work.metanet.feng.common.core.util.WebUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 表单登录失败处理逻辑
 * <p>
 * 该类实现了 AuthenticationFailureHandler 接口，在认证失败时进行处理。
 * 主要用于捕获表单登录失败的情况，并做出相应的处理，例如跳转到登录页面并附加错误信息。
 * </p>
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 当认证失败时调用该方法。
     * <p>
     * 该方法用于捕获表单登录失败的情况，记录失败信息，并通过重定向返回错误信息。
     * </p>
     * 
     * @param request 认证失败时的请求
     * @param response 认证失败后的响应
     * @param exception 认证失败抛出的异常，包含失败原因
     */
    @Override
    @SneakyThrows
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        
        // 记录认证失败的详细信息
        log.debug("表单登录失败:{}", exception.getLocalizedMessage());

        // 构建登录失败后的重定向 URL，附带错误信息
        String errorMessage = exception.getMessage();
        String encodedUrl = HttpUtil.encodeParams(String.format("/token/login?error=%s", errorMessage), 
                CharsetUtil.CHARSET_UTF_8);

        // 使用 WebUtils 获取当前响应并进行重定向
        WebUtils.getResponse().sendRedirect(encodedUrl);
    }
}
