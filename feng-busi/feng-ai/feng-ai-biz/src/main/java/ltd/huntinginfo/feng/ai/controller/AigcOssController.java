package ltd.huntinginfo.feng.ai.controller;

import ltd.huntinginfo.feng.ai.api.entity.AigcDocs;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge.SliceConfig;
import ltd.huntinginfo.feng.ai.api.entity.AigcOss;
import ltd.huntinginfo.feng.ai.job.TaskManager;
import ltd.huntinginfo.feng.ai.provider.EmbeddingProvider;
import ltd.huntinginfo.feng.ai.service.AigcDocsService;
import ltd.huntinginfo.feng.ai.service.AigcDocsSliceService;
import ltd.huntinginfo.feng.ai.service.AigcKnowledgeService;
import ltd.huntinginfo.feng.ai.service.AigcOssService;
import ltd.huntinginfo.feng.ai.service.DocPreSplitService;
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
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 文件资源管理主要功能：文件资源记录的增删改查（CRUD）功能
 * 
 * @author Edison
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RequestMapping("/oss")
@RestController
@AllArgsConstructor
@Tag(name = "生成式AI对象存储模块")
public class AigcOssController {

    private final AigcOssService aigcOssService;
    private final AigcDocsService aigcDocsService;
    private final AigcKnowledgeService aigcKnowledgeService;
    private final AigcDocsSliceService aigcDocSliceService;
    private final EmbeddingProvider embeddingProvider;
    private final DocPreSplitService docPreSplitService;

