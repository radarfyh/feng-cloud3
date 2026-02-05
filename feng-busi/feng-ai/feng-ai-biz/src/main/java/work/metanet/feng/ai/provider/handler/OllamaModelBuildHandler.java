package work.metanet.feng.ai.provider.handler;

import work.metanet.feng.ai.api.entity.AigcModel;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.ChatErrorEnum;
import work.metanet.feng.common.core.constant.enums.ProviderEnum;
import work.metanet.feng.common.core.exception.BusinessException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import java.time.Duration;

/**
 * Ollama模型处理器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
public class OllamaModelBuildHandler implements ModelBuildHandler {

    @Override
    public boolean whetherCurrentModel(AigcModel model) {
        return StrUtil.isNotBlank(model.getProvider()) && StrUtil.equals(model.getProvider(), ProviderEnum.BAAI.getCode());
    }

    @Override
    public boolean basicCheck(AigcModel model) {
        if (StringUtils.isBlank(model.getBaseUrl())) {
            throw new BusinessException(CommonConstants.FAIL, 
            		ChatErrorEnum.BASE_URL_IS_NULL.getErrorDesc(model.getProvider(), model.getType()));
        }
        return true;
    }

    @Override
    public StreamingChatLanguageModel buildStreamingChat(AigcModel model) {
        try {
            if (!whetherCurrentModel(model)) {
                return null;
            }
            if (!basicCheck(model)) {
                return null;
            }
            return OllamaStreamingChatModel
                    .builder()
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ollama streaming chat 配置报错", e);
            return null;
        }
    }

    @Override
    public ChatLanguageModel buildChatLanguageModel(AigcModel model) {
        try {
            if (!whetherCurrentModel(model)) {
                return null;
            }
            if (!basicCheck(model)) {
                return null;
            }
            return OllamaChatModel
                    .builder()
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ollama chat 配置报错", e);
            return null;
        }
    }

    @Override
    public EmbeddingModel buildEmbedding(AigcModel model) {
        try {
            if (!whetherCurrentModel(model)) {
                return null;
            }
            if (!basicCheck(model)) {
                return null;
            }
            return OllamaEmbeddingModel
                    .builder()
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .logRequests(true)
                    .logResponses(true)
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ollama embedding 配置报错", e);
            return null;
        }
    }

    @Override
    public ImageModel buildImage(AigcModel model) {
        try {
            if (!whetherCurrentModel(model)) {
                return null;
            }
            if (!basicCheck(model)) {
                return null;
            }
            return null;
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ollama image 配置报错", e);
            return null;
        }
    }
}
