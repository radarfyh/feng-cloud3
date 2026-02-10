package ltd.huntinginfo.feng.agent.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_center_config")
@Schema(name = "部级配置", description = "部级消息中心配置实体")
public class MsgCenterConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;
    
    @Schema(description = "配置类型:STRING/NUMBER/BOOLEAN/JSON", defaultValue = "STRING")
    private String configType;
    
    @Schema(description = "配置描述")
    private String configDesc;
    
    @Schema(description = "配置类别:COMMON-通用 API-接口地址 TOKEN-令牌配置", defaultValue = "COMMON")
    private String category;
    
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
}