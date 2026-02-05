package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型枚举
 * <p>
 * 该枚举类用于定义不同的字典类型，例如系统内置字典和业务字典。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DictTypeEnum implements IEnum<String> {

    /**
     * 系统内置字典类型（不可修改）
     * <p>
     * 该字典类型表示系统内置字典，通常不可被修改。
     * </p>
     */
    SYSTEM("1", "系统内置"),

    /**
     * 业务字典类型
     * <p>
     * 该字典类型表示业务相关的字典，通常可用于应用程序中的自定义业务操作。
     * </p>
     */
    BIZ("0", "业务类");

    /**
     * 类型标识
     * <p>
     * 该字段表示字典类型的标识，通常是字符串（例如 "1" 或 "0"）。
     * </p>
     */
    private final String type;

    /**
     * 描述信息
     * <p>
     * 该字段描述字典类型的含义，例如系统内置或业务类字典。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法根据字典类型标识获取对应的描述信息。
     * </p>
     * 
     * @param type 字典类型标识
     * @return 对应的字典描述信息
     */
    public static String getDescriptionByType(String type) {
        for (DictTypeEnum dictType : DictTypeEnum.values()) {
            if (dictType.getType().equals(type)) {
                return dictType.getDescription();
            }
        }
        return "未知类型";  // 未找到匹配类型时返回默认值
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
    public static DictTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(BIZ);
    }
}
