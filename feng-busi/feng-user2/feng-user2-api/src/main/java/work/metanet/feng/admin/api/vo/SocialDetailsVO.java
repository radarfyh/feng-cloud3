package work.metanet.feng.admin.api.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "社交账号视图对象")
public class SocialDetailsVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "社交账号ID", example = "1")
    private Long id;

    @Schema(description = "关联员工ID", example = "1001")
    private Long staffId;
    @Schema(description = "关联员工姓名", example = "张三")
    private String staffName;

    @Schema(description = "关联客户ID", example = "2001")
    private Long customerId;
    @Schema(description = "关联客户名称", example = "华为技术")
    private String customerName;

    @Schema(description = "关联联系人ID", example = "3001")
    private Long contactId;
    @Schema(description = "关联联系人姓名", example = "李四")
    private String contactName;

    @Schema(description = "关联类型", example = "customer")
    private String ownerType;
    
    @Schema(description = "社交平台类型", example = "wechat")
    private String type;
    @Schema(description = "社交平台类型名称", example = "微信")
    private String typeName;
    
    @Schema(description = "社交账号", example = "wx123456")
    private String socialAccount;

    @Schema(description = "账号昵称", example = "风云科技官方")
    private String nickname;

    @Schema(description = "账号头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "备注信息", example = "主要工作账号")
    private String remark;

    @Schema(description = "是否主账号，1是，0否", example = "1")
    private String isPrimary;
}