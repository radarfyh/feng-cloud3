package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限查询DTO")
public class PermissionQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "资源代码")
    private String resourceCode;
    
    @Schema(description = "资源名称")
    private String resourceName;
    
    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortOrder = "desc";
}