package work.metanet.feng.admin.api.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.entity.SysUser;

@Data
@Accessors(chain = true)
public class AiUserInfo extends SysUser implements Serializable {
    private static final long serialVersionUID = 547891924677981054L;

    /**
     * 用户所属部门
     */
    private SysDepartment dept;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;

    /**
     * 用户角色列表
     */
    private List<SysRole> roles;

    /**
     * 用户权限标识
     */
    private Set<String> perms;
}
