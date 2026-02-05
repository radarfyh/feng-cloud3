package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.ContactRelationshipType;
import work.metanet.feng.common.core.constant.enums.StringWhether;
import work.metanet.feng.common.core.util.ValidGroup;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "联系人关系数据传输对象")
public class SocialRelationshipDTO {

    @NotNull(groups = ValidGroup.Update.class, message = "关系ID不能为空")
    @Schema(description = "关系ID(更新时必填)", example = "1")
    private Long id;

    @NotNull(message = "联系人A不能为空")
    @Schema(description = "联系人A ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long contactA;

    @NotNull(message = "联系人B不能为空")
    @Schema(description = "联系人B ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1002")
    private Long contactB;

    @Schema(description = "关系类型代码", example = "colleague")
    private String relationshipType;
    
    @Schema(description = "是否双向关系,1是，0否", example = "1")
    private String biDirectional;

    @Schema(description = "关系亲密度", example = "0.75")
    private Double intimacyScore;

    @Schema(description = "最后联系时间", example = "2023-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime contactTime;

    @Schema(description = "备注", example = "大学同学")
    private String remark;
}