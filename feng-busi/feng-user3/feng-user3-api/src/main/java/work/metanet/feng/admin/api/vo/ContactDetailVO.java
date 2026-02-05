package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import work.metanet.feng.admin.api.entity.SysSocialDetails;
import work.metanet.feng.common.core.constant.enums.Gender;
import java.util.List;

/**
 * 联系人详情视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "联系人详情信息")
public class ContactDetailVO extends ContactSimpleVO {
	private static final long serialVersionUID = 1L;

	@Schema(description = "关联员工ID", example = "2001")
    private Integer staffId;

    @Schema(description = "关系类型", example = "partner")
    private String relationshipType;
    
    @Schema(description = "关系名称", example = "生意伙伴")
    private String relationshipTypeName;

    @Schema(description = "性别", example = "MALE")
    private Gender gender;
    
    @Schema(description = "性别", example = "男")
    private String genderName;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "固定电话", example = "010-88889999")
    private String phone;

    @Schema(description = "是否关键决策人，1是，0否", example = "1")
    private String isDecisionMaker;

    @Schema(description = "直属上级ID", example = "1002")
    private Integer superiorId;
    
    @Schema(description = "直属上级信息")
    private ContactSimpleVO superior;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "海淀区")
    private String district;

    @Schema(description = "详细地址", example = "中关村南大街5号")
    private String address;

    @Schema(description = "最后上门时间", example = "2023-01-15 14:30:00")
    private String visitTime;

    @Schema(description = "最新情况备注", example = "近期有采购意向")
    private String latestStatus;

    @Schema(description = "互动频次（次/月）", example = "5")
    private Integer contactFrequency;

    @Schema(description = "社交账号列表")
    private List<SysSocialDetails> socialAccounts;

    @Schema(description = "创建时间", example = "2023-01-01 00:00:00")
    private String createTime;

    @Schema(description = "更新时间", example = "2023-01-02 00:00:00")
    private String updateTime;

}