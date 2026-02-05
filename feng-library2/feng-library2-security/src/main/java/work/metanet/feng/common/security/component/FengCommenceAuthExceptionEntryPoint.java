package work.metanet.feng.common.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.FengSecurityMessageSourceUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源服务器异常处理类，处理 OAuth2 认证过程中的不同异常
 * <p>
 * 该类用于处理 {@link OAuth2AuthenticationProcessingFilter} 在认证失败时的异常。
 * 它根据不同的异常类型，返回自定义的错误信息并设置 HTTP 响应状态码。支持对认证过期、用户名不存在、凭证错误等多种错误进行细化处理。
 * </p>
 * <p>
 * 主要功能：
 * - 对不同的认证异常（如凭证过期、用户名未找到、认证失败等）设置不同的错误信息
 * - 将错误信息以 JSON 格式返回给客户端
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengCommenceAuthExceptionEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 处理认证失败时的异常
     * <p>
     * 该方法会根据不同类型的认证异常，设置相应的错误信息和 HTTP 状态码。
     * </p>
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param authException 抛出的认证异常
     * @throws IOException 如果写入响应时发生 I/O 错误
     */
    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        response.setCharacterEncoding(CommonConstants.UTF8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        R<String> result = new R<>();
        result.setMsg(authException.getMessage());
        result.setData(authException.getMessage());
        response.setStatus(BusinessEnum.WEB_UNAUTHORIZED.getCode());
        result.setCode(BusinessEnum.WEB_UNAUTHORIZED.getCode());

        // 处理具体的异常类型并设置对应的消息
        if (authException instanceof CredentialsExpiredException) {
            String msg = FengSecurityMessageSourceUtil.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired", authException.getMessage());
            result.setMsg(msg);
        }

        if (authException instanceof UsernameNotFoundException) {
            String msg = FengSecurityMessageSourceUtil.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.noopBindAccount", authException.getMessage());
            result.setMsg(msg);
        }

        if (authException instanceof BadCredentialsException) {
            String msg = FengSecurityMessageSourceUtil.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badClientCredentials", authException.getMessage());
            result.setMsg(msg);
        }

        if (authException instanceof InsufficientAuthenticationException) {
            String msg = FengSecurityMessageSourceUtil.getAccessor()
                    .getMessage("AbstractAccessDecisionManager.expireToken", authException.getMessage());
            response.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
            result.setMsg(msg);
        }
        
        // 打印响应头信息和响应内容
        Map<String, String> responseHeaders = new HashMap<>();
        for (String headerName : response.getHeaderNames()) {
            responseHeaders.put(headerName, response.getHeader(headerName));
        }
        log.debug("doFilter --> response headers: {}, result: {}", JSONUtil.toJsonStr(responseHeaders), JSONUtil.toJsonStr(result));
        
        // 返回错误信息给客户端
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));
    }
}
