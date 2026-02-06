package work.metanet.feng.common.security.listener;

import cn.hutool.core.collection.CollUtil;
import work.metanet.feng.common.security.handler.AuthenticationLogoutHandler;
import work.metanet.feng.common.security.service.FengUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken; // 新增导入
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 退出事件监听器，监听用户注销事件并执行相应的处理逻辑。
 * <p>
 * 该监听器在用户成功注销后触发，执行注销后的清理逻辑，适用于 JWT 认证的用户。
 * </p>
 */
@Slf4j
public class AuthenticationLogoutSuccessEventListener implements ApplicationListener<LogoutSuccessEvent> {

    @Autowired(required = false)
    private AuthenticationLogoutHandler logoutHandler;

    /**
     * 监听用户注销成功事件并执行注销操作。
     * <p>
     * 如果事件为 JWT 认证类型，则执行注销处理逻辑，调用 {@link AuthenticationLogoutHandler} 处理器进行清理。
     * </p>
     * 
     * @param event 注销成功事件
     */
    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        Authentication authentication = event.getAuthentication(); // 直接获取认证对象
        
        // 可选：增加调试日志，查看认证类型
        log.debug("Logout event received. Authentication type: {}", authentication.getClass().getName());
        
        // 如果注销处理器存在且是用户认证类型，则执行注销逻辑
        if (logoutHandler != null && isUserAuthentication(authentication)) {
            // 获取请求和响应对象
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                log.warn("Request attributes are null. This logout might have occurred outside of a web request context.");
                // 可以考虑不直接返回，而是尝试只传递认证信息，不传递请求/响应
                // 这取决于您的 AuthenticationLogoutHandler 实现
                return;
            }
            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();

            try {
                log.info("Processing logout success event...");
                // 注意：这里假设 AuthenticationLogoutHandler 的 handle 方法已适配为接收 Authentication 参数
                logoutHandler.handle(authentication, request, response);
                log.info("Logout success processing completed.");
            } catch (Exception e) {
                log.error("Logout success processing failed: {}", e.getMessage(), e);
            }
        } else {
            log.debug("No valid logout handler or not a user authentication event, skipping.");
        }
    }

    /**
     * 判断当前认证是否为用户认证类型。
     * <p>
     * 如果认证的主体是 FengUser 类型，或者有角色信息，则认为是用户认证。
     * </p>
     * 
     * @param authentication 当前认证信息
     * @return true 如果是用户认证类型，false 否则
     */
    private boolean isUserAuthentication(Authentication authentication) {
        // 检查认证主体是否为 FengUser 类型
        if (authentication.getPrincipal() instanceof FengUser) {
            return true;
        }
        // 如果有授权信息，则认为是用户认证
        return CollUtil.isNotEmpty(authentication.getAuthorities());
    }
}