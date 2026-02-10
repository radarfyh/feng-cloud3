package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "广播接收记录统计VO")
public class BroadcastReceiveRecordStatisticsVO {
    
    @Schema(description = "广播ID")
    private String broadcastId;
    
    @Schema(description = "总记录数")
    private Long totalCount;
    
    @Schema(description = "待送达数量")
    private Long pendingCount;
    
    @Schema(description = "已送达数量")
    private Long deliveredCount;
    
    @Schema(description = "送达失败数量")
    private Long failedCount;
    
    @Schema(description = "未读数量")
    private Long unreadCount;
    
    @Schema(description = "已读数量")
    private Long readCount;
    
    @Schema(description = "送达率")
    private Double deliveredRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
}