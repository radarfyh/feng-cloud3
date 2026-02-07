package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ltd.huntinginfo.feng.ai.api.entity.AigcEmbedStore;
import ltd.huntinginfo.feng.ai.service.AigcEmbedStoreService;
import ltd.huntinginfo.feng.common.agent.component.EmbeddingRefreshEvent;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.swagger.annotation.ApiLog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI向量库 APIs（控制类）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/embed-store")
@Tag(name = "生成式AI向量库模块")
public class AigcEmbedStoreController {

    private final AigcEmbedStoreService embedStoreService;
    private final SpringContextHolder contextHolder;

    @GetMapping("/list")
    @Operation(summary = "查询向量库列表")
    public R<List<AigcEmbedStore>> list(AigcEmbedStore data) {
        List<AigcEmbedStore> list = embedStoreService.list(Wrappers.lambdaQuery());
        list.forEach(this::hide);
        return R.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询向量库列表")
    public R<IPage<AigcEmbedStore>> page(Page<AigcEmbedStore> page, AigcEmbedStore embedStore) {
        IPage<AigcEmbedStore> iPage = embedStoreService.page(page,
        		Wrappers.<AigcEmbedStore>lambdaQuery()
                .like(embedStore.getName() != null, AigcEmbedStore::getName, embedStore.getName())
                .eq(embedStore.getProvider() != null, AigcEmbedStore::getProvider, embedStore.getProvider())
                .orderByDesc(AigcEmbedStore::getCreateTime));
        // 脱敏
        iPage.getRecords().forEach(this::hide);
        return R.ok(iPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcEmbedStore> findById(@PathVariable Integer id) {
        AigcEmbedStore store = embedStoreService.getById(id);
        hide(store);
        return R.ok(store);
    }

    @PostMapping
    @SysLog("新增向量库")
    @Operation(summary = "新增向量库")
    @PreAuthorize("@pms.hasPermission('aigc:embed-store:add')")
    public R<AigcEmbedStore> add(@RequestBody AigcEmbedStore data) {
        if (StrUtil.isNotBlank(data.getPassword()) && data.getPassword().contains("*")) {
            data.setPassword(null);
        }
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        embedStoreService.save(data);
        SpringContextHolder.publishEvent(new EmbeddingRefreshEvent(data));
        return R.ok();
    }

    @PutMapping
    @SysLog("修改向量库")
    @Operation(summary = "修改向量库")
    @PreAuthorize("@pms.hasPermission('aigc:embed-store:update')")
    public R update(@RequestBody AigcEmbedStore data) {
        if (StrUtil.isNotBlank(data.getPassword()) && data.getPassword().contains("*")) {
            data.setPassword(null);
        }
        // 保留原创建时间，不允许修改
        AigcEmbedStore existing = embedStoreService.getById(data.getId());
    	data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	data.setUpdateTime(LocalDateTime.now());
        embedStoreService.updateById(data);
        SpringContextHolder.publishEvent(new EmbeddingRefreshEvent(data));
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除向量库")
    @Operation(summary = "删除向量库")
    @PreAuthorize("@pms.hasPermission('aigc:embed-store:delete')")
    public R delete(@PathVariable Integer id) {
        AigcEmbedStore store = embedStoreService.getById(id);
        if (store != null) {
            embedStoreService.removeById(id);
            contextHolder.unregisterBean(id.toString());
        }
        return R.ok();
    }

    // 脱敏
    private void hide(AigcEmbedStore data) {
        if (data == null || StrUtil.isBlank(data.getPassword())) {
            return;
        }
        String key = StrUtil.hide(data.getPassword(), 0, data.getPassword().length());
        data.setPassword(key);
    }
}
