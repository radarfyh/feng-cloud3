package ltd.huntinginfo.feng.ai.provider.handler;

import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.ChatErrorEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

/**
 * 千问模型处理器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
public class QWenModelBuildHandler implements ModelBuildHandler {

    @Override
    public boolean whetherCurrentModel(AigcModel model) {
        return StrUtil.isNotBlank(model.getProvider()) && StrUtil.equals(model.getProvider(), ProviderEnum.ALICLOUD.getCode());
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
        if (!whetherCurrentModel(model)) {
            return null;
        }
        if (!basicCheck(model)) {
            return null;
        }
        try {
            return QwenStreamingChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .modelName(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .maxTokens(model.getResponseLimit())
                    .temperature(Float.parseFloat(model.getTemperature().toString()))
                    .topP(model.getTopP())
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("qian wen streaming chat 配置报错", e);
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
            return QwenChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .modelName(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .enableSearch(true)
                    .maxTokens(model.getResponseLimit())
                    .temperature(Float.parseFloat(model.getTemperature().toString()))
                    .topP(model.getTopP())
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("qian wen chat 配置报错", e);
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
            return QwenEmbeddingModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .modelName(model.getModel())
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("qian wen embedding 配置报错", e);
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
            log.error("qian wen image 配置报错", e);
            return null;
        }

    }
}
