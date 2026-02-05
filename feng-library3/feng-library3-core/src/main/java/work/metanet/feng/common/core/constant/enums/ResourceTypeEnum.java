package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源类型枚举
 * <p>
 * 该枚举类用于定义不同的资源类型，包括图片资源和 XML 资源等。
 * </p>
 * 
 * @author edison
 * @date 2021/1/15
 */
@Getter
@AllArgsConstructor
public enum ResourceTypeEnum implements IEnum<String> {

    /**
     * 图片资源类型
     * <p>
     * 该资源类型表示图片文件，用于存储图片。
     * </p>
     */
    IMAGE("image", "图片资源"),

    /**
     * XML 资源类型
     * <p>
     * 该资源类型表示 XML 文件，用于存储结构化数据。
     * </p>
     */
    XML("xml", "xml资源");

    /**
     * 类型标识
     * <p>
     * 该字段表示资源类型的标识符，如 "image" 或 "xml"。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述资源类型的具体含义，如 "图片资源" 或 "xml资源"。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法通过传入类型标识（如 "image" 或 "xml"）返回对应的描述信息。
     * </p>
     * 
     * @param type 资源类型标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (ResourceTypeEnum resourceType : ResourceTypeEnum.values()) {
            if (resourceType.getType().equals(type)) {
                return resourceType.getDescription();
            }
        }
        return "未知资源类型"; // 如果没有找到对应的资源类型，返回默认值
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
    public static ResourceTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(IMAGE);
    }
}
