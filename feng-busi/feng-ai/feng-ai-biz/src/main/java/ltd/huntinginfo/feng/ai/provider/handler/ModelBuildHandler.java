package ltd.huntinginfo.feng.ai.provider.handler;

import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;

/**
 * 模型处理器接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface ModelBuildHandler {

    /**
     * 判断是不是当前模型
     */
    boolean whetherCurrentModel(AigcModel model);

    /**
     * basic check
     */
    boolean basicCheck(AigcModel model);

    /**
     * streaming chat build
     */
    StreamingChatLanguageModel buildStreamingChat(AigcModel model);

    /**
     * chat build
     */
    ChatLanguageModel buildChatLanguageModel(AigcModel model);

    /**
     * embedding config
     */
    EmbeddingModel buildEmbedding(AigcModel model);

    /**
     * image config
     */
    ImageModel buildImage(AigcModel model);

}
