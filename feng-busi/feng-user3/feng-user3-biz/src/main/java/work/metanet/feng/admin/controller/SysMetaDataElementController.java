package work.metanet.feng.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysMetaDataElement;
import work.metanet.feng.admin.service.SysMetaDataElementService;
import work.metanet.feng.common.core.util.R;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 元数据单字段描述控制层
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("SysMetaDataElement")
@Tag(name = "元数据模块")
public class SysMetaDataElementController {
    /**
     * 服务对象
     */
    private final SysMetaDataElementService SysMetaDataElementService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param SysMetaDataElement 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('usr:element:query')")
    public R selectAll(Page page, SysMetaDataElement SysMetaDataElement) {
        return R.ok(this.SysMetaDataElementService.page(page, SysMetaDataElementService.convertWrapper(SysMetaDataElement)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('usr:element:query')")
    public R<List<SysMetaDataElement>> list(SysMetaDataElement SysMetaDataElement) {
           return R.ok(this.SysMetaDataElementService.list(SysMetaDataElementService.convertWrapper(SysMetaDataElement)));
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
        return R.ok(this.SysMetaDataElementService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param SysMetaDataElement 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('usr:element:add')")
    public R insert(@RequestBody SysMetaDataElement SysMetaDataElement) {
        SysMetaDataElement.setCreateTime(LocalDateTime.now());
        return R.ok(this.SysMetaDataElementService.save(SysMetaDataElement));
    }

    /**
     * 修改数据
     *
     * @param SysMetaDataElement 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('usr:element:update')")
    public R update(@RequestBody SysMetaDataElement SysMetaDataElement) {
        SysMetaDataElement.setUpdateTime(LocalDateTime.now());
        return R.ok(this.SysMetaDataElementService.updateById(SysMetaDataElement));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合SysMetaDataElement
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('usr:element:delete')")
    public R delete(@RequestParam("idList") List<String> idList) {
        return R.ok(this.SysMetaDataElementService.removeByIds(idList));
    }
}