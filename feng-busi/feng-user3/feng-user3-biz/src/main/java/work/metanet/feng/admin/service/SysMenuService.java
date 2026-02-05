package work.metanet.feng.admin.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysMenu;
import work.metanet.feng.common.core.util.R;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限表(SysMenu)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 通过角色编号查询URL 权限
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> findMenuByRoleId(Integer roleId);

    /**
     * 查询菜单
     *
     * @param voSet
     * @param type 查询类型："all"表示查询所有，其他和MenuTypeEnum代码一致
     * @param parentId
     * @return
     */
    List<Tree<Integer>> filterMenu(Set<SysMenu> voSet, String type, Integer parentId);

    /*
     *
     * @Description: 通过角色编号查询菜单集合【过滤按钮和菜单】
     * @author edison
     * @date 2021/3/30
     * @param: roleId 角色id
     * @param: type 为menu过滤按钮，不传查所有【菜单+按钮】
     * @return
     */
    List<SysMenu> getRoleList(Integer roleId, String type);

    /**
     * 批量删除菜单
     *
     * @param id:
     * @return: java.lang.Boolean
     **/
    R deleteMenuById(Integer id);

    /**
     * 更新菜单信息
     *
     * @param sysMenu 菜单信息
     * @return 成功、失败
     */
    Boolean updateMenuById(SysMenu sysMenu);

	List<SysMenu> findMenuByRoleIdAndAppCode(Integer roleId, String appCode, Integer parentId);

	Boolean saveMenu(SysMenu sysMenu);
}