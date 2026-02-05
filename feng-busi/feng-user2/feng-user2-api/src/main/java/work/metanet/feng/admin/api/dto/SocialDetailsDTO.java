package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.SocialType;
import work.metanet.feng.common.core.constant.enums.StringWhether;
import work.metanet.feng.common.core.util.ValidGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "社交账号数据传输对象")
public class SocialDetailsDTO {

    @NotNull(groups = ValidGroup.Update.class, message = "ID不能为空")
    @Schema(description = "社交账号ID(更新时必填)", example = "1")
    private Long id;

    @Schema(description = "关联员工ID", example = "1001")
    private Long staffId;

    @Schema(description = "关联客户ID", example = "2001")
    private Long customerId;

    @Schema(description = "关联联系人ID", example = "3001")
    private Long contactId;

    @Schema(description = "社交平台类型", example = "wechat")
    private String type;

    @Schema(description = "社交账号", example = "wx123456")
    private String socialAccount;

    @Schema(description = "账号昵称", example = "风云科技官方")
    private String nickname;

    @Schema(description = "账号头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "备注信息", example = "主要工作账号")
    private String remark;

    @Schema(description = "是否主账号", example = "true")
    private String isPrimary;
}