package work.metanet.feng.admin.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户关系表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "PrmCustomerRelation", description = "客户关系表")
public class PrmCustomerRelation extends Model<PrmCustomer> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "关系ID", example = "123456")
    private Integer id;
    
    @Schema(description = "客户ID", example = "1")
    private Integer customerId;
    
    @Schema(description = "关联客户ID", example = "1")
    private Integer relatedCustomerId;
    
    @Schema(description = "关联客户类型编码，关联数据字典customer_relation_type", example = "customer")
    private String relationType;
    
    @Schema(description = "关联强度,0到1之间", example = "0.5")
    private Double relationStrength;
    
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
