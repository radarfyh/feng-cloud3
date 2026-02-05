package work.metanet.feng.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysRoleMenu;
import work.metanet.feng.admin.mapper.SysRoleMenuMapper;
import work.metanet.feng.admin.service.SysRoleMenuService;
import work.metanet.feng.common.core.constant.CacheConstants;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单表(SysRoleMenu)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final CacheManager cacheFengManager;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveRoleMenus(Integer roleId, String menuIds) {
        this.remove(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getRoleId, roleId));

        if (StrUtil.isBlank(menuIds)) {
            return Boolean.TRUE;
        }
        List<SysRoleMenu> roleMenuList = Arrays.stream(menuIds.split(",")).map(id -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(Integer.valueOf(id));
            return roleMenu;
        }).collect(Collectors.toList());

        // 清空userinfo
        cacheFengManager.getCache(CacheConstants.USER_DETAILS).clear();
        roleMenuList.forEach(ele -> baseMapper.insert(ele));
        return Boolean.TRUE;
    }

    /**
     * 删除该角色对应该的菜单集合
     *
     * @param menuIds:
     * @param roleId:
     * @return: void
     **/
    @Override
    public void removeMenuIdsByRoleId(Integer roleId,List<Integer> menuIds) {
        baseMapper.removeMenuIdsByRoleId(roleId, menuIds);
    }
}