package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 密码是否加密传输枚举类型
 * <p>
 * 该枚举类定义了密码加密传输的状态，包含加密和不加密两种状态。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum EncFlagTypeEnum implements IEnum<String> {

    /**
     * 是：表示密码采用加密传输
     */
    YES("1", "是"),

    /**
     * 否：表示密码不采用加密传输
     */
    NO("0", "否");

    /**
     * 类型标识
     * <p>
     * 该字段用于表示加密传输状态的标识，"1"表示加密，"0"表示不加密。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述加密传输的状态，"是"表示加密，"否"表示不加密。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法通过传入类型（"1" 或 "0"）返回对应的描述信息。
     * </p>
     * 
     * @param type 状态标识（"1" 或 "0"）
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (EncFlagTypeEnum flag : EncFlagTypeEnum.values()) {
            if (flag.getType().equals(type)) {
                return flag.getDescription();
            }
        }
        return "未知状态"; // 如果没有匹配的类型，则返回默认的错误提示
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
    public static EncFlagTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(NO);
    }
}
