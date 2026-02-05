package work.metanet.feng.common.security.component;

import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.core.util.WebUtils;
import work.metanet.feng.common.security.exception.FengUnauthorizedException;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.service.FengUserDetailsService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.Comparator;

/**
 * 自定义DAO认证提供者
 * <p>
 * 该类用于处理基于DAO的认证逻辑，继承自 {@link AbstractUserDetailsAuthenticationProvider}。
 * </p>
 */
@Slf4j
public class FengDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private static final String CLIENT_ID_PARAM = "clientId";

    private PasswordEncoder passwordEncoder;
    private volatile String userNotFoundEncodedPassword;
    private UserDetailsService userDetailsService;
    private UserDetailsPasswordService userDetailsPasswordService;

    public FengDaoAuthenticationProvider() {
        setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }

    /**
     * 额外的认证检查
     * <p>
     * 该方法用于检查用户提供的密码是否与存储的密码匹配。
     * </p>
     *
     * @param userDetails 用户详情
     * @param authentication 认证信息
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate: no credentials provided");
            throw new BadCredentialsException("Bad credentials");
        }
        String presentedPassword = authentication.getCredentials().toString();
        // Avoid logging actual passwords
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Failed to authenticate: password mismatch. "
            		+ "presentedPassword: {}, storedPassword: {}", presentedPassword, userDetails.getPassword());
            throw new BadCredentialsException("Bad credentials");
        }
    }

    /**
     * 检索用户详情
     * <p>
     * 该方法用于根据用户名检索用户详情，并支持多客户端认证。
     * </p>
     *
     * @param username 用户名
     * @param authentication 认证信息
     * @return 用户详情
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        try {
            // Prepare protection against timing attacks
            prepareTimingAttackProtection();

            // Get the clientId from the authentication context
            Authentication clientAuthentication = SecurityContextHolder.getContext().getAuthentication();
            String clientId = getClientId(clientAuthentication);

            log.debug("Retrieving user for clientId: {}, username: {}", clientId, username);

            // Find the appropriate FengUserDetailsService based on the clientId
            FengUserDetailsService fengUserDetailsService = getUserDetailsServiceForClient(clientId);

            // Retrieve the principal (which might be a String) and fetch the FengUser
            Object principal = authentication.getPrincipal();
            UserDetails userDetails = null;

            if (principal instanceof FengUser) {
            	userDetails = (UserDetails) principal;
            } else if (principal instanceof String) {
                // If the principal is a String (username), we can use it to load the FengUser
            	userDetails = fengUserDetailsService.loadUserByUsername((String) principal);
            } else {
                throw new InternalAuthenticationServiceException("Unexpected principal type: " + principal.getClass().getName());
            }

            // Return the loaded UserDetails (not OAuth2Authentication)
            return userDetails;
        } catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            log.error("Username not found: {}", username, ex);
            throw ex;
        } catch (InternalAuthenticationServiceException ex) {
            log.error("Internal authentication service error: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Authentication failed: {}", ex.getMessage(), ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private String getClientId(Authentication clientAuthentication) {
        if (clientAuthentication == null) {
            return WebUtils.getRequest().getParameter(CLIENT_ID_PARAM);
        }
        return clientAuthentication.getName();
    }

    private FengUserDetailsService getUserDetailsServiceForClient(String clientId) {
        Map<String, FengUserDetailsService> userDetailsServiceMap = SpringContextHolder
                .getBeansOfType(FengUserDetailsService.class);

        Optional<FengUserDetailsService> optional = userDetailsServiceMap.values().stream()
                .filter(service -> service.support(clientId, null))
                .max(Comparator.comparingInt(Ordered::getOrder));

        return optional.orElseThrow(() -> new InternalAuthenticationServiceException("UserDetailsService not registered"));
    }

//    private UserDetails loadUserDetails(FengUser fengUser, FengUserDetailsService fengUserDetailsService) {
//        try {
//            return fengUserDetailsService.loadUserByUsername(fengUser.getUsername());
//        } catch (UsernameNotFoundException notFoundException) {
//            throw new FengUnauthorizedException(String.format("Username %s not found", fengUser.getUsername()), notFoundException);
//        }
//    }


    /**
     * 创建成功认证对象
     * <p>
     * 该方法用于在认证成功后创建认证对象，并处理密码升级逻辑。
     * </p>
     *
     * @param principal 认证主体
     * @param authentication 认证信息
     * @param user 用户详情
     * @return 认证对象
     */
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
            UserDetails user) {

        // Password upgrade logic
        boolean upgradeEncoding = this.userDetailsPasswordService != null
                && this.passwordEncoder.upgradeEncoding(user.getPassword());

        if (upgradeEncoding) {
            String presentedPassword = authentication.getCredentials().toString();
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            user = this.userDetailsPasswordService.updatePassword(user, newPassword);
        }

        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    /**
     * 设置密码编码器
     *
     * @param passwordEncoder 密码编码器
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }

    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setUserDetailsPasswordService(UserDetailsPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }
}
