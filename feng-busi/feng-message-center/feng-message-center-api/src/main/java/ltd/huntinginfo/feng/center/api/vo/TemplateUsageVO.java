package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模板使用统计VO")
public class TemplateUsageVO {
    
    @Schema(description = "模板代码")
    private String templateCode;
    
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "使用日期")
    private String usageDate;
    
    @Schema(description = "使用次数")
    private Long usageCount;
    
    @Schema(description = "成功次数")
    private Long successCount;
    
    @Schema(description = "失败次数")
    private Long failedCount;
    
    @Schema(description = "成功率")
    private Double successRate;
}