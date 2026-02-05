package work.metanet.feng.common.gateway.annotation;

import work.metanet.feng.common.gateway.configuration.DynamicRouteAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author edison
 * @date 2021/1/15
 * <p>
 * 开启动态路由
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DynamicRouteAutoConfiguration.class)
public @interface EnableFengDynamicRoute {

}
