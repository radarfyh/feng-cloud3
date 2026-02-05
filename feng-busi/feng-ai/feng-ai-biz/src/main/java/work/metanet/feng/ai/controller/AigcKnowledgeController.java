package work.metanet.feng.ai.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import work.metanet.feng.ai.api.dto.ChatReq;
import work.metanet.feng.ai.api.entity.AigcConversation;
import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcEmbedStore;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcKnowledge.SliceConfig;
import work.metanet.feng.ai.api.entity.AigcModel;
import work.metanet.feng.ai.api.entity.AigcOss;
import work.metanet.feng.ai.config.KnowledgeStoreFactory;
import work.metanet.feng.ai.exception.ServiceException;
import work.metanet.feng.ai.job.TaskManager;
import work.metanet.feng.ai.service.AigcConversationService;
import work.metanet.feng.ai.service.AigcDocsService;
import work.metanet.feng.ai.service.AigcDocsSliceService;
import work.metanet.feng.ai.service.AigcEmbedStoreService;
import work.metanet.feng.ai.service.AigcKnowledgeService;
import work.metanet.feng.ai.service.AigcModelService;
import work.metanet.feng.ai.service.AigcOssService;
import work.metanet.feng.ai.service.DocPreSplitService;
import work.metanet.feng.common.core.constant.enums.DocType;
import work.metanet.feng.common.core.constant.enums.SliceStatus;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 知识库管理主要功能：提供知识库的增删改查（CRUD）功能 
 * 知识库下辖文件和文档，小文件解析的内容存在一条文档记录中，大文件解析的内容存在多条文档记录中 2025.6.23
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledge")
@Tag(name = "生成式AI知识库模块")
@Slf4j
public class AigcKnowledgeController {

    private final AigcEmbedStoreService embedStoreService;
    private final AigcModelService modelService;
    private final KnowledgeStoreFactory knowledgeStore;

    private final AigcKnowledgeService knowledgeService;
    private final AigcConversationService conversationService;
    private final AigcDocsService docsService;
    private final AigcOssService ossService;

    private final AigcDocsSliceService aigcDocSliceService;
    private final DocPreSplitService docPreSplitService;

    @GetMapping("/list")
    @Operation(summary = "查询知识库列表")
    public R<List<AigcKnowledge>> list(AigcKnowledge data) {
    	LambdaQueryWrapper<AigcKnowledge> queryWrapper = Wrappers.<AigcKnowledge>lambdaQuery().orderByDesc(AigcKnowledge::getCreateTime);
        List<AigcKnowledge> list = knowledgeService.list(queryWrapper);
        getRelatedData(list);
        return R.ok(list);
    }

