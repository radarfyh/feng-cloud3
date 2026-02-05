package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysConfig;
import work.metanet.feng.admin.service.SysConfigService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 系统配置表(SysConfig)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysConfig")
@Tag(name = "系统配置模块")
public class SysConfigController {
    /**
     * 服务对象
     */
    private final SysConfigService sysConfigService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param sysConfig 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysConfig sysConfig) {
        return R.ok(this.sysConfigService.page(page, Wrappers.<SysConfig>lambdaQuery()
                .eq(null != sysConfig.getNo(), SysConfig::getNo, sysConfig.getNo())
                .eq(StringUtils.isNotBlank(sysConfig.getCode()), SysConfig::getCode, sysConfig.getCode())
                .like(StringUtils.isNotBlank(sysConfig.getDesc()), SysConfig::getDesc, sysConfig.getDesc())
                .orderByDesc(SysConfig::getCreateTime)
        ));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysConfig>> list(SysConfig sysConfig) {
        return R.ok(this.sysConfigService.list(Wrappers.<SysConfig>lambdaQuery()
                .eq(null != sysConfig.getNo(), SysConfig::getNo, sysConfig.getNo())
                .eq(StringUtils.isNotBlank(sysConfig.getCode()), SysConfig::getCode, sysConfig.getCode())
                .like(StringUtils.isNotBlank(sysConfig.getDesc()), SysConfig::getDesc, sysConfig.getDesc())
                .orderByDesc(SysConfig::getCreateTime)
        ));
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
        return R.ok(this.sysConfigService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysConfig 实体对象
     * @return 新增结果
     */
    @SysLog("新增系统配置")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('config_add')")
    @Operation(summary = "新增系统配置")
    public R insert(@Validated(ValidGroup.Save.class) @RequestBody SysConfig sysConfig) {
        long count = sysConfigService.count(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getNo, sysConfig.getNo()));
        if (count > 0) {
            return R.failed("配置编号已存在");
        }
        long count2 = sysConfigService.count(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getCode, sysConfig.getCode()));
        if (count2 > 0) {
            return R.failed("配置编码已存在");
        }
        return R.ok(this.sysConfigService.save(sysConfig));
    }

    /**
     * 修改数据
     *
     * @param sysConfig 实体对象
     * @return 修改结果
     */
    @SysLog("修改系统配置")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('config_edit')")
    @Operation(summary = "修改系统配置")
    public R update(@Validated(ValidGroup.Update.class) @RequestBody SysConfig sysConfig) {
        long count = sysConfigService.count(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getNo, sysConfig.getNo()).ne(SysConfig::getId, sysConfig.getId()));
        if (count > 0) {
            return R.failed("配置编号已存在");
        }
        long count2 = sysConfigService.count(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getCode, sysConfig.getCode()).ne(SysConfig::getId, sysConfig.getId()));
        if (count2 > 0) {
            return R.failed("配置编码已存在");
        }
        return R.ok(this.sysConfigService.updateById(sysConfig));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysConfigService.removeByIds(idList));
    }
}