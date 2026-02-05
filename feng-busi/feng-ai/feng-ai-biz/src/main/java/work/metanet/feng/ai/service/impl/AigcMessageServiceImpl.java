package work.metanet.feng.ai.service.impl;

import work.metanet.feng.ai.api.entity.AigcMessage;
import work.metanet.feng.ai.mapper.AigcMessageMapper;
import work.metanet.feng.ai.service.AigcMessageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcMessageServiceImpl extends ServiceImpl<AigcMessageMapper, AigcMessage> implements AigcMessageService {
    private final AigcMessageMapper aigcMessageMapper;

    @Override
    @Transactional
    public AigcMessage addMessage(AigcMessage message) {
        if (aigcMessageMapper.insert(message) > 0) {
        	return message;
        } else {
        	return null;
        }
    }

    @Override
    @Transactional
    public void clearMessage(Integer conversationId) {
    	aigcMessageMapper.delete(
                Wrappers.<AigcMessage>lambdaQuery()
                        .eq(AigcMessage::getConversationId, conversationId));
    }

    @Override
    public List<AigcMessage> getMessages(Integer conversationId, Integer count) {
        // 避免页面渲染压力大，只截取最新的count条数据
        return aigcMessageMapper.selectPage(new Page<>(0, count), Wrappers.<AigcMessage>lambdaQuery()
                .eq(AigcMessage::getConversationId, conversationId)
                .orderByDesc(AigcMessage::getCreateTime)
        ).getRecords();
    }

    @Override
    public List<AigcMessage> getMessages(Integer conversationId, Integer userId, Integer count) {
        // 避免页面渲染压力大，只截取最新的count条数据
        return aigcMessageMapper.selectPage(new Page<>(0, count), Wrappers.<AigcMessage>lambdaQuery()
                .eq(AigcMessage::getConversationId, conversationId)
                .eq(AigcMessage::getUserId, userId)
                .orderByAsc(AigcMessage::getCreateTime)
        ).getRecords();
    }
}

