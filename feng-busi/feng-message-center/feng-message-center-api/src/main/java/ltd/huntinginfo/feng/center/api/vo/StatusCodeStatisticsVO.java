package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "状态码统计VO")
public class StatusCodeStatisticsVO {
    
    @Schema(description = "总状态码数")
    private Long totalCount;
    
    @Schema(description = "已启用状态码数")
    private Long enabledCount;
    
    @Schema(description = "已禁用状态码数")
    private Long disabledCount;
    
    @Schema(description = "分类统计")
    private Map<String, Long> categoryStats;
    
    @Schema(description = "最终状态码数")
    private Long finalCount;
    
    @Schema(description = "非最终状态码数")
    private Long nonFinalCount;
    
    @Schema(description = "可重试状态码数")
    private Long retryableCount;
    
    @Schema(description = "不可重试状态码数")
    private Long nonRetryableCount;
    
    @Schema(description = "启用率")
    private Double enableRate;
}