package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/*
 * 用于ump_msg_statistics统计服务
 */
@Data
@Schema(description = "性能统计VO")
public class PerformanceStatisticsVO {
    
    @Schema(description = "总发送数量")
    private Long totalSendCount;
    
    @Schema(description = "平均处理时间(ms)")
    private Double avgProcessTime;
    
    @Schema(description = "平均接收时间(ms)")
    private Double avgReceiveTime;
    
    @Schema(description = "平均阅读时间(ms)")
    private Double avgReadTime;
    
    @Schema(description = "最大处理时间(ms)")
    private Integer maxProcessTime;
    
    @Schema(description = "最大接收时间(ms)")
    private Integer maxReceiveTime;
    
    @Schema(description = "最大阅读时间(ms)")
    private Integer maxReadTime;
    
    @Schema(description = "P95处理时间(ms)")
    private Integer p95ProcessTime;
    
    @Schema(description = "P95接收时间(ms)")
    private Integer p95ReceiveTime;
    
    @Schema(description = "P95阅读时间(ms)")
    private Integer p95ReadTime;
    
    @Schema(description = "慢消息比例")
    private Double slowMessageRate;
}