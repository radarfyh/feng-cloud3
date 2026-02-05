package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社交账号实体")
@TableName("sys_social_details")
public class SysSocialDetails extends Model<SysSocialDetails> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "社交账号ID", example = "1")
    private Integer id;

    @Schema(description = "关联员工ID", example = "1001")
    private Integer staffId;

    @Schema(description = "关联客户ID", example = "2001")
    private Integer customerId;

    @Schema(description = "关联联系人ID", example = "3001")
    private Integer contactId;

    @Schema(description = "关联类型(owner_type)", 
            example = "customer",
            allowableValues = {"staff", "customer", "contact"})
    private String ownerType;

    @Schema(description = "社交平台类型", 
            example = "wechat",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("type")
    private String type;

    @Schema(description = "社交账号", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "wx123456")
    private String socialAccount;

    @Schema(description = "账号昵称", example = "风云科技官方")
    private String nickname;

    @Schema(description = "账号头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "备注信息", example = "主要工作账号")
    private String remark;

    @Schema(description = "平台应用ID", example = "app123")
    private String appId;

    @Schema(description = "平台密钥(加密存储)", example = "encrypted_secret")
    private String appSecret;

    @Schema(description = "跳转链接", example = "https://oauth.example.com/callback")
    private String redirectUrl;

    @Schema(description = "是否主账号", 
            defaultValue = "0",
            example = "1")
    private String isPrimary;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2023-01-02 12:00:00")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记", 
            allowableValues = {"0", "1"}, 
            defaultValue = "0",
            example = "0")
    private String delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}