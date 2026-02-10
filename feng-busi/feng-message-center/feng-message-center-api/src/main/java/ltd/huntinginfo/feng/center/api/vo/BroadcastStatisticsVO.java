package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "广播统计VO")
public class BroadcastStatisticsVO {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "广播类型")
    private String broadcastType;
    
    @Schema(description = "广播总数")
    private Long totalCount;
    
    @Schema(description = "分发中数量")
    private Long distributingCount;
    
    @Schema(description = "已完成数量")
    private Long completedCount;
    
    @Schema(description = "总接收人数")
    private Long totalReceivers;
    
    @Schema(description = "已分发人数")
    private Long distributedReceivers;
    
    @Schema(description = "已接收人数")
    private Long receivedReceivers;
    
    @Schema(description = "已读人数")
    private Long readReceivers;
    
    @Schema(description = "分发率")
    private Double distributeRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
}