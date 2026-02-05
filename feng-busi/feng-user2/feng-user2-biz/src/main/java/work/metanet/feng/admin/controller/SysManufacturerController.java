package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.entity.SysManufacturer;
import work.metanet.feng.admin.service.SysApplicationService;
import work.metanet.feng.admin.service.SysManufacturerService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 厂商表(SysManufacturer)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysManufacturer")
@Tag(name = "厂商模块")
public class SysManufacturerController {
    /**
     * 服务对象
     */
    private final SysManufacturerService sysManufacturerService;

    private final SysApplicationService sysApplicationService;

    /**
     * 分页查询所有数据
     *
     * @param page            分页对象
     * @param sysManufacturer 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysManufacturer sysManufacturer) {
        return R.ok(this.sysManufacturerService.page(page, Wrappers.<SysManufacturer>lambdaQuery()
                .like(StringUtils.isNotBlank(sysManufacturer.getManufacturerName()), SysManufacturer::getManufacturerName, sysManufacturer.getManufacturerName())
                .eq(StringUtils.isNotBlank(sysManufacturer.getOrganCode()), SysManufacturer::getOrganCode, sysManufacturer.getOrganCode())
                .eq(StringUtils.isNotBlank(sysManufacturer.getManufacturerCode()), SysManufacturer::getManufacturerCode, sysManufacturer.getManufacturerCode())
                .orderByDesc(SysManufacturer::getCreateTime)
        ));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysManufacturer>> list(SysManufacturer sysManufacturer) {
        return R.ok(this.sysManufacturerService.list(Wrappers.<SysManufacturer>lambdaQuery()
                .like(StringUtils.isNotBlank(sysManufacturer.getManufacturerName()), SysManufacturer::getManufacturerName, sysManufacturer.getManufacturerName())
                .eq(StringUtils.isNotBlank(sysManufacturer.getOrganCode()), SysManufacturer::getOrganCode, sysManufacturer.getOrganCode())
                .eq(StringUtils.isNotBlank(sysManufacturer.getManufacturerCode()), SysManufacturer::getManufacturerCode, sysManufacturer.getManufacturerCode())
                .orderByAsc(SysManufacturer::getId)));
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
        return R.ok(this.sysManufacturerService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysManufacturer 实体对象
     * @return 新增结果
     */
    @SysLog("新增厂商")
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysManufacturer sysManufacturer) {
        R r = checkSysManufacturer(sysManufacturer, "0");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysManufacturerService.save(sysManufacturer));
    }

    /**
     * 修改数据
     *
     * @param sysManufacturer 实体对象
     * @return 修改结果
     */
    @SysLog("修改厂商")
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysManufacturer sysManufacturer) {
        R r = checkSysManufacturer(sysManufacturer, "1");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysManufacturerService.updateById(sysManufacturer));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @SysLog("删除厂商")
    @DeleteMapping
    @Operation(summary = "删除数据")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        for (Integer id : idList) {
            long count = sysApplicationService.count(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getManufacturerId, id));
            if (count > 0) {
                return R.failed("该厂商关联应用，请先解绑应用再删除:" + id);
            }
        }
        return R.ok(this.sysManufacturerService.removeByIds(idList));
    }

    /**
     * 厂商校验
     *
     * @param sysManufacturer: 厂商
     * @param type:    0-新增 1-修改
     * @return: work.metanet.feng.common.core.util.R
     **/
    private R checkSysManufacturer(SysManufacturer sysManufacturer, String type) {
        Long count = 0L;
        Long count1 = 0L;
        if ("0".equals(type)) {
            count = sysManufacturerService.count(Wrappers.<SysManufacturer>lambdaQuery().eq(SysManufacturer::getOrganCode, sysManufacturer.getOrganCode()).eq(SysManufacturer::getManufacturerCode, sysManufacturer.getManufacturerCode()));
        } else {
            count1 = sysManufacturerService.count(Wrappers.<SysManufacturer>lambdaQuery().eq(SysManufacturer::getOrganCode, sysManufacturer.getOrganCode()).eq(SysManufacturer::getManufacturerCode, sysManufacturer.getManufacturerCode()).ne(SysManufacturer::getId, sysManufacturer.getId()));
        }
        if (count > 0 || count1 > 0) {
            return R.failed("厂商编码已存在");
        }
        return R.ok();
    }
}