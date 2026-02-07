package ltd.huntinginfo.feng.ai.provider.handler;

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
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.model.zhipu.ZhipuAiImageModel;
import dev.langchain4j.model.zhipu.ZhipuAiStreamingChatModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import java.time.Duration;

/**
 * 智谱模型处理器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class ZhipuModelBuildHandler implements ModelBuildHandler {

    private final LangChatProps props;

    @Override
    public boolean whetherCurrentModel(AigcModel model) {
        return StrUtil.isNotBlank(model.getProvider()) && StrUtil.equals(model.getProvider(), ProviderEnum.ZHIPU.getCode());
    }

    @Override
    public boolean basicCheck(AigcModel model) {
        if (StringUtils.isBlank(model.getApiKey())) {
            throw new BusinessException(CommonConstants.FAIL, 
                    ChatErrorEnum.API_KEY_IS_NULL.getErrorDesc(model.getProvider(), model.getType()));
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
            return ZhipuAiStreamingChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .model(model.getModel())
                    .maxToken(model.getResponseLimit())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .callTimeout(Duration.ofMinutes(10))
                    .connectTimeout(Duration.ofMinutes(10))
                    .writeTimeout(Duration.ofMinutes(10))
                    .readTimeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("zhipu streaming chat 配置报错", e);
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
            return ZhipuAiChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .baseUrl(model.getBaseUrl())
                    .model(model.getModel())
                    .maxToken(model.getResponseLimit())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .callTimeout(Duration.ofMinutes(10))
                    .connectTimeout(Duration.ofMinutes(10))
                    .writeTimeout(Duration.ofMinutes(10))
                    .readTimeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("zhipu chat 配置报错", e);
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
            return ZhipuAiEmbeddingModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .model(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .logRequests(true)
                    .logResponses(true)
                    .callTimeout(Duration.ofMinutes(10))
                    .connectTimeout(Duration.ofMinutes(10))
                    .writeTimeout(Duration.ofMinutes(10))
                    .readTimeout(Duration.ofMinutes(10))
                    .dimensions(1024)
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("zhipu embedding 配置报错", e);
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
            return ZhipuAiImageModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .model(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .logRequests(true)
                    .logResponses(true)
                    .callTimeout(Duration.ofMinutes(10))
                    .connectTimeout(Duration.ofMinutes(10))
                    .writeTimeout(Duration.ofMinutes(10))
                    .readTimeout(Duration.ofMinutes(10))
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("zhipu image 配置报错", e);
            return null;
        }
    }
}
