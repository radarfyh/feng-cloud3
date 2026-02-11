package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用统计VO")
public class AppStatisticsVO {
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "请求次数")
    private Long requestCount;
    
    @Schema(description = "成功次数")
    private Long successCount;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "最后请求时间")
    private String lastRequestTime;
    
    @Schema(description = "频繁API")
    private String frequentApi;
    
    @Schema(description = "成功率")
    private Double successRate;
}