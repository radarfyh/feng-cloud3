package ltd.huntinginfo.feng.gateway.config;

import ltd.huntinginfo.feng.gateway.filter.FengRequestGlobalFilter;
import ltd.huntinginfo.feng.gateway.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * 网关配置类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

	/**
	 * 创建PigRequest全局过滤器
	 * @return PigRequest全局过滤器
	 */
	@Bean
	public FengRequestGlobalFilter fengRequestGlobalFilter() {
		return new FengRequestGlobalFilter();
	}

	/**
	 * 创建全局异常处理程序
	 * @param objectMapper 对象映射器
	 * @return 全局异常处理程序
	 */
	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

}
