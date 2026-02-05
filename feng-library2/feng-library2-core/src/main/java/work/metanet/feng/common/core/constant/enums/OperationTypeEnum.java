package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型类型枚举
 * <p>
 * 该枚举类定义了不同的操作类型方式。
 * </p>
 * 
 * @author edison
 * @date 2021/1/15
 */
@Getter
@AllArgsConstructor
public enum OperationTypeEnum implements IEnum<String> {

    /**
     * 新增操作
     */
    CREATE("0", "新增"),

    /**
     * 查询操作
     */
    READ("1", "查询"),

    /**
     * 修改操作
     */
    UPDATE("2", "修改"),

    /**
     * 删除操作
     */
    DELETE("3", "删除");

    /**
     * 操作类型标识
     * <p>
     * 该字段表示登录方式的标识
     * </p>
     */
    private final String type;

    /**
     * 登录方式描述
     * <p>
     * 该字段描述登录方式的具体含义
     * </p>
     */
    private final String description;

    /**
     * 根据操作类型获取描述信息
     * <p>
     * 该方法根据操作类型标识获取对应的描述信息
     * </p>
     * 
     * @param type 操作类型标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (OperationTypeEnum loginType : OperationTypeEnum.values()) {
            if (loginType.getType().equals(type)) {
                return loginType.getDescription();
            }
        }
        return "未知"; // 未找到对应方式时返回默认值
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
    public static OperationTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CREATE);
    }
}