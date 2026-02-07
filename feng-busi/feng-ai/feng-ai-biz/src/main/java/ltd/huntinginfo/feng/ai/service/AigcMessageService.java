package ltd.huntinginfo.feng.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.ai.api.entity.AigcMessage;
import java.util.List;

/**
 * AI消息服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcMessageService extends IService<AigcMessage> {
    /**
     * 新增消息到会话中：会话ID放在AigcMessage中
     * @param message
     * @return
     */
    AigcMessage addMessage(AigcMessage message);

    void clearMessage(Integer conversationId);

    List<AigcMessage> getMessages(Integer conversationId, Integer count);

    List<AigcMessage> getMessages(Integer conversationId, Integer userId, Integer count);
    
}

