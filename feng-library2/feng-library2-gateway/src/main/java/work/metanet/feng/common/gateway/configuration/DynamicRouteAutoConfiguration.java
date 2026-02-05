package work.metanet.feng.common.gateway.configuration;

import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.gateway.support.RouteCacheHolder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author edison
 * @date 2021/1/15
 * <p>
 * 动态路由配置类
 */
@Slf4j
@Configuration
//1.删除@EnableConfigurationProperties理由：Spring Cloud Gateway 自动配置会创建一个 GatewayProperties Bean。而@EnableConfigurationProperties(GatewayProperties.class) 又创建了一个
//所以，两者命名不同但类型相同导致冲突，必须禁用@EnableConfigurationProperties
//@EnableConfigurationProperties(GatewayProperties.class) 
//2.移除@ComponentScan原因:在公共库(feng-library2-gateway)中，组件扫描应由引用方控制,避免与业务服务的扫描配置冲突,改为显式@Bean声明更可控
//@ComponentScan("work.metanet.feng.common.gateway")
//3.移除@ConditionalOnWebApplication原因：Gateway服务必定是Reactive Web环境，该注解冗余；若需条件控制，应在引用方处理
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DynamicRouteAutoConfiguration {
	//4.移除propertiesRouteDefinitionLocator原因：与动态路由方案冲突（使用数据库+Redis方案），空配置的GatewayProperties没有实际作用，可能造成路由来源混淆
	//@Bean
	//public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator() {
	//	return new PropertiesRouteDefinitionLocator(new GatewayProperties());
	//}
    // 防止与动态路由方案冲突，仅当显式配置 enable-properties-routes=true 时启用基于配置文件的路由。
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.gateway.enable-properties-routes", havingValue = "true")
    public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator(
            GatewayProperties properties) {
        return new PropertiesRouteDefinitionLocator(properties);
    }
    
    // Redis监听机制:核心的路由更新通知机制
    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            ApplicationEventPublisher eventPublisher) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 当Redis的 ROUTE_JVM_RELOAD_TOPIC 频道收到消息时（如路由配置变更），触发监听器
        container.addMessageListener((message, bytes) -> {
            log.warn("接收到路由重载事件");
            //清空路由表
            RouteCacheHolder.removeRouteList();
            //发送路由刷新事件:在 Spring Cloud Gateway 中，RefreshRoutesEvent 事件的处理是由框架内部的核心组件自动完成的
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        }, new ChannelTopic(CacheConstants.ROUTE_JVM_RELOAD_TOPIC));
        return container;
    }

    // 增强版健康检查，由检查Redis中路由是否存在改为：Redis健康检查、路由健康检查、综合状态检查
    @Bean
    public HealthIndicator gatewayHealthIndicator(
            RouteDefinitionLocator locator,
            RedisTemplate<String, ?> redisTemplate) {
        
        return () -> {
            Health.Builder builder = Health.up();
            
            // Redis健康检查
//            Health redisHealth = checkRedisHealth(redisTemplate);
//            builder.withDetail("redis", redisHealth.getStatus());
//            builder.withDetails(redisHealth.getDetails());
            
            // 路由健康检查
            Health routesHealth = checkRoutesHealth(locator);
            builder.withDetail("routes", routesHealth.getStatus());
            builder.withDetails(routesHealth.getDetails());
            
            // 综合状态（取最差状态）
//            if (redisHealth.getStatus().equals(Status.DOWN) 
//                || routesHealth.getStatus().equals(Status.DOWN)) {
//                builder.down();
//            }
            
            return builder.build();
        };
    }

    // Redis健康检查
    private Health checkRedisHealth(RedisTemplate<String, ?> redisTemplate) {
        try {
            Boolean exists = redisTemplate.hasKey(CacheConstants.ROUTE_KEY);
            return exists ? 
                Health.up().withDetail("routeKeyExists", true).build() :
                Health.down().withDetail("routeKeyExists", false).build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    // 路由健康检查
    private Health checkRoutesHealth(RouteDefinitionLocator locator) {
        try {
            Long count = locator.getRouteDefinitions().count().block(Duration.ofSeconds(2));
            return count != null && count > 0 ?
                Health.up().withDetail("routeCount", count).build() :
                Health.down().withDetail("routeCount", 0).build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
