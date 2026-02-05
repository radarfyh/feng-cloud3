package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 * <p>
 * 该枚举类定义了性别数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
@AllArgsConstructor
public enum StringWhether implements IEnum<String> {
	NO("0", "否"),
	YES("1", "是");
		
    /**
     * 状态码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String name;
    
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
    public static StringWhether fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(NO);
    }
}
