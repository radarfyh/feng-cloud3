package work.metanet.feng.common.security.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    /**
     * 需要校验的权限码
     */
    String[] value() default {};
    
    /**
     * 验证逻辑：AND 或 OR
     */
    Logical logical() default Logical.OR;

    enum Logical {
        AND, OR
    }
}