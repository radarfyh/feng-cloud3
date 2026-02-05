package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客户基础DTO")
public class CustomerDTO {
    @Schema(description = "客户ID", example = "123")
    private Long id;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "风云科技")
    private String name;
    
    @Schema(description = "客户来源编码", example = "web")
    private String sourceCode;
    
    @Schema(description = "所属机构ID", example = "1001")
    private Long organId;
    
    @Schema(description = "上级客户ID", example = "1000")
    private Long parentId;
    
    @Schema(description = "关系类型", allowableValues = {"customer","agent","supplier"}, example = "customer")
    private String relationshipType;

    @Schema(description = "客户级别", example = "A")
    private String levelCode;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobile;
}