package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户关系类型枚举
 * <p>
 * 该枚举类定义了客户关系类型数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Schema(name = "CustomerRelationshipType", description = "客户关系类型枚举")
@Getter
@AllArgsConstructor
public enum CustomerRelationshipType implements IEnum<String> {
    CUSTOMER("CUSTOMER", "客户"),
    AGENT("AGENT", "代理商"),
    SUPPLIER("SUPPLIER", "供应商");

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
    public static CustomerRelationshipType fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CUSTOMER);
    }
}