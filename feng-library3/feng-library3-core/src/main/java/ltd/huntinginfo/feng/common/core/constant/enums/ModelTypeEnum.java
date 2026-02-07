package ltd.huntinginfo.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型类型枚举
 * <p>
 * 该枚举类定义了LLM模型的类型。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 */
@Getter
@AllArgsConstructor
public enum ModelTypeEnum implements IEnum<String> {

	CHAT("CHAT", "聊天"),
	EMBEDDING("EMBEDDING", "文本嵌入"),
	IMAGE("IMAGE", "图形"),
	WEB_SEARCH("OTHER", "其他");
	
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
    public static ModelTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(CHAT);
    }
}
