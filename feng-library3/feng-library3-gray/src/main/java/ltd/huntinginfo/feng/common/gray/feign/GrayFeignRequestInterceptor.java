package ltd.huntinginfo.feng.common.gray.feign;

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.util.WebUtils;
import ltd.huntinginfo.feng.common.gray.support.NonWebVersionContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author edison
 * @date 2020/1/12
 * <p>
 * feign 请求VERSION 传递
 */
@Slf4j
public class GrayFeignRequestInterceptor implements RequestInterceptor {

	/**
	 * Called for every request. Add data using methods on the supplied
	 * {@link RequestTemplate}.
	 * @param template
	 */
	@Override
	public void apply(RequestTemplate template) {
        // 从自定义上下文获取版本号
        String reqVersion = NonWebVersionContextHolder.getVersion();

		if (StrUtil.isNotBlank(reqVersion)) {
			log.debug("feign gray add header version :{}", reqVersion);
			template.header(CommonConstants.VERSION, reqVersion);
		}
	}

}
