package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户级别枚举
 * <p>
 * 该枚举类定义了客户级别数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
@AllArgsConstructor
public enum CustomerLevel implements IEnum<String> {
	A("a", "A类客户：重点投入资源，提供VIP服务"),
	B("b", "B类客户：定期跟进，推动升级"),
	C("c", "C类客户：优化服务以提升潜力"),
	D("d", "D类客户：逐步淘汰或减少投入");
		
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
    public static CustomerLevel fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(D);
    }

}
