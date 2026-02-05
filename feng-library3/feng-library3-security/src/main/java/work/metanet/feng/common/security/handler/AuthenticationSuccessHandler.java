package work.metanet.feng.common.security.handler;

import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义认证成功处理接口，用于处理用户认证成功后的业务逻辑。
 * <p>
 * 该接口主要用于在用户成功认证后执行特定的业务操作，例如生成并返回认证令牌、更新用户状态、记录日志等。
 * </p>
 * <p>
 * 实现此接口的类可以根据具体需求自定义认证成功后的后续处理逻辑。
 * </p>
 */
public interface AuthenticationSuccessHandler {

    /**
     * 处理认证成功后的业务逻辑。
     * <p>
     * 该方法在用户认证成功后被调用，通常用于生成和返回认证令牌，更新用户状态，
     * 或执行其他认证成功后的操作。
     * </p>
     * 
     * @param authentication 认证信息，包含成功认证的用户信息。
     * @param request 请求信息，包含客户端的请求信息，如IP地址、请求参数等。
     * @param response 响应信息，可以用于设置响应状态码、返回认证令牌等。
     */
    void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response);
}
