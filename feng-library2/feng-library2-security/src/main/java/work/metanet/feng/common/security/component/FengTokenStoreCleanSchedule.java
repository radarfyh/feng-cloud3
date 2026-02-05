package work.metanet.feng.common.security.component;

import work.metanet.feng.common.core.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

/**
 * 定时清理过期的 Token 存储任务
 * <p>
 * 本类实现了定时任务，用于清理 Redis 中的过期 token。它依赖于 `FengRedisTokenStore`，并通过
 * 定时任务每小时运行一次清理操作。清理的目的是移除 Redis 中已经过期的令牌，释放存储空间。
 * </p>
 */
@Slf4j
@EnableScheduling  // 启用定时任务功能
@ConditionalOnBean(AuthorizationServerConfigurerAdapter.class)  // 确保在 AuthorizationServerConfigurerAdapter 存在时启动该类
public class FengTokenStoreCleanSchedule {

    /**
     * 每小时定时执行清理 Redis 中过期的 token
     * <p>
     * 该方法会每小时触发一次，对 Redis 中的过期 token 进行清理，并记录清理的数量。
     * 使用 Cron 表达式 @hourly 来设定定时任务，每小时执行一次。
     * </p>
     */
    @Scheduled(cron = "@hourly")  // Cron 表达式：每小时执行一次
    public void doMaintenance() {
        try {
            // 从 Spring 容器中获取 FengRedisTokenStore 实例
            FengRedisTokenStore tokenStore = SpringContextHolder.getBean(FengRedisTokenStore.class);
            if (tokenStore != null) {
                // 执行清理操作
                long maintenance = tokenStore.doMaintenance();
                log.debug("清理 Redis ZADD 过期 token 数量: {}", maintenance);  // 记录清理的 token 数量
            } else {
                log.warn("FengRedisTokenStore bean 未找到，无法执行定时清理任务");
            }
        } catch (Exception e) {
            log.error("定时清理任务执行失败：", e);  // 捕获并记录异常
        }
    }
}
