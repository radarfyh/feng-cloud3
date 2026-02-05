package work.metanet.feng.admin.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.entity.SysMenu;
import work.metanet.feng.admin.service.SysApplicationService;
import work.metanet.feng.admin.service.SysMenuService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.constant.enums.MenuTypeEnum;
import work.metanet.feng.common.core.constant.enums.OperationTypeEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单权限表(SysMenu)表控制层
 * <p>
 * 本类负责处理与菜单权限相关的请求，包括菜单树的获取、菜单的增删改查等操作。
 * 支持根据用户角色动态加载菜单树，并提供对菜单权限的校验功能。
 * </p>
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sysMenu")
@Tag(name = "菜单模块")
public class SysMenuController {
    /**
     * 服务对象
     */
    private final SysMenuService sysMenuService;

    private final SysApplicationService sysApplicationService;

    /**
     * 获取当前登录用户的菜单树（不包含按钮）
     * <p>
     * 根据用户角色和传入的过滤条件（如菜单类型、父节点ID、应用编码等），动态生成用户的菜单树。
     * 超级管理员可以获取所有菜单，普通用户只能获取其角色对应的菜单。
     * </p>
     *
     * @param type            菜单类型（可选）："menu"表示只获取菜单，不传则获取菜单和按钮
     * @param parentId        父节点ID（可选）：用于指定菜单树的根节点
     * @param applicationCode 应用编码（必填）：用于过滤指定应用的菜单
     * @param sysIsShow       是否显示（可选）：用于过滤指定显示状态的菜单
     * @return 当前用户的树形菜单
     */
    @Operation(summary = "平台下获取当前登录用户菜单树【不需要按钮】")
    @Parameters({@Parameter(name = "type", description = "菜单类型:传menu只要菜单，不传给菜单加按钮", required = false), 
    	@Parameter(name = "parentId", description = "父级id", required = false), 
    	@Parameter(name = "applicationCode", description = "平台应用编码", required = true)})
    @GetMapping("/applicationTree")
    public R getUserMenuTree(String type, Integer parentId, String applicationCode, String sysIsShow) {
        // 参数校验
        if (StrUtil.isBlank(applicationCode)) {
             //return R.failed("应用编码不能为空");
        }
        
        // 获取符合条件的菜单
        Set<SysMenu> all = new HashSet<>();
        List<Integer> roles = SecurityUtils.getRoles();
        
        // 根据 sysIsShow（是否在前端显示） 过滤应用编码
        List<String> applicationCodes = null;
        if (StrUtil.isNotBlank(sysIsShow)) {
            List<SysApplication> list = sysApplicationService.list(Wrappers.<SysApplication>lambdaQuery()
            		.eq(StrUtil.isNotBlank(sysIsShow), SysApplication::getSysIsShow, sysIsShow));
            if (CollectionUtil.isNotEmpty(list)) {
                applicationCodes = list.stream().map(SysApplication::getApplicationCode).collect(Collectors.toList());
            }
        }
        
        // 超级管理员获取所有菜单，普通用户获取角色对应的菜单
        if (roles.contains(BuiltInRoleEnum.ADMIN.getId())) {
            all.addAll(sysMenuService.list(Wrappers.<SysMenu>lambdaQuery()
            		.eq(StrUtil.isNotBlank(applicationCode), SysMenu::getApplicationCode, applicationCode)
            		.eq(parentId != null, SysMenu::getParentId, parentId)));
        } else {
            final Set<SysMenu> finalAll = all;
            SecurityUtils.getRoles().forEach(roleId -> {
            	List<SysMenu> list = sysMenuService.findMenuByRoleId(roleId);
            	finalAll.addAll(list);
            });
        }
        
        if (CollectionUtil.isNotEmpty(applicationCodes)) {
            final List<String> finalApplicationCodes = applicationCodes;

            all = all.stream()
                    .filter(sysMenu -> finalApplicationCodes.contains(sysMenu.getApplicationCode()))
                    .collect(Collectors.toSet());
        }
        
        List<Tree<Integer>> list = sysMenuService.filterMenu(all, type, parentId);
        
        writeLog(Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(list));
        
        return R.ok(list);
    }