    private void getRelatedData(List<AigcKnowledge> records) {
        Map<Integer, List<AigcEmbedStore>> embedStoreMap = embedStoreService.list().stream().collect(Collectors.groupingBy(AigcEmbedStore::getId));
        Map<Integer, List<AigcModel>> embedModelMap = modelService.list().stream().collect(Collectors.groupingBy(AigcModel::getId));
        Map<Integer, List<AigcDocs>> docsMap = docsService.list().stream().collect(Collectors.groupingBy(AigcDocs::getKnowledgeId));
        Map<Integer, List<AigcConversation>> conversationMap = conversationService.list().stream()
        		.filter(conv -> conv.getKnowledgeId() != null)
        		.collect(Collectors.groupingBy(AigcConversation::getKnowledgeId));
        records.forEach(item -> {
        	// 文档列表
            List<AigcDocs> docs = docsMap.get(item.getId());
            if (docs != null) {
                item.setDocsNum(docs.size());
                item.setTotalSize(docs.stream().filter(d -> d.getSize() != null).mapToLong(AigcDocs::getSize).sum());
                item.setDocs(docs);
            }
            // 模型
            if (item.getEmbedModelId() != null) {
                List<AigcModel> list = embedModelMap.get(item.getEmbedModelId());
                item.setEmbedModel(list == null ? null : list.get(0));
            }
            // 向量库配置
            if (item.getEmbedStoreId() != null) {
                List<AigcEmbedStore> list = embedStoreMap.get(item.getEmbedStoreId());
                item.setEmbedStore(list == null ? null : list.get(0));
            }
            // 会话
            List<AigcConversation> conversations = conversationMap.get(item.getId());
            if (conversations != null) {
            	item.setConversations(conversations);
            }
        });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询知识库列表")
    public R<IPage<AigcKnowledge>> list(Page<AigcKnowledge> page, AigcKnowledge data) {
        
    	LambdaQueryWrapper<AigcKnowledge> queryWrapper = Wrappers.<AigcKnowledge>lambdaQuery()
                .like(!StrUtil.isBlank(data.getName()), AigcKnowledge::getName, data.getName())
                .eq(data.getAppId() != null && data.getAppId() > 0, AigcKnowledge::getAppId, data.getAppId())
                .orderByDesc(AigcKnowledge::getCreateTime);
        Page<AigcKnowledge> iPage = knowledgeService.page(page, queryWrapper);

        getRelatedData(iPage.getRecords());

        return R.ok(iPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcKnowledge> findById(@PathVariable Integer id) {
        AigcKnowledge knowledge = knowledgeService.getById(id);
        if (knowledge.getEmbedStoreId() != null) {
            knowledge.setEmbedStore(embedStoreService.getById(knowledge.getEmbedStoreId()));
        }
        if (knowledge.getEmbedModelId() != null) {
            knowledge.setEmbedModel(modelService.getById(knowledge.getEmbedModelId()));
        }
        return R.ok(knowledge);
    }

    @PostMapping
    @SysLog("新增知识库")
    @Operation(summary = "新增知识库")
    @PreAuthorize("@pms.hasPermission('aigc:knowledge:add')")
    public R<?> add(@RequestBody AigcKnowledge data) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        data.setCreateTime(now);
        data.setUpdateTime(null);
        knowledgeService.save(data);
        knowledgeStore.init();
        return R.ok();
    }

    @PutMapping
    @SysLog("更新知识库")
    @Operation(summary = "更新知识库")
    @PreAuthorize("@pms.hasPermission('aigc:knowledge:update')")
    public R<?> update(@RequestBody AigcKnowledge data) {
        // 保留原创建时间，不允许修改
    	AigcKnowledge existing = knowledgeService.getById(data.getId());
    	data.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	data.setUpdateTime(LocalDateTime.now());
        knowledgeService.updateById(data);
        knowledgeStore.init();
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SysLog("删除知识库")
    @Operation(summary = "删除知识库")
    @PreAuthorize("@pms.hasPermission('aigc:knowledge:delete')")
    public R<?> delete(@PathVariable Integer id) {
        knowledgeService.removeKnowledge(id);
        knowledgeStore.init();
        return R.ok();
    }
    
    @SysLog("更新文档的文本内容")
    @PostMapping("/text")
    @PreAuthorize("@pms.hasPermission('aigc:embedding:text')")
    @Operation(summary = "更新文档的文本内容")
    public R<?> text(@RequestBody AigcDocs data, SliceConfig sliceConfig) {
        if (StrUtil.isBlankIfStr(data.getContent())) {
            throw new ServiceException("文本内容不能为空");
        }
        
        data.setType(DocType.TEXT.getCode()).setSliceStatus(SliceStatus.PENDING.getCode());
        
        Integer userId = SecurityUtils.getUser().getId();
        
        TaskManager.submitTask(userId, Executors.callable(() -> {
        	docsService.textEmbeddingTask(userId, data, sliceConfig);
        }));

        return R.ok();
    }
    
    @PostMapping("/uploadFile/{knowledgeId}")
    @PreAuthorize("@pms.hasPermission('aigc:embedding:docs')")
    @Operation(summary = "上传文件")
    @Deprecated
    public R<?> uploadFile(MultipartFile file, @PathVariable Integer knowledgeId) {
        Integer userId = SecurityUtils.getUser().getId();
        AigcOss oss = ossService.upload(file, userId);
        
        if (oss != null) {
        	R.failed("上传文件失败");
        }
    	// 预拆分并存储到aigc_docs
    	List<AigcDocs> list = docPreSplitService.preSplitAndStore(oss, oss.getKnowledgeId());
    	// 对于与拆分的文档记录进行切片
    	list.forEach(docs -> {
            TaskManager.submitTask(userId, Executors.callable(() -> {
            	docsService.embedDocsSlice(userId, docs, null);
            }));
    	});
        return R.ok("上传文件成功");
    }
    
    @SysLog("上传文件")
    @PostMapping("/uploadOss/{knowledgeId}")
    @PreAuthorize("@pms.hasPermission('aigc:embedding:docs')")
    @Operation(summary = "上传文件")
    public R<?> uploadOss(AigcOss oss, @PathVariable Integer knowledgeId) {
    	AigcOss oldOss = ossService.getById(knowledgeId);
    	
        Boolean ret = ossService.updateById(oss);
        
        if (!ret) {
        	R.failed("上传文件失败");
        }
        
        if (!StrUtil.equals(oss.getUrl(), oldOss.getUrl()) 
        		|| (StrUtil.equals(oss.getUrl(), oldOss.getUrl()) && oss.getSize() != oldOss.getSize())) {
            // 重新切分前删除以前的切片
            if (oss.getKnowledgeId() != null && oss.getKnowledgeId() > 0) {
            	LambdaQueryWrapper<AigcDocs> queryWrapper = Wrappers.<AigcDocs>lambdaQuery()
                        .eq(oss.getKnowledgeId() != null && oss.getKnowledgeId() > 0, AigcDocs::getKnowledgeId, oss.getKnowledgeId());
            	List<AigcDocs> list = docsService.list(queryWrapper);
            	list.forEach(docs -> {
            		docsService.clearDocSlices(docs.getId());
            	});
            }
        	// 预拆分并存储到aigc_docs
        	List<AigcDocs> list = docPreSplitService.preSplitAndStore(oss, knowledgeId);
        	// 对于与拆分的文档记录进行切片
        	list.forEach(docs -> {
            	Integer userId = SecurityUtils.getUser().getId();
                TaskManager.submitTask(userId, Executors.callable(() -> {
                	docsService.embedDocsSlice(userId, docs, null);
                }));
        	});
        }
        return R.ok("上传文件成功");
    }

    @SysLog("重新更新文档内容")
    @GetMapping("/re-embed/{docsId}")
    @Operation(summary = "重新更新文档内容")
    public R<?> reEmbed(@PathVariable Integer docsId, SliceConfig sliceConfig) {
    	Integer userId = SecurityUtils.getUser().getId();
        AigcDocs docs = docsService.getById(docsId);
        if (docs == null) {
            throw new ServiceException("没有查询到文档数据");
        }
        
        if ("TEXT".equals(docs.getType())) {
            // clear before re-embed
            docsService.clearDocSlices(docsId);
            
            text(docs, sliceConfig);
        }
        else if ("OSS".equals(docs.getType())) {
            // clear before re-embed
            docsService.clearDocSlices(docsId);
            
            TaskManager.submitTask(userId, Executors.callable(() -> {
                docsService.embedDocsSlice(userId, docs, sliceConfig);
            }));
        }
        return R.ok();
    }

    @PostMapping("/search")
    @Operation(summary = "搜索")
    public R<List<Map<String, Object>>> search(@RequestBody ChatReq data) {
        return R.ok(docsService.search(data));
    }
    
    @PostMapping("/full-text")
    @Operation(summary = "搜索")
    public R<List<Map<String, Object>>> fullText(@RequestBody ChatReq data) {
        return R.ok(docsService.fullTextSearch(data));
    }
}

