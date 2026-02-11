package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用统计排名VO")
public class AppStatisticsRankingVO {
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "发送数量")
    private Long sendCount;
    
    @Schema(description = "接收数量")
    private Long receiveCount;
    
    @Schema(description = "阅读数量")
    private Long readCount;
    
    @Schema(description = "错误数量")
    private Long errorCount;
    
    @Schema(description = "成功率")
    private Double successRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
    
    @Schema(description = "错误率")
    private Double errorRate;
}