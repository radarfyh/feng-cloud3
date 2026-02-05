package work.metanet.feng.ai.controller;

import work.metanet.feng.ai.api.entity.AigcDocsSlice;
import work.metanet.feng.ai.service.AigcDocsSliceService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.swagger.annotation.ApiLog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档切片管理主要功能：切片记录的增删改查（CRUD）
 * 
 * @author Edison
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/docs/slice")
@Tag(name = "生成式AI文档切片模块")
public class AigcDocsSliceController {

    private final AigcDocsSliceService docsSliceService;

    @GetMapping("/list")
    @Operation(summary = "查询文档切片列表")
    public R<List<AigcDocsSlice>> list(AigcDocsSlice data) {
        return R.ok(docsSliceService.list(data));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询文档切片列表")
    public R<IPage<AigcDocsSlice>> list(Page<AigcDocsSlice> page, AigcDocsSlice data) {

        return R.ok(docsSliceService.page(page, Wrappers.<AigcDocsSlice>lambdaQuery()
                .eq(data.getKnowledgeId() != null, AigcDocsSlice::getKnowledgeId, data.getKnowledgeId())
                .eq(data.getDocsId() != null, AigcDocsSlice::getDocsId, data.getDocsId())
                .orderByDesc(AigcDocsSlice::getCreateTime)
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcDocsSlice> findById(@PathVariable String id) {
        return R.ok(docsSliceService.getById(id));
    }

    @PostMapping
    @SysLog("新增切片数据")
    @Operation(summary = "新增文档 切片")
    @PreAuthorize("@pms.hasPermission('aigc:docs:slice:add')")
    public R<?> add(@RequestBody AigcDocsSlice data) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        docsSliceService.save(data);
        return R.ok();
    }

    @PutMapping
    @SysLog("修改切片数据")
    @Operation(summary = "修改文档 切片")
    @PreAuthorize("@pms.hasPermission('aigc:docs:slice:update')")
    public R<?> update(@RequestBody AigcDocsSlice data) {
        // 保留原创建时间，不允许修改
    	AigcDocsSlice existing = docsSliceService.getById(data.getId());
    	data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	data.setUpdateTime(LocalDateTime.now());
    	docsSliceService.updateById(data);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除切片数据")
    @Operation(summary = "删除文档切片")
    @PreAuthorize("@pms.hasPermission('aigc:docs:slice:delete')")
    public R<?> delete(@PathVariable Integer id) {
    	docsSliceService.removeById(id);
        return R.ok();
    }
}

