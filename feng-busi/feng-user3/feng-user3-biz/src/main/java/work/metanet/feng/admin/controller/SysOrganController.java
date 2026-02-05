package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.vo.SysTenantVO;
import work.metanet.feng.admin.service.SysOrganService;
import work.metanet.feng.admin.service.SysTenantService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 机构表(SysOrgan)表控制层
 *
 * @author edison
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysOrgan")
@Tag(name = "机构模块")
public class SysOrganController {
    /**
     * 服务对象
     */
    private final SysOrganService sysOrganService;
    
    private final SysTenantService sysTenantService;

    private final RedisTemplate redisTemplate;

    /**
     * 机构分页条件查询
     *
     * @param page     分页对象
     * @param sysOrgan 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "机构分页条件查询")
    public R selectAll(Page page, SysOrgan sysOrgan) {
        return R.ok(sysOrganService.page(page, Wrappers.<SysOrgan>lambdaQuery()
                .like(StringUtils.isNotBlank(sysOrgan.getOrganName()), SysOrgan::getOrganName, sysOrgan.getOrganName())
                .eq(StringUtils.isNotBlank(sysOrgan.getOrganCode()), SysOrgan::getOrganCode, sysOrgan.getOrganCode())
                .eq(StringUtils.isNotBlank(sysOrgan.getOrganType()), SysOrgan::getOrganType, sysOrgan.getOrganType())
                .orderByAsc(SysOrgan::getId)
        ));
    }


    /**
     * 获取所有机构列表
     *
     * @return Response对象
     */
    @Operation(summary = "获取所有机构列表")
    @Cacheable(value = CacheConstants.ORGAN_DETAILS, unless = "#result.data.isEmpty()")
    @GetMapping("/list")
    public R<List<SysOrgan>> list() {
        return R.ok(this.sysOrganService.list());
    }


    /**
     * 获取机构树
     *
     * @return 树形菜单
     */
    @Operation(summary = "获取机构树")
    @GetMapping(value = "/tree")
    @Cacheable(value = CacheConstants.ORGAN_DETAILS, key = "#p0 != null ? #p0 : 'allOrgan4Tenant'", unless = "#result.data.isEmpty()")
    public R getTree(@RequestParam Integer tenantId, @RequestParam String organCode) {
        return R.ok(sysOrganService.selectTree(tenantId, organCode));
    }

    /**
     * 条件获取机构列表
     *
     * @return Response对象
     */
    @Operation(summary = "条件获取机构列表")
    @GetMapping("/getSysOrganList")
    public R<List<SysOrgan>> getSysOrganList(SysOrgan sysOrgan) {
        return R.ok(this.sysOrganService.list(Wrappers.<SysOrgan>lambdaQuery()
                .like(StringUtils.isNotBlank(sysOrgan.getOrganName()), SysOrgan::getOrganName, sysOrgan.getOrganName())
                .eq(StringUtils.isNotBlank(sysOrgan.getOrganCode()), SysOrgan::getOrganCode, sysOrgan.getOrganCode())
                .eq(StringUtils.isNotBlank(sysOrgan.getOrganType()), SysOrgan::getOrganType, sysOrgan.getOrganType())
        ));
    }


    /**
     * 获取当前用户的机构列表
     *
     * @return Response对象
     */
    @Operation(summary = "获取当前用户的机构列表")
    @GetMapping("/getOrganListByUser")
    public R<List<SysOrgan>> getOrganListByUser() {
        if (SecurityUtils.getRoles().contains(1)) {
           return R.ok(sysOrganService.list());
        }
        Integer userId = SecurityUtils.getUser().getId();
        return R.ok(sysOrganService.getOrganListByUser(userId));
    }

    /**
     * 获取当前用户的租户列表
     *
     * @return Response对象
     */
    @Operation(summary = "获取当前用户的租户列表")
    @GetMapping("/getTenantListByUser")
    public R<List<SysTenantVO>> getTenantListByUser() {
        if (SecurityUtils.getRoles().contains(1)) {
           return R.ok(sysTenantService.listEntity(new SysTenant()));
        }
        Integer userId = SecurityUtils.getUser().getId();
        SysTenantVO tenantVO = sysTenantService.getTenantListByUser(userId);
        List<SysTenantVO> list = new ArrayList<>();
        list.add(tenantVO);
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
        return R.ok(this.sysOrganService.getById(id));
    }

    @Operation(summary = "通过机构code获取机构详情")
    @GetMapping("/details")
    public R<SysOrgan> getSysOrganDetails(@RequestParam("organCode") String organCode) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(SysOrgan.class));
        if (redisTemplate.opsForHash().hasKey(CacheConstants.ORGAN_DETAILS, organCode)) {
            SysOrgan sysOrgan = (SysOrgan) redisTemplate.opsForHash().get(CacheConstants.ORGAN_DETAILS, organCode);
            return R.ok(sysOrgan);
        }
        return R.ok(sysOrganService.getOne(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, organCode)));
    }

    /**
     * 新增机构
     *
     * @param sysOrgan 实体对象
     * @return 新增结果
     */
    @SysLog("新增机构")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('org_add')")
    @CacheEvict(value = CacheConstants.ORGAN_DETAILS, allEntries = true)
    @Operation(summary = "新增机构")
    public R saveSysOrgan(@Validated(ValidGroup.Save.class) @RequestBody SysOrgan sysOrgan) {
        return sysOrganService.saveSysOrgan(sysOrgan);
    }

    /**
     * 修改机构
     *
     * @param sysOrgan 实体对象
     * @return 修改结果
     */
    @SysLog("修改机构")
    @Operation(summary = "修改机构")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('org_edit')")
    @CacheEvict(value = CacheConstants.ORGAN_DETAILS, allEntries = true)
    public R updateSysOrganById(@Validated(ValidGroup.Update.class) @RequestBody SysOrgan sysOrgan) {
        return sysOrganService.updateSysOrganById(sysOrgan);
    }

    /**
     * 删除机构
     *
     * @param id 主键结合
     * @return 删除结果
     */
    @SysLog("删除机构")
    @Operation(summary = "删除机构")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('org_del')")
    @CacheEvict(value = CacheConstants.ORGAN_DETAILS, allEntries = true)
    public R deleteSysOrgan(@RequestParam("id") Integer id) {
        return sysOrganService.deleteSysOrgan(id);
    }
}