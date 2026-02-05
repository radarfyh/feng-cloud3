package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型枚举类
 * <p>
 * 该枚举类定义了不同的数据权限类型，包括查询所有数据、用户自定义权限、当前级别及子级权限等。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum implements IEnum<Integer> {

    /**
     * 查询所有数据
     * <p>
     * 该权限类型允许查询所有的数据，不受任何限制。
     * </p>
     */
    ALL(0, "查询所有数据"),

    /**
     * 自定义
     * <p>
     * 该权限类型允许用户自定义权限范围，灵活性较高。
     * </p>
     */
    CUSTOM(1, "自定义权限"),

    /**
     * 本级及子级
     * <p>
     * 该权限类型允许查询当前级别及其下属子级的数据。
     * </p>
     */
    OWN_CHILD_LEVEL(2, "本级及子级"),

    /**
     * 本级
     * <p>
     * 该权限类型仅限查询当前级别的数据，不包括子级。
     * </p>
     */
    OWN_LEVEL(3, "本级");

    /**
     * 权限类型标识
     * <p>
     * 每种数据权限类型都有一个唯一的标识，方便在系统中引用和判断。
     * </p>
     */
    private final Integer type;

    /**
     * 权限类型描述
     * <p>
     * 描述该权限类型的含义，提供人类可读的信息。
     * </p>
     */
    private final String description;

    /**
     * 根据类型值获取对应的描述
     * <p>
     * 根据权限类型的标识值获取对应的权限类型描述信息。
     * </p>
     * 
     * @param type 权限类型标识
     * @return 权限类型描述
     */
    public static String getDescriptionByType(Integer type) {
        for (DataScopeTypeEnum typeEnum : DataScopeTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum.getDescription();
            }
        }
        return "未知权限类型"; // 如果未找到对应的类型，返回默认值
    }
    
    // code直接存入数据库
    @Override
    public Integer getValue() {
        return this.type; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public Integer getCode() {
        return type;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static DataScopeTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(OWN_LEVEL);
    }
}
