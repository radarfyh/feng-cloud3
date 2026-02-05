package work.metanet.feng.common.security.aspect;

import work.metanet.feng.common.security.annotation.RequiresPermission;
import work.metanet.feng.common.security.component.PermissionService;
import work.metanet.feng.common.security.exception.FengForbiddenException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 权限校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final PermissionService permissionService;

    @Before("@annotation(requiresPermission)")
    public void doBefore(JoinPoint point, RequiresPermission requiresPermission) {
        String[] permissions = requiresPermission.value();
        if (permissions.length == 0) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            permissions = new String[]{signature.getMethod().getName()};
        }

        boolean hasPermission;
        if (requiresPermission.logical() == RequiresPermission.Logical.AND) {
            hasPermission = permissionService.requireAllPermission(permissions);
        } else {
            hasPermission = permissionService.requirePermission(permissions);
        }

        if (!hasPermission) {
            throw new FengForbiddenException("没有访问权限");
        }
    }
}