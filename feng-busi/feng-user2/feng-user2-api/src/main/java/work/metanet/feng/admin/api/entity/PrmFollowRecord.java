package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("prm_follow_record")
@Schema(description = "客户跟踪记录实体")
public class PrmFollowRecord extends Model<PrmFollowRecord> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID", example = "1")
    private Integer id;

    @Schema(description = "关联的关系ID", example = "1001")
    private Integer relationshipId;

    @Schema(description = "关联的客户ID", example = "2001")
    private Integer customerId;

    @Schema(description = "关联的联系人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    private Integer contactId;

    @Schema(description = "跟踪方式", 
            allowableValues = {"phone", "visit", "email", "meeting"},
            example = "phone")
    private String followType;

    @Schema(description = "跟踪内容", example = "讨论产品需求")
    private String content;

    @Schema(description = "跟踪结果", example = "意向明确")
    private String result;

    @Schema(description = "跟进时间", example = "2023-11-31 10:00:00")
    private LocalDateTime followTime;
    
    @Schema(description = "下次跟进时间", example = "2023-12-31 10:00:00")
    private LocalDateTime nextFollowTime;

    @Schema(description = "跟进人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4001")
    private Integer staffId;

    @Schema(description = "跟进人姓名", example = "张三")
    private String staffName;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间", example = "2023-01-02 10:00:00")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记", 
            allowableValues = {"0", "1"}, 
            defaultValue = "0",
            example = "0")
    private String delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}