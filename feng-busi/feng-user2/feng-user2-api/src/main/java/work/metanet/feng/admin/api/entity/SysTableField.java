package work.metanet.feng.admin.api.entity;
 
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
 * 数据字段(DfTableField)实体类
 */
@Data
@Schema(description = "数据字段")
@EqualsAndHashCode(callSuper = true)
public class SysTableField extends Model<SysTableField> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    
    /**表ID*/    
    @Schema(description = "表ID")
    private Integer tableId;
    /**字段说明*/    
    @Schema(description = "字段说明")
    private String fieldComment;
    /**字段名*/    
    @Schema(description = "字段名")
    private String fieldName;
    /**字段中文名*/    
    @Schema(description = "字段中文名")
    private String fieldNameChinese;
    /**业务关键名词*/    
    @Schema(description = "业务关键名词")
    private String busiTerm;
    /**字段类型*/    
    @Schema(description = "字段类型")
    private String fieldType;
    /**字段长度*/    
    @Schema(description = "字段长度")
    private Integer fieldSize = 0 ;
    /**列位置*/    
    @Schema(description = "列位置")
    private Integer fieldSequence;
    /**必填 0 否 1 是 */    
    @Schema(description = "必填 0 否 1 是 ")
    private String fieldRequired;
    /**所属分级*/    
    @Schema(description = "所属分级")
    private String fieldGrade;
    /**映射类型 local本地, other 其他*/    
    @Schema(description = "映射类型 local本地、other其他")
    private String mappingType;
    /**映射*/    
    @Schema(description = "映射")
    private String rangeMapping;
    /**键值 1 primary 2 foreign  3 unique*/    
    @Schema(description = "键值 1 primary 2 foreign  3 unique")
    private String fieldKey;
    /**索引*/    
    @Schema(description = "索引")
    private String fieldIndex;
    /*是否可查询 0 否 1 是 */
    @Schema(description = "是否可查询 0 否 1 是")
    private String isQuery;
    /**是否关联 0 否 1 是 */
    @Schema(description = "是否表关联字段 0 否 1 是  (一张表中只能有一个)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String associated;
    @Schema(description = "标识符")
    private String  identifier;
    
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
