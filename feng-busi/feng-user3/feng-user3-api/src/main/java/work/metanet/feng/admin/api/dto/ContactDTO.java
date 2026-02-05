package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.ContactRelationshipType;
import work.metanet.feng.common.core.constant.enums.Gender;
import work.metanet.feng.common.core.constant.enums.Position;
import work.metanet.feng.common.core.constant.enums.StringWhether;
import work.metanet.feng.common.core.util.ValidGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 联系人数据传输对象
 */
@Data
@Schema(description = "联系人数据传输对象")
public class ContactDTO {

    @NotNull(groups = ValidGroup.Update.class, message = "联系人ID不能为空")
    @Schema(description = "联系人ID（更新时必填）", example = "1001")
    private Long id;

    @NotBlank(message = "联系人姓名不能为空")
    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "性别", example = "1")
    private Gender gender;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobile;

    @Schema(description = "固定电话", example = "010-88889999")
    private String phone;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "职务", example = "技术总监")
    private String position;

    @Schema(description = "是否关键决策人，是1，否0", example = "1")
    private String isDecisionMaker;

    @Schema(description = "是否主要联系人，是1，否0", example = "1")
    private String isPrimary;

    @Schema(description = "直属上级ID", example = "1002")
    private Long superiorId;

    @Schema(description = "关联员工ID", example = "2001")
    private Long staffId;

    @NotNull(message = "所属客户ID不能为空")
    @Schema(description = "所属客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    private Long customerId;

    @Schema(description = "关系类型", example = "partner")
    private String relationshipType;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "海淀区")
    private String district;

    @Schema(description = "详细地址", example = "中关村南大街5号")
    private String address;

    @Schema(description = "最新情况备注", example = "近期有采购意向")
    private String latestStatus;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;
}