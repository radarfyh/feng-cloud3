package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.RoleMenuDTO;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.vo.SysRoleVO;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 系统角色表(SysRole)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 通过用户ID，查询角色信息
     *
     * @param userId
     * @return
     */
    List<SysRoleVO> listRolesByUserId(Integer userId);

    /**
     * 根据角色id配置菜单和操作权限
     *
     * @param roleMenuDTO 角色&菜单列表
     * @return
     */
    Boolean updateRoleMenus(RoleMenuDTO roleMenuDTO);

    /**
     * 删除角色
     *
     * @param id:
     * @return: work.metanet.feng.common.core.util.R
     **/
    R delete(Integer id);

    /**
     * 获取当前用户的角色列表
     *
     * @param userId:
     * @return R
     */
    List<SysRoleVO> getRoleListByUser(Integer userId);

    /**
     * 获取所有角色列表
     *
     * @return R
     */
    List<SysRoleVO> listAll();

    /**
     * 通过团队角色编码集合查询角色id集合
     *
     * @param organCode
     * @param roleCodes
     * @return
     */
    List<Integer> getRoleIdsByCode(String organCode, List<String> roleCodes);
    /**
     * 通过角色编码集合查询角色id集合
     */
	List<Integer> getRoleIdsByCode(List<String> roleCodes);
}