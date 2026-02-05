package work.metanet.feng.admin.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员科室关联表(SysStaffDept)实体类
 *
 * @author edison
 * @since 2022-12-26 09:26:08
 */
@Data
@Schema(description = "人员科室关联表")
@EqualsAndHashCode(callSuper = true)
public class SysStaffDept extends Model<SysStaffDept> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**
     * 人员id
     */
    @Schema(description = "人员id")
    private Integer staffId;
    /**
     * 科室id
     */
    @Schema(description = "科室id")
    private Integer departmentId;


}
