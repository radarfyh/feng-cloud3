package work.metanet.feng.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import dev.langchain4j.data.segment.TextSegment;
import work.metanet.feng.ai.api.dto.EmbeddingR;
import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcKnowledge.SliceConfig;

import java.util.List;

/**
 * AI知识库服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcKnowledgeService extends IService<AigcKnowledge> {

    List<AigcDocs> getDocsByKb(Integer knowledgeId);

    void removeKnowledge(Integer knowledgeId);

    List<EmbeddingR> embeddingText(Integer knowledgeId, Integer docId, Integer userId, String content, SliceConfig sliceConfig);

	List<EmbeddingR> embeddingDocs(Integer knowledgeId, Integer docId, Integer userId, Integer ossId, SliceConfig sliceConfig);

	void embedAndSaveSlices(AigcDocs doc, List<TextSegment> segments);
}

