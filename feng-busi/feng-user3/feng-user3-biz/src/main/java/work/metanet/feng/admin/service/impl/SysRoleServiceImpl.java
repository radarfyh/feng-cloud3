package work.metanet.feng.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.RoleMenuDTO;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.entity.SysRoleMenu;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.entity.SysUserRole;
import work.metanet.feng.admin.api.vo.SysRoleVO;
import work.metanet.feng.admin.mapper.SysRoleMapper;
import work.metanet.feng.admin.service.SysRoleMenuService;
import work.metanet.feng.admin.service.SysRoleService;
import work.metanet.feng.admin.service.SysUserRoleService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 系统角色表(SysRole)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private SysRoleMenuService sysRoleMenuService;

    private final SysUserService sysUserService;

    private final SysUserRoleService sysUserRoleService;

    private final RedisTemplate redisTemplate;

    /**
     * 通过用户ID，查询角色信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysRoleVO> listRolesByUserId(Integer userId) {
        return baseMapper.listRolesByUserId(userId);
    }


    /**
     * 根据角色菜单列表
     *
     * @param roleMenuDTO 角色&菜单列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
    @Override
    public Boolean updateRoleMenus(RoleMenuDTO roleMenuDTO) {
        if (StrUtil.isNotBlank(roleMenuDTO.getApplicationCode())) {
            //应用编码不为空，则单个应用配置权限,先查出当前角色对应该应用下的所有权限
            List<Integer> menuIds = baseMapper.selectMenuIdListByRoleId(roleMenuDTO.getApplicationCode(), roleMenuDTO.getRoleId());
            //再删除该角色对应该应用下的所有权限
            sysRoleMenuService.removeMenuIdsByRoleId(roleMenuDTO.getRoleId(), menuIds);
        } else {
            //应用编码为空，则批量配置所有应用权限，删除该角色对应的所有权限
            sysRoleMenuService.remove(Wrappers.<SysRoleMenu>update().lambda().eq(SysRoleMenu::getRoleId, roleMenuDTO.getRoleId()));
        }
        if (StrUtil.isNotBlank(roleMenuDTO.getMenuIds())) {
            sysRoleMenuService.saveRoleMenus(roleMenuDTO.getRoleId(), roleMenuDTO.getMenuIds());
        }
        //查询该角色id对应的所有用户名集合
        List<UserInfo> list = new ArrayList<>();
        List<SysUser> userList = sysUserService.findRoleByUsers(roleMenuDTO.getRoleId());
        for (SysUser sysUser : userList) {
            list.add(sysUserService.findUserInfo(sysUser));
        }
        if (null != list && list.size() > 0) {
            //将该用户对应的所有的菜单权限，缓存到redis
            for (UserInfo userInfo : list) {
                String key = String.format("%s:%s", CacheConstants.USER_INFO, userInfo.getSysUser().getUsername());
                redisTemplate.opsForValue().set(key, userInfo, 1, TimeUnit.DAYS);
            }
        }
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R delete(Integer id) {
    	
    	// 判断数据库中的内置字段 值是否为1，是1则表示内置角色，不允许删除，在控制器判断过是否属于内置角色枚举类的一员，此处是双重保险
        SysRole sysRole = this.getById(id);
        if ("1".equals(sysRole.getIsDefault())) {
            return R.failed("内置角色不允许删除");
        }
        Long count = sysUserRoleService.count(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getRoleId, id));
        if (count > 0) {
            return R.failed("该角色已绑定用户，请先解绑再删除该角色");
        }
        //删除该角色所有权限再删除该角色
        sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, id));
        return R.ok(this.removeById(id));
    }

    @Override
    public List<SysRoleVO> getRoleListByUser(Integer userId) {
        return baseMapper.listRolesByUserId(userId);
    }

    @Override
    public List<SysRoleVO> listAll() {
        return baseMapper.listAll();
    }

    @Override
    public List<Integer> getRoleIdsByCode(String organCode, List<String> roleCodes) {
        List<Integer> list = new ArrayList<>();
        for (String roleCode : roleCodes) {
            SysRole sysRole = getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getOrganCode, organCode).eq(SysRole::getRoleCode, roleCode));
            if (Objects.nonNull(sysRole)) list.add(sysRole.getId());
        }
        return list;
    }
    
    @Override
    public List<Integer> getRoleIdsByCode(List<String> roleCodes) {
        List<Integer> list = new ArrayList<>();
        for (String roleCode : roleCodes) {
            SysRole sysRole = getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, roleCode));
            if (Objects.nonNull(sysRole)) list.add(sysRole.getId());
        }
        return list;
    }
}