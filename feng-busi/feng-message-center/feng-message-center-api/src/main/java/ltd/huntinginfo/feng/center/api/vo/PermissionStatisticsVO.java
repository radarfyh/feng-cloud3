package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限统计VO")
public class PermissionStatisticsVO {
    
    @Schema(description = "总权限数")
    private Long totalCount;
    
    @Schema(description = "已启用权限数")
    private Long enabledCount;
    
    @Schema(description = "已禁用权限数")
    private Long disabledCount;
    
    @Schema(description = "读权限数")
    private Long readCount;
    
    @Schema(description = "写权限数")
    private Long writeCount;
    
    @Schema(description = "所有操作权限数")
    private Long allOperationCount;
    
    @Schema(description = "启用率")
    private Double enableRate;
    
    @Schema(description = "读权限占比")
    private Double readRate;
    
    @Schema(description = "写权限占比")
    private Double writeRate;
    
    @Schema(description = "所有操作权限占比")
    private Double allOperationRate;
}