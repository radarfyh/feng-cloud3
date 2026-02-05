package work.metanet.feng.admin.util;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.entity.SysRouteConf;
import work.metanet.feng.common.gateway.vo.RouteDefinitionVo;

/**
 * 数据库动态路由配置处理工具（sys_route_conf）
 */
public class DynamicRouteUtil {
	
	// 数据库sys_route_conf字段predicate转换为网关路由断言定义PredicateDefinition
    static public List<PredicateDefinition> toPredicateDefinition(Object predicates) {
    	try {
    		if (predicates.toString().trim().startsWith("[")) {
    			List<PredicateDefinition> predicateDefinitions = JSONUtil.toList(predicates.toString(), PredicateDefinition.class);
    			return predicateDefinitions;
    		}
    		else {
    			String jsonStrFilters = "[" + predicates.toString().trim() + "]";
    			List<PredicateDefinition> predicateDefinitions = JSONUtil.toList(jsonStrFilters, PredicateDefinition.class);
    			return predicateDefinitions;
    		}
    	} catch(Exception e) {
	        String jsonStrPredicates = JSONUtil.toJsonStr(predicates);
	        List<PredicateDefinition> predicateDefinitions = JSONUtil.toList(jsonStrPredicates, PredicateDefinition.class);
	        
	        return predicateDefinitions;
    	}
    }
    
    // 数据库sys_route_conf字段filter转换为网关路由过滤定义FilterDefinition
    static public List<FilterDefinition> toFilterDefinition(Object filters) {
        try {
        	if (filters.toString().trim().startsWith("[")) {
        		List<FilterDefinition> filterDefinitions = JSONUtil.toList(filters.toString(), FilterDefinition.class);
        		return filterDefinitions;
        	}
        	else {
        		String jsonStrFilters = "[" + filters.toString().trim() + "]";
        		List<FilterDefinition> filterDefinitions = JSONUtil.toList(jsonStrFilters, FilterDefinition.class);
        		return filterDefinitions;
        	}
        } catch(Exception e) {
        	String jsonStrFilters = JSONUtil.toJsonStr(filters);
        	List<FilterDefinition> filterDefinitions = JSONUtil.toList(jsonStrFilters, FilterDefinition.class);
        	return filterDefinitions;
        }
    }

    // 实体对象转换为网关路由定义VO
    static public RouteDefinitionVo convertToRouteDefinitionVo(SysRouteConf conf) {
    	RouteDefinitionVo definition = new RouteDefinitionVo();
        definition.setRouteName(conf.getRouteName());
        definition.setRouteId(conf.getRouteId());
        
        RouteDefinition routeDefinition = convertToRouteDefinition(conf);
        BeanUtil.copyProperties(routeDefinition, definition);
        
        return definition;
    }
    
    // 实体对象转换为网关路由定义
    static public RouteDefinition convertToRouteDefinition(SysRouteConf conf) {
    	RouteDefinition definition = new RouteDefinition();
        definition.setId(conf.getRouteId());
        definition.setUri(URI.create(conf.getUri()));
        definition.setOrder(conf.getSortOrder());
        
        // 防御性拷贝        
        definition.setPredicates(toPredicateDefinition(conf.getPredicates()));        

        definition.setFilters(toFilterDefinition(conf.getFilters()));
        
        if (StrUtil.isNotBlank(conf.getMetaData())) {
            definition.setMetadata(JSONUtil.toBean(conf.getMetaData(), Map.class));
        }
        
        return definition;
    }
    
    // 判断是否包含swagger路由、静态资源路由和Knife4j路径
    static public boolean shouldIncludeRoute(SysRouteConf route) {
        // 排除Swagger路由
        if (isSwaggerRoute(route)) {
            return false;
        }
        // 不排除静态资源路由
        if ("static-resources".equals(route.getRouteId())) {
            return true;
        }
        // 排除Knife4j相关路径
        if (route.getRouteId().contains("knife4j") || route.getRouteId().contains("doc")) {
            return false;
        }
        return true;
    }
    
    // 判断是否包含Swagger路由
    static public boolean isSwaggerRoute(SysRouteConf route) {
        if (route == null) return false;
        
        // 通过routeId判断
        if ("swagger-resources".equals(route.getRouteId()) 
            || "swagger-ui".equals(route.getRouteId())) {
            return true;
        }
        
        // 通过filters判断
        if (route.getFilters() != null) {
            return toFilterDefinition(route.getFilters()).stream()
                .anyMatch(filter -> filter.getName().contains("Swagger"));
        }
        
        return false;
    }
}
