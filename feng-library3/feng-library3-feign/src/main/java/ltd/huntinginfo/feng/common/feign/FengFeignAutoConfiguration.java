/*
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ltd.huntinginfo.feng.common.feign;

import org.springframework.cloud.openfeign.FengFeignClientsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ltd.huntinginfo.feng.common.feign.core.FengFeignInnerRequestInterceptor;
import ltd.huntinginfo.feng.common.feign.core.FengFeignRequestCloseInterceptor;

/**
 * Sentinel Feign 自动配置类
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Configuration(proxyBeanMethods = false)
@Import(FengFeignClientsRegistrar.class)
public class FengFeignAutoConfiguration {

	/**
	 * 创建并返回FengFeignRequestCloseInterceptor实例
	 * @return FengFeignRequestCloseInterceptor实例
	 */
	@Bean
	public FengFeignRequestCloseInterceptor fengFeignRequestCloseInterceptor() {
		return new FengFeignRequestCloseInterceptor();
	}

	/**
	 * 创建并返回FengFeignInnerRequestInterceptor实例
	 * @return FengFeignInnerRequestInterceptor 内部请求拦截器实例
	 */
	@Bean
	public FengFeignInnerRequestInterceptor fengFeignInnerRequestInterceptor() {
		return new FengFeignInnerRequestInterceptor();
	}

}
