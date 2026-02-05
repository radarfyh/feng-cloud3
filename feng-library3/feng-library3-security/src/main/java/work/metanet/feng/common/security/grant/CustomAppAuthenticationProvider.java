package work.metanet.feng.common.security.grant;

import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.security.service.FengUserDetailsService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义APP认证提供者
 * <p>
 * 该类负责处理与APP相关的自定义身份认证，支持根据客户端ID选择相应的用户认证服务。
 * </p>
 */
@Slf4j
public class CustomAppAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * 用户属性校验
     */
    @Setter
    private UserDetailsChecker preAuthenticationChecks = new AccountStatusUserDetailsChecker();

    /**
     * 校验用户信息
     * <p>
     * 该方法校验认证请求中提供的用户名和密码信息。
     * 如果没有提供凭证，抛出 {@link BadCredentialsException}。
     * </p>
     *
     * @param userDetails 用户信息
     * @param authentication 认证信息
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    /**
     * 执行认证
     * <p>
     * 该方法用于执行实际的认证逻辑，包括从 Spring Context 中获取相应的用户详情服务，
     * 根据客户端信息选择合适的服务来校验用户凭证。
     * </p>
     *
     * @param authentication 认证请求
     * @return 认证后的 {@link CustomAppAuthenticationToken}
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("Bad credentials");
        }

        CustomAppAuthenticationToken requestToken = (CustomAppAuthenticationToken) authentication;
        
        // 获取客户端认证信息
        Authentication clientAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = clientAuthentication.getName();

        // 从 SpringContext 中获取 FengUserDetailsService 实现
        Map<String, FengUserDetailsService> userDetailsServiceMap = SpringContextHolder
                .getBeansOfType(FengUserDetailsService.class);

        // 根据客户端ID和认证类型找到对应的 FengUserDetailsService 实现
        FengUserDetailsService userDetailsService = userDetailsServiceMap.values().stream()
                .filter(service -> service.support(clientId, requestToken.getGrantType()))
                .max(Comparator.comparingInt(Ordered::getOrder))
                .orElseThrow(() -> new InternalAuthenticationServiceException("UserDetailsService not registered"));

        // 获取用户名（手机号）
        String phone = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);

        // 校验用户信息
        preAuthenticationChecks.check(userDetails);

        // 创建并返回认证的令牌
        CustomAppAuthenticationToken token = new CustomAppAuthenticationToken(userDetails);
        token.setDetails(authentication.getDetails());
        return token;
    }

    /**
     * 用于获取用户信息（此方法不被实际使用）
     * <p>
     * 该方法用于获取用户详细信息，但在此自定义认证提供者中并未实现此逻辑。
     * </p>
     *
     * @param phone 用户手机号
     * @param authentication 认证信息
     * @return null
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected UserDetails retrieveUser(String phone, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        return null; // 未实现，返回null
    }

    /**
     * 判断是否支持某种类型的认证请求
     * <p>
     * 该方法用于判断是否支持指定的认证请求类型。此处仅支持 {@link CustomAppAuthenticationToken} 类型。
     * </p>
     *
     * @param authentication 认证类
     * @return 如果支持，返回 true
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAppAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
