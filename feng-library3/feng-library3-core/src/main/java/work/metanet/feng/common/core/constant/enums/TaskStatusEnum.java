package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务流程状态枚举
 * <p>
 * 该枚举类定义了任务流程的不同状态，包括未提交、审核中、已完成和驳回等。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum TaskStatusEnum implements IEnum<String> {

    /**
     * 未提交
     * <p>
     * 该状态表示任务尚未提交。
     * </p>
     */
    UNSUBMIT("0", "未提交"),

    /**
     * 审核中
     * <p>
     * 该状态表示任务正在审核中。
     * </p>
     */
    CHECK("1", "审核中"),

    /**
     * 已完成
     * <p>
     * 该状态表示任务已完成。
     * </p>
     */
    COMPLETED("2", "已完成"),

    /**
     * 驳回
     * <p>
     * 该状态表示任务已被驳回。
     * </p>
     */
    OVERRULE("9", "驳回");

    /**
     * 状态标识
     * <p>
     * 该字段表示任务流程状态的标识符，如 "0" 表示未提交，"1" 表示审核中等。
     * </p>
     */
    private final String status;

    /**
     * 状态描述
     * <p>
     * 该字段描述任务流程状态的具体含义，如 "未提交"、"审核中" 等。
     * </p>
     */
    private final String description;

    /**
     * 根据状态标识获取描述信息
     * <p>
     * 该方法根据任务状态标识（如 "0" 或 "1"）返回对应的描述信息。
     * </p>
     * 
     * @param status 状态标识
     * @return 对应的描述信息
     */
    public static String getDescriptionByStatus(String status) {
        for (TaskStatusEnum taskStatus : TaskStatusEnum.values()) {
            if (taskStatus.getStatus().equals(status)) {
                return taskStatus.getDescription();
            }
        }
        return "未知状态"; // 如果没有找到对应的状态，返回默认值
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
    public static TaskStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(UNSUBMIT);
    }
}
