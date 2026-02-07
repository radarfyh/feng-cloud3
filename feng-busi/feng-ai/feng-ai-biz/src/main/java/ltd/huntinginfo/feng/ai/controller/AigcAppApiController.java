package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ltd.huntinginfo.feng.ai.api.entity.AigcAppApi;
import ltd.huntinginfo.feng.ai.api.vo.AigcAppApiPromptVO;
import ltd.huntinginfo.feng.ai.config.AppChannelStore;
import ltd.huntinginfo.feng.ai.service.AigcAppApiService;
import ltd.huntinginfo.feng.common.core.constant.AppConst;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI渠道控制器，向前端提供API
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/api")
@Tag(name = "生成式AI应用API模块")
public class AigcAppApiController {

    private final AigcAppApiService appApiService;
    private final AppChannelStore appChannelStore;

    @GetMapping("/create/{id}/{channel}")
    @Operation(summary = "创建频道")
    public R<?> create(@PathVariable Integer id, @PathVariable String channel) {
        String uuid = AppConst.PREFIX + IdUtil.simpleUUID();
        appApiService.save(new AigcAppApi()
                .setAppId(id)
                .setApiKey(uuid)
                .setChannel(channel));
        appChannelStore.init();
        return R.ok();
    }

    @GetMapping("/list")
    @Operation(summary = "查询数据列表")
    public R<List<AigcAppApi>> list(AigcAppApi data) {
        return appApiService.list(data);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('aigc:app-api:query')")
    public R<IPage<AigcAppApi>> page(Page<AigcAppApi> page, AigcAppApi data) {
        return appApiService.page(page, data);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询数据")
    public R<AigcAppApiPromptVO> findById(@PathVariable Integer id) {
        AigcAppApi appApi = appApiService.getAppApiWithClassifiedPrompts(id);
        AigcAppApiPromptVO vo = new AigcAppApiPromptVO()
                .setId(appApi.getId())
                .setAppId(appApi.getAppId())
                .setApiKey(appApi.getApiKey())
                .setChannel(appApi.getChannel())
                .setPrompts(appApi.getPrompts());
        return R.ok(vo);
    }

    @PostMapping
    @SysLog("新增API渠道")
    @Operation(summary = "新增API渠道")
    @PreAuthorize("@pms.hasPermission('aigc:app-api:add')")
    public R<?> add(@RequestBody AigcAppApi data) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        appApiService.save(data);
        appChannelStore.init();
        return R.ok();
    }

    @PutMapping
    @SysLog("修改API渠道")
    @Operation(summary = "修改API渠道")
    @PreAuthorize("@pms.hasPermission('aigc:app-api:update')")
    public R<?> update(@RequestBody AigcAppApi data) {
        // 保留原创建时间，不允许修改
    	AigcAppApi existing = appApiService.getById(data.getId());
        data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
        data.setUpdateTime(LocalDateTime.now());
        appApiService.updateById(data);
        appChannelStore.init();
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除API渠道")
    @Operation(summary = "删除API渠道")
    @PreAuthorize("@pms.hasPermission('aigc:app-api:delete')")
    public R<?> delete(@PathVariable Integer id) {
        appApiService.removeById(id);
        appChannelStore.init();
        return R.ok();
    }
}
