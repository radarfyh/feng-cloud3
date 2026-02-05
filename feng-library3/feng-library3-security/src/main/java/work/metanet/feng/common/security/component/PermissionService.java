package work.metanet.feng.common.security.component;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.constant.enums.Logical;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 权限判断工具类
 * <p>
 * 本类提供用于判断当前用户是否具备特定权限的方法。支持检查用户角色和权限，特别是超级管理员角色的权限判断。
 * </p>
 */
@Slf4j
@Component("pms")
public class PermissionService {
	private final Authentication authentication;
	
	PermissionService() {
		authentication = SecurityContextHolder.getContext().getAuthentication();
	}

    /**
     * 判断接口是否有任意指定权限
     * <p>
     * 此方法通过检查当前用户的权限是否包含传入的任何一个权限来判断是否具备相应权限。
     * 特别地，支持超级管理员（角色ID为1）直接通过权限校验。
     * </p>
     *
     * @param permissions 需要检查的权限列表
     * @return 如果用户拥有任意一个权限，则返回 true，否则返回 false
     */
    public boolean hasPermission(String... permissions) {
        if (ArrayUtil.isEmpty(permissions)) {
            // 如果没有提供权限列表，直接返回 false
            log.warn("No permissions provided.");
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // 如果没有获取到认证信息，返回 false
            log.warn("No authentication found.");
            authentication = this.authentication;
            return false;
        }

        // 检查是否为超级管理员
        List<Integer> roles = SecurityUtils.getRoles();
        if (roles.contains(BuiltInRoleEnum.ADMIN.getId())) {
            // 如果是超级管理员，直接返回 true
            log.info("User is a super admin.");
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 打印所有权限的原始列表
        log.debug("Checking permissions: {}, with authorities: {}", (Object) permissions, authorities);

        // 处理权限流
        boolean resp = authorities.stream()
                .map(GrantedAuthority::getAuthority)    // 提取每个权限的名称
                .filter(StringUtils::hasText)            // 过滤掉无效权限（空字符串或 null）
                .anyMatch(permission -> {
                    // 检查权限是否匹配
                    boolean match = PatternMatchUtils.simpleMatch(permissions, permission);
                    log.debug("Permission '{}' match result: {}", permission, match);
                    return match;
                });

        log.debug("Permission check result: {}", resp);

        return resp;
    }
    /**
     * 检查单个权限
     */
    private boolean checkPermission(String permission, Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::hasText)
                .anyMatch(auth -> PatternMatchUtils.simpleMatch(permission, auth));
    }

    /**
     * 通用权限检查方法
     */
    private boolean checkPermissions(boolean checkAll, String... permissions) {
        if (ArrayUtil.isEmpty(permissions)) {
            log.warn("No permissions provided.");
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("No authentication found.");
            return false;
        }

        // 超级管理员检查
        if (isAdmin()) {
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.debug("Checking permissions: {}, with authorities: {}", Arrays.toString(permissions), authorities);

        return checkAll ? 
            Arrays.stream(permissions).allMatch(p -> checkPermission(p, authorities)) :
            Arrays.stream(permissions).anyMatch(p -> checkPermission(p, authorities));
    }

    public boolean isAdmin() {
        List<Integer> roles = SecurityUtils.getRoles();
        if (roles == null) {
        	return false;
        }
        boolean isAdmin = roles.contains(BuiltInRoleEnum.ADMIN.getId());
        if (isAdmin) {
            log.info("User is a super admin. roles: {}", JSONUtil.toJsonPrettyStr(roles));
        }
        return isAdmin;
    }

    /**
     * 检查任意权限（OR逻辑）
     */
    public boolean requirePermission(String... permissions) {
        return checkPermissions(false, permissions);
    }

    /**
     * 检查所有权限（AND逻辑）
     */
    public boolean requireAllPermission(String... permissions) {
        return checkPermissions(true, permissions);
    }}
