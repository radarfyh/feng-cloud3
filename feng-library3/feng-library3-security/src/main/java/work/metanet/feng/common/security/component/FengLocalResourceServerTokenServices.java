package work.metanet.feng.common.security.component;

import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.security.exception.FengUnauthorizedException;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.service.FengUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * 资源服务器令牌服务，支持本地资源验证
 * <p>
 * 该类负责加载 OAuth2 认证信息，并使用本地服务提供相应的用户认证信息，
 * 支持基于客户端 ID 获取相应的用户详细信息进行认证。
 * </p>
 */
@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class FengLocalResourceServerTokenServices implements ResourceServerTokenServices {

    private final TokenStore tokenStore;

    /**
     * 加载认证信息，若令牌有效，返回相应的 OAuth2 认证信息
     * 
     * @param accessToken 访问令牌
     * @return OAuth2Authentication 返回 OAuth2 认证信息
     * @throws AuthenticationException 认证失败异常
     * @throws InvalidTokenException 无效令牌异常
     */
    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        
        OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);

        if (oAuth2Authentication == null) {
            return null;  // 令牌无效，返回 null
        }

        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        if (!(oAuth2Authentication.getPrincipal() instanceof FengUser)) {
            return oAuth2Authentication;  // 返回原始的 OAuth2Authentication
        }

        String clientId = oAuth2Request.getClientId();

        // Only log in debug mode, avoid exposing sensitive information in production
        if (log.isDebugEnabled()) {
            log.debug("loadAuthentication --> accessToken: {}, clientId: {}", accessToken, clientId);
        }

        // 获取 FengUserDetailsService Bean
        Map<String, FengUserDetailsService> userDetailsServiceMap = SpringContextHolder
                .getBeansOfType(FengUserDetailsService.class);

        Optional<FengUserDetailsService> optionalService = userDetailsServiceMap.values().stream()
                .filter(service -> service.support(clientId, oAuth2Request.getGrantType()))
                .max(Comparator.comparingInt(Ordered::getOrder));

        // 如果没有找到匹配的服务，抛出异常
        FengUserDetailsService fengUserDetailsService = optionalService.orElseThrow(
                () -> new InternalAuthenticationServiceException("UserDetailsService not registered"));

        FengUser fengUser = (FengUser) oAuth2Authentication.getPrincipal();
        UserDetails userDetails;

        try {
            // 使用找到的 FengUserDetailsService 加载用户详细信息
            userDetails = fengUserDetailsService.loadUserByUser(fengUser);
        } catch (UsernameNotFoundException notFoundException) {
            throw new FengUnauthorizedException(String.format("%s username not found", fengUser.getUsername()), notFoundException);
        }

        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
        OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
        authentication.setAuthenticated(true);

        return authentication;
    }

    /**
     * 读取访问令牌，此方法不支持
     * 
     * @param accessToken 访问令牌
     * @return 不支持此操作，抛出异常
     * @throws UnsupportedOperationException 不支持的操作异常
     */
    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
