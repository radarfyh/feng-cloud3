package work.metanet.feng.admin.api.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "联系人关系视图对象")
public class SocialRelationshipVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "关系ID", example = "1")
    private Long id;
    
    @Schema(description = "联系人A ID", example = "1001")
    private Long contactA;
    @Schema(description = "联系人A信息")
    private ContactSimpleVO contactAInfo;
    
    @Schema(description = "联系人B ID", example = "1002")
    private Long contactB;
    @Schema(description = "联系人B信息")
    private ContactSimpleVO contactBInfo;

    @Schema(description = "关系类型", example = "colleague")
    private String relationshipType;
    
    @Schema(description = "关系类型名称", example = "同事")
    private String relationshipTypeName;

    @Schema(description = "是否双向关系,1是，0否", example = "1")
    private String biDirectional;

    @Schema(description = "关系亲密度", example = "0.75")
    private Double intimacyScore;

    @Schema(description = "最后联系时间", example = "2023-01-01 10:00:00")
    private String contactTime;

    @Schema(description = "互动频次", example = "5")
    private Integer contactFrequency;

    @Schema(description = "备注", example = "大学同学")
    private String remark;
}