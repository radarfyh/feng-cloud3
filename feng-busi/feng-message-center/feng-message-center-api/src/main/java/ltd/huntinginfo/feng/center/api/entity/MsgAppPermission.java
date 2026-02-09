package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_app_permission")
@Schema(name = "应用权限", description = "应用权限实体，定义应用可访问的资源")
public class MsgAppPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "关联应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;

    @Schema(description = "资源标识符(格式:服务:资源:操作)", 
           example = "api:device:read",
           requiredMode = Schema.RequiredMode.REQUIRED)
    private String resourceCode;

    @Schema(description = "资源描述")
    private String resourceName;

    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
    private String delFlag;
}