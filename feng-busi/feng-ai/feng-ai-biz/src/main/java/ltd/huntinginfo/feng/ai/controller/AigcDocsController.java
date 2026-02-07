package ltd.huntinginfo.feng.ai.controller;

import ltd.huntinginfo.feng.ai.api.entity.AigcDocs;
import ltd.huntinginfo.feng.ai.api.entity.AigcOss;
import ltd.huntinginfo.feng.ai.exception.ServiceException;
import ltd.huntinginfo.feng.ai.job.TaskManager;
import ltd.huntinginfo.feng.ai.service.AigcDocsService;
import ltd.huntinginfo.feng.ai.service.AigcDocsSliceService;
import ltd.huntinginfo.feng.ai.service.AigcKnowledgeService;
import ltd.huntinginfo.feng.ai.service.AigcOssService;
import ltd.huntinginfo.feng.common.core.constant.enums.DocType;
import ltd.huntinginfo.feng.common.core.constant.enums.SliceStatus;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 文档管理主要功能：
 * 1、用户新增文档时，保存文本内容到aigc_docs.content中，并对文本内容（content）进行切片
 * 2、用户上传文件时，保存文件到MINIO或其他对象存储服务中，并保存链接到aigc_oss.url中，解析DOC、PDF、PNG、JPG文件的内容，并转存到文档记录中，并对其进行切片存于切片记录中
 * 3、文档记录的增删改查（CRUD）
 * 4、文档类型废弃，不再分为TEXT、FILE、OSS，一律只存文本内容 2025.6.25
 * 
 * @author Edison
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/docs")
@Tag(name = "生成式AI文档 模块")
public class AigcDocsController {

    private final AigcDocsService docsService;
    private final AigcKnowledgeService knowledgeService;
    private final AigcDocsSliceService docsSliceService;
    private final AigcOssService ossService;

    @GetMapping("/list")
    @Operation(summary = "查询文档列表")
    public R<List<AigcDocs>> list(AigcDocs data) {
        return R.ok(docsService.list(data));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询文档列表")
    public R<IPage<AigcDocs>> list(Page<AigcDocs> page, AigcDocs data) {
        LambdaQueryWrapper<AigcDocs> queryWrapper = Wrappers.<AigcDocs>lambdaQuery()
                .eq(data.getKnowledgeId() != null, AigcDocs::getKnowledgeId, data.getKnowledgeId())
                .eq(data.getSliceStatus() != null, AigcDocs::getSliceStatus, data.getSliceStatus())
                .like(StringUtils.isNotEmpty(data.getName()), AigcDocs::getName, data.getName())
                .like(StringUtils.isNotEmpty(data.getOrigin()), AigcDocs::getOrigin, data.getOrigin())
                .orderByDesc(AigcDocs::getCreateTime);
            
        return R.ok(docsService.page(page, queryWrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcDocs> findById(@PathVariable String id) {
        return R.ok(docsService.getById(id));
    }

    @PostMapping
    @SysLog("新增文档")
    @Operation(summary = "新增文档")
    @PreAuthorize("@pms.hasPermission('aigc:docs:add')")
    public R<?> add(@RequestBody AigcDocs data) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);

        // 加记录
        docsService.addDocs(data);
        
        Integer userId = SecurityUtils.getUser().getId();
        
        if (StrUtil.isBlankIfStr(data.getContent())) {
            throw new ServiceException("文本内容不能为空");
        }
        data.setSliceStatus(SliceStatus.PENDING.getCode());
        TaskManager.submitTask(userId, Executors.callable(() -> {
        	docsService.textEmbeddingTask(userId, data, null);
        }));
        
        return R.ok();
    }

    @PutMapping
    @SysLog("修改文档")
    @Operation(summary = "修改文档")
    @PreAuthorize("@pms.hasPermission('aigc:docs:update')")
    public R<?> update(@RequestBody AigcDocs data) {
        // 保留原创建时间，不允许修改
    	AigcDocs existing = docsService.getById(data.getId());
    	data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	data.setUpdateTime(LocalDateTime.now());

        if (data.getId() <= 0) {
        	throw new ServiceException("ID不能小于等于0");
        }
    	docsService.updateDocs(data);
    	
    	Integer userId = SecurityUtils.getUser().getId();

        if (StrUtil.isBlankIfStr(data.getContent())) {
            throw new ServiceException("文本内容不能为空");
        }
        data.setSliceStatus(SliceStatus.PENDING.getCode());
        docsService.clearDocSlices(data.getId());
        TaskManager.submitTask(userId, Executors.callable(() -> {
        	docsService.textEmbeddingTask(userId, data, null);
        }));
    	
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除文档")
    @Operation(summary = "删除文档")
    @PreAuthorize("@pms.hasPermission('aigc:docs:delete')")
    @Transactional
    public R<?> delete(@PathVariable Integer id) {
        // 删除文档
        boolean ret = docsService.removeById(id);
        if (ret) {
            LambdaQueryWrapper<AigcOss> queryWrapper = Wrappers.<AigcOss>lambdaQuery()
                    .eq(id != null && id > 0, AigcOss::getDocId, id);
        	ossService.remove(queryWrapper);
        	// 删除切面数据
	        docsService.clearDocSlices(id);
        }
        return R.ok();
    }
    
    @PostMapping("/reSlice/{docId}")
    @SysLog("重新切片文档")
    @Operation(summary = "重新切片文档")
    @PreAuthorize("@pms.hasPermission('aigc:docs:reslice')")
    public R<?> reSliceDocument(@PathVariable Integer docId) {
        docsService.reSliceDocument(docId);
        return R.ok("重新切片任务已触发");
    }
}

