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

package ltd.huntinginfo.feng.common.security.annotation;

import ltd.huntinginfo.feng.common.security.component.FengResourceServerAutoConfiguration;
import ltd.huntinginfo.feng.common.security.component.FengResourceServerConfiguration;
import ltd.huntinginfo.feng.common.security.feign.FengFeignClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Feng资源服务器注解
 * <p>
 * 通过导入相关配置类启用Feng资源服务器功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import({ FengResourceServerAutoConfiguration.class, FengResourceServerConfiguration.class,
		FengFeignClientConfiguration.class })
public @interface EnableFengResourceServer {

}
