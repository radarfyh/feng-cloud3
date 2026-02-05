package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交账号类型枚举
 * <p>
 * 该枚举类定义了社交账号类型数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
@AllArgsConstructor
public enum SocialType implements IEnum<String> {
    WECHAT("wechat", "微信"),
    LINKEDIN("linkedin", "领英"),
    WEIBO("weibo", "微博"),
    QQ("qq", "QQ");
		
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
    public static SocialType fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(WECHAT);
    }

}
