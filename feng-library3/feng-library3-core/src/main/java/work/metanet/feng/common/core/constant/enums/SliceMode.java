package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档切片模式枚举
 * <p>
 * 该枚举类定义了文档切片模式。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/18
 */
@Getter
@AllArgsConstructor
public enum SliceMode implements IEnum<String> {
	PARAGRAPH("PARAGRAPH", "段落"),
	SENTENCE("SENTENCE", "语句"),
	FIXED("FIXED", "固定");
		
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
    public static SliceMode fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(SENTENCE);
    }

}
