package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "消息统计汇总VO")
public class MsgStatisticsSummaryVO {
    
    @Schema(description = "总发送数量")
    private Long totalSendCount;
    
    @Schema(description = "总发送成功数量")
    private Long totalSendSuccessCount;
    
    @Schema(description = "总发送失败数量")
    private Long totalSendFailedCount;
    
    @Schema(description = "总接收数量")
    private Long totalReceiveCount;
    
    @Schema(description = "总阅读数量")
    private Long totalReadCount;
    
    @Schema(description = "总错误数量")
    private Long totalErrorCount;
    
    @Schema(description = "总重试数量")
    private Long totalRetryCount;
    
    @Schema(description = "平均处理时间(ms)")
    private Double avgProcessTime;
    
    @Schema(description = "平均接收时间(ms)")
    private Double avgReceiveTime;
    
    @Schema(description = "平均阅读时间(ms)")
    private Double avgReadTime;
    
    @Schema(description = "发送成功率")
    private Double sendSuccessRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
    
    @Schema(description = "错误率")
    private Double errorRate;
    
    @Schema(description = "重试率")
    private Double retryRate;
}