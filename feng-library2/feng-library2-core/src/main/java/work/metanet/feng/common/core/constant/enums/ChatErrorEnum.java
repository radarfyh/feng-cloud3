package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

/**
 * @author GB
 * @desc
 * @since 2024-08-21
 */
@AllArgsConstructor
public enum ChatErrorEnum implements IEnum<Integer> {
    API_KEY_IS_NULL(1000, "模型 %s %s api key 为空，请检查配置"),
    BASE_URL_IS_NULL(1003, "模型 %s %s base url 为空，请检查配置"),
    SECRET_KEY_IS_NULL(1005, "模型 %s %s base secret Key 为空，请检查配置"),
    ;

    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误描述，用于展示给用户
     */
    private String errorDesc;

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDesc(String modelName, String type) {
        return String.format(errorDesc, modelName, type);
    }
    // code直接存入数据库
    @Override
    public Integer getValue() {
        return this.errorCode; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public Integer getCode() {
        return errorCode;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static ChatErrorEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(API_KEY_IS_NULL);
    }
}
