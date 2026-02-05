package work.metanet.feng.admin.api.entity;
 
import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据表(SysTable)实体类
 */
@Data
@Schema(description = "数据表")
@EqualsAndHashCode(callSuper = true)
public class SysTable extends Model<SysTable> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    
    @Schema(description = "数据源ID")
    private Integer datasourceId;
    
    /**表英文名*/    
    @Schema(description = "表英文名")
    private String tableName;
    /**表中文名*/    
    @Schema(description = "表中文名")
    private String tableNameChinese;

    /**表备注*/    
    @Schema(description = "表备注")
    private String tableComment;
    /**系统*/
    @Schema(description = "系统")
    private String businessSegment;
    /**系统下领域*/
    @Schema(description = "系统下领域")
    private String businessDomain;
    /**标签：JSON字符串*/
    @Schema(description = "标签：JSON字符串")
    private String label;

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
