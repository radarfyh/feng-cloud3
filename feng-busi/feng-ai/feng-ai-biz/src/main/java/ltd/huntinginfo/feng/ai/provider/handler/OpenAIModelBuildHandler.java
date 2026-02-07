package ltd.huntinginfo.feng.ai.provider.handler;

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.ChatErrorEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.properties.LangChatProps;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * OpenAI模型处理器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class OpenAIModelBuildHandler implements ModelBuildHandler {

    private final LangChatProps props;

    /**
     * 合并处理支持OpenAI接口的模型
     */
    @Override
    public boolean whetherCurrentModel(AigcModel model) {
    	String provider = model.getProvider();
        return StrUtil.equals(provider, ProviderEnum.OPENAI.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.DEEPSEEK.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.GOOGLE.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.CLAUDE.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.DOUBAO.getCode()) || 
        		StrUtil.equals(provider, ProviderEnum.OTHER.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.MOKAAI.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.JURASSIC.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.META.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.NETEASE.getCode()) ||
        		StrUtil.equals(provider, ProviderEnum.XUNFEI.getCode());
    }
    
    @Override
    public boolean basicCheck(AigcModel model) {
        String apiKey = model.getApiKey();
        if (StrUtil.isBlank(apiKey)) {
            throw new BusinessException(CommonConstants.FAIL, ChatErrorEnum.API_KEY_IS_NULL.getErrorDesc(model.getProvider(), model.getType()));
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
            return OpenAiStreamingChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .maxTokens(model.getResponseLimit())
                    .temperature(model.getTemperature())
                    .logRequests(true)
                    .logResponses(true)
                    .topP(model.getTopP())
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(model.getProvider() + " Streaming Chat 模型配置报错", e);
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
            return OpenAiChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .maxTokens(model.getResponseLimit())
                    .temperature(model.getTemperature())
                    .logRequests(true)
                    .logResponses(true)
                    .topP(model.getTopP())
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(model.getProvider() + " Chat 模型配置报错", e);
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
            OpenAiEmbeddingModel openAiEmbeddingModel = OpenAiEmbeddingModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .dimensions(model.getDimension())
                    .logRequests(true)
                    .logResponses(true)
                    .dimensions(1024)
                    .timeout(Duration.ofMinutes(10))
                    .build();
            return openAiEmbeddingModel;
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(model.getProvider() + " Embedding 模型配置报错", e);
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
            return OpenAiImageModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .modelName(model.getModel())
                    .size(model.getImageSize())
                    .quality(model.getImageQuality())
                    .style(model.getImageStyle())
                    .logRequests(true)
                    .logResponses(true)
                    .timeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(model.getProvider() + " Image 模型配置报错", e);
            return null;
        }


    }
}
