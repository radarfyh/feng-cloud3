package work.metanet.feng.admin.api.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.ContactRelationshipType;

@Data
@Schema(description = "联系人-客户关系网络视图对象")
public class ContactCustomerRelationshipVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "关系ID")
    private Integer relationshipId;
    
    @Schema(description = "联系人A信息")
    private ContactSimpleVO contactA;
    
    @Schema(description = "联系人B信息")
    private ContactSimpleVO contactB;
    
    @Schema(description = "关联客户信息（如果存在）")
    private CustomerSimpleVO customerA;
    private CustomerSimpleVO customerB;
    
    @Schema(description = "关系类型")
    private String relationshipType;
    
    @Schema(description = "亲密度分数")
    private Double intimacyScore;
    
    @Schema(description = "最后互动时间")
    private LocalDateTime lastContactTime;
}