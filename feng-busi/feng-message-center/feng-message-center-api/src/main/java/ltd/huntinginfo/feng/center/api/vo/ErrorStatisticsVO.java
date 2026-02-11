package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/*
 * 用于ump_msg_statistics统计服务
 */
@Data
@Schema(description = "错误统计VO")
public class ErrorStatisticsVO {
    
    @Schema(description = "错误类型")
    private String errorType;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "首次发生时间")
    private String firstOccurrence;
    
    @Schema(description = "最后发生时间")
    private String lastOccurrence;
    
    @Schema(description = "影响API")
    private String affectedApi;
    
    @Schema(description = "错误率")
    private Double errorRate;
}