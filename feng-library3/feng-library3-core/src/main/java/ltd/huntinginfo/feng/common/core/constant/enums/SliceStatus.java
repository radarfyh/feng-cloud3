package ltd.huntinginfo.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档切片状态枚举
 * <p>
 * 该枚举类定义了文档切片状态。
 * </p>
 * 
 * @author edison
 * @since 2025/5/9
 */
@Getter
@AllArgsConstructor
public enum SliceStatus implements IEnum<Integer> {
	PENDING(0, "待处理"),
	PROCESSING(1, "处理中"),
	COMPLETED(2, "已完成"),
	FAILED(3, "失败");
	
    /**
     * 编码
     */
    private final int code;

    /**
     * 描述信息
     */
    private final String msg;
    
    // code直接存入数据库
    @Override
    public Integer getValue() {
        return this.code; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public Integer getCode() {
        return code;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static SliceStatus fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(FAILED);
    }

}
