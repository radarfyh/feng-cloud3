package ltd.huntinginfo.feng.ai.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.ai.api.dto.ChatReq;
import ltd.huntinginfo.feng.ai.api.dto.EmbeddingR;
import ltd.huntinginfo.feng.ai.api.entity.*;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge.SliceConfig;
import ltd.huntinginfo.feng.ai.config.PgVectorFullTextSearchStore;
import ltd.huntinginfo.feng.ai.config.ProxyProperties;
import ltd.huntinginfo.feng.ai.mapper.AigcDocsMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcDocsSliceMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcMessageMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcOssMapper;
import ltd.huntinginfo.feng.ai.provider.EmbeddingProvider;
import ltd.huntinginfo.feng.ai.service.*;
import ltd.huntinginfo.feng.common.core.constant.enums.BuiltInRoleEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.SliceStatus;
import ltd.huntinginfo.feng.common.core.util.ServletUtil;

import static ltd.huntinginfo.feng.common.core.constant.EmbedConst.KNOWLEDGE;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * 文档管理-服务实现类（合并向量相关功能）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class AigcDocsServiceImpl extends ServiceImpl<AigcDocsMapper, AigcDocs> implements AigcDocsService {
	
	private static final Double FULL_TEXT_MIN_SCORE = 0.05;
	private static final Double CHINESE_FULL_TEXT_MIN_SCORE = 0.01;
	private static final Integer FULL_TEXT_LIMIT = 10;

    private final AigcDocsSliceMapper docsSliceMapper;
    private final AigcKnowledgeService knowledgeService;
    private final AigcOssService ossService;
    private final AigcOssMapper ossMapper;
    private final EmbeddingProvider embeddingProvider;
    private final AigcDocsMapper docsMapper;
    private final ProxyProperties proxyProperties;
    private final AigcMessageMapper messageMapper;

    @Override
    public List<AigcDocs> list(AigcDocs data) {
        List<AigcDocs> list = baseMapper.selectList(Wrappers.<AigcDocs>lambdaQuery()
                .like(StrUtil.isNotBlank(data.getName()), AigcDocs::getName, data.getName())
                .orderByDesc(AigcDocs::getCreateTime));
        return list;
    }
    
    /**
     * 添加文档
     * <p>
     * 该方法用于将文档数据添加到数据库，并设置创建时间。
     * </p>
     * 
     * @param data 文档实体
     */
    @Override
    @Transactional
    public void addDocs(AigcDocs data) {
        // 设置创建时间
        try {
            docsMapper.insert(data);
            log.info("Document added with ID: {}", data.getId());
        } catch (Exception e) {
            log.error("Error while adding document: {}", data, e);
            throw e;
        }
    }

    /**
     * 删除文档及其切片
     * <p>
     * 该方法删除指定文档及与之关联的所有文档切片。
     * </p>
     * 
     * @param docsId 文档ID
     */
    @Override
    @Transactional
    public void removeSlicesOfDoc(Integer docsId) {
        try {
            LambdaQueryWrapper<AigcDocsSlice> deleteWrapper = Wrappers.<AigcDocsSlice>lambdaQuery()
                    .eq(AigcDocsSlice::getDocsId, docsId);
            int count = docsSliceMapper.delete(deleteWrapper);
            log.debug("Removed all slices of doc [{}], count: [{}]", docsId, count);
        } catch (Exception e) {
            log.error("Error while removing slices of doc [{}]", docsId, e);
            throw e;
        }
    }
    /**
     * 更新文档
     * <p>
     * 该方法用于更新现有的文档数据。
     * </p>
     * 
     * @param data 文档实体
     */
    @Override
    @Transactional
    public void updateDocs(AigcDocs data) {
        try {
            docsMapper.updateById(data);
            log.info("Document updated with ID: {}", data.getId());
        } catch (Exception e) {
            log.error("Error while updating document: {}", data, e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    @Deprecated
    public void autoSliceDocument(AigcDocs doc) {
        // 仅处理需要切片的状态
        if (doc.getSliceStatus() != null && doc.getSliceStatus() == SliceStatus.COMPLETED.getCode()) {
            return;
        }

        try {
            // 1. 更新状态为"处理中"
            doc.setSliceStatus(SliceStatus.PROCESSING.getCode());
            docsMapper.updateById(doc);

            // 2. 清除旧切片
            clearDocSlices(doc.getId());

            // 3. 执行切片（不同类型处理）
            List<AigcDocsSlice> slices;
            if (StrUtil.isBlank(doc.getContent())) {
                log.warn("文档内容字段为空");
                return;
            }
            
            slices = sliceTextContent(doc);

            // 4. 向量化处理并保存切片
            embedAndSaveSlices(doc, slices);

            // 5. 更新文档状态
            doc.setSliceStatus(SliceStatus.COMPLETED.getCode()); // 已完成
            doc.setSliceNum(slices.size());
            doc.setLastSliceTime(LocalDateTime.now());
            docsMapper.updateById(doc);

        } catch (Exception e) {
            log.error("文档切片失败 - docId: {}", doc.getId(), e);
            doc.setSliceStatus(SliceStatus.FAILED.getCode()); // 标记为失败
            docsMapper.updateById(doc);
            throw new RuntimeException("文档切片失败", e);
        }
    }

    @Override
    @Transactional
    @Deprecated
    public void reSliceDocument(Integer docId) {
        AigcDocs doc = docsMapper.selectById(docId);
        if (doc == null) throw new RuntimeException("文档不存在");
        
        // 重置状态后触发自动切片
        doc.setSliceStatus(SliceStatus.PENDING.getCode());
        docsMapper.updateById(doc);
        autoSliceDocument(doc);
    }

    @Override
    @Transactional
    public void clearDocSlices(Integer docsId) {
        // 设置代理（如果启用）
        if (proxyProperties.isEnabled()) {
            System.setProperty("http.proxyHost", proxyProperties.getHttp().getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxyProperties.getHttp().getPort()));
            System.setProperty("https.proxyHost", proxyProperties.getHttps().getHost());
            System.setProperty("https.proxyPort", String.valueOf(proxyProperties.getHttps().getPort()));
            System.setProperty("https.protocols", "TLSv1.2");
        }
        
        if (docsId == null || docsId <= 0) {
            return;
        }
        
        AigcDocs docs = docsMapper.selectById(docsId);
        if (ObjUtil.isNull(docs)) {
        	return;
//            List<AigcDocs> list = docsMapper.selectList(Wrappers.<AigcDocs>lambdaQuery()
//                    .eq(AigcDocs::getId, docsId)
//                    .orderByDesc(AigcDocs::getCreateTime));
//            if (ObjUtil.isNull(list) || list.size() <= 0) {
//            	return;
//            }
//            docs = list.get(0);
        }

        
        // remove from embedding store
        LambdaQueryWrapper<AigcDocsSlice> selectWrapper = Wrappers.<AigcDocsSlice>lambdaQuery()
                .select(AigcDocsSlice::getVectorId)
                .eq(AigcDocsSlice::getDocsId, docsId)
                .eq(AigcDocsSlice::getKnowledgeId, docs.getKnowledgeId());
        
        // 使用 Collectors.toList() 来收集流中的元素到列表中
        List<String> vectorIds = docsSliceMapper.selectList(selectWrapper)
                .stream()
                .map(AigcDocsSlice::getVectorId)
                .collect(Collectors.toList()); 
        
        if (vectorIds.isEmpty()) {
            return;
        }

        EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(docs.getKnowledgeId());
        
        try {
        	embeddingStore.removeAll(vectorIds);
        } catch (Exception e) {
        	log.error("clearDocSlices-->删除向量数据库失败:{}", e.getMessage());
        }
        // remove from docSlice
        removeSlicesOfDoc(docsId);
        
        // 清理代理设置
        if (proxyProperties.isEnabled()) {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
        }
    }

    /**
     * 切片OSS对象，传递URL，废弃
     */
    @Override
    @Deprecated
    public void embedDocsSlice(Integer ossId, Integer userId, AigcDocs data, String url, SliceConfig sliceConfig) {
        // 切割文档
        List<EmbeddingR> list = knowledgeService.embeddingDocs(
        		data.getKnowledgeId(),
        		data.getId(),
        		userId,
        		ossId,
        		sliceConfig);
    }
    
    /**
     * 切片Minio对象
     */
    @Override
    public void embedDocsSlice(Integer userId, AigcDocs data, SliceConfig sliceConfig) {
    	
        LambdaQueryWrapper<AigcOss> selectWrapper = Wrappers.<AigcOss>lambdaQuery()
                .eq(AigcOss::getDocId, data.getId());
    	List<AigcOss> listOss = ossService.list(selectWrapper);
    	listOss.forEach(oss -> {
            List<EmbeddingR> list = knowledgeService.embeddingDocs(
            		data.getKnowledgeId(),
            		data.getId(),
            		userId,
            		oss.getId(),
            		sliceConfig);
    	});
    }

    /**
     * 切片文本内容
     */
    @Override
    public void textEmbeddingTask(Integer userId, AigcDocs data, SliceConfig sliceConfig) {
        try {
            List<EmbeddingR> list = knowledgeService.embeddingText(
            		data.getKnowledgeId(),
            		data.getId(),
            		userId,
            		data.getContent(),
            		sliceConfig);
            if (list != null) {
            	docsMapper.updateById(new AigcDocs()
            		.setId(data.getId())
            		.setSliceStatus(SliceStatus.COMPLETED.getCode()).setSliceNum(1)
            		.setUpdateBy(userId.toString())
            		.setSliceNum(list.size())
            		.setSize(new Long(data.getContent().length()))
            		.setMaxLength(sliceConfig.getMaxSliceSize())
            		.setOverlapSize(sliceConfig.getOverlapSize())
            		.setSliceMode(sliceConfig.getSliceMode()));
            }
        } catch (Exception e) {
            //e.printStackTrace();
        	log.error("更新文档的文本内容错误-->{}", e.getMessage());

            // delete data
            removeSlicesOfDoc(data.getId());
        }  finally  {

        }
    }
    
    private void saveMessage(String content, ChatReq req, Integer inputToken, Integer outputToken, String role) {
        if (req.getConversationId() != null) {
            AigcMessage message = new AigcMessage();
            BeanUtils.copyProperties(req, message);
        
            message.setPromptTokens(inputToken)
            	.setTokens(outputToken)
            	.setStatus("0")
            	.setCreateBy(req.getUsername())
            	.setCreateTime(LocalDateTime.now())
            	.setRole(role)
            	.setMessage(content);
            if (StrUtil.isBlank(message.getIp())) {
            	message.setIp(ServletUtil.getIpAddr());
            }            
            messageMapper.insert(message);
        }
    }
    
    /**
     * 向量相似度搜索
     */
    @Override
    public List<Map<String, Object>> search(ChatReq data) {
        if (data.getKnowledgeId() <= 0 || StrUtil.isBlank(data.getMessage())) {
            return Collections.emptyList();
        }
        
        // 设置代理（如果启用）
        if (proxyProperties.isEnabled()) {
            System.setProperty("http.proxyHost", proxyProperties.getHttp().getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxyProperties.getHttp().getPort()));
            System.setProperty("https.proxyHost", proxyProperties.getHttps().getHost());
            System.setProperty("https.proxyPort", String.valueOf(proxyProperties.getHttps().getPort()));
            System.setProperty("https.protocols", "TLSv1.2");
        }
        
        try {
        	// 保存用户消息
        	saveMessage(data.getMessage(), data, 0, 0, BuiltInRoleEnum.USER.getCode());
            
        	// 构造查询条件
	        EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(data.getKnowledgeId());
	        EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(data.getKnowledgeId());
	        Embedding queryEmbedding = embeddingModel.embed(data.getMessage()).content();
	        
	        // 发送查询请求
	        Filter filter = metadataKey(KNOWLEDGE).isEqualTo(data.getKnowledgeId());
	        //filter.and(metadataKey(User).isEqualTo(SecurityUtils.getUser().getId()));
	        EmbeddingSearchResult<TextSegment> list = embeddingStore.search(EmbeddingSearchRequest
	                .builder()
	                .queryEmbedding(queryEmbedding)
	                .filter(filter)
	                .build());
	        
	        // 处理查询结果
	        List<Map<String, Object>> result = new ArrayList<>();
	        list.matches().forEach(i -> {
	            TextSegment embedded = i.embedded();
	            Map<String, Object> map = embedded.metadata().toMap();
	            map.put("text", "- " + embedded.text());
	            result.add(map);
	        });
	        
	        // 保存助手消息
	        String strResult = result.stream()
	        	    // 从 Map 中提取 "text" 字段的值（假设值是 String 类型）
	        	    .map(map -> (String) map.get("text")) 
	        	    // 过滤掉 null 和空白字符串
	        	    .filter(text -> StrUtil.isNotBlank(text)) 
	        	    // 用换行符拼接
	        	    .collect(Collectors.joining("\n")); 
	        saveMessage(strResult, data, 0, 0, BuiltInRoleEnum.ASSISTANT.getCode());
	        
	        return result;
        } catch (Exception e) {
	        log.error("无法连接向量模型服务，请检查网络或配置", e);
	        throw new RuntimeException("向量模型服务暂时不可用，请稍后重试");
	    } finally {
            // 清理代理设置
            if (proxyProperties.isEnabled()) {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
                System.clearProperty("https.proxyHost");
                System.clearProperty("https.proxyPort");
            }
	    }
    }
    /**
     * 全文搜索
     */
    @Override
    public List<Map<String, Object>> fullTextSearch(ChatReq data) {
        if (data.getKnowledgeId() <= 0 || StrUtil.isBlank(data.getMessage())) {
            return Collections.emptyList();
        }
        
        // 设置代理（如果启用）
        if (proxyProperties.isEnabled()) {
            System.setProperty("http.proxyHost", proxyProperties.getHttp().getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxyProperties.getHttp().getPort()));
            System.setProperty("https.proxyHost", proxyProperties.getHttps().getHost());
            System.setProperty("https.proxyPort", String.valueOf(proxyProperties.getHttps().getPort()));
            System.setProperty("https.protocols", "TLSv1.2");
        }
        
        try {
            // 保存用户消息
            saveMessage(data.getMessage(), data, 0, 0, BuiltInRoleEnum.USER.getCode());
            
            // 获取全文搜索存储
            PgVectorFullTextSearchStore searchStore = embeddingProvider.getFullTextSearchStore(data.getKnowledgeId());
            
            // 执行全文搜索（设置最小分数0.05，最多返回10条结果）
            Double minScore = FULL_TEXT_MIN_SCORE;
            Integer limit = FULL_TEXT_LIMIT;
            // 中文查询使用更低阈值
            String query = data.getMessage();
            if (isChinese(query)) {
                minScore = CHINESE_FULL_TEXT_MIN_SCORE;
            }
            List<TextSegment> searchResults = searchStore.fullTextSearch(
            	query, 
                limit, 
                minScore
            );
            
            // 处理查询结果
            List<Map<String, Object>> result = new ArrayList<>();
            searchResults.forEach(segment -> {
                Map<String, Object> map = segment.metadata().toMap();
                map.put("text", "- " + segment.text());
                // 添加搜索分数（如果存在）
                if (segment.metadata().containsKey("search_score")) {
                    map.put("score", segment.metadata().get("search_score"));
                }
                result.add(map);
            });
            
            // 保存助手消息
            String strResult = result.stream()
                .map(map -> (String) map.get("text")) 
                .filter(text -> StrUtil.isNotBlank(text)) 
                .collect(Collectors.joining("\n")); 
            saveMessage(strResult, data, 0, 0, BuiltInRoleEnum.ASSISTANT.getCode());
            
            return result;
        } catch (Exception e) {
            log.error("全文搜索失败，请检查服务配置", e);
            throw new RuntimeException("搜索服务暂时不可用，请稍后重试");
        } finally {
            // 清理代理设置
            if (proxyProperties.isEnabled()) {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
                System.clearProperty("https.proxyHost");
                System.clearProperty("https.proxyPort");
            }
        }
    }
    
    /**
     * 判断是否为中文
     * @param text
     * @return
     */
    private boolean isChinese(String text) {
        return text.matches(".*[\u4e00-\u9fa5]+.*");
    }
    
    @Deprecated
    private List<AigcDocsSlice> sliceTextContent(AigcDocs doc) {
        // LangChain4j 文本切片
        List<TextSegment> segments = DocumentSplitters.recursive(
            500,  // 每段最大长度
            50    // 重叠长度
        ).split(Document.from(doc.getContent()));
        
        return IntStream.range(0, segments.size())
                .mapToObj(index -> {
                    TextSegment segment = segments.get(index);
                    AigcDocsSlice aigcDocsSlice = new AigcDocsSlice();
                    AigcKnowledge knowledge = knowledgeService.getById(doc.getKnowledgeId());
                    aigcDocsSlice.setEmbedStoreId(knowledge.getEmbedStoreId())
                        .setDocsId(doc.getId())
                        .setKnowledgeId(doc.getKnowledgeId())
                        .setName(doc.getName() + "-切片" + (index + 1))
                        .setSliceIndex(index + 1)
                        .setContentHash(DigestUtils.md5Hex(segment.text()))
                        .setContent(segment.text())
                        .setWordNum(segment.text().length())
                        .setIsEmbedding(false)
                        .setCreateBy(doc.getCreateBy())
                        .setCreateTime(LocalDateTime.now())
                        .setDelFlag("0");
                    
                    return aigcDocsSlice;
                })
                .collect(Collectors.toList());
    }

    @Deprecated
    private List<AigcDocsSlice> sliceFileContent(AigcDocs doc) {
        AigcOss req = new AigcOss().setDocId(doc.getId());
        LambdaQueryWrapper<AigcOss> queryWrapper = Wrappers.<AigcOss>lambdaQuery()
                .eq(req.getDocId() != null && req.getDocId() > 0, AigcOss::getDocId, req.getDocId());
        AigcOss ret = ossMapper.selectById(queryWrapper);
        if (StrUtil.isBlank(doc.getContent())) {
            doc.setContent(ossService.extractFileContent(ret.getId())); // 提取文件内容
        } else {
            doc.setContent(StrUtil.concat(true, doc.getContent(), " \n\n ", ossService.extractFileContent(ret.getId())));
        }
        return sliceTextContent(doc);
    }

    @Deprecated
    private void embedAndSaveSlices(AigcDocs doc, List<AigcDocsSlice> slices) {
        // 批量向量化处理
        List<TextSegment> segments = slices.stream()
                .map(slice ->                 	
                	TextSegment.from(slice.getContent(), 
                			Metadata.metadata(KNOWLEDGE, doc.getKnowledgeId())
                            .put("docId", doc.getId())
                            .put("sliceId", slice.getId()))
                )
                .collect(Collectors.toList());

        EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(doc.getKnowledgeId());
        EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(doc.getKnowledgeId());
        
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        List<String> vectorIds = embeddingStore.addAll(embeddings, segments);

        // 更新切片向量ID并保存
        for (int i = 0; i < slices.size(); i++) {
            AigcDocsSlice slice = slices.get(i);
            slice.setEmbedStoreId(Integer.parseInt(vectorIds.get(i)))
                 .setIsEmbedding(true);
            AigcDocsSlice ret = docsSliceMapper.selectById(slice.getId());
            if (ret.getId() > 0) {
            	docsSliceMapper.updateById(slice);
            }
            else {
            	docsSliceMapper.insert(slice);
            }
        }
    }
}