package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "系统配置统计VO")
public class SystemConfigStatisticsVO {
    
    @Schema(description = "总配置数")
    private Long totalCount;
    
    @Schema(description = "已启用配置数")
    private Long enabledCount;
    
    @Schema(description = "已禁用配置数")
    private Long disabledCount;
    
    @Schema(description = "分类统计")
    private Map<String, Long> categoryStats;
    
    @Schema(description = "类型统计")
    private Map<String, Long> typeStats;
    
    @Schema(description = "启用率")
    private Double enableRate;
}