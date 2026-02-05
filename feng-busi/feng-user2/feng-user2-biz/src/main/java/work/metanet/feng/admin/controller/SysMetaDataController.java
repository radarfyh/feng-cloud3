package work.metanet.feng.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysMetaData;
import work.metanet.feng.admin.service.SysMetaDataService;
import work.metanet.feng.common.core.util.R;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 元数据多字段描述控制层
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("SysMetaData")
@Tag(name = "元数据集合模块")
public class SysMetaDataController {
    /**
     * 服务对象
     */
    private final SysMetaDataService SysMetaDataService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param SysMetaData 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('usr:meta:query')")
    public R selectAll(Page page, SysMetaData SysMetaData) {
        return R.ok(this.SysMetaDataService.page(page, SysMetaDataService.convertWrapper(SysMetaData)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('usr:meta:query')")
    public R<List<SysMetaData>> list(SysMetaData SysMetaData) {
           return R.ok( this.SysMetaDataService.list(SysMetaDataService.convertWrapper(SysMetaData)));
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
        return R.ok(this.SysMetaDataService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param SysMetaData 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('usr:meta:add')")
    public R insert(@RequestBody SysMetaData SysMetaData) {
        SysMetaData.setCreateTime(LocalDateTime.now());
        return R.ok(this.SysMetaDataService.save(SysMetaData));
    }

    /**
     * 修改数据
     *
     * @param SysMetaData 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('usr:meta:update')")
    public R update(@RequestBody SysMetaData SysMetaData) {
        SysMetaData.setUpdateTime(LocalDateTime.now());
        return R.ok(this.SysMetaDataService.updateById(SysMetaData));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('usr:meta:delete')")
    public R delete(@RequestParam("idList") List<String> idList) {
        return R.ok(this.SysMetaDataService.removeByIds(idList));
    }
}