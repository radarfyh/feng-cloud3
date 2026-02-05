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
@Schema(description = "租户配置表")
@TableName("sys_tenant_config")
public class SysTenantConfig extends Model<SysTenantConfig> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "配置ID")
    private Integer id;

    @Schema(description = "租户ID，用于所有非全局表")
    private Integer tenantId = 1;

    @Schema(description = "租户内部用户ID，用于用户敏感表")
    private Integer tenantUserId;

    @Schema(description = "最大用户数")
    private Integer maxUser = 10;

    @Schema(description = "最大存储空间（字节, 默认1GB）")
    private Long maxStorage = 1073741824L;

    @Schema(description = "允许自定义LOGO")
    private String allowCustomLogo = "0";

    @Schema(description = "API访问权限")
    private String apiAccessEnabled = "0";

    @Schema(description = "数据导出权限")
    private String dataExportEnabled = "0";

    @Schema(description = "密码策略")
    private String passwordPolicy;

    @Schema(description = "登录失败锁定阈值")
    private String loginFailLimit = "5";

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