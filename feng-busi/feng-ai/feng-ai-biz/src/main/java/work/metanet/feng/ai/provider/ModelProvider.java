package work.metanet.feng.ai.provider;

import cn.hutool.core.util.ObjectUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.AllArgsConstructor;
import work.metanet.feng.ai.config.ModelStoreFactory;

import org.springframework.stereotype.Component;

/**
 * 模型提供者
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Component
@AllArgsConstructor
public class ModelProvider {

    private final ModelStoreFactory modelStoreFactory;

    public StreamingChatLanguageModel stream(Integer modelId) {
        StreamingChatLanguageModel streamingChatModel = modelStoreFactory.getStreamingChatModel(modelId);
        if (ObjectUtil.isNotEmpty(streamingChatModel)) {
            return streamingChatModel;
        }
        throw new RuntimeException("没有匹配到模型，请检查模型配置！");
    }

    public ChatLanguageModel text(Integer modelId) {
        ChatLanguageModel chatLanguageModel = modelStoreFactory.getChatLanguageModel(modelId);
        if (ObjectUtil.isNotEmpty(chatLanguageModel)) {
            return chatLanguageModel;
        }
        throw new RuntimeException("没有匹配到模型，请检查模型配置！");
    }

    public ImageModel image(Integer modelId) {
        ImageModel imageModel = modelStoreFactory.getImageModel(modelId);
        if (ObjectUtil.isNotEmpty(imageModel)) {
            return imageModel;
        }
        throw new RuntimeException("没有匹配到模型，请检查模型配置！");
    }
}
