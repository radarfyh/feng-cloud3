package ltd.huntinginfo.feng.ai.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.mapper.AigcModelMapper;
import ltd.huntinginfo.feng.ai.provider.handler.ModelBuildHandler;
import ltd.huntinginfo.feng.common.core.constant.ModelConst;
import ltd.huntinginfo.feng.common.core.constant.enums.ModelTypeEnum;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

/**
 * 模型工厂类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Configuration
@Slf4j
public class ModelStoreFactory {

    @Autowired
    private AigcModelMapper aigcModelMapper;
    @Autowired
    private List<ModelBuildHandler> modelBuildHandlers;

    private final List<AigcModel> modelStore = new ArrayList<>();
    private final Map<Integer, StreamingChatLanguageModel> streamingChatMap = new ConcurrentHashMap<>();
    private final Map<String, ChatLanguageModel> chatLanguageMap = new ConcurrentHashMap<>();
    private final Map<Integer, EmbeddingModel> embeddingModelMap = new ConcurrentHashMap<>();
    private final Map<Integer, ImageModel> imageModelMap = new ConcurrentHashMap<>();

    @Async
    @PostConstruct
    public void init() {
        modelStore.clear();
        streamingChatMap.clear();
        chatLanguageMap.clear();
        embeddingModelMap.clear();
        imageModelMap.clear();

        List<AigcModel> list = aigcModelMapper.selectList(Wrappers.<AigcModel>lambdaQuery());
        list.forEach(model -> {
            if (Objects.equals(model.getBaseUrl(), "")) {
                model.setBaseUrl(null);
            }

            chatHandler(model);
            embeddingHandler(model);
            imageHandler(model);
        });

        modelStore.forEach(i -> log.info("已成功注册模型：{} -- {}， 模型配置：{}", i.getProvider(), i.getType(), i));
    }

    private void chatHandler(AigcModel model) {
        try {
        	String type = model.getType();
            if (!StrUtil.equals(type, ModelTypeEnum.CHAT.getCode())) {
                return;
            }
            modelBuildHandlers.forEach(x -> {
                StreamingChatLanguageModel streamingChatLanguageModel = x.buildStreamingChat(model);
                if (ObjectUtil.isNotEmpty(streamingChatLanguageModel)) {
                    streamingChatMap.put(model.getId(), streamingChatLanguageModel);
                    modelStore.add(model);
                }

                ChatLanguageModel languageModel = x.buildChatLanguageModel(model);
                if (ObjectUtil.isNotEmpty(languageModel)) {
                    chatLanguageMap.put(model.getId() + ModelConst.TEXT_SUFFIX, languageModel);
                }
            });
        } catch (Exception e) {
            log.error("model 【 id: {} name: {}】streaming chat 配置报错", model.getId(), model.getName());
        }
    }

    private void embeddingHandler(AigcModel model) {
        try {
        	String type = model.getType();
            if (!StrUtil.equals(type, ModelTypeEnum.EMBEDDING.getCode())) {
                return;
            }
            modelBuildHandlers.forEach(x -> {
                EmbeddingModel embeddingModel = x.buildEmbedding(model);
                if (ObjectUtil.isNotEmpty(embeddingModel)) {
                    embeddingModelMap.put(model.getId(), embeddingModel);
                    modelStore.add(model);
                }
            });

        } catch (Exception e) {
            log.error("model 【id{} name{}】 embedding 配置报错", model.getId(), model.getName());
        }
    }

    private void imageHandler(AigcModel model) {
        try {
        	String type = model.getType();
            if (!StrUtil.equals(type, ModelTypeEnum.IMAGE.getCode())) {
                return;
            }
            modelBuildHandlers.forEach(x -> {
                ImageModel imageModel = x.buildImage(model);
                if (ObjectUtil.isNotEmpty(imageModel)) {
                    imageModelMap.put(model.getId(), imageModel);
                    modelStore.add(model);
                }
            });
        } catch (Exception e) {
            log.error("model 【id{} name{}】 image 配置报错", model.getId(), model.getName());
        }
    }

    public StreamingChatLanguageModel getStreamingChatModel(Integer modelId) {
        return streamingChatMap.get(modelId);
    }

    public boolean containsStreamingChatModel(Integer modelId) {
        return streamingChatMap.containsKey(modelId);
    }

    public ChatLanguageModel getChatLanguageModel(Integer modelId) {
        return chatLanguageMap.get(modelId + ModelConst.TEXT_SUFFIX);
    }

    public boolean containsChatLanguageModel(Integer modelId) {
        return chatLanguageMap.containsKey(modelId + ModelConst.TEXT_SUFFIX);
    }

    public EmbeddingModel getEmbeddingModel(Integer modelId) {
        return embeddingModelMap.get(modelId);
    }

    public boolean containsEmbeddingModel(Integer modelId) {
        return embeddingModelMap.containsKey(modelId);
    }

    public ImageModel getImageModel(Integer modelId) {
        return imageModelMap.get(modelId);
    }

    public boolean containsImageModel(Integer modelId) {
        return imageModelMap.containsKey(modelId);
    }
}
