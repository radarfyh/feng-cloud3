package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型枚举
 * <p>
 * 该枚举类用于定义不同类型的验证码，包括圆圈干扰验证码、GIF验证码、干扰线验证码和扭曲干扰验证码。
 * </p>
 * 
 * @author edison
 */
@Getter
@AllArgsConstructor
public enum HutoolCaptchaTypeEnums implements IEnum<String>  {

    /**
     * 圆圈干扰验证码
     * <p>
     * 该验证码类型利用圆圈干扰元素来增加识别难度。
     * </p>
     */
    CIRCLE("1", "圆圈干扰验证码"),

    /**
     * GIF 动态验证码
     * <p>
     * 该验证码类型生成动态的 GIF 图像，增加验证码的复杂性。
     * </p>
     */
    GIF("2", "GIF 动态验证码"),

    /**
     * 干扰线验证码
     * <p>
     * 该验证码类型通过干扰线来增加图片中的噪音，从而提升验证码的安全性。
     * </p>
     */
    LINE("3", "干扰线验证码"),

    /**
     * 扭曲干扰验证码
     * <p>
     * 该验证码类型通过图像扭曲来使验证码变得更加难以识别。
     * </p>
     */
    SHEAR("4", "扭曲干扰验证码");

    /**
     * 类型标识
     * <p>
     * 该字段表示验证码类型的标识。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述验证码类型的含义。
     * </p>
     */
    private final String description;

    
    /**
     * 获取验证码类型的描述
     * <p>
     * 该方法根据验证码类型获取其对应的描述信息。
     * </p>
     * 
     * @param captchaType 验证码类型
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (HutoolCaptchaTypeEnums typeEnums : HutoolCaptchaTypeEnums.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums.getDescription();
            }
        }
        return "未知验证码类型"; // 如果没有找到对应的验证码类型，返回默认值
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
    public static HutoolCaptchaTypeEnums fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CIRCLE);
    }
}
