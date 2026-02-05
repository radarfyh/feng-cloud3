package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 职务枚举
 * <p>
 * 该枚举类定义了职务数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
@AllArgsConstructor
@Deprecated
public enum Position implements IEnum<String> {
	DEPARTMENT_STAFF("a", "部门职员、初级或其对等职务"),
	DEPARTMENT_HEAD("b", "部门主任、中级或其对等职务"),
	MANAGER("c", "经理、高级或其对等职务"),
	GENERAL_MANAGER("d", "总经理、最高决策层或其对等职务");
		
    /**
     * 状态码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String msg;
    
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
    public static Position fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(DEPARTMENT_STAFF);
    }

}
