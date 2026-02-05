package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户科室表(SysUserDepartment)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Data
@Schema(description = "SysUserDepartment")
@EqualsAndHashCode(callSuper = true)
public class SysUserDepartment extends Model<SysUserDepartment> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**用户ID*/    
    @Schema(description = "用户ID")
    private Integer userId;
    /**科室ID*/    
    @Schema(description = "科室ID")
    private Integer deptId;

}