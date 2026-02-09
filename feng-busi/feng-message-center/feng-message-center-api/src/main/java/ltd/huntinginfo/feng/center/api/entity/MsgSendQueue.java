package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "msg_send_queue", autoResultMap = true)
@Schema(name = "消息发送队列", description = "消息发送队列表实体")
public class MsgSendQueue implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;
    
    // ========== 队列信息 ==========
    @Schema(description = "队列类型:SEND-发送消息 CALLBACK-回调业务系统 RETRY-重试", requiredMode = Schema.RequiredMode.REQUIRED)
    private String queueType;
    
    @Schema(description = "队列状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败", defaultValue = "PENDING")
    private String queueStatus;
    
    @Schema(description = "优先级1-10，数字越小优先级越高", defaultValue = "5")
    private Integer priority;
    
    @Schema(description = "执行时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date executeTime;
    
    @Schema(description = "最大重试次数", defaultValue = "3")
    private Integer maxRetry;
    
    @Schema(description = "当前重试次数", defaultValue = "0")
    private Integer currentRetry;
    
    // ========== 任务数据 ==========
    @Schema(description = "消息ID，关联msg_agent_mapping.id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String msgId;
    
    // ========== 执行结果 ==========
    @Schema(description = "结果代码")
    private String resultCode;
    
    @Schema(description = "结果消息")
    private String resultMessage;
    
    @Schema(description = "执行开始时间")
    private Date executeStartTime;
    
    @Schema(description = "执行结束时间")
    private Date executeEndTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;
}