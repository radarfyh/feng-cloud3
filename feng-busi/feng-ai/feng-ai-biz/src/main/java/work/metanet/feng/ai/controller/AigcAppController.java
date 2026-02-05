package work.metanet.feng.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import work.metanet.feng.ai.api.entity.AigcApp;
import work.metanet.feng.ai.api.entity.AigcAppApi;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.config.AppStore;
import work.metanet.feng.ai.service.AigcAppApiService;
import work.metanet.feng.ai.service.AigcAppService;
import work.metanet.feng.ai.service.AigcKnowledgeService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.exception.BusinessException;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AI应用程序信息控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
@Tag(name = "生成式AI应用模块")
public class AigcAppController {

    private final AigcAppService aigcAppService;
    private final AigcAppApiService aigcAppApiService;
    private final AppStore appStore;
    private final AigcKnowledgeService knowledgeService;

    @GetMapping("/channel/api/{appId}")
    @Operation(summary = "查询应用渠道")
    public R<AigcAppApi> getApiChanel(@PathVariable String appId) {
        List<AigcAppApi> list = aigcAppApiService.list(Wrappers.<AigcAppApi>lambdaQuery().eq(AigcAppApi::getAppId, appId));
        return R.ok(list.isEmpty() ? null : list.get(0));
    }

    @GetMapping("/list")
    @Operation(summary = "查询应用列表")
    public R<List<AigcApp>> list(AigcApp data) {
    	List<AigcApp> list = aigcAppService.list(data);
        return R.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询应用数据")
    public R<IPage<AigcApp>> page(Page<AigcApp> page, AigcApp data) {
    	IPage<AigcApp> result = aigcAppService.page(page, data);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询应用数据")
    public R<AigcApp> findById(@PathVariable String id) {
        AigcApp app = aigcAppService.getById(id);
        return R.ok(app);
    }

    @PostMapping
    @SysLog("新增应用")
    @PreAuthorize("@pms.hasPermission('aigc:app:add')")
    @Operation(summary = "新增应用")
    public R add(@RequestBody AigcApp data) {
        validateKnowledgeConsistency(data.getKnowledgeIds());
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        aigcAppService.save(data);

        if (data.getKnowledgeIds() != null && !data.getKnowledgeIds().isEmpty()) {
            knowledgeService.update(null, Wrappers.<AigcKnowledge>lambdaUpdate()
                    .in(AigcKnowledge::getId, data.getKnowledgeIds())
                    .set(AigcKnowledge::getAppId, data.getId()));
        }

        appStore.init();
        return R.ok();
    }

    @PutMapping
    @SysLog("修改应用")
    @PreAuthorize("@pms.hasPermission('aigc:app:update')")
    @Operation(summary = "修改应用")
    public R update(@RequestBody AigcApp data) {
        validateKnowledgeConsistency(data.getKnowledgeIds());
        // 保留原创建时间，不允许修改
        AigcApp existing = aigcAppService.getById(data.getId());
        data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
        data.setUpdateTime(LocalDateTime.now());
        aigcAppService.updateById(data);

        // 清除旧绑定关系
        knowledgeService.update(null, Wrappers.<AigcKnowledge>lambdaUpdate()
                .eq(AigcKnowledge::getAppId, data.getId())
                .set(AigcKnowledge::getAppId, null));

        // 重新绑定新关系
        if (data.getKnowledgeIds() != null && !data.getKnowledgeIds().isEmpty()) {
            knowledgeService.update(null, Wrappers.<AigcKnowledge>lambdaUpdate()
                    .in(AigcKnowledge::getId, data.getKnowledgeIds())
                    .set(AigcKnowledge::getAppId, data.getId()));
        }

        appStore.init();
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除应用")
    @PreAuthorize("@pms.hasPermission('aigc:app:delete')")
    @Operation(summary = "删除应用")
    public R delete(@PathVariable Integer id) {
        aigcAppService.removeById(id);
        knowledgeService.update(null, Wrappers.<AigcKnowledge>lambdaUpdate()
                .eq(AigcKnowledge::getAppId, id)
                .set(AigcKnowledge::getAppId, null));
        appStore.init();
        return R.ok();
    }

    private void validateKnowledgeConsistency(List<Integer> knowledgeIds) {
        if (knowledgeIds == null || knowledgeIds.isEmpty()) return;

        List<AigcKnowledge> list = knowledgeService.list(Wrappers.<AigcKnowledge>lambdaQuery()
                .in(AigcKnowledge::getId, knowledgeIds));

        Set<Integer> modelIds = new HashSet<>();
        Set<Integer> storeIds = new HashSet<>();

        list.forEach(know -> {
            modelIds.add(know.getEmbedModelId());
            storeIds.add(know.getEmbedStoreId());
        });

        if (modelIds.size() > 1) {
            throw new BusinessException(CommonConstants.FAIL, "请选择相同向量模型配置的知识库");
        }
        if (storeIds.size() > 1) {
            throw new BusinessException(CommonConstants.FAIL, "请选择相同向量库数据源配置的知识库");
        }
    }
}
