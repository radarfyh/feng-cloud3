package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理状态枚举
 * <p>
 * 该枚举类定义了处理状态的不同类型。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum ProcessStatusEnum implements IEnum<String> {

    /**
     * 活跃状态
     * <p>
     * 该状态表示当前流程处于活跃状态。
     * </p>
     */
    ACTIVE("active", "活跃"),

    /**
     * 挂起状态
     * <p>
     * 该状态表示当前流程被挂起。
     * </p>
     */
    SUSPEND("suspend", "挂起");

    /**
     * 状态标识
     * <p>
     * 该字段表示流程状态的标识符，如 "active" 或 "suspend"。
     * </p>
     */
    private final String status;

    /**
     * 描述信息
     * <p>
     * 该字段描述流程状态的具体含义。
     * </p>
     */
    private final String description;

    /**
     * 根据状态标识获取描述信息
     * <p>
     * 该方法根据状态标识获取对应的描述信息。
     * </p>
     * 
     * @param status 状态标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByStatus(String status) {
        for (ProcessStatusEnum processStatus : ProcessStatusEnum.values()) {
            if (processStatus.getStatus().equals(status)) {
                return processStatus.getDescription();
            }
        }
        return "未知状态"; // 如果没有匹配的状态，返回默认值
    }
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.status; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return status;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static ProcessStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(SUSPEND);
    }
}
