package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 联系人简略视图对象
 */
@Data
@Schema(description = "联系人简略信息")
public class ContactSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "联系人ID", example = "1001")
    private Integer id;
    
    @Schema(description = "所属客户ID", example = "3001")
    private Integer customerId;
    
    @Schema(description = "所属客户")
    private CustomerSimpleVO customer;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "职务代码", example = "DEPARTMENT_STAFF")
    private String position;
    
    @Schema(description = "职务名称", example = "部门职员")
    private String positionName;

    @Schema(description = "是否主要联系人，是1，否0", example = "1")
    private String isPrimary;

    @Schema(description = "手机号码（脱敏）", example = "138****1234")
    private String mobile;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "最后联系时间", example = "2023-01-01 10:00:00")
    private String contactTime;

    @Schema(description = "关系亲密度（0-1）", example = "0.75")
    private Double intimacyScore;
}