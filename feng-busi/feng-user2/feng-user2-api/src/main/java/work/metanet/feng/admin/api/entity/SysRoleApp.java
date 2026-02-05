package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色应用表(SysRoleApp)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysRoleApp")
@EqualsAndHashCode(callSuper = true)
public class SysRoleApp extends Model<SysRoleApp> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**角色ID*/    
    @Schema(description = "角色ID")
    private Integer roleId;

    /**应用ID*/    
    @Schema(description = "应用ID")
    private Integer appId;

}