package ltd.huntinginfo.feng.admin.api.entity.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一角色信息表实体类
 */
@Data
@TableName("unique_role_info")
@EqualsAndHashCode(callSuper = true)
@Schema(name = "统一角色信息", 
       description = "统一角色信息实体，包含角色基础信息和状态信息，用于对接统一认证系统")
public class UniqueRole extends Model<UniqueRole> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "主键UUID", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "统一角色编号", 
           example = "ROLE001",
           requiredMode = RequiredMode.REQUIRED)
    private String roleId;

    @Schema(description = "角色名称", 
           example = "系统管理员",
           requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "角色代码", 
           example = "admin")
    private String code;

    @Schema(description = "显示顺序", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private Integer sort;

    @Schema(description = "状态(0-禁用 1-启用)", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "角色类型(参见RoleTypeEnum枚举类)", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "备注", 
           example = "系统最高权限角色")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", 
           example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", 
           example = "2023-08-15T10:00:00")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者", 
           example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新时间", 
           example = "2023-08-15T10:00:00")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记（0-正常 1-删除）", 
           example = "0",
           allowableValues = {"0", "1"})
    private String delFlag;
}
