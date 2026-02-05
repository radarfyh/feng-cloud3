package work.metanet.feng.admin.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.admin.service.SysApplicationService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import work.metanet.feng.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用系统表(SysApplication)表控制层
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sysApplication")
@Tag(name = "应用模块")
public class SysApplicationController {
    /**
     * 服务对象
     */
    private final SysApplicationService sysApplicationService;

    /**
     * 分页查询所有数据
     *
     * @param page           分页对象
     * @param sysApplication 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysApplication sysApplication) {
        return R.ok(this.sysApplicationService.page(page, Wrappers.<SysApplication>lambdaQuery()
        		.like(StringUtils.isNotBlank(sysApplication.getAppName()), SysApplication::getAppName, sysApplication.getAppName())
        		.eq(StringUtils.isNotBlank(sysApplication.getApplicationCode()), SysApplication::getApplicationCode, sysApplication.getApplicationCode())
        		.eq(StringUtils.isNotBlank(sysApplication.getStatus()), SysApplication::getStatus, sysApplication.getStatus())
        		.eq(StringUtils.isNotBlank(sysApplication.getIsFengPortal()), SysApplication::getIsFengPortal, sysApplication.getIsFengPortal())
        		.eq(StringUtils.isNotBlank(sysApplication.getFengType()), SysApplication::getFengType, sysApplication.getFengType())
        		.eq(StringUtils.isNotBlank(sysApplication.getClientType()), SysApplication::getClientType, sysApplication.getClientType())
        		.eq(StringUtils.isNotBlank(sysApplication.getIsMicro()), SysApplication::getIsMicro, sysApplication.getIsMicro())
        		.eq(StringUtils.isNotBlank(sysApplication.getSysIsShow()), SysApplication::getSysIsShow, sysApplication.getSysIsShow()).orderByAsc(SysApplication::getId)));
    }

    /**
     * 分页查询当前登录登录用户的应用列表
     *
     * @param page           分页对象
     * @param sysApplication 查询实体
     * @return 所有数据
     */
    @GetMapping("/getApplicationByUserPage")
    @Operation(summary = "分页查询当前登录登录用户的应用列表【管理端】")
    public R getApplicationByUserPage(Page page, SysApplication sysApplication) {
        return R.ok(this.sysApplicationService.getApplicationByUserPage(page, sysApplication));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Cacheable(value = CacheConstants.APPLICATION_DETAILS, unless = "#result.data.isEmpty()")
    @Operation(summary = "查询全部应用列表")
    public R<List<SysApplication>> list(String fengType) {
        return R.ok(this.sysApplicationService.list(Wrappers.<SysApplication>lambdaQuery().eq(StrUtil.isNotBlank(fengType), SysApplication::getFengType, fengType)));
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
        return R.ok(this.sysApplicationService.getById(id));
    }

    /**
     * 校验appid和appSecret
     *
     * @param appid
     * @param appSecret
     * @return 单条数据
     */
    @Inner
    @GetMapping("/checkAppId")
    @Operation(summary = "校验appid和appSecret")
    public R<SysApplication> checkAppId(@RequestParam("appid") String appid, @RequestParam("appSecret") String appSecret) {
        return R.ok(this.sysApplicationService.getOne(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getAppId, appid).eq(SysApplication::getAppSecret, appSecret)));
    }

