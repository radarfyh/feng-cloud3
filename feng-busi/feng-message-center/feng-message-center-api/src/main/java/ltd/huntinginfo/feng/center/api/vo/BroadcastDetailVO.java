package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "广播详情VO")
public class BroadcastDetailVO {
    
    @Schema(description = "广播ID")
    private String id;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "广播类型")
    private String broadcastType;
    
    @Schema(description = "目标范围配置")
    private Map<String, Object> targetScope;
    
    @Schema(description = "目标范围描述")
    private String targetDescription;
    
    @Schema(description = "总接收人数")
    private Integer totalReceivers;
    
    @Schema(description = "已分发数量")
    private Integer distributedCount;
    
    @Schema(description = "已接收数量")
    private Integer receivedCount;
    
    @Schema(description = "已读人数")
    private Integer readCount;
    
    @Schema(description = "状态")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "完成时间")
    private LocalDateTime completeTime;
    
    @Schema(description = "分发进度")
    private Double distributeProgress;
    
    @Schema(description = "接收进度")
    private Double receiveProgress;
    
    @Schema(description = "阅读进度")
    private Double readProgress;
}