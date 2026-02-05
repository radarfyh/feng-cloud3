package work.metanet.feng.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.ai.service.AigcPromptService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.swagger.annotation.ApiLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提示语控制类（APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequestMapping("/prompt")
@Tag(name = "生成式AI提示词模块")
@RequiredArgsConstructor
public class AigcPromptController {

    private final AigcPromptService aigcPromptService;

    @GetMapping("/list")
    @Operation(summary = "查询提示词列表")
    public R<List<AigcPrompt>> list(AigcPrompt data) {
        return R.ok(aigcPromptService.list(data));
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('aigc:prompt:query')")
    public R<IPage<AigcPrompt>> page(Page<AigcPrompt> page, AigcPrompt data) {
        return R.ok(aigcPromptService.page(page, data));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取提示词")
    public R<AigcPrompt> getById(@PathVariable Integer id) {
        return R.ok(aigcPromptService.getById(id));
    }

    @PostMapping
    @SysLog("新增提示词")
    @PreAuthorize("@pms.hasPermission('aigc:prompt:add')")
    @Operation(summary = "新增提示词")
    public R<Boolean> add(@RequestBody AigcPrompt prompt) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        prompt.setCreateTime(now);
        prompt.setUpdateTime(null);
        return R.ok(aigcPromptService.save(prompt));
    }

    @PutMapping
    @SysLog("修改提示词")
    @PreAuthorize("@pms.hasPermission('aigc:prompt:update')")
    @Operation(summary = "修改提示词")
    public R<Boolean> update(@RequestBody AigcPrompt prompt) {
        // 保留原创建时间，不允许修改
        AigcPrompt existing = aigcPromptService.getById(prompt.getId());
        prompt.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
        prompt.setUpdateTime(LocalDateTime.now());
        return R.ok(aigcPromptService.updateById(prompt));
    }

    @DeleteMapping("/{id}")
    @SysLog("删除提示词")
    @PreAuthorize("@pms.hasPermission('aigc:prompt:delete')")
    @Operation(summary = "删除提示词")
    public R<Boolean> delete(@PathVariable Integer id) {
        return R.ok(aigcPromptService.removeById(id));
    }
}
