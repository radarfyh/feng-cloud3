package work.metanet.feng.common.security.grant;

import cn.hutool.core.util.StrUtil;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义的资源所有者电话令牌授予者，用于基于手机号和验证码（或密码）进行认证。
 * 继承自 AbstractTokenGranter，用于OAuth2授权过程中的自定义授权类型（mobile）。
 */
public class ResourceOwnerCustomeAppTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "mobile";  // 授权类型：手机号
    private final AuthenticationManager authenticationManager;  // 认证管理器

    /**
     * 构造函数，初始化自定义的授权方式。
     * 
     * @param authenticationManager 认证管理器，用于处理手机号和验证码的认证
     * @param tokenServices 授权服务器的token服务
     * @param clientDetailsService 客户端详情服务
     * @param requestFactory OAuth2请求工厂
     */
    public ResourceOwnerCustomeAppTokenGranter(AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    /**
     * 用于提供更灵活的构造器，允许指定自定义的授权类型。
     * 
     * @param authenticationManager 认证管理器
     * @param tokenServices token服务
     * @param clientDetailsService 客户端详情服务
     * @param requestFactory OAuth2请求工厂
     * @param grantType 授权类型（默认是"mobile"）
     */
    protected ResourceOwnerCustomeAppTokenGranter(AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    /**
     * 获取 OAuth2 认证信息。
     * 
     * @param client 客户端详情
     * @param tokenRequest 请求参数
     * @return 返回 OAuth2 认证信息
     * @throws InvalidGrantException 如果认证失败，抛出 InvalidGrantException 异常
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

        // 获取手机号和验证码
        String mobile = parameters.get("mobile");
        String code = parameters.get("code");

        // 校验手机号和验证码不能为空
        if (StrUtil.isBlank(mobile) || StrUtil.isBlank(code)) {
            throw new InvalidGrantException("Bad credentials [params must contain phone and code]");
        }

        // 防止下游泄露 code
        parameters.remove("code");

        // 构建自定义认证令牌
        Authentication userAuth = new CustomAppAuthenticationToken(mobile, code, tokenRequest.getGrantType());
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);

        try {
            // 使用认证管理器进行认证
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            // 捕获账户状态异常（如过期、锁定、禁用等）或凭证错误
            throw new InvalidGrantException(ase.getMessage());
        }

        // 如果认证失败，抛出 InvalidGrantException 异常
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + mobile);
        }

        // 创建 OAuth2 请求并返回认证信息
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
