package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "PrmCustomer", description = "客户表(PRM增强)")
public class PrmCustomer extends Model<PrmCustomer> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "客户ID", example = "123456")
    private Integer id;

    @Schema(description = "机构ID，为空表示该客户不存在于机构表", example = "1001")
    private Integer organId;

    @TableField(value = "name", condition = SqlCondition.LIKE)
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "风云科技")
    private String name;

    @Schema(description = "客户关系类型代码")
    private String relationshipType;

    @Schema(description = "客户来源编码", example = "web")
    private String sourceCode;

    @Schema(description = "上级客户ID", example = "1000")
    private Integer parentId;

    @Schema(description = "客户手机号码", example = "13800138000")
    private String mobile;

    @Schema(description = "客户级别编码", example = "A")
    private String levelCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后跟踪时间", type = "string", format = "date-time")
    private LocalDateTime followTime;

    @Schema(description = "跟踪结果", example = "意向强烈")
    private String followResult;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", type = "string", format = "date-time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间", type = "string", format = "date-time")
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