    /**
     * 根据条件获取所有菜单树
     *
     * @param applicationCode 应用编码
     * @param parentId        父节点ID
     * @return 树形菜单
     */
    @Operation(summary = "根据条件获取所有菜单树")
    @Parameters({
        @Parameter(name = "parentId", description = "父级id", required = false),
        @Parameter(name = "applicationCode", description = "应用编码", required = false)
    })
    @GetMapping(value = "/tree")
    public R getTree(
        @RequestParam(required = false) Integer parentId,
        @RequestParam(required = false) String applicationCode) {

        final Set<SysMenu> menuSet = new HashSet<>();
        final boolean isAdmin = SecurityUtils.getRoles().contains(BuiltInRoleEnum.ADMIN.getId()); //是否管理员
        final boolean hasAppCode = StrUtil.isNotBlank(applicationCode);
        final Integer parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;

        if (isAdmin) {
            // 构建通用查询条件
            LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery()
            		.eq(parent != null && parent > 0, SysMenu::getParentId, parent)
            		.eq(hasAppCode, SysMenu::getApplicationCode, applicationCode);
            // 管理员直接查询数据库
            menuSet.addAll(sysMenuService.list(queryWrapper));
        } else {
            // 普通用户按角色获取菜单后二次过滤
            SecurityUtils.getRoles().forEach(roleId -> 
            	menuSet.addAll(sysMenuService.findMenuByRoleIdAndAppCode(roleId, applicationCode, parent))
            );
        }

        // 构建树形结构（服务层处理树形逻辑）
        List<Tree<Integer>> menuTree = sysMenuService.filterMenu(menuSet, "all", parent);

        // 记录操作日志
        writeLog(
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            JSONUtil.toJsonStr(menuTree)
        );
        
        return R.ok(menuTree);
    }

    /**
     * 返回角色的菜单集合
     *
     * @param roleId 角色ID
     * @return 属性集合
     */
    @Operation(summary = "返回角色的菜单集合【可过滤菜单和按钮】")
    @Parameters({@Parameter(name = "roleId", description = "角色id", required = true), 
    	@Parameter(name = "type", description = "过滤条件：0-菜单 1-按钮，不传查所有", required = false)})
    @GetMapping("/list")
    public R<List<SysMenu>> getRoleList(@RequestParam("roleId") Integer roleId, @RequestParam("type") String type) {
    	List<SysMenu> list = sysMenuService.getRoleList(roleId, type);
    	
    	writeLog(Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(list));
    	
        return R.ok(list);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@PathVariable Serializable id) {
    	SysMenu sysMenu = this.sysMenuService.getById(id);
    	
    	writeLog(Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(sysMenu));
    	
        return R.ok(sysMenu);
    }
    
    private void writeLog(String methodName, String jsonResult) {
    	log.debug("{}-->返回结果：{}", methodName, jsonResult);
    }

    /**
     * 新增数据
     *
     * @param sysMenu 实体对象
     * @return 新增结果
     */
    @SysLog("新增菜单")
    @PostMapping
    @Operation(summary = "新增菜单")
    @PreAuthorize("@pms.hasPermission('menu_add')")
    public R insert(@Validated(ValidGroup.Save.class) @RequestBody SysMenu sysMenu) {
        R r = checkSysMenu(sysMenu, OperationTypeEnum.CREATE);
        if (r.getCode() != 0) return r;
        
        Boolean result = this.sysMenuService.saveMenu(sysMenu);
        writeLog(Thread.currentThread().getStackTrace()[1].getMethodName(), JSONUtil.toJsonStr(result));
        
        return R.ok(result);
    }

