package work.metanet.feng.admin.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.admin.api.entity.SysRouteConf;
import work.metanet.feng.admin.service.SysRouteConfService;
import work.metanet.feng.common.core.exception.RouteLoadException;
import work.metanet.feng.common.gateway.service.RouteDefinitionService;
import work.metanet.feng.admin.util.DynamicRouteUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteDefinitionServiceImpl implements RouteDefinitionService {

    private final SysRouteConfService sysRouteConfService;
    
    @Override
    public List<RouteDefinition> getActiveRouteDefinitions(boolean includeSwagger) {
        try {
            List<SysRouteConf> routes = sysRouteConfService.listByScope(
                new QueryWrapper<SysRouteConf>()
                    .eq("del_flag", "0")
                    .orderByAsc("sort_order"));
            
            return routes.stream()
                .filter(route -> includeSwagger || !DynamicRouteUtil.isSwaggerRoute(route))
                .map(DynamicRouteUtil::convertToRouteDefinition)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取路由定义失败", e);
            throw new RouteLoadException("加载路由配置失败", e);
        }
    }

    @Override
    public Optional<RouteDefinition> getRouteDefinition(String routeId) {
        return Optional.ofNullable(sysRouteConfService.getById(routeId))
            .filter(conf -> "0".equals(conf.getDelFlag()))
            .map(DynamicRouteUtil::convertToRouteDefinition);
    }
}
