package work.metanet.feng.common.security.component;

import work.metanet.feng.common.security.util.FengSecurityMessageSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * 前置身份验证检查类。
 * 用于检查用户账户是否已锁定、禁用或过期。
 */
@Slf4j
public class FengPreAuthenticationChecks implements UserDetailsChecker {

    // 使用MessageSourceAccessor来获取消息
    private static final MessageSourceAccessor messages = FengSecurityMessageSourceUtil.getAccessor();

    /**
     * 检查用户是否满足认证前提条件
     * @param user 用户详情
     * @throws LockedException 用户账户被锁定
     * @throws DisabledException 用户账户被禁用
     * @throws AccountExpiredException 用户账户已过期
     */
    @Override
    public void check(UserDetails user) {
        // 检查用户账户是否被锁定
        if (!user.isAccountNonLocked()) {
            log.warn("User account is locked: {}", user.getUsername());
            throw new LockedException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }

        // 检查用户账户是否被禁用
        if (!user.isEnabled()) {
            log.warn("User account is disabled: {}", user.getUsername());
            throw new DisabledException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }

        // 检查用户账户是否过期
        if (!user.isAccountNonExpired()) {
            log.warn("User account is expired: {}", user.getUsername());
            throw new AccountExpiredException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }
}
