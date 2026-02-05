package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限函数类型
 * <p>
 * 该枚举用于指定数据查询的不同函数类型，如查询所有数据（SELECT *）和计算数据总数（COUNT）。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DataScopeFuncEnum implements IEnum<String> {

    /**
     * 查询全部数据，返回所有数据记录。
     * 生成 SQL 查询为：SELECT * FROM (originSql) temp_data_scope WHERE temp_data_scope.dept_id IN (1)
     */
    ALL("*", "查询全部数据"),

    /**
     * 查询数据的总数量，返回 COUNT(1) 作为结果。
     * 生成 SQL 查询为：SELECT COUNT(1) FROM (originSql) temp_data_scope WHERE temp_data_scope.dept_id IN (1)
     */
    COUNT("COUNT(1)", "查询数据总数");

    /**
     * 函数类型（如 SELECT * 或 COUNT(1)）
     */
    private final String type;

    /**
     * 函数描述，简要说明该函数的作用
     */
    private final String description;

    /**
     * 根据函数类型获取对应的 SQL 查询部分。
     * 
     * @param dataScopeFuncEnum 数据权限函数枚举
     * @return 返回对应的 SQL 查询部分（例如 "*" 或 "COUNT(1)"）
     */
    public static String getSqlType(DataScopeFuncEnum dataScopeFuncEnum) {
        return dataScopeFuncEnum != null ? dataScopeFuncEnum.getType() : null;
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
    public static DataScopeFuncEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(COUNT);
    }
}