    /**
     * 修改数据
     *
     * @param sysMenu 实体对象
     * @return 修改结果
     */
    @SysLog("修改菜单")
    @PutMapping
    @Operation(summary = "修改菜单")
    @PreAuthorize("@pms.hasPermission('menu_edit')")
    public R updateMenuById(@Validated(ValidGroup.Update.class) @RequestBody SysMenu sysMenu) {
        R r = checkSysMenu(sysMenu, OperationTypeEnum.UPDATE);
        if (r.getCode() != 0) return r;
        return R.ok(this.sysMenuService.updateMenuById(sysMenu));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @SysLog("删除菜单")
    @DeleteMapping
    @Operation(summary = "删除菜单")
    @PreAuthorize("@pms.hasPermission('menu_del')")
    public R deleteMenuById(@RequestParam("id") Integer id) {
        return sysMenuService.deleteMenuById(id);
    }

    /**
     * 菜单参数校验
     * <p>
     * 校验菜单名称和权限标识是否已存在，避免重复数据。
     * </p>
     *
     * @param sysMenu 菜单实体
     * @param type    操作类型：OperationTypeEnum
     * @return 校验结果：成功返回 R.ok()，失败返回 R.failed()
     */
    private R checkSysMenu(SysMenu sysMenu, OperationTypeEnum type) {
    	LambdaQueryWrapper<SysMenu> queryWrapper;
        // 校验路径或者权限标识是否已存在
        if (type == OperationTypeEnum.CREATE) {
        	if (StrUtil.isNotBlank(sysMenu.getPath()) || StrUtil.isNotBlank(sysMenu.getPermission())) {
	        	if (sysMenu.getType().equals("0")) {
	        		// 菜单，按路径查重
	        		queryWrapper = Wrappers.<SysMenu>lambdaQuery()
	                        .eq(StrUtil.isNotBlank(sysMenu.getPath()), SysMenu::getPath, sysMenu.getPath());
	        	} else {
	        		// 按钮，按权限标识查重
	        		queryWrapper = Wrappers.<SysMenu>lambdaQuery()
	                        .eq(StrUtil.isNotBlank(sysMenu.getPermission()), SysMenu::getPermission, sysMenu.getPermission());
	        	}
	        	
	            Long count1 = sysMenuService.count(queryWrapper);
	            if (count1 > 0) {
	                return R.failed("菜单路径或者按钮权限标识已存在");
	            }
        	}
            Long count2 = sysMenuService.count(Wrappers.<SysMenu>lambdaQuery()
                    .eq(StrUtil.isNotBlank(sysMenu.getMenuName()), SysMenu::getMenuName, sysMenu.getMenuName()));
            if (count2 > 0) {
                return R.failed("名称已存在");
            }
        } else if (type == OperationTypeEnum.UPDATE) {
        	if (StrUtil.isNotBlank(sysMenu.getPath()) || StrUtil.isNotBlank(sysMenu.getPermission())) {
	        	if (sysMenu.getType().equals("0")) {
	        		// 菜单，按路径查重
	        		queryWrapper = Wrappers.<SysMenu>lambdaQuery()
	                        .eq(StrUtil.isNotBlank(sysMenu.getPath()), SysMenu::getPath, sysMenu.getPath())
	                        .ne(SysMenu::getId, sysMenu.getId());
	        	} else {
	        		// 按钮，按权限标识查重
	        		queryWrapper = Wrappers.<SysMenu>lambdaQuery()
	                        .eq(StrUtil.isNotBlank(sysMenu.getPermission()), SysMenu::getPermission, sysMenu.getPermission())
	                        .ne(SysMenu::getId, sysMenu.getId());
	        	}
	            Long count1 = sysMenuService.count(queryWrapper);
	            if (count1 > 0) {
	                return R.failed("菜单路径或者按钮权限标识已存在");
	            }
        	}
            Long count2 = sysMenuService.count(Wrappers.<SysMenu>lambdaQuery()
                    .eq(StrUtil.isNotBlank(sysMenu.getMenuName()), SysMenu::getMenuName, sysMenu.getMenuName())
                    .ne(SysMenu::getId, sysMenu.getId()));
            if (count2 > 0) {
                return R.failed("名称已存在");
            }
        } else if (type == OperationTypeEnum.DELETE) {
            
        } else if (type == OperationTypeEnum.READ) {
            
        } else {
        	return R.failed("checkSysMenu调用时参数type传入错误");
        }
        return R.ok();
    }
}