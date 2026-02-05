package work.metanet.feng.common.core.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行业枚举
 * <p>
 * 该枚举类定义了行业数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
@AllArgsConstructor
public enum Industry implements IEnum<String> {
	A("a", "农林牧渔业"),
	A01("a01", "农业"),
	B("b", "采矿业"),
	D("d", "电力、燃气及水生产和供应业"),
	G("g", "交通运输、仓储和邮政业"),
	I("i", "信息传输、软件和信息技术服务业"),
	M("m", "科学研究和技术服务业"),
	N("n", "水利、环境和公共设施管理业"),
	OTHER("other", "其他");
		
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
    public static Industry fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(OTHER);
    }

}
