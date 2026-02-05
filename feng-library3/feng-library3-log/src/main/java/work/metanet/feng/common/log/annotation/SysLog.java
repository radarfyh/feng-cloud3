package work.metanet.feng.common.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * @param value 操作名称（如"用户登录"）
 * @param type 操作类型（如"login"、"create"等业务标识,可关联LogTypeEnum）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /** 操作名称（必填） */
    String value();
    
    /** 操作类型（可选，默认空字符串，可关联LogTypeEnum） */
    String type() default "";
}
