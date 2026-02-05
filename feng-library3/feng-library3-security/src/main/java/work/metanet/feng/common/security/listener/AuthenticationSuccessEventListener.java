package work.metanet.feng.common.security.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.security.handler.AuthenticationSuccessHandler;
import work.metanet.feng.common.security.service.FengUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 认证成功事件监听器，监听用户认证成功并执行后续处理。
 * <p>
 * 该监听器会在用户认证成功后触发，并执行所有注册的认证成功处理逻辑，例如记录日志、更新用户信息等。
 * </p>
 */
@Slf4j
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired(required = false)
    private List<AuthenticationSuccessHandler> successHandlerList;

    /**
     * 监听认证成功事件，执行认证成功后的处理逻辑。
     * <p>
     * 在用户认证成功后，遍历所有注册的 `AuthenticationSuccessHandler` 实现，执行相关的处理逻辑。
     * </p>
     * 
     * @param event 认证成功事件
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = (Authentication) event.getSource();

        // 判断是否存在处理器且认证信息是用户认证
        if (CollUtil.isNotEmpty(successHandlerList) && isUserAuthentication(authentication)) {
            // 获取当前请求和响应对象
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                log.error("请求属性为空，无法获取 HttpServletRequest 和 HttpServletResponse，跳过成功处理。");
                return;
            }

            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();

            // 处理认证成功事件
            try {
                for (AuthenticationSuccessHandler successHandler : successHandlerList) {
                    log.info("执行认证成功处理器: {}", request.getRequestURL());
                    successHandler.handle(authentication, request, response);
                }
                log.info("所有认证成功处理器已执行完毕。");
            } catch (Exception e) {
                log.error("认证成功处理失败: {}", e.getMessage());
            }
        } else {
//            log.info("无有效的认证成功处理器，或不是用户认证类型的事件，跳过处理。{}", JSONUtil.toJsonStr(authentication));
        }
    }

    /**
     * 判断当前认证是否为用户认证
     * <p>
     * 如果认证的主体是 FengUser 类型，或者认证信息中有有效的权限信息，则认为是用户认证。
     * </p>
     * 
     * @param authentication 当前认证信息
     * @return true 如果是用户认证，false 否则
     */
    private boolean isUserAuthentication(Authentication authentication) {
        return authentication.getPrincipal() instanceof FengUser
                || CollUtil.isNotEmpty(authentication.getAuthorities());
    }
}
