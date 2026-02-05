package work.metanet.feng.ai.provider;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcKnowledge.SliceConfig;
import work.metanet.feng.ai.config.EmbeddingStoreFactory;
import work.metanet.feng.ai.config.KnowledgeStoreFactory;
import work.metanet.feng.ai.config.ModelStoreFactory;
import work.metanet.feng.ai.config.PgVectorFullTextSearchStore;
import work.metanet.feng.ai.provider.splitter.ParagraphSplitter;
import work.metanet.feng.ai.provider.splitter.SentenceSplitter;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.SliceMode;
import work.metanet.feng.common.core.exception.BusinessException;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 向量提供者
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class EmbeddingProvider {

    private final EmbeddingStoreFactory embeddingStoreFactory;
    private final KnowledgeStoreFactory knowledgeStoreFactory;
    private final ModelStoreFactory modelStoreFactory;
    
    /**
     * 获取基于句子的分割器
     * - 分割符号：句号、分号、感叹号、回车符、换行符
     * - 最大长度50字符，超过则向前搜索逗号分割
     */
    public static DocumentSplitter sentenceSplitter(int maxLength, int overlapSize) {
        return new SentenceSplitter(maxLength, overlapSize);
    }

    /**
     * 获取基于段落的分割器
     * - 分割符号：\n, \r
     * - 最大长度300字符，超过则向前搜索句号、感叹号、分号分割
     */
    public static DocumentSplitter paragraphSplitter() {
        return new ParagraphSplitter();
    }

    /**
     * 获取递归分割器
     */
    public static DocumentSplitter recursiveSplitter(int maxSegmentSize, int maxOverlapSize) {
        return DocumentSplitters.recursive(maxSegmentSize, maxOverlapSize);
    }
    
    public static DocumentSplitter splitter(SliceConfig conf) {
    	if (StrUtil.equals(conf.getSliceMode(), SliceMode.SENTENCE.getCode())) {
	    	return sentenceSplitter(conf.getMaxSliceSize(), conf.getOverlapSize());
    	} else if (StrUtil.equals(conf.getSliceMode(), SliceMode.PARAGRAPH.getCode())) {
    		return paragraphSplitter();
    	} else {
    		return recursiveSplitter(conf.getMaxSliceSize(), conf.getOverlapSize());
    	}
    	
    }

    public EmbeddingModel getEmbeddingModel(List<Integer> knowledgeIds) {
        List<Integer> storeIds = new ArrayList<>();
        knowledgeIds.forEach(id -> {
            if (knowledgeStoreFactory.containsKnowledge(id)) {
                AigcKnowledge data = knowledgeStoreFactory.getKnowledge(id);
                if (data.getEmbedModelId() != null) {
                    storeIds.add(data.getEmbedModelId());
                }
            }
        });
        if (storeIds.isEmpty()) {
            throw new BusinessException(CommonConstants.FAIL, "知识库缺少Embedding Model配置，请先检查配置");
        }

        HashSet<Integer> filterIds = new HashSet<>(storeIds);
        if (filterIds.size() > 1) {
            throw new BusinessException(CommonConstants.FAIL, "存在多个不同Embedding Model的知识库，请先检查配置");
        }

        return modelStoreFactory.getEmbeddingModel(storeIds.get(0));
    }

    public EmbeddingModel getEmbeddingModel(Integer knowledgeId) {
        if (knowledgeStoreFactory.containsKnowledge(knowledgeId)) {
            AigcKnowledge data = knowledgeStoreFactory.getKnowledge(knowledgeId);
            if (modelStoreFactory.containsEmbeddingModel(data.getEmbedModelId())) {
                return modelStoreFactory.getEmbeddingModel(data.getEmbedModelId());
            }
        }
        throw new BusinessException(CommonConstants.FAIL, "没有找到匹配的向量模型");
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore(Integer knowledgeId) {
        if (knowledgeStoreFactory.containsKnowledge(knowledgeId)) {
            AigcKnowledge data = knowledgeStoreFactory.getKnowledge(knowledgeId);
            if (embeddingStoreFactory.containsEmbeddingStore(data.getEmbedStoreId())) {
                return embeddingStoreFactory.getEmbeddingStore(data.getEmbedStoreId());
            }
        }
        throw new BusinessException(CommonConstants.FAIL, "没有找到匹配的向量数据库");
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore(List<Integer> knowledgeIds) {
        List<Integer> storeIds = new ArrayList<>();
        knowledgeIds.forEach(id -> {
            if (knowledgeStoreFactory.containsKnowledge(id)) {
                AigcKnowledge data = knowledgeStoreFactory.getKnowledge(id);
                if (data.getEmbedStoreId() != null) {
                    storeIds.add(data.getEmbedStoreId());
                }
            }
        });
        if (storeIds.isEmpty()) {
            throw new BusinessException(CommonConstants.FAIL, "知识库缺少Embedding Store配置，请先检查配置");
        }

        HashSet<Integer> filterIds = new HashSet<>(storeIds);
        if (filterIds.size() > 1) {
            throw new BusinessException(CommonConstants.FAIL, "存在多个不同Embedding Store数据源的知识库，请先检查配置");
        }

        return embeddingStoreFactory.getEmbeddingStore(storeIds.get(0));
    }
    
    public PgVectorFullTextSearchStore getFullTextSearchStore(Integer knowledgeId) {
        if (knowledgeStoreFactory.containsKnowledge(knowledgeId)) {
            AigcKnowledge data = knowledgeStoreFactory.getKnowledge(knowledgeId);
            if (embeddingStoreFactory.containsEmbeddingStore(data.getEmbedStoreId())) {
                EmbeddingStore<TextSegment> store = embeddingStoreFactory.getEmbeddingStore(data.getEmbedStoreId());
                if (store instanceof PgVectorFullTextSearchStore) {
                    return (PgVectorFullTextSearchStore) store;
                }
            }
        }
        throw new BusinessException(CommonConstants.FAIL, "没有找到匹配的全文搜索存储");
    }

}
