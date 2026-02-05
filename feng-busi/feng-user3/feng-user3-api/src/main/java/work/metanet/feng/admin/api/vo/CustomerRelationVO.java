package work.metanet.feng.admin.api.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客户关系视图")
public class CustomerRelationVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "关系ID", example = "123456")
    private Integer id;
    
    @Schema(description = "客户ID", example = "1")
    private Integer customerId;
    @Schema(description = "客户信息，对应customerId", example = "{}")
    private PrmCustomerVO customerInfo;
    
    @Schema(description = "关联客户ID", example = "1")
    private Integer relatedCustomerId;
    @Schema(description = "关联客户信息，对应relatedCustomerId", example = "{}")
    private PrmCustomerVO relatedCustomerInfo;
    
    @Schema(description = "关联客户类型编码，关联数据字典customer_relation_type", example = "customer")
    private String relationType;
    
    @Schema(description = "关联客户类型名称，关联数据字典customer_relation_type", example = "客户")
    private String relationTypeName;    
    
    @Schema(description = "关联强度,0到1之间", example = "0.5")
    private Double relationStrength;
    
    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", type = "string", format = "date-time")
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @Schema(description = "修改时间", type = "string", format = "date-time")
    private LocalDateTime updateTime;
}
