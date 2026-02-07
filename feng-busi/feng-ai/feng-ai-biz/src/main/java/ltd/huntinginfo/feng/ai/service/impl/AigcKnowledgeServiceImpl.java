package ltd.huntinginfo.feng.ai.service.impl;

import ltd.huntinginfo.feng.ai.mapper.AigcDocsMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcDocsSliceMapper;
import ltd.huntinginfo.feng.ai.api.dto.EmbeddingR;
import ltd.huntinginfo.feng.ai.api.entity.AigcDocs;
import ltd.huntinginfo.feng.ai.api.entity.AigcDocsSlice;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge.SliceConfig;
import ltd.huntinginfo.feng.ai.api.entity.AigcOss;
import ltd.huntinginfo.feng.ai.config.ProxyProperties;
import ltd.huntinginfo.feng.ai.mapper.AigcKnowledgeMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcOssMapper;
import ltd.huntinginfo.feng.ai.provider.EmbeddingProvider;
import ltd.huntinginfo.feng.ai.service.AigcKnowledgeService;
import ltd.huntinginfo.feng.ai.service.AigcOssService;
import ltd.huntinginfo.feng.common.core.constant.EmbedConst;
import ltd.huntinginfo.feng.common.core.constant.enums.SliceMode;
import ltd.huntinginfo.feng.common.core.constant.enums.SliceStatus;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;

