package work.metanet.feng.admin.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.RoleMenuDTO;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.entity.SysUserRole;
import work.metanet.feng.admin.service.SysRoleService;
import work.metanet.feng.admin.service.SysUserRoleService;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.constant.enums.OperationTypeEnum;
import work.metanet.feng.common.core.constant.enums.RoleTypeEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.idempotent.annotation.Idempotent;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色表(SysRole)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sysRole")
@Tag(name = "角色模块")
public class SysRoleController {
	
    /**
     * 服务对象
     */
    private final SysRoleService sysRoleService;

    private final SysUserRoleService sysUserRoleService;

    /**
     * 分页查询所有数据
     *
     * @param page    分页对象
     * @param sysRole 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysRole sysRole) {
        List<Integer> roles = SecurityUtils.getRoles();
        IPage result = this.sysRoleService.page(page, 
        		Wrappers.<SysRole>lambdaQuery()
        			//.ne(!roles.contains(BuiltInRoleEnum.ADMIN.getId()), SysRole::getType, "0")
        			.eq(StrUtil.isNotBlank(sysRole.getOrganCode()), SysRole::getOrganCode, sysRole.getOrganCode())
        			.like(StringUtils.isNotBlank(sysRole.getRoleName()), SysRole::getRoleName, sysRole.getRoleName())
        			.like(StringUtils.isNotBlank(sysRole.getRoleCode()), SysRole::getRoleCode, sysRole.getRoleCode())
        			.eq(StringUtils.isNotBlank(sysRole.getType()), SysRole::getType, sysRole.getType())
        			.orderByDesc(SysRole::getCreateTime));
        
        log.debug("selectAll-->返回结果：{}", JSONUtil.toJsonStr(result));
        
        return R.ok(result);
    }

    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysRole>> list(SysRole sysRole) {
        return R.ok(this.sysRoleService.list(Wrappers.<SysRole>lambdaQuery()
        		.eq(StrUtil.isNotBlank(sysRole.getOrganCode()), SysRole::getOrganCode, sysRole.getOrganCode())
        		.like(StringUtils.isNotBlank(sysRole.getRoleName()), SysRole::getRoleName, sysRole.getRoleName())
        		.eq(StringUtils.isNotBlank(sysRole.getType()), SysRole::getType, sysRole.getType())
        		.orderByDesc(SysRole::getCreateTime)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据")
    public R<SysRole> selectOne(@PathVariable Serializable id) {
        return R.ok(this.sysRoleService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysRole 实体对象
     * @return 新增结果
     */
    @SysLog("新增角色")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('role_add')")
    @Operation(summary = "新增角色")
    public R insert(@Validated(ValidGroup.Save.class) @RequestBody SysRole sysRole) {
        R r = checkSysRole(sysRole, OperationTypeEnum.CREATE);
        if (r.getCode() != 0) return r;
        if (StrUtil.isBlank(sysRole.getRoleCode())) {
            sysRole.setRoleCode(IdUtil.fastSimpleUUID());
        }
        return R.ok(this.sysRoleService.save(sysRole));
    }

    /**
     * 修改数据
     *
     * @param sysRole 实体对象
     * @return 修改结果
     */
    @SysLog("修改角色")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('role_edit')")
    @Operation(summary = "修改角色")
    public R update(@Validated(ValidGroup.Update.class) @RequestBody SysRole sysRole) {
        R r = checkSysRole(sysRole, OperationTypeEnum.UPDATE);
        if (r.getCode() != 0) return r;
        return R.ok(this.sysRoleService.updateById(sysRole));
    }

    /**
     * 删除数据
     *
     * @param id 主键结合
     * @return 删除结果
     */
    @Idempotent(key = "#id", expireTime = 5, info = "请勿重复删除角色")
    @SysLog("删除角色")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('role_del')")
    @Operation(summary = "删除角色")
    public R delete(@RequestParam("id") Integer id) {
    	SysRole sysRole = new SysRole();
    	sysRole.setId(id);
        R r = checkSysRole(sysRole, OperationTypeEnum.DELETE);
        if (r.getCode() != 0) return r;
        return sysRoleService.delete(id);
    }

    /**
     * 根据角色id配置菜单和操作权限
     *
     * @param roleMenuDTO 角色对象
     * @return success、false
     */
    @Operation(summary = "根据角色id配置菜单和操作权限,逗号隔开")
    @SysLog("根据角色id配置菜单和操作权限")
    @PutMapping("/menu")
    @PreAuthorize("@pms.hasPermission('role_perm')")
    public R saveRoleMenus(@Validated(ValidGroup.Update.class) @RequestBody RoleMenuDTO roleMenuDTO) {
    	List<Integer> roles = SecurityUtils.getRoles();
    	if (roles.contains(BuiltInRoleEnum.ADMIN.getId())) {
    		return R.ok(sysRoleService.updateRoleMenus(roleMenuDTO));
    	}
    	return R.failed("非管理员不允许授权");
    }

