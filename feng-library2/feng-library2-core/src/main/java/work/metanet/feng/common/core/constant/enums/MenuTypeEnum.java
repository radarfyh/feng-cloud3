package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 * <p>
 * 该枚举类定义了菜单和按钮的类型，用于表示不同的菜单项类型。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum implements IEnum<String> {

    /**
     * 菜单项
     * <p>
     * 该类型表示一个菜单项。
     * </p>
     */
    MENU("0", "menu", "菜单"),

    /**
     * 按钮项
     * <p>
     * 该类型表示一个按钮项。
     * </p>
     */
    BUTTON("1", "button", "按钮");

    /**
     * 类型标识
     * <p>
     * 该字段表示菜单类型的标识符，"0" 表示菜单，"1" 表示按钮。
     * </p>
     */
    private final String type;

    /**
     * 类型代码
     * <p>
     * 该字段表示菜单类型的代码，"menu" 表示菜单，"button" 表示按钮。
     * </p>
     */
    private final String code;
    
    /**
     * 描述信息
     * <p>
     * 该字段描述菜单项类型的具体含义，如 "菜单" 或 "按钮"。
     * </p>
     */
    private final String description;

    /**
     * 根据类型获取描述信息
     * <p>
     * 该方法通过传入菜单类型标识（"0" 或 "1"）返回对应的描述信息。
     * </p>
     * 
     * @param type 菜单类型标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (MenuTypeEnum menuType : MenuTypeEnum.values()) {
            if (menuType.getType().equals(type)) {
                return menuType.getDescription();
            }
        }
        return "未知菜单类型"; // 如果没有找到对应的类型，返回默认值
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
    public static MenuTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(BUTTON);
    }
}