import cn.hutool.core.util.ObjUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aigc知识管理服务实现类
 * <p>
 * 该类处理 AIGC 知识的增删改查操作，并提供对文档和文档切片的管理功能。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AigcKnowledgeServiceImpl extends ServiceImpl<AigcKnowledgeMapper, AigcKnowledge> implements AigcKnowledgeService {

    private final AigcDocsMapper aigcDocsMapper;
    private final AigcDocsSliceMapper aigcDocsSliceMapper;
    private final EmbeddingProvider embeddingProvider;
    private final AigcOssService aigcOssService;
    private final AigcOssMapper aigcOssMapper;
    private final ProxyProperties proxyProperties;

    /**
     * 根据知识ID获取文档列表
     * <p>
     * 该方法返回与指定知识 ID 相关联的所有文档。
     * </p>
     * 
     * @param knowledgeId 知识ID
     * @return 文档列表
     */
    @Override
    public List<AigcDocs> getDocsByKb(Integer knowledgeId) {
        return aigcDocsMapper.selectList(Wrappers.<AigcDocs>lambdaQuery()
                .eq(AigcDocs::getKnowledgeId, knowledgeId));
    }

    /**
     * 删除知识及其相关文档和切片，还有文件
     * <p>
     * 该方法根据知识 ID 删除知识数据，同时删除与该知识相关联的所有文档及切片，文件。
     * </p>
     * 
     * @param knowledgeId 知识ID
     */
    @Override
    @Transactional
    public void removeKnowledge(Integer knowledgeId) {
        try {
            // 删除知识
            baseMapper.deleteById(knowledgeId);
            log.info("Knowledge deleted with ID: {}", knowledgeId);

            // 删除相关文档及文档切片
            List<Integer> docsIds = getDocsByKb(knowledgeId).stream().map(AigcDocs::getId).collect(Collectors.toList());
            docsIds.forEach(this::removeSlicesOfDoc);
            // 删除文件            
            aigcOssMapper.delete(Wrappers.<AigcOss>lambdaQuery()
                    .eq(AigcOss::getKnowledgeId, knowledgeId));
        } catch (Exception e) {
            log.error("Error while removing knowledge with ID: {}", knowledgeId, e);
            throw new BusinessException(e);
        }
    }

    public void removeSlicesOfDoc(Integer docsId) {
    	aigcDocsMapper.deleteById(docsId);
    }
    
    /**
     * 对文本进行切片 
     * @param req：存放文本、知识ID、文档ID、用户ID、用户请求消息
     * @param sliceConfig：切片参数配置
     */
    @Override
    public List<EmbeddingR> embeddingText(Integer knowledgeId, Integer docId, Integer userId, String content, SliceConfig sliceConfig) {
        try {
	    	List<EmbeddingR> list = embeddingContent(knowledgeId, docId, userId, null, content, sliceConfig);
	        log.info("Text文本向量解析成功：KnowledgeId={}, DocId={}, 切片数量={}", knowledgeId, docId, list.size());
	        return list;
        } catch(Exception e) {
        	log.error("Text文本向量解析失败", e.getMessage());
        	throw new BusinessException(e);
        }
    }
    
    private List<EmbeddingR> embeddingContent(Integer knowledgeId, Integer docId, Integer userId, Integer ossId, String content, SliceConfig sliceConfig) {
        // 设置代理（如果启用）
        if (proxyProperties.isEnabled()) {
            System.setProperty("http.proxyHost", proxyProperties.getHttp().getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxyProperties.getHttp().getPort()));
            System.setProperty("https.proxyHost", proxyProperties.getHttps().getHost());
            System.setProperty("https.proxyPort", String.valueOf(proxyProperties.getHttps().getPort()));
            System.setProperty("https.protocols", "TLSv1.2");
        }
        
        List<EmbeddingR> list = new ArrayList<>();
        try {

            // 指定元数据为知识ID，文档ID和用户ID （实际上，文档ID和用户ID均可以从知识ID获取，所有有效字段为知识ID）
            Map<String, String> metadata = Map.of(EmbedConst.KNOWLEDGE, knowledgeId.toString(), 
            		EmbedConst.Document, docId.toString(),
            		EmbedConst.User, userId.toString());
            // 根据请求文本和元数据，创建新文档对象
            Document document = new Document(content, Metadata.from(metadata));
            
        	// 判断切片模式是否有效，无效则重置为默认值（按句子切割）
        	if (ObjUtil.isEmpty(sliceConfig) || sliceConfig.getSliceMode() == null) {
        		sliceConfig = new SliceConfig(SliceConfig.MAX_SLICE_SIZE, SliceConfig.OVERLAP_SIZE, SliceMode.SENTENCE.getCode());
        	}
            DocumentSplitter splitter = EmbeddingProvider.splitter(sliceConfig);
            // 对新文档对象切分
            List<TextSegment> segments = splitter.split(document);
            
            // 特别注意： 百度bge-large-zh的最大文本数量为16个文本，每个文本最大长度为2000，最大令牌数量为512（每个令牌可能对应1到5个字符）
            if (segments.size() > 15 || 
            		(sliceConfig.getSliceMode().equals(SliceMode.FIXED.getCode()) && sliceConfig.getMaxSliceSize() > 2000)) {
            	// 大于16段，则强制改为固定切分
            	
            	// 强制最大切片尺寸为1024（假设每个令牌平均对应字符数量为2，如果超过令牌数量，百度会返回错误）
            	Integer maxSliceSize = document.text().length() / 15 < 1024 ? document.text().length() / 15 : 1024;
            	Integer overlapSize = maxSliceSize / 10;
            	sliceConfig.setMaxSliceSize(maxSliceSize);
            	sliceConfig.setOverlapSize(overlapSize);
            	sliceConfig.setSliceMode(SliceMode.FIXED.getCode());
            	splitter = EmbeddingProvider.splitter(sliceConfig);
            	segments = splitter.split(document);
            }
        	if (segments.size() > 15) {
        		// 强制移除多余15段的内容，即抛弃多余的内容
        		while (segments.size() > 15) {
        			segments.remove(segments.size() - 1);
        		}
        	}            
            // 获得模型
            EmbeddingModel embeddingModel = null;
            try {
            	embeddingModel = embeddingProvider.getEmbeddingModel(knowledgeId);
            }catch (Exception e) {
            	log.error("embeddingContent-->getEmbeddingModel获取嵌入式模型异常：{}", e.getMessage());
            	throw new BusinessException(e);
            }
            // 获得向量库（PGVector）配置
            EmbeddingStore<TextSegment> embeddingStore = null;
            try {
            	if (embeddingModel != null) {
            		embeddingStore = embeddingProvider.getEmbeddingStore(knowledgeId);
            	}
            }catch (Exception e) {
            	log.error("embeddingContent-->getEmbeddingStore获取嵌入式数据库异常：{}", e.getMessage());
            	throw new BusinessException(e);
            }
            
            List<Embedding> embeddings = new ArrayList<>();
            try {
	            // 加切片到向量库
            	if (embeddingStore != null) {
            		embeddings = embeddingModel.embedAll(segments).content();
            	}
            } catch (Exception e) {
            	log.error("embeddingContent-->embedAll模型嵌入文本异常：{}", e.getMessage());
            	embeddings.clear();
            	segments.clear();
            	document = null;
            	throw new BusinessException(e);
            }
            List<String> vectorIds = new ArrayList<>();
            try {
	            // ** 重要 ** 保存到向量库，返回向量ID  （通用数据库采用mysql或者PGVector，向量库采用PGVector，通用数据库表名aigc_docs_slice,
	            // PGVector表名可指定，目前为aigc_embed_store_vector, 由langchain4j操作aigc_embed_store_vector ）
	            if (!embeddings.isEmpty()) {
	            	vectorIds = embeddingStore.addAll(embeddings, segments);
	            }
            } catch (Exception e) {
            	log.error("embeddingContent-->addAll向量库嵌入文本异常：{}", e.getMessage());
            	vectorIds.clear();
            	embeddings.clear();
            	segments.clear();
            	document = null;
            	throw new BusinessException(e);
            }
            
            if (!vectorIds.isEmpty()) {
	            // 返回向量库的向量ID和切分后的文本
	            for (int i = 0; i < vectorIds.size(); i++) {
	                list.add(new EmbeddingR()
	                		.setVectorId(vectorIds.get(i))
	                		.setText(segments.get(i).text()));
	            	// 获取后插入到知识库的切片表
	                aigcDocsSliceMapper.insert(new AigcDocsSlice()
		    			.setSliceIndex(i)
		                .setKnowledgeId(knowledgeId)
		                .setDocsId(docId)
		                .setOssId(ossId)
		                .setVectorId(vectorIds.get(i))
		                .setName("embeddingContent")
		                .setWordNum(segments.get(i).text().length())
		                .setCreateBy(userId.toString())
		                .setCreateTime(LocalDateTime.now())
		                .setContent(segments.get(i).text())
	                );
	            }
            }
        	vectorIds.clear();
        	embeddings.clear();
        	segments.clear();
        	document = null;
        } catch (Exception e) {
            log.error("文档向量化失败:{}", e);
            throw new BusinessException(e);
        } finally {
        	
            // 清理代理设置
            if (proxyProperties.isEnabled()) {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
                System.clearProperty("https.proxyHost");
                System.clearProperty("https.proxyPort");
            }
        }

        return list;
    }

    /**
     * 对OSS文件进行切片
     * @param ossId：文件ID
     * @param req：存放知识ID、文档ID、用户ID、文本内容
     * @param sliceConfig：切片参数配置
     */
    @Override
    public List<EmbeddingR> embeddingDocs(Integer knowledgeId, Integer docId, Integer userId, Integer ossId, SliceConfig sliceConfig) {
    	Document document = null;
    	try {
//    		document = UrlDocumentLoader.load(req.getUrl(), new ApacheTikaDocumentParser());
    		String content = "";
    		if (ossId != null && ossId > 0) {
    			content = aigcOssService.extractFileContent(ossId);
    		} else {
        		log.error("OSS ID 无效");
        		return null;
    		}
    		if (StrUtil.isBlank(content)) {
        		log.error("OSS文件读取后的内容为空");
        		return null;
    		}
    		document = Document.from(content);
    	} catch (Exception e) {
    		log.error("OSS文件读取异常：{}", e.getMessage());
    		return null;
    	}
    	try {
            List<EmbeddingR> list = embeddingContent(knowledgeId, docId, userId, ossId, document.text(), sliceConfig);
            
            // 更新文档中的切片状态和切片统计数据
            aigcDocsMapper.updateById(new AigcDocs()
            		.setId(docId)
            		.setSliceStatus(SliceStatus.COMPLETED.getCode())
            		.setSliceNum(list.size())
            		.setSize(new Long(document.text().length()))
            		.setUpdateBy(userId.toString()));
            
            log.info("OSS文件向量解析成功：KnowledgeId={}, DocsID={}, 切片数量={}, 文本长度={}", knowledgeId, docId, list.size(), document.text().length());
            return list;
    	} catch (Exception e) {
    		log.error("OSS文件向量解析异常：{}", e.getMessage());
    		throw new BusinessException(e);
    	}

    }

    @Override
    @Transactional
    @Deprecated
    public void embedAndSaveSlices(AigcDocs doc, List<TextSegment> segments) {
        // 设置代理（如果启用）
        if (proxyProperties.isEnabled()) {
            System.setProperty("http.proxyHost", proxyProperties.getHttp().getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxyProperties.getHttp().getPort()));
            System.setProperty("https.proxyHost", proxyProperties.getHttps().getHost());
            System.setProperty("https.proxyPort", String.valueOf(proxyProperties.getHttps().getPort()));
        }
        
        try {
            EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(doc.getKnowledgeId());
            EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(doc.getKnowledgeId());
            
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            List<String> vectorIds = embeddingStore.addAll(embeddings, segments);

            // 保存切片信息
            for (int i = 0; i < segments.size(); i++) {
                TextSegment segment = segments.get(i);
                AigcDocsSlice slice = new AigcDocsSlice()
                        .setKnowledgeId(doc.getKnowledgeId())
                        .setDocsId(doc.getId())
                        .setEmbedStoreId(Integer.parseInt(vectorIds.get(i)))
                        .setName(doc.getName() + "-切片" + (i + 1))
                        .setContent(segment.text())
                        .setWordNum(segment.text().length())
                        .setIsEmbedding(true);
                
                aigcDocsSliceMapper.insert(slice);
            }
        } catch (Exception e) {
            log.error("文档切片向量化失败", e);
            throw new BusinessException(e);
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
}
