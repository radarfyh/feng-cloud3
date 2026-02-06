package work.metanet.feng.common.security.service;

import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * 用户详细信息服务，提供基于手机号登录和 check-token 校验功能的用户信息加载
 * <p>
 * 该类负责从缓存或远程服务加载用户信息，并且提供基于手机号的用户信息获取方式。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class FengAppUserDetailsServiceImpl implements FengUserDetailsService {

    private final UserDetailsService fengDefaultUserDetailsServiceImpl;
    private final RemoteUserService remoteUserService;
    private final CacheManager cacheManager;

    /**
     * 通过手机号加载用户信息
     * <p>
     * 首先检查缓存中是否存在用户信息，若存在则直接返回；如果缓存中不存在，则从远程服务获取用户信息并缓存。
     * </p>
     * 
     * @param phone 手机号
     * @return UserDetails 用户详细信息
     */
    @Override
    public UserDetails loadUserByUsername(String phone) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(phone) != null) {
            log.debug("从缓存中获取用户信息: {}", phone);
            return (FengUser) cache.get(phone).get();
        }

        try {
            // 从远程服务获取用户信息
            R<UserInfo> result = remoteUserService.social(phone, SecurityConstants.FROM_IN);
            UserDetails userDetails = getUserDetails(result);

            if (cache != null) {
                cache.put(phone, userDetails);
                log.debug("将用户信息缓存: {}", phone);
            }

            return userDetails;
        } catch (Exception e) {
            log.error("加载用户信息失败: {}", e.getMessage(), e);
            // 修改：抛出 Spring Security 标准异常
            throw new AuthenticationServiceException("加载用户信息失败", e);
        }
    }

    /**
     * 使用 check-token 时的用户加载方法
     * 
     * @param fengUser FengUser 实例
     * @return UserDetails 用户详细信息
     */
    @Override
    public UserDetails loadUserByUser(FengUser fengUser) {
        return fengDefaultUserDetailsServiceImpl.loadUserByUsername(fengUser.getUsername());
    }

    /**
     * 判断是否支持该客户端和授权类型
     * 
     * @param clientId 目标客户端
     * @param grantType 授权类型
     * @return true/false
     */
    @Override
    public boolean support(String clientId, String grantType) {
        return SecurityConstants.GRANT_MOBILE.equals(grantType);
    }
}
