
package work.metanet.feng.admin.config;

import work.metanet.feng.admin.api.entity.SysRouteConf;
import work.metanet.feng.admin.service.SysRouteConfService;
import work.metanet.feng.admin.util.DynamicRouteUtil;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.gateway.support.DynamicRouteInitEvent;
import work.metanet.feng.common.gateway.vo.RouteDefinitionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Async;

/**
 * @author edison
 * @date 2018/10/31
 * <p>
 * 容器启动后保存配置文件里面的路由信息到Redis
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamicRouteInitRunner implements InitializingBean {

    private final RedisTemplate<String, ?> redisTemplate;
    private final SysRouteConfService routeConfService;
    private final RedisMessageListenerContainer listenerContainer;

    // WebServerInitializedEvent在服务启动时触发
    // DynamicRouteInitEvent在DynamicRouteAutoConfiguration中触发：当Redis的 ROUTE_JVM_RELOAD_TOPIC 频道收到消息时（如路由配置变更），触发监听器
    @Async
    @Order
    @EventListener({ WebServerInitializedEvent.class, DynamicRouteInitEvent.class })
    public void initRoute() {
        try {
            // 重试最多3次
            int maxRetries = 3;
            int retryCount = 0;
            
            while (retryCount < maxRetries) {
                try {
                    // 清空旧路由
                    redisTemplate.delete(CacheConstants.ROUTE_KEY);
                    log.info("开始初始化网关路由，尝试 {} / {}", retryCount + 1, maxRetries);

                    // 从数据库加载有效路由,存储到Redis Hash结构
                    loadAndStoreRoutes();
                    
                    // 通知所有网关节点刷新
                    redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "路由信息,网关缓存更新");
                    log.info("初始化网关路由成功");
                    return;
                    
                } catch (Exception e) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    log.warn("路由初始化失败，准备重试...", e);
                    Thread.sleep(2000); // 等待2秒后重试
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            log.error("路由初始化被中断", e);
        } catch (Exception e) {
            log.error("初始化网关路由失败", e);
        }
    }

    // 处理路由
    private void loadAndStoreRoutes() {
        routeConfService.list().stream()
            .filter(DynamicRouteUtil::shouldIncludeRoute) // 使用新的过滤方法
            .forEach(this::saveRouteToRedis); // 保存路由到redis
    }
    
    // 保存路由到redis
    private void saveRouteToRedis(SysRouteConf route) {
        RouteDefinitionVo vo = DynamicRouteUtil.convertToRouteDefinitionVo(route);

        log.debug("加载路由ID：{},{}", route.getRouteId(), vo);
        
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
        redisTemplate.opsForHash().put(CacheConstants.ROUTE_KEY, route.getRouteId(), vo);
    } 
   
    // afterPropertiesSet()中监听ROUTE_REDIS_RELOAD_TOPIC频道
    @Override
    public void afterPropertiesSet() {
        listenerContainer.addMessageListener((message, bytes) -> {
            log.warn("接收到重新加载路由事件");
            initRoute();
        }, new ChannelTopic(CacheConstants.ROUTE_REDIS_RELOAD_TOPIC));
    }
}
