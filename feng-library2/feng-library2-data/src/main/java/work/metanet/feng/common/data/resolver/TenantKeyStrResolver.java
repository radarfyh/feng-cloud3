package work.metanet.feng.common.data.resolver;

import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.data.tenant.TenantContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户字符串处理，方便其他模块获取租户信息
 * <p>
 * 该类用于提供租户信息的获取方法，包括生成租户相关的缓存键以及获取当前租户ID、客户端ID、用户名等。
 * </p>
 */
@Slf4j
public class TenantKeyStrResolver implements KeyStrResolver {

    /**
     * 传入字符串增加租户ID，并将其与提供的分隔符连接
     *
     * @param in    输入字符串
     * @param split 分割符
     * @return 增加租户编号后的字符串
     */
    @Override
    public String extract(String in, String split) {
        return CacheConstants.FENG_TENANT + split + in;
    }

    /**
     * 获取当前租户ID
     *
     * @return 当前租户ID，如果当前租户信息不可用，则返回null
     */
    @Override
    public String key() {
        return TenantContextHolder.getTenant();
    }
    
    /**
     * 获取当前客户端ID
     * <p>
     * 如果当前认证信息为OAuth2身份验证，则从认证信息中提取客户端ID；如果无法获取客户端ID，则返回null。
     * </p>
     * 
     * @return 当前客户端ID
     */
    @Override
    public String getClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            return auth2Authentication.getOAuth2Request().getClientId();
        }
        // 日志记录，便于排查问题
        log.warn("Authentication does not contain OAuth2Authentication, clientId could not be retrieved.");
        return null;
    }

    /**
     * 获取当前用户名
     * <p>
     * 如果当前认证信息不可用，返回null。如果无法从认证信息中获取用户名，应该抛出自定义异常或返回一个默认值。
     * </p>
     * 
     * @return 当前用户名
     */
    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // 返回默认值
            log.warn("Authentication is null, username could not be retrieved.");
            return null;
        }
        return authentication.getName();
    }

}
