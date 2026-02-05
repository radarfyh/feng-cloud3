package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import work.metanet.feng.common.core.constant.enums.Gender;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("prm_contact")
@Schema(description = "联系人实体")
public class PrmContact extends Model<PrmContact> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "联系人ID", example = "1001")
    private Integer id;

    @Schema(description = "关联员工ID", example = "2001")
    private Integer staffId;

    @Schema(description = "所属客户ID", example = "3001")
    private Integer customerId;

    @Schema(description = "关系类型", example = "partner")
    private String relationshipType;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "性别", example = "MALE")
    private Gender gender;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobile;

    @Schema(description = "固定电话", example = "010-88889999")
    private String phone;

    @Schema(description = "电子邮箱", example = "contact@example.com")
    private String email;

    @Schema(description = "职务代码", example = "A")
    private String position;

    @Schema(description = "是否关键决策人", 
            defaultValue = "0",
            example = "1")
    private String isDecisionMaker;

    @Schema(description = "是否主要联系人", 
            defaultValue = "0",
            example = "1")
    private String isPrimary;

    @Schema(description = "直属上级ID", example = "1002")
    private Integer superiorId;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "海淀区")
    private String district;

    @Schema(description = "详细地址", example = "中关村南大街5号")
    private String address;

    @Schema(description = "最后联系时间", example = "2023-01-01 10:00:00")
    private LocalDateTime contactTime;

    @Schema(description = "最后上门时间", example = "2023-01-15 14:30:00")
    private LocalDateTime visitTime;

    @Schema(description = "最新情况", example = "近期有采购意向")
    private String latestStatus;

    @Schema(description = "关系亲密度", 
            minimum = "0", 
            maximum = "1", 
            defaultValue = "0.5",
            example = "0.75")
    private Double intimacyScore;

    @Schema(description = "互动频次", example = "12")
    private Integer contactFrequency;

    @Schema(description = "所属机构编码", example = "fengyun")
    private String organCode;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2023-01-01 00:00:00")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2023-01-02 00:00:00")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记", 
            allowableValues = {"0", "1"}, 
            defaultValue = "0",
            example = "0")
    private String delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}