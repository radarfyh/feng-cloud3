package work.metanet.feng.common.security.grant;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 自定义鉴权令牌，继承自 AbstractAuthenticationToken
 * <p>
 * 该类扩展了 Spring Security 的认证令牌，用于在客户端认证中传递认证信息。它包含用户的手机号码（principal）、验证码或密码（code）以及授权类型（grantType）。
 * </p>
 */
public class CustomAppAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    /**
     * 验证码/密码
     */
    private final String code;

    /**
     * 授权类型
     */
    @Getter
    private final String grantType;

    /**
     * 构造一个新的认证令牌
     * <p>
     * 用于客户端认证，携带手机号、验证码或密码以及授权类型。
     * </p>
     * 
     * @param phone 手机号
     * @param code 验证码或密码
     * @param grantType 授权类型
     */
    public CustomAppAuthenticationToken(String phone, String code, String grantType) {
        super(AuthorityUtils.NO_AUTHORITIES); // 默认没有授权权限
        this.principal = phone;
        this.code = code;
        this.grantType = grantType;
    }

    /**
     * 构造一个认证令牌，用户已通过身份验证
     * <p>
     * 用于存储认证后的用户信息，包括用户详细信息和授权。
     * </p>
     * 
     * @param sysUser 认证后的用户信息
     */
    public CustomAppAuthenticationToken(UserDetails sysUser) {
        super(sysUser.getAuthorities());
        this.principal = sysUser;
        this.code = null;  // 已认证用户无需验证码或密码
        this.grantType = null; // 已认证用户无需授权类型
        super.setAuthenticated(true); // 设置为已认证
    }

    /**
     * 获取认证主体（即用户信息）
     * 
     * @return 返回用户信息，手机号或已认证用户
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 获取认证凭证（即验证码或密码）
     * 
     * @return 返回验证码或密码
     */
    @Override
    public Object getCredentials() {
        return this.code;
    }
}
