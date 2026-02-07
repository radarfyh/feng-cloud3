package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ltd.huntinginfo.feng.admin.api.dto.CustomerDTO;
import ltd.huntinginfo.feng.ai.api.dto.ModelDTO;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.service.AigcModelService;
import ltd.huntinginfo.feng.common.agent.component.ProviderRefreshEvent;
import ltd.huntinginfo.feng.common.core.constant.enums.ModelTypeEnum;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

/**
 * AI模型控制类（APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/model")
@Tag(name = "生成式AI模型模块")
public class AigcModelController {

    private final AigcModelService modelService;
    private final SpringContextHolder contextHolder;

    @GetMapping("/list")
    @Operation(summary = "按类型查询模型列表")
    public R<List<AigcModel>> list(String type) {
    	AigcModel data = new AigcModel();
    	data.setType(type);
    	List<AigcModel> result = modelService.list(data);
    	log.info("AigcMessageController --> getById --> id: {}, result: {}", type, result);
        return R.ok(result);
    }
    
    @PostMapping("/listAll")
    @Operation(summary = "查询模型列表")
    public R<List<AigcModel>> listAll(@RequestBody ModelDTO dto) {
    	AigcModel model = new AigcModel();
    	BeanUtil.copyProperties(dto, model);
    	List<AigcModel> result = modelService.list(model);
        return R.ok(result);
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询模型列表")
    public R<IPage<AigcModel>> list(Page<AigcModel> page, AigcModel data) {
        Page<AigcModel> iPage = modelService.page(data, page);
        return R.ok(iPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcModel> findById(@PathVariable Integer id) {
        return R.ok(modelService.selectById(id));
    }

    @PostMapping
    @SysLog("添加模型")
    @Operation(summary = "新增模型")
    @PreAuthorize("@pms.hasPermission('aigc:model:add')")
    public R add(@RequestBody AigcModel data) {
        if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
            data.setApiKey(null);
        }
        if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
            data.setSecretKey(null);
        }
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        modelService.save(data);
        SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
        return R.ok();
    }

    @PutMapping
    @SysLog("修改模型")
    @Operation(summary = "修改模型")
    @PreAuthorize("@pms.hasPermission('aigc:model:update')")
    public R update(@RequestBody AigcModel data) {
        if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
            data.setApiKey(null);
        }
        if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
            data.setSecretKey(null);
        }
        // 保留原创建时间，不允许修改
        AigcModel existing = modelService.getById(data.getId());
        data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
        data.setUpdateTime(LocalDateTime.now());
        modelService.updateById(data);
        SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除模型")
    @Operation(summary = "删除模型")
    @PreAuthorize("@pms.hasPermission('aigc:model:delete')")
    public R delete(@PathVariable Integer id) {
        modelService.removeById(id);

        // Delete dynamically registered beans, according to ID
        contextHolder.unregisterBean(id.toString());
        return R.ok();
    }
}

