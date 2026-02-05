package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交登录类型枚举
 * <p>
 * 该枚举类定义了不同的社交登录方式，包括账号密码登录、验证码登录、以及各大社交平台的登录方式。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum implements IEnum<String> {

    /**
     * 账号密码登录
     */
    PWD("pwd", "账号密码登录"),

    /**
     * 验证码登录
     */
    SMS("sms", "验证码登录"),

    /**
     * QQ登录
     */
    QQ("qq", "QQ登录"),

    /**
     * 微信登录
     */
    WECHAT("wx", "微信登录"),

    /**
     * 微信小程序登录
     */
    MINI_APP("mini", "微信小程序"),

    /**
     * 码云登录
     */
    GITEE("gitee", "码云登录"),

    /**
     * 开源中国登录
     */
    OSC("osc", "开源中国登录");

    /**
     * 登录类型标识
     * <p>
     * 该字段表示登录方式的标识，如 "PWD" 表示账号密码登录，"SMS" 表示验证码登录等。
     * </p>
     */
    private final String type;

    /**
     * 登录方式描述
     * <p>
     * 该字段描述登录方式的具体含义，如 "账号密码登录"、"微信登录" 等。
     * </p>
     */
    private final String description;

    /**
     * 根据登录类型获取描述信息
     * <p>
     * 该方法根据登录类型标识获取对应的描述信息，便于业务逻辑中根据标识获取登录方式的描述。
     * </p>
     * 
     * @param type 登录类型标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByType(String type) {
        for (LoginTypeEnum loginType : LoginTypeEnum.values()) {
            if (loginType.getType().equals(type)) {
                return loginType.getDescription();
            }
        }
        return "未知登录方式"; // 未找到对应的登录方式时返回默认值
    }
    
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.type; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return type;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static LoginTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(PWD);
    }
}
