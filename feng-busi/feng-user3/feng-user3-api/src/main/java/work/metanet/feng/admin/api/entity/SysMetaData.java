package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 多字段描述实体类
 */
@Data
@Schema(description = "SysMetaData")
@EqualsAndHashCode(callSuper = true)
public class SysMetaData extends Model<SysMetaData> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "父ID")
    private Integer parentId;
    
    /**分类*/    
    @Schema(description = "分类")
    private String category;
    
    /**标识符*/    
    @Schema(description = "标识符")
    private String identifier;
    
    /**名称*/    
    @Schema(description = "名称")
    private String name;

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