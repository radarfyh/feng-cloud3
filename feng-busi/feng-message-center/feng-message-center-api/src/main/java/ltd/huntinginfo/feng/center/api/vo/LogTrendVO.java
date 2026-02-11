package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志趋势VO")
public class LogTrendVO {
    
    @Schema(description = "时间区间")
    private String timePeriod;
    
    @Schema(description = "总日志数")
    private Long totalCount;
    
    @Schema(description = "认证日志数")
    private Long authCount;
    
    @Schema(description = "操作日志数")
    private Long operationCount;
    
    @Schema(description = "系统日志数")
    private Long systemCount;
    
    @Schema(description = "INFO级别日志数")
    private Long infoCount;
    
    @Schema(description = "WARN级别日志数")
    private Long warnCount;
    
    @Schema(description = "ERROR级别日志数")
    private Long errorCount;
    
    @Schema(description = "错误率")
    private Double errorRate;
}