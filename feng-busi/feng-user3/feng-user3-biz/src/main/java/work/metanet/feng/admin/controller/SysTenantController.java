package work.metanet.feng.admin.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.entity.SysTenantConfig;
import work.metanet.feng.admin.api.vo.SysTenantVO;
import work.metanet.feng.admin.service.SysTenantConfigService;
import work.metanet.feng.admin.service.SysTenantService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户控制层
 * <p>
 * 该类提供了租户 的相关接口，包括项目的增、删、改、查、分页等功能。
 * </p>
 *
 * @author EdisonFeng
 * @since 2025-6-13
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysTenant")
@Tag(name = "租户模块")
public class SysTenantController {

    /**
     * 服务对象
     */
    private final SysTenantService sysTenantService;
    private final SysTenantConfigService sysTenantConfigService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param sysTenant 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @RequiresPermission({"usr:tenant:query"})
    public R selectAll(Page page, SysTenant sysTenant) {
        return R.ok(this.sysTenantService.pageEntity(page, sysTenant));
    }

    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @RequiresPermission({"usr:tenant:query"})
    public R<List<SysTenantVO>> list(SysTenant sysTenant) {
        return R.ok(this.sysTenantService.listEntity(sysTenant));
    }

    /**
     * 根据主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@PathVariable Serializable id) {
        return R.ok(this.sysTenantService.getById(id));
    }
    
    /**
     * 检查租户编码是否存在
     *
     * @param tenantCode 租户编码
     * @return r.data 不为空 存在 r.data 为空 不存在
     */
    @GetMapping("/checkCode")
    @Operation(summary = "检查租户编码是否存在")
    public R<SysTenantVO> checkTenantCode(@RequestParam("tenantCode") String tenantCode) {
    	SysTenant sysTenant = new SysTenant();
    	sysTenant.setTenantCode(tenantCode);
    	List<SysTenantVO> list = this.sysTenantService.listEntity(sysTenant);
    	//Boolean exists = list.stream().anyMatch(tenant -> tenantCode.equals(tenant.getTenantCode()));
    	if (list.size() > 0) {
    		return R.ok(list.get(0));
    	} else {
    		return R.failed("所查询的租户编码不存在");
    	}
    }
    
    /**
     * 获取启用的租户信息
     *
     * @return 启用列表
     */
    @GetMapping("/activeList")
    @Operation(summary = "获取启用的租户信息")
    @RequiresPermission({"usr:tenant:query"})
    public R<List<SysTenantVO>> getActiveTenants() {
    	SysTenant sysTenant = new SysTenant();
    	sysTenant.setStatus("1"); //1表示启用
    	sysTenant.setEnableTime(LocalDateTime.now()); // 启用时间小于等于当前时间
    	sysTenant.setExpireTime(LocalDateTime.now()); // 过期时间大于等于当前时间
    	List<SysTenantVO> list = this.sysTenantService.listEntity(sysTenant);
    	
        return R.ok(list);
    }
    
    /**
     * 新增数据
     *
     * @param sysTenant 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @RequiresPermission({"usr:tenant:add"})
    public R insert(@RequestBody SysTenant sysTenant) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        sysTenant.setCreateTime(now);
        sysTenant.setUpdateTime(null);
    	
        R r = checkSysTenant(sysTenant, "1");
        if (r.getCode() != 0) return r;
        if (StrUtil.isBlank(sysTenant.getTenantCode())) {
            sysTenant.setTenantCode(IdUtil.fastSimpleUUID());
        }
        return R.ok(this.sysTenantService.save(sysTenant));
    }
    
    /**
     * 新增租户配置数据
     *
     * @param sysTenantConfig 租户配置实体对象
     * @return 新增结果
     */
    @PostMapping("/config")
    @Operation(summary = "新增租户配置数据")
    @RequiresPermission({"usr:tenant-config:add"})
    public R insertConfig(@RequestBody SysTenantConfig sysTenantConfig) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        sysTenantConfig.setCreateTime(now);
        sysTenantConfig.setUpdateTime(null);
        return R.ok(this.sysTenantConfigService.save(sysTenantConfig));
    }

    /**
     * 修改数据
     *
     * @param sysTenant 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @RequiresPermission({"usr:tenant:update"})
    public R update(@RequestBody SysTenant sysTenant) {
        // 保留原创建时间，不允许修改
    	SysTenant existing = sysTenantService.getById(sysTenant.getId());
    	sysTenant.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	sysTenant.setUpdateTime(LocalDateTime.now());
    	
        R r = checkSysTenant(sysTenant, "2");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysTenantService.updateById(sysTenant));
    }
    
    /**
     * 修改租户配置数据
     *
     * @param sysTenantConfig 租户配置实体对象
     * @return 修改结果
     */
    @PutMapping("/config")
    @Operation(summary = "修改租户配置数据")
    @RequiresPermission({"usr:tenant-config:update"})
    public R updateConfig(@RequestBody SysTenantConfig sysTenantConfig) {
        // 保留原创建时间，不允许修改
    	SysTenantConfig existing = sysTenantConfigService.getById(sysTenantConfig.getId());
    	sysTenantConfig.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	sysTenantConfig.setUpdateTime(LocalDateTime.now());
    	
        return R.ok(this.sysTenantConfigService.updateById(sysTenantConfig));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @RequiresPermission({"usr:tenant:delete"})
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysTenantService.removeByIds(idList));
    }
    
    /**
     * 删除租户配置数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/config")
    @Operation(summary = "删除租户配置数据")
    @RequiresPermission({"usr:tenant-config:delete"})
    public R deleteConfig(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysTenantConfigService.removeByIds(idList));
    }
    
    /**
     * 项目校验
     * @param: sysTenant
     * @return 校验结果
     */
    private R checkSysTenant(SysTenant sysTenant, String type) {
        if ("1".equals(type)) {
            Long count = sysTenantService.count(Wrappers.<SysTenant>lambdaQuery().eq(SysTenant::getTenantCode, sysTenant.getTenantCode()));
            if (count > 0) {
                return R.failed("租户编码已存在");
            }
        } else {
            Long count = sysTenantService.count(Wrappers.<SysTenant>lambdaQuery().eq(SysTenant::getTenantCode, sysTenant.getTenantCode()).ne(SysTenant::getId, sysTenant.getId()));
            if (count > 0) {
                return R.failed("租户编码已存在");
            }
        }
        return R.ok();
    }
}


