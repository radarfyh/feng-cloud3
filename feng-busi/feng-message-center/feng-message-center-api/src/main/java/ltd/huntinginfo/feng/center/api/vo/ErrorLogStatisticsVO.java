package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/*
 * 用于ump_system_log日志服务
 */
@Data
@Schema(description = "错误日志统计VO")
public class ErrorLogStatisticsVO {
    
    @Schema(description = "错误类型")
    private String errorType;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "首次发生时间")
    private String firstOccurrence;
    
    @Schema(description = "最后发生时间")
    private String lastOccurrence;
    
    @Schema(description = "影响API")
    private String affectedApi;
    
    @Schema(description = "影响应用")
    private String affectedApp;
}