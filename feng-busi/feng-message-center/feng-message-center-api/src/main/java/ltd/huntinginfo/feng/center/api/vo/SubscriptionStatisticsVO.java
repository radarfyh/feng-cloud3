package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订阅统计VO")
public class SubscriptionStatisticsVO {
    
    @Schema(description = "总订阅数")
    private Long totalCount;
    
    @Schema(description = "活跃订阅数")
    private Long activeCount;
    
    @Schema(description = "非活跃订阅数")
    private Long inactiveCount;
    
    @Schema(description = "推送模式订阅数")
    private Long pushModeCount;
    
    @Schema(description = "轮询模式订阅数")
    private Long pollModeCount;
    
    @Schema(description = "总消息数")
    private Long totalMessages;
    
    @Schema(description = "平均消息数")
    private Double avgMessages;
    
    @Schema(description = "活跃率")
    private Double activeRate;
}