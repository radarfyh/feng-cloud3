package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@Schema(description = "回调记录查询DTO")
public class CallbackQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "最小重试次数")
    private Integer minRetryCount;
    
    @Schema(description = "最大重试次数")
    private Integer maxRetryCount;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortOrder = "desc";
}