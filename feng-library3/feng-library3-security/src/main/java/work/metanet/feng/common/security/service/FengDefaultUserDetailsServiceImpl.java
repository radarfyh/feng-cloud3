package work.metanet.feng.common.security.service;

import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.RetOps;
import work.metanet.feng.common.security.component.FengPreAuthenticationChecks;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cn.hutool.json.JSONUtil;

import java.util.Objects;

/**
 * 默认的用户详细信息服务实现类，用于处理基于用户名的用户加载逻辑。
 * 该实现通过从缓存中获取用户信息，若缓存中不存在则通过远程服务加载用户数据。
 * 如果用户数据不为空，会对用户信息进行有效性检查，并缓存用户数据以提升性能。
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class FengDefaultUserDetailsServiceImpl implements FengUserDetailsService {

    private final RemoteUserService remoteUserService;
    private final CacheManager cacheManager;
    private final UserDetailsChecker detailsChecker; // 改为通过构造函数注入

    // 如果FengPreAuthenticationChecks是唯一的实现，可以这样注入
    // 如果有多个实现，可以通过@Qualifier指定
    public FengDefaultUserDetailsServiceImpl(RemoteUserService remoteUserService, 
                                             CacheManager cacheManager, 
                                             FengPreAuthenticationChecks detailsChecker) {
        this.remoteUserService = remoteUserService;
        this.cacheManager = cacheManager;
        this.detailsChecker = detailsChecker;
    }

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        // 先从缓存中获取用户信息
        UserDetails userDetails = getUserDetailsFromCache(username);
        
        // 如果缓存中不存在，则从远程服务加载用户信息
        if (userDetails == null) {
            userDetails = loadUserFromRemoteService(username);
        }
        
        log.debug("{}-->userDetails: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), userDetails);

        // 检查用户账号的状态（例如是否被禁用、过期等）
        if (detailsChecker != null) {
            detailsChecker.check(userDetails);
        }

        // 将用户信息存入缓存
        cacheUserDetails(username, userDetails);
        
        return userDetails;
    }

    /**
     * 从缓存中获取用户详细信息。
     *
     * @param username 用户名
     * @return 用户详细信息
     */
    private UserDetails getUserDetailsFromCache(String username) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(username) != null) {
            return (FengUser) cache.get(username).get();
        }
        return null;
    }

    /**
     * 从远程服务加载用户信息并进行处理。
     *
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户不存在，抛出此异常
     */
    private UserDetails loadUserFromRemoteService(String username) throws UsernameNotFoundException {
    	log.debug("loadUserFromRemoteService-->{},{}", username, SecurityConstants.FROM_IN);
    	
    	R<UserInfo> result = remoteUserService.info(username, SecurityConstants.FROM_IN);
    	log.debug("loadUserFromRemoteService-->{},{}", username, JSONUtil.toJsonStr(result.getData()));
    	
        if (result.getData() == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return getUserDetails(result);
    }

    /**
     * 将加载的用户信息存入缓存。
     *
     * @param username 用户名
     * @param userDetails 用户详细信息
     */
    private void cacheUserDetails(String username, UserDetails userDetails) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null) {
            cache.put(username, userDetails);
        }
    }

    /**
     * 获取执行顺序，确保该实现优先于其他用户服务加载。
     *
     * @return 返回一个非常小的整数，以确保此实现优先执行
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