    /**
     * 新增应用
     *
     * @param sysApplication 实体对象
     * @return 新增结果
     */
    @SysLog("新增应用")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('application_add')")
    @CacheEvict(value = CacheConstants.APPLICATION_DETAILS, allEntries = true)
    @Operation(summary = "新增应用")
    public R insert(@Validated(ValidGroup.Save.class) @RequestBody SysApplication sysApplication) {
        R r = checkSysApplication(sysApplication, "0");
        if (r.getCode() != 0) return r;
        if (StrUtil.isNotBlank(sysApplication.getAppSecret())) {
            long count = sysApplicationService.count(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getAppSecret, sysApplication.getAppSecret()));
            if (count > 0) {
                return R.failed("appSecret重复");
            }
        }
        return R.ok(this.sysApplicationService.save(sysApplication));
    }

    /**
     * 修改应用
     *
     * @param sysApplication 实体对象
     * @return 修改结果
     */
    @SysLog("修改应用")
    @Operation(summary = "修改应用")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('application_edit')")
    @CacheEvict(value = CacheConstants.APPLICATION_DETAILS, allEntries = true)
    public R update(@Validated(ValidGroup.Update.class) @RequestBody SysApplication sysApplication) {
        R r = checkSysApplication(sysApplication, "1");
        if (r.getCode() != 0) return r;
        if (StrUtil.isNotBlank(sysApplication.getAppSecret())) {
            long count = sysApplicationService.count(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getAppSecret, sysApplication.getAppSecret()).ne(SysApplication::getId, sysApplication.getId()));
            if (count > 0) {
                return R.failed("appSecret重复");
            }
        }
        return R.ok(this.sysApplicationService.updateById(sysApplication));
    }

    /**
     * 删除应用
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @SysLog("删除应用")
    @Operation(summary = "删除应用")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('application_del')")
    @CacheEvict(value = CacheConstants.APPLICATION_DETAILS, allEntries = true)
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysApplicationService.removeByIds(idList));
    }

    /**
     * 通过用户id获取应用列表
     *
     * @param userId 用户id
     * @return
     */
    @Operation(summary = "通过用户id获取应用列表")
    @GetMapping("/getAppListByUserId")
    public R<List<SysApplicationVO>> getAppListByUserId(@RequestParam("userId") Integer userId) {
    	List<SysApplicationVO> list = sysApplicationService.getAppListByUserId(userId);
    	
    	log.debug("getAppListByUserId-->返回结果：{}", JSONUtil.toJsonStr(list));
    	
        return R.ok();
    }

    /**
     * 获取当前登录用户的应用列表
     *
     * @param
     * @return
     */
    @Operation(summary = "获取当前登录用户的应用列表")
    @GetMapping("/getAppListByUser")
    public R getAppListByUser(
            @RequestParam(value = "sysIsShow", required = false) String sysIsShow,
            @RequestParam(value = "fengType", required = false) String fengType) {
        
        List<Integer> roles = SecurityUtils.getRoles();
        if (roles.contains(1)) {
            return R.ok(sysApplicationService.list(Wrappers.<SysApplication>lambdaQuery()
                    .eq(StrUtil.isNotBlank(sysIsShow), SysApplication::getSysIsShow, sysIsShow)
                    .eq(StrUtil.isNotBlank(fengType), SysApplication::getFengType, fengType)));
        }

        Integer userId = SecurityUtils.getUser().getId();
        List<SysApplicationVO> sysApplicationVOList = sysApplicationService.getAppListByUserId(userId);

        // 使用链式过滤确保两个条件同时生效
        Stream<SysApplicationVO> stream = sysApplicationVOList.stream();
        if (StrUtil.isNotBlank(sysIsShow)) {
            stream = stream.filter(v -> Objects.equals(v.getSysIsShow().trim(), sysIsShow));
        }
        if (StrUtil.isNotBlank(fengType)) {
            stream = stream.filter(v -> Objects.equals(v.getFengType(), fengType));
        }
        sysApplicationVOList = stream.collect(Collectors.toList());

        log.debug("getAppListByUser-->返回结果：{}", JSONUtil.toJsonStr(sysApplicationVOList));
        return R.ok(sysApplicationVOList);
    }

    /**
     * 通过角色id获取应用列表
     *
     * @param roleId 角色id
     * @return
     */
    @Operation(summary = "通过角色id获取应用列表")
    @GetMapping("/getAppListByRoleId")
    public R<List<SysApplicationVO>> getAppListByRoleId(@RequestParam("roleId") Integer roleId) {
        return R.ok(sysApplicationService.getAppListByRoleId(roleId));
    }

    /**
     * 厂商校验
     *
     * @param sysApplication: 厂商
     * @param type:           0-新增 1-修改
     * @return: work.metanet.feng.common.core.util.R
     **/
    private R checkSysApplication(SysApplication sysApplication, String type) {
        Long count = 0L;
        Long count1 = 0L;
        if ("0".equals(type)) {
            count = sysApplicationService.count(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getApplicationCode, sysApplication.getApplicationCode()));
        } else {
            count1 = sysApplicationService.count(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getApplicationCode, sysApplication.getApplicationCode()).ne(SysApplication::getId, sysApplication.getId()));
        }
        if (count > 0 || count1 > 0) {
            return R.failed("应用编码已存在");
        }
        return R.ok();
    }
}