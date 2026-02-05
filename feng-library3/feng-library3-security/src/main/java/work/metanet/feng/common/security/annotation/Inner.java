package work.metanet.feng.common.security.annotation;

import java.lang.annotation.*;

/**
 * 服务调用鉴权注解
 * <p>
 * 此注解用于标识需要进行内部服务调用鉴权的接口或类。
 * 通常用于验证请求的合法性，确保请求来自于可信的服务。
 * </p>
 * <p>
 * 该注解支持在方法或类上使用。可以通过设置属性来控制是否启用AOP统一处理，
 * 并支持特殊的字段空值校验。
 * </p>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

    /**
     * 是否启用 AOP 统一处理
     * <p>
     * 默认启用 AOP 统一处理。如果设置为 false，将不会执行 AOP 操作，
     * 需要手动在方法中进行处理。
     * </p>
     * 
     * @return true 启用 AOP 统一处理，false 不启用
     */
    boolean value() default true;

    /**
     * 需要特殊判空的字段（预留字段）
     * <p>
     * 该字段用于指定需要特殊处理判空的字段。当前版本预留此字段，暂不使用。
     * 若需要根据业务需求自定义判空字段，可以在后续版本中扩展此功能。
     * </p>
     *
     * @return 需要判空的字段名称数组
     */
    String[] field() default {};
}
