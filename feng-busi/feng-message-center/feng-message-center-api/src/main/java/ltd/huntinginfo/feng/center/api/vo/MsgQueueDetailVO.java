package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "消息队列详情VO")
public class MsgQueueDetailVO {
    
    @Schema(description = "任务ID")
    private String id;
    
    @Schema(description = "队列类型")
    private String queueType;
    
    @Schema(description = "队列名称")
    private String queueName;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "任务数据")
    private Map<String, Object> taskData;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "执行时间")
    private LocalDateTime executeTime;
    
    @Schema(description = "最大重试次数")
    private Integer maxRetry;
    
    @Schema(description = "当前重试次数")
    private Integer currentRetry;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "工作者ID")
    private String workerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "结果代码")
    private String resultCode;
    
    @Schema(description = "结果消息")
    private String resultMessage;
    
    @Schema(description = "错误堆栈")
    private String errorStack;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "耗时（秒）")
    private Long costSeconds;
}