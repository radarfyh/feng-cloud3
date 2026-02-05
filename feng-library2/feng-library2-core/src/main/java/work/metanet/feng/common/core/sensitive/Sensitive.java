package work.metanet.feng.common.core.sensitive;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import work.metanet.feng.common.core.constant.enums.SensitiveTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象脱敏注解
 * <p>
 * 该注解用于标记需要进行脱敏处理的字段。支持前缀和后缀长度配置，并且可以自定义脱敏字符。
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerialize.class)
public @interface Sensitive {

    /**
     * 脱敏数据类型
     * <p>
     * 非 CUSTOMER 类型时，`prefixNoMaskLen`、`suffixNoMaskLen` 和 `maskStr` 将被忽略。
     * </p>
     * 
     * @return 脱敏类型，默认为 CUSTOMER
     */
    SensitiveTypeEnum type() default SensitiveTypeEnum.CUSTOM;

    /**
     * 前置不需要打码的长度
     * <p>
     * 该参数指定了脱敏数据中前缀部分不需要脱敏的字符数量。
     * </p>
     * 
     * @return 前缀不打码的长度，默认为 0
     */
    int prefixNoMaskLen() default 0;

    /**
     * 后置不需要打码的长度
     * <p>
     * 该参数指定了脱敏数据中后缀部分不需要脱敏的字符数量。
     * </p>
     * 
     * @return 后缀不打码的长度，默认为 0
     */
    int suffixNoMaskLen() default 0;

    /**
     * 用什么字符打码
     * <p>
     * 该参数指定了脱敏字符，默认为 "*"。
     * </p>
     * 
     * @return 脱敏字符，默认为 "*"
     */
    String maskStr() default "*";
}
