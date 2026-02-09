package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_poll_cursor")
@Schema(name = "轮询游标", description = "部级消息轮询游标实体")
public class MsgPollCursor implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;

    @Schema(description = "游标键", defaultValue = "DEFAULT")
    private String cursorKey;
    
    @Schema(description = "部级游标值")
    private String ybid;
    
    @Schema(description = "上次轮询时间")
    private Date lastPollTime;
    
    @Schema(description = "轮询间隔(秒)，≥10", defaultValue = "10")
    private Integer pollInterval;
    
    @Schema(description = "轮询次数", defaultValue = "0")
    private Integer pollCount;
    
    @Schema(description = "获取消息总数", defaultValue = "0")
    private Integer messageCount;
    
    @Schema(description = "上次获取消息时间")
    private Date lastMessageTime;
    
    @Schema(description = "状态:0-停止 1-运行", defaultValue = "1")
    private Integer status;
    
    @Schema(description = "连续错误次数", defaultValue = "0")
    private Integer errorCount;
    
    @Schema(description = "上次错误信息")
    private String lastError;
    
    @Schema(description = "上次成功时间")
    private Date lastSuccessTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;
    
    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
    private String delFlag;
}