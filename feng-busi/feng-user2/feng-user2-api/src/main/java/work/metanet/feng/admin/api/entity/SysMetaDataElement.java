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

/**
 * 单字段描述实体类
 */
@Data
@Schema(description = "SysMetaDataElement")
@EqualsAndHashCode(callSuper = true)
public class SysMetaDataElement extends Model<SysMetaDataElement> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**分类*/    
    @Schema(description = "分类")
    private String category;
    /**标识符*/    
    @Schema(description = "标识符")
    private String identifier;
    /**名称*/    
    @Schema(description = "名称")
    private String name;
    /**定义*/    
    @Schema(description = "定义")
    private String define;
    /**数据类型*/    
    @Schema(description = "数据类型")
    private String dataType;
    /**表示格式*/    
    @Schema(description = "表示格式")
    private String representFormat;
    /**允许值*/    
    @Schema(description = "允许值")
    private String allowableValue;
    /**版本号*/    
    @Schema(description = "版本号")
    private String versionNo;

    /**更新次数*/    
    @Schema(description = "更新次数")
    private Integer updateCount;

    /**分类ID*/
    @Schema(description = "分类ID")
    private String categoryId;
    /**数据长度*/
    @Schema(description = "数据长度")
    private String dataLength;
    /**单位*/
    @Schema(description = "单位")
    private String unit;
    /**状态*/
    @Schema(description = "状态")
    private String status;
    /**是否标准*/
    @Schema(description = "是否标准")
    private String uncriterion;
    /**备注*/
    @Schema(description = "备注")
    private String remark;
    /**内部标识*/
    @Schema(description = "内部标识")
    private String dataCodeIn;

    @Schema(description = "排序字段")
    @TableField(exist = false)
    private String sortBy;
    
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