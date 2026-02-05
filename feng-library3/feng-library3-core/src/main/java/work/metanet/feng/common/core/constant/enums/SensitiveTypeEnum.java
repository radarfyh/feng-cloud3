package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感信息类型枚举类
 * <p>
 * 该枚举类用于表示各种敏感信息类型，如用户姓名、身份证号、手机号等，
 * 并且为脱敏处理提供了对应的类型标识。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum SensitiveTypeEnum implements IEnum<String> {

    /**
     * 自定义脱敏
     * <p>
     * 该类型用于自定义脱敏规则，其他类型的脱敏方式将会被忽略。
     * </p>
     */
    CUSTOM("0", "自定义脱敏"),

    /**
     * 用户名脱敏
     * <p>
     * 该类型用于脱敏用户的姓名，显示前后部分字符，部分字符用*代替。（例如：风*华，刘*）
     * </p>
     */
    CHINESE_NAME("1", "用户名脱敏"),

    /**
     * 身份证号脱敏
     * <p>
     * 该类型用于脱敏身份证号，部分字符用*代替，仅显示部分信息。（例如：110110********1234）
     * </p>
     */
    ID_CARD("2", "身份证号脱敏"),

    /**
     * 座机号脱敏
     * <p>
     * 该类型用于脱敏座机号码，部分字符用*代替，仅显示后四位。（例如：****1234）
     * </p>
     */
    FIXED_PHONE("3", "座机号脱敏"),

    /**
     * 手机号脱敏
     * <p>
     * 该类型用于脱敏手机号，部分字符用*代替，仅显示部分信息。（例如：135****1234）
     * </p>
     */
    MOBILE_PHONE("4", "手机号脱敏"),

    /**
     * 地址脱敏
     * <p>
     * 该类型用于脱敏地址信息，仅显示前部分内容，后部分用*代替。（例如：北京********）
     * </p>
     */
    ADDRESS("5", "地址脱敏"),

    /**
     * 电子邮件地址脱敏
     * <p>
     * 该类型用于脱敏电子邮件地址，部分字符用*代替。（例如：r*****h@xx.com）
     * </p>
     */
    EMAIL("6", "电子邮件地址脱敏"),

    /**
     * 银行卡号脱敏
     * <p>
     * 该类型用于脱敏银行卡号，部分字符用*代替，仅显示部分信息。（例如：611101************1234）
     * </p>
     */
    BANK_CARD("7", "银行卡号脱敏"),

    /**
     * 密码脱敏
     * <p>
     * 该类型用于脱敏密码，始终显示为******，长度无关。（始终显示为******，与密码长度无关）
     * </p>
     */
    PASSWORD("8", "密码脱敏"),

    /**
     * 密钥脱敏
     * <p>
     * 该类型用于脱敏密钥，始终显示为******，长度无关。（始终显示为******，与密钥长度无关）
     * </p>
     */
    KEY("9", "密钥脱敏");

    /**
     * 类型标识
     * <p>
     * 该字段表示敏感信息类型的标识。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述敏感信息类型的含义。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法通过传入类型标识来返回对应的描述信息。
     * </p>
     * 
     * @param type 敏感信息类型标识
     * @return 对应的描述信息，如果没有匹配的类型，则返回默认值 "未知脱敏类型"
     */
    public static String getDescriptionByType(String type) {
        for (SensitiveTypeEnum sensitiveType : SensitiveTypeEnum.values()) {
            if (sensitiveType.getType().equals(type)) {
                return sensitiveType.getDescription();
            }
        }
        return "未知脱敏类型"; // 如果没有找到对应的敏感信息类型，返回默认值
    }
    
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.type; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return type;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static SensitiveTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CUSTOM);
    }
}
