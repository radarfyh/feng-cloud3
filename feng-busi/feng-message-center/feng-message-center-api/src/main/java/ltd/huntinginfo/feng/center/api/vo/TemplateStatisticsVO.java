package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "模板统计VO")
public class TemplateStatisticsVO {
    
    @Schema(description = "总模板数")
    private Long totalCount;
    
    @Schema(description = "已启用模板数")
    private Long enabledCount;
    
    @Schema(description = "已禁用模板数")
    private Long disabledCount;
    
    @Schema(description = "类型统计")
    private Map<String, Long> typeStats;
    
    @Schema(description = "启用率")
    private Double enableRate;
}