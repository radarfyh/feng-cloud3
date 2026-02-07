package ltd.huntinginfo.feng.ai.service;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import ltd.huntinginfo.feng.ai.api.dto.ChatReq;
import ltd.huntinginfo.feng.ai.api.dto.ImageR;
import ltd.huntinginfo.feng.ai.utils.AiEmitter;

/**
 * 长对话服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface LangChatService {

    TokenStream chat(ChatReq req, AiEmitter emit);

    TokenStream singleChat(ChatReq req, AiEmitter emit);

    String text(ChatReq req, AiEmitter emit);

    Response<Image> image(ImageR req, AiEmitter emit);
}
