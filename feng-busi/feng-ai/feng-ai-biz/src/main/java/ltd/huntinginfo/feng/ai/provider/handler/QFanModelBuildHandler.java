package ltd.huntinginfo.feng.ai.provider.handler;

import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.config.FengQianfanEmbeddingModel;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.ChatErrorEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.qianfan.QianfanStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

/**
 * 千帆模型处理器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
public class QFanModelBuildHandler implements ModelBuildHandler {

    @Override
    public boolean whetherCurrentModel(AigcModel model) {
        return StrUtil.isNotBlank(model.getProvider()) && StrUtil.equals(model.getProvider(), ProviderEnum.BAIDU.getCode());
    }

    @Override
    public boolean basicCheck(AigcModel model) {
        if (StringUtils.isBlank(model.getApiKey())) {
            throw new BusinessException(CommonConstants.FAIL, 
                    ChatErrorEnum.API_KEY_IS_NULL.getErrorDesc(model.getProvider(), model.getType()));
        }
        if (StringUtils.isBlank(model.getSecretKey())) {
            throw new BusinessException(CommonConstants.FAIL, 
                    ChatErrorEnum.SECRET_KEY_IS_NULL.getErrorDesc(model.getProvider(), model.getType()));
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
            return QianfanStreamingChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .secretKey(model.getSecretKey())
                    .modelName(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Qianfan  streaming chat 配置报错", e);
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
            return QianfanChatModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .secretKey(model.getSecretKey())
                    .modelName(model.getModel())
                    .baseUrl(model.getBaseUrl())
                    .temperature(model.getTemperature())
                    .topP(model.getTopP())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Qianfan chat 配置报错", e);
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
            return FengQianfanEmbeddingModel
                    .builder()
                    .apiKey(model.getApiKey())
                    .modelName(model.getModel())
                    .secretKey(model.getSecretKey())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Qianfan embedding 配置报错", e);
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
            log.error("Qianfan image 配置报错", e);
            return null;
        }

    }
}
