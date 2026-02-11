package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "API调用统计VO")
public class ApiCallStatisticsVO {
    
    @Schema(description = "API路径")
    private String apiPath;
    
    @Schema(description = "HTTP方法")
    private String httpMethod;
    
    @Schema(description = "调用次数")
    private Long callCount;
    
    @Schema(description = "成功次数")
    private Long successCount;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "最大耗时(ms)")
    private Integer maxCostTime;
    
    @Schema(description = "成功率")
    private Double successRate;
    
    @Schema(description = "错误率")
    private Double errorRate;
}