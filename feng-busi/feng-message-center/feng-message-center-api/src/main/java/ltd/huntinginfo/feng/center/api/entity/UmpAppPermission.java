package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用权限表实体类
 * 对应表：ump_app_permission
 * 作用：管理应用的API访问权限，实现细粒度的权限控制
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_app_permission")
@Schema(description = "应用权限表实体")
public class UmpAppPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "应用标识")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "资源标识符")
    @TableField("resource_code")
    private String resourceCode;

    @Schema(description = "资源描述")
    @TableField("resource_name")
    private String resourceName;

    @Schema(description = "操作:*所有 READ读 WRITE写")
    @TableField("operation")
    private String operation;

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记:0-正常 1-删除")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}