package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 联系人关系网络实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("prm_social_relationship")
@Schema(description = "联系人关系网络实体")
public class PrmSocialRelationship extends Model<PrmSocialRelationship> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "关系ID", example = "1")
    private Integer id;

    @Schema(description = "联系人A ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Integer contactA;

    @Schema(description = "联系人B ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1002")
    private Integer contactB;

    @Schema(description = "关系类型", example = "colleague")
    private String relationshipType;

    @Schema(description = "是否双向关系", 
            defaultValue = "1",
            example = "1")
    private String biDirectional;

    @Schema(description = "关系亲密度", 
            minimum = "0", 
            maximum = "1", 
            defaultValue = "0.5",
            example = "0.75")
    private Double intimacyScore;

    @Schema(description = "最后联系时间", example = "2023-01-01 10:00:00")
    private LocalDateTime contactTime;

    @Schema(description = "互动频次(次/月)", example = "5")
    private Integer contactFrequency;

    @Schema(description = "互动记录摘要(JSON格式)，备用", 
            example = "{\"lastMeeting\":\"2023-01-01\",\"topics\":[\"项目合作\",\"技术交流\"]}")
    private String interactionHistory;

    @Schema(description = "备注", example = "大学同学，关系密切")
    private String remark;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", type = "string", format = "date-time")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间", type = "string", format = "date-time")
    private Date updateTime;

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
