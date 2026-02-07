package ltd.huntinginfo.feng.common.feign.core;

import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * FengFeign 内部请求拦截器，用于处理 Feign 请求的 Token 校验
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class FengFeignInnerRequestInterceptor implements RequestInterceptor, Ordered {

	/**
	 * 为每个请求调用，使用提供的{@link RequestTemplate}方法添加数据
	 * @param template 请求模板
	 */
	@Override
	public void apply(RequestTemplate template) {
		Method method = template.methodMetadata().method();
		NoToken noToken = method.getAnnotation(NoToken.class);
		if (noToken != null) {
			template.header(SecurityConstants.FROM, SecurityConstants.FROM_IN);
		}
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

}
