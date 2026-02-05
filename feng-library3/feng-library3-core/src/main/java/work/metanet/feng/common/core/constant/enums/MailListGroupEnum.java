package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件列表群组类型枚举
 * <p>
 * 该枚举类用于定义邮件列表的群组类型，包括集团公司、分支机构、部门和职员等类型。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum MailListGroupEnum implements IEnum<String> {

    /**
     * 集团公司
     * <p>
     * 该群组类型表示集团公司，适用于所有集团内的公司或单位。
     * </p>
     */
    GROUP("group", "集团公司"),

    /**
     * 分支机构
     * <p>
     * 该群组类型表示分支机构，适用于集团下的各个分支机构。
     * </p>
     */
    BRANCH("branch", "分支机构"),

    /**
     * 部门
     * <p>
     * 该群组类型表示部门，适用于集团或分支机构内的部门。
     * </p>
     */
    DEPT("dept", "部门"),

    /**
     * 职员
     * <p>
     * 该群组类型表示职员，适用于集团、分支机构或部门的个体员工。
     * </p>
     */
    STAFF("staff", "职员");

    /**
     * 群组类型标识
     * <p>
     * 该字段表示群组类型的标识符，例如 "group" 表示集团公司，"branch" 表示分支机构等。
     * </p>
     */
    private final String code;

    /**
     * 群组描述
     * <p>
     * 该字段描述了群组类型的具体含义，例如 "集团公司"、"分支机构" 等。
     * </p>
     */
    private final String name;

    /**
     * 根据群组类型标识获取描述信息
     * <p>
     * 该方法通过传入群组类型标识（如 "group", "branch"）返回对应的描述信息。
     * </p>
     * 
     * @param code 群组类型标识
     * @return 对应的群组描述信息
     */
    public static String getNameByCode(String code) {
        for (MailListGroupEnum group : MailListGroupEnum.values()) {
            if (group.getCode().equals(code)) {
                return group.getName();
            }
        }
        return "未知群组"; // 如果没有找到对应的群组类型，返回默认值
    }
    
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
    public static MailListGroupEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(STAFF);
    }
}
