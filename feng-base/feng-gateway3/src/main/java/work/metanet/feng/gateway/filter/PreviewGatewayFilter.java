package work.metanet.feng.gateway.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

/**
 * 演示环境过滤处理
 */
@Slf4j
@Component
public class PreviewGatewayFilter extends AbstractGatewayFilterFactory {

    // 定义需要放行的token参数名常量
    private static final String TOKEN = "token";

    /**
     * 核心过滤方法实现
     * @param config 过滤器配置对象（本示例未使用）
     * @return GatewayFilter 网关过滤器实例
     */
    @Override
    public GatewayFilter apply(Object config) {
        // 返回一个Lambda表达式实现GatewayFilter接口
        return (exchange, chain) -> {
            // 从exchange中获取当前请求对象
            ServerHttpRequest request = exchange.getRequest();

            /**
             * 放行条件判断：
             * 1. 请求方法为GET时直接放行
             * 2. 请求路径中包含token参数时放行
             */
            if (StrUtil.equalsIgnoreCase(request.getMethodValue(), HttpMethod.GET.name())
                    || StrUtil.containsIgnoreCase(request.getURI().getPath(), TOKEN)) {
                // 满足条件时继续执行后续过滤器链
                return chain.filter(exchange);
            }

            /**
             * 拦截逻辑：
             * 1. 记录警告日志（演示环境禁止操作）
             * 2. 返回423 Locked状态码（HTTP 423表示资源被锁定）
             */
            log.warn("演示环境不能操作-> {},{}", request.getMethodValue(), request.getURI().getPath());
            
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.LOCKED); // 设置423状态码
            return response.setComplete(); // 直接返回响应，中断过滤器链
        };
    }
}
