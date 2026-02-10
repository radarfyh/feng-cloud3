package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统配置查询DTO")
public class SystemConfigQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "配置键")
    private String configKey;
    
    @Schema(description = "配置类型")
    private String configType;
    
    @Schema(description = "配置类别")
    private String category;
    
    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "asc")
    private String sortOrder = "asc";
}