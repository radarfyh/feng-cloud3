package work.metanet.feng.common.security.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 扩展用户信息类，继承自 Spring Security 的 User 类。
 * 此类添加了额外的用户属性，如用户ID、科室信息、头像、职位等，用于身份验证和授权处理。
 * <p>
 * 该类用于管理用户的详细信息，通常与系统中的用户角色和权限管理结合使用。
 * </p>
 */
public class FengUser extends User {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 用户ID
     */
    @Getter
    private Integer id;

    /**
     * 科室id
     */
    @Getter
    private Integer deptId;

    /**
     * 科室编码
     */
    @Getter
    @Schema(description = "科室编码")
    private String deptCode;

    /**
     * 是否首次登录：0-否/1-是 默认1
     */
    @Getter
    @Schema(description = "是否首次登录：0-否/1-是 默认1")
    private String firstLogin;

    /**
     * 手机号
     */
    @Getter
    private String phone;

    /**
     * 头像
     */
    @Getter
    private String avatar;

    /**
     * 租戶ID
     */
    @Getter
    private Integer tenantId;

    /**
     * 机构编码
     */
    @Getter
    private String orgCode;

    /**
     * 构造函数，初始化 FengUser 实例。此构造函数扩展了 Spring Security User 的默认构造函数。
     * 在此基础上增加了额外的用户属性，如用户ID、科室、职位等。
     *
     * @param id                    用户ID
     * @param username              用户名
     * @param password              用户密码
     * @param phone                 手机号
     * @param avatar                用户头像
     * @param tenantId              租户ID
     * @param orgCode               机构编码
     * @param deptId                科室ID
     * @param deptCode              科室编码
     * @param firstLogin            是否首次登录
     * @param enabled               用户是否启用
     * @param accountNonExpired     账户是否过期
     * @param credentialsNonExpired 凭证是否过期
     * @param accountNonLocked      账户是否锁定
     * @param authorities           用户权限
     */
    @JsonCreator
    public FengUser(
            @JsonProperty("id") Integer id, 
            @JsonProperty("username") String username, 
            @JsonProperty("password") String password, 
            @JsonProperty("phone") String phone, 
            @JsonProperty("avatar") String avatar, 
            @JsonProperty("tenantId") Integer tenantId, 
            @JsonProperty("orgCode") String orgCode, 
            @JsonProperty("deptId") Integer deptId, 
            @JsonProperty("deptCode") String deptCode, 
            @JsonProperty("firstLogin") String firstLogin, 
            @JsonProperty("enabled") boolean enabled, 
            @JsonProperty("accountNonExpired") boolean accountNonExpired, 
            @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired, 
            @JsonProperty("accountNonLocked") boolean accountNonLocked, 
            @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.phone = phone;
        this.avatar = avatar;
        this.tenantId = tenantId;
        this.orgCode = orgCode;
        this.deptId = deptId;
        this.deptCode = deptCode;
        this.firstLogin = firstLogin;
        // 注意：不再设置 accountNonLocked 字段，使用父类的状态
    }
}