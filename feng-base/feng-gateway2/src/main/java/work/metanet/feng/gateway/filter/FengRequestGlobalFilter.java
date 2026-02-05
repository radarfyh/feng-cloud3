package work.metanet.feng.gateway.filter;

import work.metanet.feng.common.core.constant.SecurityConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * 全局拦截器，作用所有的微服务
 * <p>
 * 1. 对请求头中参数进行处理 from 参数进行清洗 2. 重写StripPrefix = 1,支持全局
 * <p>
 * 支持swagger添加X-Forwarded-Prefix header （F SR2 已经支持，不需要自己维护）
 */
@Slf4j
@Component
public class FengRequestGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 全局过滤器核心方法
     * @param exchange 包含请求和响应的上下文对象
     * @param chain 过滤器链，用于继续执行后续过滤器
     * @return Mono<Void> 异步处理结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求头清洗：移除敏感头信息
        ServerHttpRequest request = exchange.getRequest().mutate()
                // 移除自定义的FROM头（防止内部服务冒充调用）
                .headers(httpHeaders -> httpHeaders.remove(SecurityConstants.FROM))
                .build();

        // 2. 路径重写处理（StripPrefix功能）
        // 保留原始请求URL（用于后续追踪）
        addOriginalRequestUrl(exchange, request.getURI());
        
        // 获取原始路径（如：/service-a/api/resource）
        String rawPath = request.getURI().getRawPath();
        
        // 路径重写逻辑：跳过第一级路径（如将/service-a/api/resource变为/api/resource）
        String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/"))
                .skip(1L)  // 跳过第一个路径段
                .collect(Collectors.joining("/"));
        
        // 构建新请求对象
        ServerHttpRequest newRequest = request.mutate()
                .path(newPath)  // 设置新路径
                .build();
        
        // 将新URI存入exchange属性（供后续过滤器使用）
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
        
        log.info("FengRequestGlobalFilter 路径重建{}-->{}", rawPath, newPath);
        
        // 3. 继续执行过滤器链（传入修改后的请求）
        return chain.filter(exchange.mutate()
                .request(newRequest.mutate().build())
                .build());
    }

    /**
     * 过滤器执行顺序
     * @return 优先级数值（数值越小优先级越高）
     */
    @Override
    public int getOrder() {
        // 设置为高优先级（-1000），确保在大多数过滤器之前执行
        return -1000; 
    }
}
