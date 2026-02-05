package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 岗位类别枚举
 * <p>
 * 该枚举类定义了岗位类别数据字典。
 * </p>
 */
@Schema(name = "JobCategory", description = "岗位类别枚举")
@Getter
@AllArgsConstructor
public enum JobCategory implements IEnum<String> {
    BUSINESS("business", "业务线"),
    MANAGEMENT("management", "管理线"),
    TECHNOLOGY("technology", "技术线"),
    OTHER("other", "其他");

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
    public static JobCategory fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(OTHER);
    }
}