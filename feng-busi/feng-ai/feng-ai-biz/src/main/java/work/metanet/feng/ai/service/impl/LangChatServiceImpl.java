package work.metanet.feng.ai.service.impl;

import cn.hutool.core.util.StrUtil;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.ai.api.dto.ChatReq;
import work.metanet.feng.ai.api.dto.ImageR;
import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.ai.provider.EmbeddingProvider;
import work.metanet.feng.ai.provider.ModelProvider;
import work.metanet.feng.ai.service.Agent;
import work.metanet.feng.ai.service.LangChatService;
import work.metanet.feng.ai.utils.AiEmitter;
import work.metanet.feng.ai.utils.PropertyEscapeUtils;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.exception.BusinessException;
import work.metanet.feng.common.core.properties.ChatProps;
import work.metanet.feng.common.core.util.PromptUtil;

import org.springframework.stereotype.Service;

import java.util.function.Function;

import static work.metanet.feng.common.core.constant.EmbedConst.KNOWLEDGE;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * 长对话服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Service
@AllArgsConstructor
public class LangChatServiceImpl implements LangChatService {

    private final ModelProvider provider;
    private final EmbeddingProvider embeddingProvider;
    private final ChatProps chatProps;

    private AiServices<Agent> build(StreamingChatLanguageModel streamModel, ChatLanguageModel model, ChatReq req) {
        AiServices<Agent> aiServices = AiServices.builder(Agent.class)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(req.getConversationId())
                        .chatMemoryStore(new PersistentChatMemoryStore())
                        .maxMessages(chatProps.getMemoryMaxMessage())
                        .build());

        if (req.getPrompts() != null && !req.getPrompts().isEmpty()) {
            // 过滤出内容不为空的提示词
            String systemPrompt = req.getPrompts().stream()
                    .map(AigcPrompt::getContent)
                    .filter(StrUtil::isNotBlank)
                    .reduce((a, b) -> a + "\n\n" + b)
                    .orElse(null);

            if (StrUtil.isNotBlank(systemPrompt)) {
                aiServices.systemMessageProvider(memoryId -> systemPrompt);
            }
        }

        if (streamModel != null) {
            aiServices.streamingChatLanguageModel(streamModel);
        }
        if (model != null) {
            aiServices.chatLanguageModel(model);
        }
        return aiServices;
    }


    @Override
    public TokenStream chat(ChatReq req, AiEmitter emit) {
        StreamingChatLanguageModel model = provider.stream(req.getModelId());
        if (req.getConversationId() <= 0) {
            req.setConversationId(null); // 自增长字段，可以指定NULL
        }

        AiServices<Agent> aiServices = build(model, null, req);

        if (req.getKnowledgeId() != null && req.getKnowledgeId() > 0) {
            req.getKnowledgeIds().add(req.getKnowledgeId());
        }

        if (req.getKnowledgeIds() != null && !req.getKnowledgeIds().isEmpty()) {
            Function<Query, Filter> filter = (query) -> metadataKey(KNOWLEDGE).isIn(req.getKnowledgeIds());
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingProvider.getEmbeddingStore(req.getKnowledgeIds()))
                    .embeddingModel(embeddingProvider.getEmbeddingModel(req.getKnowledgeIds()))
                    .dynamicFilter(filter)
                    .build();
            aiServices.retrievalAugmentor(DefaultRetrievalAugmentor
                    .builder()
                    .contentRetriever(contentRetriever)
                    .build());
        }
        Agent agent = aiServices.build();
        
        // 避免报错：Error: Value for the variable ' $t('docPage.fileManagement') ' is missing
        req.setMessage(PropertyEscapeUtils.escapeSpringProperties(req.getMessage()));
        
        return agent.stream(req.getConversationId(), req.getMessage());
    }

    @Override
    public TokenStream singleChat(ChatReq req, AiEmitter emit) {
        StreamingChatLanguageModel model = provider.stream(req.getModelId());
        if (req.getConversationId() <= 0) {
            req.setConversationId(null);
        }

        AiServices<Agent> aiServices = build(model, null, req);

        // 处理 prompts
        if (req.getPrompts() != null && !req.getPrompts().isEmpty()) {
            String systemPrompt = req.getPrompts().stream()
                    .map(AigcPrompt::getContent)
                    .filter(StrUtil::isNotBlank)
                    .reduce((a, b) -> a + "\n\n" + b)
                    .orElse(null);

            if (StrUtil.isNotBlank(systemPrompt)) {
            	emit.setPrompt(PromptUtil.build(req.getMessage(), systemPrompt));
            } else {
            	emit.setPrompt(PromptUtil.build(req.getMessage(), null));
            }
        } else {
        	emit.setPrompt(PromptUtil.build(req.getMessage(), null));
        }

        Agent agent = aiServices.build();
        return agent.stream(req.getConversationId(), emit.getPrompt().text());
    }


    @Override
    public String text(ChatReq req, AiEmitter emit) {
        if (req.getConversationId() <= 0) {
            req.setConversationId(null);
        }

        try {
            ChatLanguageModel model = provider.text(req.getModelId());
            Agent agent = build(null, model, req).build();
            String text = agent.text(req.getConversationId(), req.getMessage());
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Response<Image> image(ImageR req, AiEmitter emit) {
        try {
            ImageModel model = provider.image(req.getModelId());
            return model.generate(req.getPrompt().text());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonConstants.FAIL, "图片生成失败");
        }
    }
}
