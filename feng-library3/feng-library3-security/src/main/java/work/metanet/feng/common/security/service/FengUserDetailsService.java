package work.metanet.feng.common.security.service; 

import cn.hutool.core.util.ArrayUtil;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.exception.FengAuth2Exception;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.*;

/**
 * 用户详细信息服务接口，继承自 Spring Security 的 UserDetailsService，并提供用户信息构建和客户端校验等功能。
 * <p>
 * 该接口用于获取用户的详细信息，支持用户身份验证的扩展，并可通过客户端ID和授权类型校验客户端请求是否合法。
 * </p>
 * <p>
 * 此接口扩展了 UserDetailsService，提供了自定义的用户详情加载、客户端支持和排序功能。
 * </p>
 */
public interface FengUserDetailsService extends UserDetailsService, Ordered {

    /**
     * 校验是否支持该客户端和授权类型
     * <p>
     * 默认实现返回 true，表示支持所有客户端和授权类型。子类可以重写此方法进行自定义校验。
     * </p>
     *
     * @param clientId  请求客户端ID
     * @param grantType 授权类型
     * @return true 如果支持该客户端和授权类型，false 否则
     */
    default boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 获取排序值，默认为最大值 0。
     * <p>
     * 此方法用于支持按优先级排序多个实现类，默认实现返回 0，子类可以根据需要调整排序值。
     * </p>
     *
     * @return 排序值，默认为 0
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 根据用户信息构建 UserDetails 对象
     * <p>
     * 此方法接受一个包含用户信息的结果对象，并将其转换为 Spring Security 的 UserDetails 类型。
     * </p>
     *
     * @param result 用户信息的封装对象
     * @return 构建的 UserDetails 对象
     */
    default UserDetails getUserDetails(R<UserInfo> result) {
        return convertUserDetails(result.getData());
    }

    /**
     * 将 UserInfo 转换为 UserDetails 对象
     * <p>
     * 该方法用于将从远程服务获取到的用户信息对象转换为 Spring Security 的 UserDetails 对象，
     * 并处理账号的状态（如禁用、锁定等）。
     * </p>
     *
     * @param info 用户信息对象
     * @return 返回构建的 UserDetails 对象
     * @throws FengAuth2Exception 如果账号已禁用，则抛出异常
     */
    default UserDetails convertUserDetails(UserInfo info) {
        // 检查账号是否禁用
        if (info.getSysUser().getStatus().equals(CommonConstants.STATUS_USERNAME)) {
            throw new FengAuth2Exception("该账号已禁用，请联系管理员启用");
        }

        Set<String> dbAuthsSet = new HashSet<>();
        if (ArrayUtil.isNotEmpty(info.getRoles())) {
            // 获取角色并转换为权限
            Arrays.stream(info.getRoles()).forEach(roleId -> dbAuthsSet.add(SecurityConstants.ROLE + roleId));
            // 获取资源权限
            dbAuthsSet.addAll(Arrays.asList(info.getPermissions()));
        }

        // 将权限集合转为 GrantedAuthority 列表
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();

        // 密码处理：检查是否已经包含编码器前缀
        String password = user.getPassword();
        if (!password.startsWith("{") && !password.contains("}")) {
            // 如果没有前缀，添加默认的BCRYPT前缀
            password = SecurityConstants.BCRYPT + password;
        }
        
        // 构建并返回 Spring Security 的 FengUser 对象
        return new FengUser(
            user.getId(), 
            user.getUsername(), 
            password,
            user.getPhone(), 
            user.getAvatar(), 
            user.getTenantId(),
            user.getOrganCode(), 
            user.getDeptId(), 
            info.getDeptCode(),
            user.getFirstLogin(), 
            true, true, true, 
            !CommonConstants.STATUS_LOCK.equals(user.getStatus()), 
            authorities
        );
    }

    /**
     * 根据 FengUser 实体加载 UserDetails
     * <p>
     * 该方法根据 FengUser 对象加载 UserDetails，通常用于获取当前用户的详细信息。
     * </p>
     *
     * @param fengUser 用户对象
     * @return 返回对应的 UserDetails 对象
     */
    default UserDetails loadUserByUser(FengUser fengUser) {
        return this.loadUserByUsername(fengUser.getUsername());
    }
}
