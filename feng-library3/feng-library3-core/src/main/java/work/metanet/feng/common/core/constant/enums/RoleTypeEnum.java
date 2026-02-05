package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型枚举
 * <p>
 * 该枚举类定义了不同的角色类型，包括系统角色、自定义角色、特殊角色。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnum implements IEnum<Integer> {

    /**
     * 系统角色
     */
    SYSTEM_ROLE(0, "系统角色"),

    /**
     * 自定义角色
     */
    CUSTOM_ROLE(1, "自定义角色"),

    /**
     * 特殊角色
     */
    SPEC_ROLE(3, "特殊角色");

    /**
     * 角色类型编码
     * <p>
     * 该字段表示角色类型编码。
     * </p>
     */
    private final Integer code;

    /**
     * 角色类型描述
     * <p>
     * 该字段描述角色类型的具体含义。
     * </p>
     */
    private final String description;

    /**
     * 根据角色类型获取描述信息
     * <p>
     * 该方法根据角色类型编码获取对应的描述信息。
     * </p>
     * 
     * @param code 角色类型编码
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(Integer code) {
        for (RoleTypeEnum roleType : RoleTypeEnum.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType.getDescription();
            }
        }
        return "未知角色类型"; // 未找到时返回默认值
    }
    
    // code直接存入数据库
    @Override
    public Integer getValue() {
        return this.code; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public Integer getCode() {
        return code;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static RoleTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CUSTOM_ROLE);
    }
}

