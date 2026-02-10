package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "回调记录统计VO")
public class CallbackStatisticsVO {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "总记录数")
    private Long totalCount;
    
    @Schema(description = "待处理数量")
    private Long pendingCount;
    
    @Schema(description = "处理中数量")
    private Long processingCount;
    
    @Schema(description = "成功数量")
    private Long successCount;
    
    @Schema(description = "失败数量")
    private Long failedCount;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "平均重试次数")
    private Double avgRetryCount;
    
    @Schema(description = "平均响应时间(ms)")
    private Double avgResponseTime;
    
    @Schema(description = "成功率")
    private Double successRate;
}