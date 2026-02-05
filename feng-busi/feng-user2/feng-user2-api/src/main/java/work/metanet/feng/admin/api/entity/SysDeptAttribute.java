package work.metanet.feng.admin.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室属性关联表(SysDeptAttribute)实体类
 *
 * @author edison
 * @since 2022-11-01 11:39:31
 */
@Data
@Schema(description = "科室属性关联表")
@EqualsAndHashCode(callSuper = true)
public class SysDeptAttribute extends Model<SysDeptAttribute> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "科室id")
    private Integer deptId;
    /**
     * 科室属性编码
     */
    @Schema(description = "科室属性编码")
    private String deptAttribute;


}
