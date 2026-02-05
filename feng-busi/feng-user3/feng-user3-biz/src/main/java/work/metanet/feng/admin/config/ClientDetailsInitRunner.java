package work.metanet.feng.admin.config;

import cn.hutool.core.util.StrUtil;
import work.metanet.feng.admin.service.SysOauthClientDetailsService;
import work.metanet.feng.admin.service.SysOrganService;
import work.metanet.feng.admin.service.SysTenantService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.data.tenant.TenantBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author edison
 * @date 2020/11/18
 * <p>
 * oauth 客户端认证参数初始化
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class ClientDetailsInitRunner implements InitializingBean {

    private final SysOauthClientDetailsService clientDetailsService;

    private final RedisMessageListenerContainer listenerContainer;

    private final SysTenantService sysTenantService;

    private final RedisTemplate redisTemplate;

    @Async
    @Order
    @EventListener({WebServerInitializedEvent.class, ClientDetailsInitEvent.class})
    public void initClientDetails() {
        log.info("初始化客户端信息开始 ");

        // 1. 查询全部租户循环遍历
        sysTenantService.list().forEach(tenant -> {
        	String tenantId = tenant.getId().toString();
        	
            TenantBroker.runAs(tenantId, (id) -> {
                try {
	            	// 2. 查询当前租户的所有客户端信息 (排除客户端扩展信息为空)
	                clientDetailsService.list().stream().filter(client -> {
	                    return StrUtil.isNotBlank(client.getAdditionalInformation());
	                }).forEach(client -> {
	                    // 3. 拼接key admin:client_config_flag:clinetId
	                    String key = String.format("%s:%s", CacheConstants.CLIENT_FLAG, client.getClientId());
	                    // 4. hashkey clientId 保存客户端信息
	                    redisTemplate.opsForValue().set(key, client.getAdditionalInformation());
	                    
	                    log.info("初始化客户端信息, key: {}, additional:{}, id: {}", key, client.getAdditionalInformation(), id);
	                });
                } catch (Exception e) {
                    log.error("Failed to init client details for tenant {}", tenantId, e);
                }
            });
        });

        log.info("初始化客户端信息结束 ");
    }

    /**
     * 客户端刷新事件
     */
    public static class ClientDetailsInitEvent extends ApplicationEvent {

        public ClientDetailsInitEvent(Object source) {
            super(source);
        }

    }

    /**
     * redis 监听配置,监听 user2_redis_client_reload_topic,重新加载Redis
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        listenerContainer.addMessageListener((message, bytes) -> {
            log.warn("接收到重新Redis 重新加载客户端配置事件");
            initClientDetails();
        }, new ChannelTopic(CacheConstants.CLIENT_REDIS_RELOAD_TOPIC));
    }

}
