package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单表(SysRoleMenu)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 更新角色菜单
     *
     * @param roleId  角色id
     * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
     * @return
     */
    Boolean saveRoleMenus(Integer roleId, String menuIds);

    /**
     * 删除该角色对应该的菜单集合
     *
     * @param menuIds:
     * @param roleId:
     * @return: void
     **/
    void removeMenuIdsByRoleId(Integer roleId, List<Integer> menuIds);
}