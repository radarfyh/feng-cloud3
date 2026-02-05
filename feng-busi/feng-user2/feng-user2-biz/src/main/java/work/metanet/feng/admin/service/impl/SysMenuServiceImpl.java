package work.metanet.feng.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysMenu;
import work.metanet.feng.admin.api.entity.SysRoleMenu;
import work.metanet.feng.admin.mapper.SysMenuMapper;
import work.metanet.feng.admin.service.SysMenuService;
import work.metanet.feng.admin.service.SysRoleMenuService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.MenuTypeEnum;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuService sysRoleMenuService;

    @Override
    @Cacheable(value = CacheConstants.MENU_DETAILS, key = "#roleId", unless = "#result.isEmpty()")
    public List<SysMenu> findMenuByRoleId(Integer roleId) {
    	List<SysMenu> list = baseMapper.listMenusByRoleId(roleId);
    	
    	log.debug("{}-->返回结果：{}", Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(list));
    	
        return list;
    }
    
    @Override
    @Cacheable(value = CacheConstants.MENU_DETAILS, key = "(#roleId?:'role') + ':' + (#appCode?:'app') + ':' + (#parentId?:'parent')", unless = "#result.isEmpty()")
    public List<SysMenu> findMenuByRoleIdAndAppCode(Integer roleId, String appCode, Integer parentId) {
    	List<SysMenu> list = baseMapper.listMenusByRoleIdAndAppCode(roleId, appCode, parentId);
    	
    	log.debug("{}-->返回结果：{}", Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(list));
    	
        return list;
    }

    @Override
    public List<Tree<Integer>> filterMenu(Set<SysMenu> all, String type, Integer parentId) {
        type = StrUtil.isNotBlank(type) ? type : "all";
        List<TreeNode<Integer>> collect = all.stream().filter(menuTypePredicate(type)).map(getNodeFunction())
                .collect(Collectors.toList());
        Integer parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
        
        List<Tree<Integer>> tree = TreeUtil.build(collect, parent);
        log.debug("filterMenu-->返回结果：{}", JSONUtil.toJsonStr(tree));
        
        return tree;
    }

    @Override
    public List<SysMenu> getRoleList(Integer roleId, String type) {
        type = StrUtil.isNotBlank(type) ? type : "all";
        List<SysMenu> menuVOList = baseMapper.listMenusByRoleId(roleId);
        
        List<SysMenu> list = menuVOList.stream().filter(menuTypePredicate(type)).collect(Collectors.toList());
        log.debug("{}-->返回结果：{}", Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(list));
        
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
    public R deleteMenuById(Integer id) {
        // 查询父节点为当前节点的节点
        List<SysMenu> menuList = this.list(Wrappers.<SysMenu>query().lambda().eq(SysMenu::getParentId, id));
        if (CollUtil.isNotEmpty(menuList)) {
            return R.failed("请先删除下级菜单");
        }
        sysRoleMenuService.remove(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getMenuId, id));
        // 删除当前菜单
        return R.ok(this.removeById(id));
    }

    @Override
    @CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
    public Boolean updateMenuById(SysMenu sysMenu) {
        return this.updateById(sysMenu);
    }
    
    // 新增方法：保存菜单并清除缓存
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
    public Boolean saveMenu(SysMenu sysMenu) {
        return this.save(sysMenu);
    }
    
    @NotNull
    private Function<SysMenu, TreeNode<Integer>> getNodeFunction() {
        return menu -> {
            TreeNode<Integer> node = new TreeNode<>();
            node.setId(menu.getId());
            node.setName(menu.getMenuName());
            node.setParentId(menu.getParentId());
            node.setWeight(menu.getSort());
            // 扩展属性
            Map<String, Object> extra = new HashMap<>();
            extra.put("icon", menu.getIcon());
            extra.put("path", menu.getPath());
            extra.put("permission", menu.getPermission());
            extra.put("applicationCode", menu.getApplicationCode());
            extra.put("label", menu.getMenuName());
            extra.put("sort", menu.getSort());
            extra.put("keepAlive", menu.getKeepAlive());
            extra.put("type", menu.getType());
            node.setExtra(extra);
            return node;
        };
    }

    /**
     * menu 类型断言
     *
     * @param type 类型：代码和MenuTypeEnum一致
     * @return Predicate
     */
    private Predicate<SysMenu> menuTypePredicate(String type) {
        return sysMenu -> {
            if (MenuTypeEnum.MENU.getCode().equals(type)) {
                //只要菜单
                return !MenuTypeEnum.BUTTON.getType().equals(sysMenu.getType());
            }
            //传所有【菜单+按钮】
            return true;
        };
    }

}