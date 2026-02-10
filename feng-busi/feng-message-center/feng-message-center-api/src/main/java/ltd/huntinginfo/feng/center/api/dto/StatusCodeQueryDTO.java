package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "状态码查询DTO")
public class StatusCodeQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "状态码")
    private String statusCode;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "分类")
    private String category;
    
    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "父状态码")
    private String parentCode;
    
    @Schema(description = "是否为最终状态:0-否 1-是")
    private Integer isFinal;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "asc")
    private String sortOrder = "asc";
}