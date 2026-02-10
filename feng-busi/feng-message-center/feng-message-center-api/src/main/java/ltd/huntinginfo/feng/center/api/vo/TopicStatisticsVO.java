package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "主题统计VO")
public class TopicStatisticsVO {
    
    @Schema(description = "总主题数")
    private Long totalCount;
    
    @Schema(description = "系统主题数")
    private Long systemCount;
    
    @Schema(description = "自定义主题数")
    private Long customCount;
    
    @Schema(description = "已启用主题数")
    private Long enabledCount;
    
    @Schema(description = "已禁用主题数")
    private Long disabledCount;
    
    @Schema(description = "总订阅者数")
    private Long totalSubscribers;
    
    @Schema(description = "平均订阅者数")
    private Double avgSubscribers;
    
    @Schema(description = "启用率")
    private Double enableRate;
}