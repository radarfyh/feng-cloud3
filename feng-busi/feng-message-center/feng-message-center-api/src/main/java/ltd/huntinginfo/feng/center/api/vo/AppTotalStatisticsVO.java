package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用统计VO")
public class AppTotalStatisticsVO {
    
    @Schema(description = "总应用数")
    private Long totalCount;
    
    @Schema(description = "直接接入应用数")
    private Long directCount;
    
    @Schema(description = "代理接入应用数")
    private Long agentCount;
    
    @Schema(description = "已启用应用数")
    private Long enabledCount;
    
    @Schema(description = "已禁用应用数")
    private Long disabledCount;
    
    @Schema(description = "启用率")
    private Double enableRate;
}