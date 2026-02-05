package work.metanet.feng.common.gateway.support;

import org.springframework.context.ApplicationEvent;

/**
 * @author edison
 * @date 2021/1/15
 * <p>
 * 路由初始化事件
 */
public class DynamicRouteInitEvent extends ApplicationEvent {

	public DynamicRouteInitEvent(Object source) {
		super(source);
	}

}
