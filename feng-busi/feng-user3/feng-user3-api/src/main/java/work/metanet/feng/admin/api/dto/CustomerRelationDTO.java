package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客户关系DTO")
public class CustomerRelationDTO {
    @Schema(description = "关系ID", example = "123")
    private Long id;

    @Schema(description = "客户ID", example = "1")
    private Integer customerId;
    
    @Schema(description = "关联客户ID", example = "1")
    private Integer relatedCustomerId;
    
    @Schema(description = "关联客户类型编码，关联数据字典customer_relation_type", example = "customer")
    private String relationType;
    
    @Schema(description = "关联强度,0到1之间", example = "0.5")
    private Double relationStrength;
}
