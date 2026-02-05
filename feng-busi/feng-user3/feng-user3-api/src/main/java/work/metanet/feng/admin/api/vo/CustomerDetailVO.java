package work.metanet.feng.admin.api.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户详情视图对象")
public class CustomerDetailVO extends PrmCustomerVO {
	private static final long serialVersionUID = 1L;

	@Schema(description = "关联联系人列表")
    private List<ContactSimpleVO> contacts;
    
    @Schema(description = "社交账号列表")
    private List<SocialDetailsVO> socialAccounts;
    
    @Schema(description = "跟踪记录")
    private List<FollowRecordVO> followRecords;
    
    @Schema(description = "客户关系强度指数", example = "0.75")
    private Double relationshipScore;
    
}