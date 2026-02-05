package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "客户基础视图对象")
public class PrmCustomerVO implements Serializable {
	private static final long serialVersionUID = 1L;
    
    @Schema(description = "客户ID", example = "1001")
    private Long id;
    
    @Schema(description = "机构ID，为空表示该客户不存在于机构表", example = "1001")
    private Long organId;
    
    @Schema(description = "客户名称", example = "风云科技")
    private String name;
    
    @Schema(description = "客户关系类型代码")
    private String relationshipType;
    
    @Schema(description = "客户关系类型名称")
    private String relationshipTypeName;
    
    @Schema(description = "客户来源编码", example = "ad")
    private String sourceCode;
   
    @Schema(description = "客户来源名称", example = "公告公司")
    private String sourceName;
    
    @Schema(description = "上级客户ID", example = "1000")
    private Long parentId;
    
    @Schema(description = "上级客户简略信息")
    private CustomerSimpleVO parentCustomer;
    
    @Schema(description = "手机号码（脱敏）", example = "138****1234")
    private String mobile;
    
    @Schema(description = "客户级别编码", example = "A")
    private String levelCode;
    
    @Schema(description = "客户级别名称", example = "A类")
    private String levelName;
    
    @Schema(description = "最后跟踪时间", example = "2023-01-01 14:30:00")
    private Date followTime;
    
    @Schema(description = "客户级别名称")
    private List<ContactSimpleVO> contacts;
}