package ltd.huntinginfo.feng.admin.api.entity.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 机关代码表实体类（政府单位、民间组织等）
 */
@Data
@TableName("gov_agency")
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 空集合不序列化
public class GovAgency implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 主键id自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 政府机关名称
     */
    private String name;

    /**
     * 政府机关代码
     */
    private String code;

    /**
     * 上级政府机关代码
     */
    private String parentCode;

    /**
     * 政府机关级别
     */
    private Integer level;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 子政府机关列表(非数据库字段)
     */
    @TableField(exist = false)
    private List<GovAgency> children;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除标记(0:正常,1:删除)
     */
    @TableLogic
    private String delFlag;
}