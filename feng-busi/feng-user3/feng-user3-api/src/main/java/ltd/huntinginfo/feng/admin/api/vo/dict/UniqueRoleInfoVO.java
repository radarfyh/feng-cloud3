package ltd.huntinginfo.feng.admin.api.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 统一角色信息视图对象
 */
@Data
@Schema(name = "统一角色信息VO", 
       description = "统一角色信息视图对象，包含角色基础信息和状态信息")
public class UniqueRoleInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID（自增）", 
           example = "1")
    private Integer id;

    @Schema(description = "统一角色编号", 
           example = "ROLE001")
    private String roleId;

    @Schema(description = "角色名称", 
           example = "系统管理员")
    private String name;

    @Schema(description = "角色代码", 
           example = "admin")
    private String code;

    @Schema(description = "显示顺序", 
           example = "1")
    private Integer sort;

    @Schema(description = "状态(0-禁用 1-启用)", 
           example = "1")
    private Integer status;

    @Schema(description = "角色类型(参见RoleTypeEnum枚举类)", 
           example = "1")
    private Integer type;

    @Schema(description = "备注", 
           example = "系统最高权限角色")
    private String remark;

}

