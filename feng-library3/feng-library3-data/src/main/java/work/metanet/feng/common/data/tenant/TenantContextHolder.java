package work.metanet.feng.common.data.tenant;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.ttl.TransmittableThreadLocal;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.data.cache.RedisAutoCacheManager;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.util.SecurityUtils;

/**
 * 租户上下文工具类，用于管理当前线程的租户信息。
 * <p>
 * 使用此工具类时需谨慎，避免直接调用 {@code setTenantId} 方法。推荐使用 {@code TenantBroker} 进行租户上下文切换。
 * </p>
 *
 * @author EdisonFeng
 * @date 2025/06/15
 */
@UtilityClass
@Slf4j
public class TenantContextHolder {
    // 通过 Spring 上下文持有器获取 RedisTemplate（静态类中注入）
    private static final StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);    
    // 使用 TransmittableThreadLocal，支持跨线程传递
    private static final ThreadLocal<String> TENANT_CONTEXT = new TransmittableThreadLocal<>();
    
    /**
     * 设置当前线程的租户 ID（租户ID）。
     * <p>
     * <b>谨慎使用此方法，避免在多层方法调用中滥用。建议通过 {@code TenantBroker} 进行租户切换管理。</b>
     * </p>
     *
     * @param tenantId 当前租户的租户ID
     */
    public void setTenant(String tenant) {
    	log.debug("setTenant:{}", tenant);
        TENANT_CONTEXT.set(tenant);
//        try {
//            if (StrUtil.isNotBlank(tenant)) {
//                redisTemplate.opsForValue().set(getRedisKey(), tenant, 2, TimeUnit.HOURS);
//            }
//        } catch (Exception e) {
//            log.warn("setTenant 存入 Redis 失败: {}", e.getMessage());
//        }
    }
    
    /**
     * 获取当前线程的租户 ID（租户ID）。
     * <p>
     * 如果当前线程未设置租户信息，则返回 {@code null}。可以考虑抛出异常或返回默认值以避免空指针异常。
     * </p>
     *
     * @return 当前租户的租户ID
     */
    public String getTenant() {
    	String tenant = TENANT_CONTEXT.get();
    	if (StrUtil.isNotBlank(tenant)) {
	    	log.debug("getTenant:{}", tenant);
	        return tenant;
    	}
//        // 尝试从当前请求中获取 header -> Redis
//        String tenantId = extractTenantIdFromHeader();
//        if (StrUtil.isBlank(tenantId)) {
//            String redisKey = getRedisKey();
//            tenantId = redisTemplate.opsForValue().get(redisKey);
//            if (StrUtil.isNotBlank(tenantId)) {
//                log.warn("getTenant recovered from Redis: {}", tenantId);
//                TENANT_CONTEXT.set(tenantId);
//                return tenantId;
//            }
//        }
//
//        log.warn("getTenant not found in ThreadLocal or Header or Redis");
        return null;
    }
    
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;

        return attrs.getRequest();
    }
    
    private String extractTenantIdFromHeader() {
        try {
            return SecurityUtils.resolveTenantIdSimple(getRequest());
        } catch (Exception e) {
            log.warn("extractTenantIdFromHeader error: {}", e.getMessage());
            return null;
        }
    }
    
    private String getRedisKey() {
        FengUser user = SecurityUtils.getUser(SecurityUtils.getAuthentication());
        String deptCode = "defaultDept";  // 默认值
        String username = "anonymous";    // 默认值

        if (user != null) {
            deptCode = user.getDeptCode();
            username = user.getUsername();
        } else {
            UserInfo info = SecurityUtils.resolveUserFromRedisToken(getRequest());
            if (info != null) {
                deptCode = info.getDeptCode();
                username = info.getSysUser().getUsername();
            }
        }

        // 使用更可靠的唯一标识（如请求ID）
        return String.format("%s:%s:%s:%s", 
            CacheConstants.FENG_TENANT, 
            deptCode, 
            username, 
            TENANT_CONTEXT.get());  // 替换线程ID
    }


    /**
     * 清除当前线程的租户信息。
     * <p>
     * 确保在每次请求处理结束后调用此方法，避免线程信息泄漏。
     * </p>
     */
    public void clear() {
    	TENANT_CONTEXT.remove();
    }
}
