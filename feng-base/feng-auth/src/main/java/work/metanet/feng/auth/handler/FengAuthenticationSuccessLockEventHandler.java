package work.metanet.feng.auth.handler;

import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.security.handler.AuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录成功后清除登录失败次数
 * <p>
 * 该类用于在用户登录成功后清除对应的登录失败次数锁定，允许用户重新登录。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengAuthenticationSuccessLockEventHandler implements AuthenticationSuccessHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final KeyStrResolver tenantKeyStrResolver;

    /**
     * 处理登录成功事件
     * <p>
     * 该方法用于在登录成功时清除用户之前的登录失败次数。
     * </p>
     * 
     * @param authentication 登录的 Authentication 对象
     * @param request 请求对象
     * @param response 响应对象
     */
    @Async
    @Override
    public void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        // 获取用户名
        String username = authentication.getName();
        String tenantKey = tenantKeyStrResolver.key();  // 获取租户信息

        if (tenantKey == null || tenantKey.isEmpty()) {
            log.warn("Tenant key is empty for user: {}", username);
        } else {
            // 生成Redis的Key，格式化为: LOGIN_ERROR_TIMES:{tenantKey}:{username}
            String key = String.format("%s:%s:%s", CacheConstants.LOGIN_ERROR_TIMES, tenantKey, username);

            try {
                // 删除登录失败次数的缓存
                redisTemplate.delete(key);
                log.info("Successfully cleared lock for user: {}", username);
            } catch (Exception e) {
                log.error("Error clearing lock for user: {}", username, e);
            }
        }
    }
}
