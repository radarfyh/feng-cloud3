package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量数据库提供者 枚举
 * <p>
 * 该枚举类定义了向量数据库提供者。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 */
@Getter
@AllArgsConstructor
public enum EmbedStoreEnum implements IEnum<String> {

    REDIS("REDIS", "REDIS向量库"),
    PGVECTOR("PGVECTOR", "PGVECTOR向量库"),
    MILVUS("MILVUS", "MILVUS向量库"),
    ;
	
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
    public static EmbedStoreEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(PGVECTOR);
    }
}
