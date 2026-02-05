package work.metanet.feng.common.security.listener;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.security.handler.AuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 认证失败事件监听器，用于监听认证失败事件并处理失败逻辑。
 * <p>
 * 本监听器会在认证失败时，调用自定义的认证失败处理器链来执行自定义的失败处理逻辑，如记录日志、通知等。
 * 如果事件是 {@link AuthenticationFailureProviderNotFoundEvent} 类型的失败事件，则直接忽略。
 * </p>
 */
@Slf4j
public class AuthenticationFailureEventListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

	@Autowired(required = false)
	private List<AuthenticationFailureHandler> failureHandlerList;

	/**
	 * 处理认证失败事件。
	 * <p>
	 * 如果事件类型是 {@link AuthenticationFailureProviderNotFoundEvent}，则不做处理。否则，调用失败处理器链
	 * 来处理认证失败的异常。
	 * </p>
	 * 
	 * @param event 认证失败事件
	 */
	@Override
	public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
		// 如果是认证提供者未找到的失败事件，直接忽略
		if (event instanceof AuthenticationFailureProviderNotFoundEvent) {
			return;
		}

		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return; // 如果请求属性为空，直接返回，避免空指针异常
		}
		HttpServletRequest request = requestAttributes.getRequest();
		HttpServletResponse response = requestAttributes.getResponse();

		AuthenticationException authenticationException = event.getException();
		Authentication authentication = (Authentication) event.getSource();

		// 如果存在失败处理器列表，遍历并执行处理
		if (CollUtil.isNotEmpty(failureHandlerList)) {
			failureHandlerList.forEach(failureHandler -> {
				try {
					// 调用失败处理器处理失败逻辑
					failureHandler.handle(authenticationException, authentication, request, response);
				} catch (Exception e) {
					// 记录异常日志
					log.error("Error occurred while handling authentication failure", e);
				}
			});
		}
	}
}
