package work.metanet.feng.admin.api.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema(description = "跟踪记录视图对象")
public class FollowRecordVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "记录ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "关联的关系ID", example = "1001")
    private Long relationshipId;

    @Schema(description = "关联的客户ID", example = "2001")
    private Long customerId;
    
    @Schema(description = "关联的客户名称", example = "华为技术")
    private String customerName;

    @Schema(description = "关联的联系人ID", requiredMode = RequiredMode.REQUIRED, example = "3001")
    private Long contactId;
    
    @Schema(description = "关联的联系人姓名", example = "张三")
    private String contactName;

    @Schema(description = "跟踪方式编码", example = "phone")
    private String followType;
    
    @Schema(description = "跟踪方式名称", requiredMode = RequiredMode.REQUIRED, example = "电话")
    private String followTypeName;

    @Schema(description = "跟踪内容", example = "讨论产品需求")
    private String content;

    @Schema(description = "跟踪结果", requiredMode = RequiredMode.REQUIRED, example = "意向明确")
    private String result;
    
    @Schema(description = "跟进时间", example = "2023-11-31 10:00:00")
    private String followTime;

    @Schema(description = "下次跟进时间", example = "2023-12-31 10:00:00")
    private String nextFollowTime;

    @Schema(description = "跟进人ID", example = "4001")
    private Long staffId;

    @Schema(description = "跟进人姓名", example = "张三")
    private String staffName;

    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private String createTime;
}