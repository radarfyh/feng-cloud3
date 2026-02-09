package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_status_code")
@Schema(name = "消息状态码", description = "消息状态码实体")
public class MsgStatusCode implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "状态码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusCode;

    @Schema(description = "状态名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusName;
    
    @Schema(description = "状态描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;
    
    @Schema(description = "分类:PROXY-代理平台 CENTER-部级消息中心", defaultValue = "PROXY")
    private String category;
    
    @Schema(description = "父状态码")
    private String parentCode;
    
    @Schema(description = "排序", defaultValue = "0")
    private Integer sortOrder;
    
    @Schema(description = "是否为最终状态:0-否 1-是", defaultValue = "0")
    private Integer isFinal;
    
    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;
}