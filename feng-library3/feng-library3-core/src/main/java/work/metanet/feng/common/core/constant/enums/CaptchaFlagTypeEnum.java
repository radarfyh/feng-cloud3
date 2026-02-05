package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码状态类型枚举
 * <p>
 * 该枚举类定义了验证码的开关状态（开启/关闭）。用于标识验证码是否启用。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum CaptchaFlagTypeEnum implements IEnum<String> {

    /**
     * 开启验证码
     * <p>
     * 该状态表示验证码功能已启用。
     * </p>
     */
    ON("1", "开启验证码"),

    /**
     * 关闭验证码
     * <p>
     * 该状态表示验证码功能已关闭。
     * </p>
     */
    OFF("0", "关闭验证码");

    /**
     * 状态类型
     * <p>
     * 该字段表示验证码状态的类型，通常是字符串表示的标志（"1" 或 "0"）。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述验证码状态的含义。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法根据传入的类型（"1" 或 "0"）返回对应的描述信息。
     * </p>
     * 
     * @param type 状态类型（"1" 或 "0"）
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (CaptchaFlagTypeEnum flag : CaptchaFlagTypeEnum.values()) {
            if (flag.getType().equals(type)) {
                return flag.getDescription();
            }
        }
        return "未知状态"; // 若没有匹配的类型时返回默认值
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
    public static CaptchaFlagTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(OFF);
    }
}
