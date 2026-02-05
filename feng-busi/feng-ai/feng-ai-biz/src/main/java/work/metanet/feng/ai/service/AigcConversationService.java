package work.metanet.feng.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.ai.api.entity.AigcConversation;
import java.util.List;

/**
 * AI会话服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcConversationService extends IService<AigcConversation> {

    /**
     * 获取会话列表
     */
    List<AigcConversation> conversations(Integer userId);

    /**
     * 获取会话分页列表
     */
    IPage<AigcConversation> conversationPages(AigcConversation data, Page<AigcConversation> page);
    
    /**
     * 按ID查询会话数据
     * @param id
     * @return
     */
    AigcConversation findById(Integer id);
    
    /**
     * 新增会话
     */
    AigcConversation addConversation(AigcConversation conversation);

    /**
     * 修改会话
     */
    void updateConversation(AigcConversation conversation);

    /**
     * 删除会话
     */
    void delConversation(Integer conversationId);

}