    @GetMapping("/list")
    @Operation(summary = "查询对象存储列表")
    public R<List<AigcOss>> list() {
        List<AigcOss> list = aigcOssService.list(Wrappers.<AigcOss>lambdaQuery()
                .eq(AigcOss::getUserId, SecurityUtils.getUser().getId())
                .orderByDesc(AigcOss::getCreateTime)
        );
        return R.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @PreAuthorize("@pms.hasPermission('aigc:oss:query')")
    public R<IPage<AigcOss>> list(Page<AigcOss> page, AigcOss data) {
        LambdaQueryWrapper<AigcOss> queryWrapper = Wrappers.<AigcOss>lambdaQuery()
                .like(!StrUtil.isBlank(data.getOriginalFilename()), AigcOss::getOriginalFilename, data.getOriginalFilename())
                .like(!StrUtil.isBlank(data.getFilename()), AigcOss::getFilename, data.getFilename())
                .like(!StrUtil.isBlank(data.getUrl()), AigcOss::getUrl, data.getUrl())
                .eq(data.getKnowledgeId() != null && data.getKnowledgeId() > 0, AigcOss::getKnowledgeId, data.getKnowledgeId()) 
                .eq(!StrUtil.isBlank(data.getPlatform()), AigcOss::getPlatform, data.getPlatform())
                .orderByDesc(AigcOss::getCreateTime);
        IPage<AigcOss> iPage = aigcOssService.page(page, queryWrapper);
        return R.ok(iPage);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcOss> findById(@PathVariable Integer id) {
        return R.ok(aigcOssService.getById(id));
    }
    
    @PostMapping("/upload")
    @SysLog("上传OSS文件")
    @Operation(summary = "上传文件，已废弃")
    @Deprecated
    public R<?> upload(MultipartFile file) {
        AigcOss oss = aigcOssService.upload(file, SecurityUtils.getUser().getId());
        
    	// 预拆分并存储到aigc_docs
    	List<AigcDocs> list = docPreSplitService.preSplitAndStore(oss, oss.getKnowledgeId());
    	// 对于与拆分的文档记录进行切片
    	AigcKnowledge knowledge = aigcKnowledgeService.getById(oss.getKnowledgeId());
    	SliceConfig sliceConfig = new SliceConfig(knowledge.getMaxLength(), knowledge.getOverlapSize(), knowledge.getSliceMode());

    	list.forEach(docs -> {
        	Integer userId = SecurityUtils.getUser().getId();
            TaskManager.submitTask(userId, Executors.callable(() -> {
            	// 改为切文本 2025.6.23
            	//aigcDocsService.embedDocsSlice(userId, docs, null);
            	aigcDocsService.textEmbeddingTask(userId, docs, sliceConfig);
            }));
    	});
        
        return R.ok(oss);
    }
    
    @PostMapping
    @SysLog("新增文件资源")
    @Operation(summary = "新增文件资源")
    @PreAuthorize("@pms.hasPermission('aigc:oss:add')")
    public R<?> add(@RequestBody AigcOss data) {
    	Integer knowledgeId = data.getKnowledgeId();
    	if (knowledgeId == null) {
    		return R.failed("知识库ID不能为空");
    	}
    	
    	boolean ret = aigcOssService.save(data);
    	
    	// 预拆分并存储到aigc_docs
    	List<AigcDocs> list = docPreSplitService.preSplitAndStore(data, knowledgeId);
    	
    	// 对于与拆分的文档记录进行切片
    	AigcKnowledge knowledge = aigcKnowledgeService.getById(knowledgeId);
    	SliceConfig sliceConfig = new SliceConfig(knowledge.getMaxLength(), knowledge.getOverlapSize(), knowledge.getSliceMode());

    	list.forEach(docs -> {
        	Integer userId = SecurityUtils.getUser().getId();
            TaskManager.submitTask(userId, Executors.callable(() -> {
            	// 改为切文本 2025.6.23
            	//aigcDocsService.embedDocsSlice(userId, docs, null);
            	aigcDocsService.textEmbeddingTask(userId, docs, sliceConfig);
            }));
    	});
        
        return R.ok(ret);
    }

    @PutMapping
    @SysLog("更新文件资源")
    @Operation(summary = "更新文件资源")
    @PreAuthorize("@pms.hasPermission('aigc:oss:update')")
    public R<?> update(@RequestBody AigcOss data) {
    	Integer knowledgeId = data.getKnowledgeId();
    	if (knowledgeId == null) {
    		return R.failed("知识库ID不能为空");
    	}
    	
    	AigcOss oldOss = aigcOssService.getById(data.getId());
    	
        Boolean ret = aigcOssService.updateById(data);
        
        if (!StrUtil.equals(data.getUrl(), oldOss.getUrl()) 
        		|| (StrUtil.equals(data.getUrl(), oldOss.getUrl()) && data.getSize() != oldOss.getSize())) {
            // 重新切分前删除以前的切片
            if (data.getKnowledgeId() != null && data.getKnowledgeId() > 0) {
            	LambdaQueryWrapper<AigcDocs> queryWrapper = Wrappers.<AigcDocs>lambdaQuery()
                        .eq(knowledgeId != null && knowledgeId > 0, AigcDocs::getKnowledgeId, knowledgeId);
            	List<AigcDocs> list = aigcDocsService.list(queryWrapper);
            	list.forEach(docs -> {
            		aigcDocsService.clearDocSlices(docs.getId());
            	});
            }
        	// 预拆分并存储到aigc_docs
        	List<AigcDocs> list = docPreSplitService.preSplitAndStore(data, knowledgeId);
        	// 对于与拆分的文档记录进行切片
        	AigcKnowledge knowledge = aigcKnowledgeService.getById(knowledgeId);
        	SliceConfig sliceConfig = new SliceConfig(knowledge.getMaxLength(), knowledge.getOverlapSize(), knowledge.getSliceMode());
        	list.forEach(docs -> {
            	Integer userId = SecurityUtils.getUser().getId();
                TaskManager.submitTask(userId, Executors.callable(() -> {
                	// 改为切文本 2025.6.23
                	//aigcDocsService.embedDocsSlice(userId, docs, null);
                	aigcDocsService.textEmbeddingTask(userId, docs, sliceConfig);
                }));
        	});
        }

        return R.ok(ret);
    }

    @DeleteMapping("/{id}")
    @SysLog("删除文件资源")
    @Operation(summary = "删除文件资源")
    @PreAuthorize("@pms.hasPermission('aigc:oss:delete')")
    public R<?> delete(@PathVariable Integer id) {
    	AigcOss oldOss = aigcOssService.getById(id);
    	
        boolean ret = aigcOssService.removeById(id);
        if (ret) {
            // 删除切片和文档
            if (oldOss.getKnowledgeId() != null && oldOss.getKnowledgeId() > 0) {
            	LambdaQueryWrapper<AigcDocs> queryWrapper = Wrappers.<AigcDocs>lambdaQuery()
                        .eq(oldOss.getKnowledgeId() != null && oldOss.getKnowledgeId() > 0, AigcDocs::getKnowledgeId, oldOss.getKnowledgeId())
                        .eq(oldOss.getId() != null && oldOss.getId() > 0, AigcDocs::getOssId, oldOss.getId());
            	List<AigcDocs> list = aigcDocsService.list(queryWrapper);
            	list.forEach(docs -> {
            		aigcDocsService.clearDocSlices(docs.getId());
            		aigcDocsService.removeById(docs.getId());
            	});
            }
        }
        return R.ok(ret);
    }
}
