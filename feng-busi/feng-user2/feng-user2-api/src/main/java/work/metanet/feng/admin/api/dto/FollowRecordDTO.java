package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.FollowType;
import work.metanet.feng.common.core.util.ValidGroup;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "跟踪记录数据传输对象")
public class FollowRecordDTO {

    @NotNull(groups = ValidGroup.Update.class, message = "记录ID不能为空")
    @Schema(description = "记录ID(更新时必填)", example = "1")
    private Integer id;

    @Schema(description = "关联的关系ID", example = "1001")
    private Integer relationshipId;

    @Schema(description = "关联的客户ID", example = "2001")
    private Integer customerId;

    @Schema(description = "关联的联系人ID", example = "3001")
    private Integer contactId;

    @Schema(description = "跟踪方式", example = "phone")
    private String followType;

    @Schema(description = "跟踪内容", example = "讨论产品需求")
    private String content;

    @Schema(description = "跟踪结果", example = "意向明确")
    private String result;
    
    @Schema(description = "跟进时间", example = "2023-11-31 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followTime;
    
    @Schema(description = "下次跟进时间", example = "2023-12-31 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    @Schema(description = "跟进人ID", example = "4001")
    private Integer staffId;

    @Schema(description = "跟进人姓名", example = "张三")
    private String staffName;
}