package ltd.huntinginfo.feng.ai.service;

import ltd.huntinginfo.feng.ai.api.dto.ChatReq;
import ltd.huntinginfo.feng.ai.api.dto.ImageR;
import ltd.huntinginfo.feng.ai.api.entity.AigcOss;
import ltd.huntinginfo.feng.ai.utils.AiEmitter;

/**
 * 对话请求服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface ChatService {

    void chat(ChatReq req, AiEmitter emit);


    /**
     * 文本请求
     */
    String text(ChatReq req, AiEmitter emit);

    /**
     * 文生图
     */
    AigcOss image(ImageR req, AiEmitter emit);
}
