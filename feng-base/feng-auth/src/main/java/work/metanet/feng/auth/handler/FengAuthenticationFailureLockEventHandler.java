package work.metanet.feng.auth.handler;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.exception.FengAuth2Exception;
import work.metanet.feng.common.security.handler.AuthenticationFailureHandler;
import work.metanet.feng.common.security.service.FengUser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录失败锁定处理器
 * <p>
 * 该类用于处理登录失败事件，并在密码错误超过指定次数时锁定账号。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengAuthenticationFailureLockEventHandler implements AuthenticationFailureHandler {

    private static final String STATUS_LOCK = "1";
    private static final long DEFAULT_LOGIN_ERROR_TIMES = 5L;
    private static final long DEFAULT_LOCK_TIME_MINUTES = 30L;

    private final RedisTemplate<String, String> redisTemplate;
    private final KeyStrResolver tenantKeyStrResolver;
    private final CacheManager cacheManager;
    private final RemoteUserService remoteUserService;

    /**
     * 处理登录失败事件
     * <p>
     * 该方法用于处理登录失败事件，并在密码错误超过指定次数时锁定账号。
     * </p>
     *
     * @param authenticationException 认证异常
     * @param authentication 认证信息
     * @param request HTTP 请求
     * @param response HTTP 响应
     */
    @Async
    @Override
    @SneakyThrows
    public void handle(AuthenticationException authenticationException, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 只处理账号密码错误异常
            if (!(authenticationException instanceof BadCredentialsException)) {
                return;
            }
            String username = authentication.getName();
            
            log.debug("handle-->{},{}", username, SecurityConstants.FROM_IN);
            R<UserInfo> result = remoteUserService.info(username, SecurityConstants.FROM_IN);
            log.debug("handle-->{},{}", username, JSONUtil.toJsonStr(result.getData()));
            
            if (Objects.nonNull(result.getData())) {
                log.debug("用户{}登陆密码错误，进入登录操作次数锁定功能....", username);
                String key = String.format("%s:%s:%s", CacheConstants.LOGIN_ERROR_TIMES, tenantKeyStrResolver.key(), username);
                Long times = redisTemplate.opsForValue().increment(key);

                // 设置自动过期时间
                redisTemplate.expire(key, DEFAULT_LOCK_TIME_MINUTES, TimeUnit.MINUTES);

                if (DEFAULT_LOGIN_ERROR_TIMES <= times) {
                    log.info("密码错误超过5次，账号{}加锁", username);
                    
                    // 1、先查询redis是否有UserDetails
                    Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
                    UserDetails userDetails;
                    // 2、没有的话就查数据库
                    if (cache != null && cache.get(username) != null) {
                        FengUser user = (FengUser) cache.get(username).get();
                        user.setAccountNonLocked(false); // 5次密码错误锁定账号
                        userDetails = user;
                    } else {
                        userDetails = getUserDetails(result);
                    }
                    if (null != cache) {
                        cache.put(username, userDetails);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理登录失败事件错误", e);
        }
    }

    /**
     * 构建用户详情
     * <p>
     * 该方法用于根据用户信息构建 Spring Security 的 UserDetails 对象。
     * </p>
     *
     * @param result 用户信息
     * @return 用户详情
     * @throws UsernameNotFoundException 如果用户不存在
     * @throws FengAuth2Exception 如果用户被禁用
     */
    private UserDetails getUserDetails(R<UserInfo> result) {
        if (result == null || result.getData() == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        UserInfo info = result.getData();
        // 判断账号是否启用禁用
        if (info.getSysUser().getStatus().equals(CommonConstants.STATUS_USERNAME)) {
            throw new FengAuth2Exception("该账号已禁用，请联系管理员启用");
        }
        Set<String> dbAuthsSet = new HashSet<>();
        if (ArrayUtil.isNotEmpty(info.getRoles())) {
            // 获取角色
            Arrays.stream(info.getRoles()).forEach(roleId -> dbAuthsSet.add(SecurityConstants.ROLE + roleId));
            // 获取资源
            dbAuthsSet.addAll(Arrays.asList(info.getPermissions()));
            
            log.debug("{}-->dbAuthsSet:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(dbAuthsSet));
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();
        // 5次密码错误锁定账号
        user.setStatus(STATUS_LOCK);
        boolean enabled = StrUtil.equals(user.getStatus(), CommonConstants.STATUS_NORMAL);
        // 构造security用户
        return new FengUser(user.getId(), 
        		user.getUsername(), 
        		SecurityConstants.BCRYPT + user.getPassword(), 
        		user.getPhone(), 
        		user.getAvatar(), 
        		user.getTenantId(), 
        		user.getOrganCode(), 
        		user.getDeptId(), 
        		info.getDeptCode(), 
        		user.getFirstLogin(), 
        		true, 
        		true, 
        		true, 
        		!CommonConstants.STATUS_LOCK.equals(user.getStatus()), authorities);
    }
}