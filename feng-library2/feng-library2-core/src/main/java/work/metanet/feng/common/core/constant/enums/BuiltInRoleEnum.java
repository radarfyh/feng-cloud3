package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统内置角色枚举
 * <p>
 * 该枚举类定义了不同的统内置角色，包括超级管理员、普通职员。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum BuiltInRoleEnum implements IEnum<String> {

    /**
     * 超级管理员
     */
    ADMIN(1, "admin", "超级管理员"),
    
    /**
     * 普通职员（默认）
     */
    DEFAULT(2, "default", "普通职员"),

    /**
     * 项目领导，不适用于小组
     */
    LEADER(3, "leader", "领导"),

    /**
     * 项目成员，不适用于小组
     */
    MEMBER(4, "memeber", "成员"),
    
    /**
     * AI助手
     */
    ASSISTANT(5, "ASSISTANT", "AI Agent助手"),
    
    /**
     * AI系统
     */
    SYSTEM(6, "SYSTEM", "AI Agent系统"),

    /**
     * AI系统
     */
    USER(7, "USER", "AI Agent 用户");
    
    /**
     * 标识
     * <p>
     * 该字段表示内置角色标识。
     * </p>
     */
    private final Integer id;
    
    /**
     * 编码
     * <p>
     * 该字段表示内置角色编码。
     * </p>
     */
    private final String code;

    /**
     * 描述
     * <p>
     * 该字段描述内置角色的含义。
     * </p>
     */
    private final String description;

    /**
     * 根据编码获取描述信息
     * <p>
     * 该方法根据编码获取对应的描述信息。
     * </p>
     * 
     * @param code 编码
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String code) {
        for (BuiltInRoleEnum buildInRole : BuiltInRoleEnum.values()) {
            if (buildInRole.getCode().equals(code)) {
                return buildInRole.getDescription();
            }
        }
        return DEFAULT.getDescription(); // 未找到时返回默认值
    }
    
    /**
     * 根据ID获取编码
     * <p>
     * 该方法根据ID编码获取对应的编码信息。
     * </p>
     * 
     * @param id 标识
     * @return 对应的编码信息
     */
    public static String getCodeById(Integer id) {
        for (BuiltInRoleEnum buildInRole : BuiltInRoleEnum.values()) {
            if (buildInRole.getId().equals(id)) {
                return buildInRole.getCode();
            }
        }
        return DEFAULT.getCode(); // 未找到时返回默认值
    }
    
    /**
     * 判断指定编码是否在本枚举中
     * @param roleCode
     * @return
     */
    public static boolean isBuiltInRole(String roleCode) {
        return Arrays.stream(BuiltInRoleEnum.values()).anyMatch(e -> e.getCode().equals(roleCode));
    }
    
    public static boolean isBuiltInRole(Integer roleId) {
        return Arrays.stream(BuiltInRoleEnum.values()).anyMatch(e -> e.getId() == roleId);
    }
    
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.code; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return code;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static BuiltInRoleEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(DEFAULT);
    }
}


