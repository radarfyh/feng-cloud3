package work.metanet.feng.common.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义认证失败处理接口，处理认证失败时的业务逻辑。
 * <p>
 * 该接口用于在认证失败时执行特定的业务逻辑，例如记录日志、返回自定义的错误信息等。
 * </p>
 * <p>
 * 实现此接口的类需要定义具体的失败处理逻辑。
 * </p>
 */
public interface AuthenticationFailureHandler {

    /**
     * 处理认证失败的业务逻辑。
     * <p>
     * 该方法将在认证失败时被调用，具体实现类可以根据业务需求进行相应的处理，
     * 如记录日志、返回错误响应、向客户端展示友好的错误信息等。
     * </p>
     * 
     * @param authenticationException 认证异常，包含认证失败的详细信息。
     * @param authentication 认证信息，包含认证失败时的用户信息等。
     * @param request 请求信息，包含客户端请求的所有信息。
     * @param response 响应信息，可以用来设置响应状态码、返回错误信息等。
     * 
     * @throws Exception 如果发生处理失败的异常，可以抛出该异常供外部处理。
     */
    void handle(AuthenticationException authenticationException, Authentication authentication,
                HttpServletRequest request, HttpServletResponse response) throws Exception;
}
