package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/*
 * 用于ump_system_log日志服务
 */
@Data
@Schema(description = "性能统计VO")
public class LogPerformanceStatisticsVO {
    
    @Schema(description = "总请求数")
    private Long totalRequests;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "最大耗时(ms)")
    private Integer maxCostTime;
    
    @Schema(description = "最小耗时(ms)")
    private Integer minCostTime;
    
    @Schema(description = "P95耗时(ms)")
    private Integer p95CostTime;
    
    @Schema(description = "P99耗时(ms)")
    private Integer p99CostTime;
    
    @Schema(description = "平均内存使用(KB)")
    private Double avgMemoryUsage;
    
    @Schema(description = "最大内存使用(KB)")
    private Integer maxMemoryUsage;
    
    @Schema(description = "慢请求比例")
    private Double slowRequestRate;
}