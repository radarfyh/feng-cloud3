package work.metanet.feng.common.security.handler;

import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义认证登出后置处理接口，处理用户退出后的业务逻辑。
 * <p>
 * 该接口用于在用户成功登出后执行特定的业务逻辑，例如清理缓存、记录日志、通知系统其他模块等。
 * </p>
 * <p>
 * 实现此接口的类需要定义具体的登出后处理逻辑。
 * </p>
 */
public interface AuthenticationLogoutHandler {

    /**
     * 处理用户登出后的业务逻辑。
     * <p>
     * 该方法将在用户成功登出后被调用，具体实现类可以根据业务需求进行相应的处理，
     * 如清理缓存、记录日志、更新用户会话状态等。
     * </p>
     * 
     * @param authentication 认证信息，包含已认证的用户信息。
     * @param request 请求信息，包含客户端请求的所有信息。
     * @param response 响应信息，可以用来设置响应状态码、返回成功消息等。
     */
    void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response);
}
