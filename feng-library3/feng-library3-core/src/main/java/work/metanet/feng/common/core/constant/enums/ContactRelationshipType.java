package work.metanet.feng.common.core.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 联系人关系类型枚举
 * <p>
 * 该枚举类定义了联系人关系类型数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Schema(name = "ContactRelationshipType", description = "联系人关系类型枚举")
@Getter
@AllArgsConstructor
public enum ContactRelationshipType {
	COLLEAGUE(0, "同事"),
	PARTNER(1, "生意伙伴"),
	FAMILY(2, "家庭"),
	RELATIVE(3, "亲戚"),
	FRIEND(4, "朋友");
	
	@EnumValue
	@JsonValue
	private final Integer value;
    private final String name;
}