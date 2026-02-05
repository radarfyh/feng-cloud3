package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 前端风格类型枚举
 * <p>
 * 该枚举类用于定义不同的前端风格类型，包括 avue 风格和 element 风格。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum StyleTypeEnum implements IEnum<String> {

    /**
     * avue 风格
     * <p>
     * 该风格类型表示 antd 风格的前端界面。
     * </p>
     */
    AVUE("0", "antd 风格"),

    /**
     * element 风格
     * <p>
     * 该风格类型表示 element 风格的前端界面。
     * </p>
     */
    ELEMENT("1", "element 风格");

    /**
     * 风格标识
     * <p>
     * 该字段表示前端风格类型的标识符，"0" 表示 avue 风格，"1" 表示 element 风格。
     * </p>
     */
    private final String style;

    /**
     * 风格描述
     * <p>
     * 该字段描述风格的具体含义，如 "avue 风格" 或 "element 风格"。
     * </p>
     */
    private final String description;

    /**
     * 根据风格标识获取描述信息
     * <p>
     * 该方法通过传入风格标识（如 "0" 或 "1"）返回对应的描述信息。
     * </p>
     * 
     * @param style 风格标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByStyle(String style) {
        for (StyleTypeEnum type : StyleTypeEnum.values()) {
            if (type.getStyle().equals(style)) {
                return type.getDescription();
            }
        }
        return "未知风格"; // 若没有匹配的风格标识，返回默认值
    }
    
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.style; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return style;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static StyleTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(AVUE);
    }
}
