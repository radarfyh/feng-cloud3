package ltd.huntinginfo.feng.agent.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_topic")
@Schema(name = "消息主题", description = "消息主题实体")
public class MsgTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "主题代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "主题名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "描述")
    private String description;
    
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