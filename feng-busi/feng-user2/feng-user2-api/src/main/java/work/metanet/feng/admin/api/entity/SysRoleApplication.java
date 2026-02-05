package work.metanet.feng.admin.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色外部应用关联表(RoleApplication)实体类
 *
 * @author edison
 * @since 2022-08-09 10:40:05
 */
@Data
@Schema(description = "角色外部应用关联表")
@EqualsAndHashCode(callSuper = true)
public class SysRoleApplication extends Model<SysRoleApplication> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Integer roleId;
    /**
     * 外部应用id
     */
    @Schema(description = "外部应用id")
    private Integer applicationId;


}
