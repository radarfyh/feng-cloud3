package work.metanet.feng.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * 智能体服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface Agent {

    TokenStream stream(@MemoryId Integer id, @UserMessage String message);

    String text(@MemoryId Integer id, @UserMessage String message);
}
