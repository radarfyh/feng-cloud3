package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@Schema(description = "消息队列查询DTO")
public class MsgQueueQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "队列类型")
    private String queueType;
    
    @Schema(description = "队列名称")
    private String queueName;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "最小优先级")
    private Integer priorityMin;
    
    @Schema(description = "最大优先级")
    private Integer priorityMax;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "asc")
    private String sortOrder = "asc";
}