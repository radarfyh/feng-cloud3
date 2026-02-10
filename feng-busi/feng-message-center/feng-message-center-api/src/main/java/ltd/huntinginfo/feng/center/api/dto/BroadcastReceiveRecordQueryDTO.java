package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@Schema(description = "广播接收记录查询DTO")
public class BroadcastReceiveRecordQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "广播ID")
    private String broadcastId;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "接收者类型")
    private String receiverType;
    
    @Schema(description = "接收状态")
    private String receiveStatus;
    
    @Schema(description = "阅读状态:0-未读 1-已读")
    private Integer readStatus;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortOrder = "desc";
}