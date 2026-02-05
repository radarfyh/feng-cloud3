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
@Schema(description = "租户信息表")
@TableName("sys_tenant")
public class SysTenant extends Model<SysTenant> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "租户ID（全局唯一）")
    private Integer id;

    @Schema(description = "租户编码（唯一业务标识）")
    private String tenantCode;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "租户描述")
    private String description;

    @Schema(description = "租户状态: 0-试用,1-正式,2-禁用,3-过期")
    private String status = "0";

    @Schema(description = "启用时间")
    private LocalDateTime enableTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "计费方案编码")
    private String billingPlan = "BASIC";

    @Schema(description = "方案名称")
    private String planName = "基础版";

    @Schema(description = "支付状态：0-未支付,1-已支付")
    private String paymentStatus = "0";

    @Schema(description = "隔离模式：0-共享库,1-独立库,2-独立Schema")
    private String isolationMode = "0";

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除")
    private String delFlag = "0";
}