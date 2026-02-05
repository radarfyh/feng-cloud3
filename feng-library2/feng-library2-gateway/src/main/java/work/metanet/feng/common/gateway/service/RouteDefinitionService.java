package work.metanet.feng.common.gateway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * 路由定义服务，未使用
 */
public interface RouteDefinitionService {
    /**
     * 获取活动路由定义
     * @param includeSwagger 是否包含Swagger路由（网关服务应传false）
     */
    List<RouteDefinition> getActiveRouteDefinitions(boolean includeSwagger);
    
    /**
     * 获取指定ID的路由定义
     */
    Optional<RouteDefinition> getRouteDefinition(String routeId);
    
    /**
     * 路由定义版本号（用于缓存控制）
     */
    default long getVersion() {
        return System.currentTimeMillis();
    }
}
