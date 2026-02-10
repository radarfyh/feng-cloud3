package ltd.huntinginfo.feng.agent.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "msg_agent_log", autoResultMap = true)
@Schema(name = "消息日志", description = "消息处理日志实体")
public class MsgAgentLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;
    
    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String msgId;
    
    // ========== 日志信息 ==========
    @Schema(description = "日志类型:SEND-发送 CALLBACK-回调 RETRY-重试 ERROR-错误 POLL-轮询 TOKEN-令牌 STATUS-状态更新", 
           requiredMode = Schema.RequiredMode.REQUIRED)
    private String logType;
    
    @Schema(description = "日志级别:DEBUG/INFO/WARN/ERROR", defaultValue = "INFO")
    private String logLevel;
    
    @Schema(description = "日志内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String logContent;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "日志详情JSON")
    private Object logDetail;
    
    @Schema(description = "操作名称")
    private String operation;
    
    @Schema(description = "API地址")
    private String apiUrl;
    
    @Schema(description = "HTTP方法")
    private String httpMethod;
    
    @Schema(description = "HTTP状态码")
    private Integer httpStatus;
    
    @Schema(description = "响应时间(ms)")
    private Integer responseTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;
}