    /**
     * 通过角色ID查询角色列表
     *
     * @param roleIdList 角色ID
     * @return
     */
    @Operation(summary = "通过角色ID集合查询角色列表")
    @PostMapping("/getRoleList")
    public R getRoleList(@RequestBody List<Integer> roleIdList) {
        return R.ok(sysRoleService.listByIds(roleIdList));
    }
    
    /**
     * 通过角色编码查询角色列表
     *
     * @param roleCodeList 角色编码
     * @return
     */
    @Operation(summary = "通过角色编码集合查询角色列表")
    @PostMapping("/getRoleListByCode")
    public R getRoleListByCode(@RequestBody List<String> roleCodeList) {
    	List<Integer> ids = sysRoleService.getRoleIdsByCode(roleCodeList);
        return R.ok(sysRoleService.listByIds(ids));
    }

    /**
     * 通过用户id查询角色列表
     *
     * @param userId 用户id
     * @return
     */
    @Operation(summary = "通过用户id查询角色列表")
    @GetMapping("/listRolesByUserId")
    public R listRolesByUserId(@RequestParam("userId") Integer userId) {
        List<SysUserRole> sysUserRoles = sysUserRoleService.list(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userId));
        if (null != sysUserRoles && sysUserRoles.size() > 0) {
            List<Integer> roles = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            if (roles.contains(BuiltInRoleEnum.ADMIN.getId())) { // 是管理员，则返回所有
                return R.ok(sysRoleService.listAll());
            }
            return R.ok(sysRoleService.listRolesByUserId(userId)); // 不是管理员，则返回用户自己的角色列表
        }
        return null;
    }

    /**
     * 获取当前用户的角色列表
     *
     * @param
     * @return
     */
    @Operation(summary = "获取当前用户的角色列表")
    @GetMapping("/getRoleListByUser")
    public R getRoleListByUser() {
        if (SecurityUtils.getRoles().contains(BuiltInRoleEnum.ADMIN.getId())) { // 是管理员，则返回所有
            return R.ok(sysRoleService.listAll());
        }
        Integer userId = SecurityUtils.getUser().getId();
        return R.ok(sysRoleService.getRoleListByUser(userId)); // 不是管理员，则返回用户自己的角色列表
    }

    /*
     * @Description: 角色校验
     * @param: sysRole
     * @param type 操作类型 OperationTypeEnum
     */
    private R checkSysRole(SysRole sysRole, OperationTypeEnum type) {
    	
        if (type == OperationTypeEnum.CREATE) { // 新增时检查组织代码、角色编码
        	if (BuiltInRoleEnum.isBuiltInRole(sysRole.getRoleCode())) {
        		return R.failed("这是内置角色的编码，不能使用。");
        	}
        	
            Long count = sysRoleService.count(Wrappers.<SysRole>lambdaQuery()
            		.eq(SysRole::getOrganCode, sysRole.getOrganCode())
            		.eq(SysRole::getRoleCode, sysRole.getRoleCode()));
            if (count > 0) {
                return R.failed("角色编码已存在");
            }
        } else if (type == OperationTypeEnum.UPDATE) { // 修改时检查组织代码、角色编码
        	if (BuiltInRoleEnum.isBuiltInRole(sysRole.getId())) {
        		return R.failed("内置角色不能修改");
        	}
        	if (BuiltInRoleEnum.isBuiltInRole(sysRole.getRoleCode())) {
        		return R.failed("这是内置角色的编码，不能使用。");
        	}
        	
            Long count = sysRoleService.count(Wrappers.<SysRole>lambdaQuery()
            		.eq(SysRole::getOrganCode, sysRole.getOrganCode())
            		.eq(SysRole::getRoleCode, sysRole.getRoleCode())
            		.ne(SysRole::getId, sysRole.getId()));
            if (count > 0) {
                return R.failed("角色编码已存在");
            }
        } else if (type == OperationTypeEnum.DELETE) { // 修改时检查角色编码是否内置的7个代码
        	if (BuiltInRoleEnum.isBuiltInRole(sysRole.getId())) {
        		return R.failed("内置角色不能删除");
        	}
        } else if (type == OperationTypeEnum.READ) {
        	// 读取无限制
        } else {
        	return R.failed("checkSysRole调用时参数type传入错误");
        }
        return R.ok();
    }
}