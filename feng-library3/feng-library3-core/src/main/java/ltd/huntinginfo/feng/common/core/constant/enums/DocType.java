package ltd.huntinginfo.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档类型枚举
 * <p>
 * 该枚举类定义了文档类型。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 */
@Getter
@AllArgsConstructor
public enum DocType implements IEnum<String> {
	TEXT("TEXT", "文本输入"),
	FILE("FILE", "文件系统上传"),
	OSS("OSS", "对象存储服务上传");
	
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
    public static DocType fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(TEXT);
    }
}
