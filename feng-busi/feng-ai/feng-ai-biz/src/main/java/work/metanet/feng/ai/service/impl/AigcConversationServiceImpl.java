package work.metanet.feng.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.ai.api.entity.AigcConversation;
import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcMessage;
import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.ai.mapper.AigcAppApiMapper;
import work.metanet.feng.ai.mapper.AigcConversationMapper;
import work.metanet.feng.ai.mapper.AigcMessageMapper;
import work.metanet.feng.ai.mapper.AigcPromptMapper;
import work.metanet.feng.ai.service.AigcConversationService;
import work.metanet.feng.common.core.constant.SecurityConstants;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI会话服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcConversationServiceImpl extends ServiceImpl<AigcConversationMapper, AigcConversation> implements
        AigcConversationService {
    private final AigcConversationMapper aigcConversationMapper;
    private final AigcMessageMapper aigcMessageMapper;
    private final AigcPromptMapper aigcPromptMapper;
    private final AigcAppApiMapper aigcAppApiMapper;
    private final RemoteUserService remoteUserService;

    @Override
    public List<AigcConversation> conversations(Integer userId) {
        return aigcConversationMapper.selectList(
                Wrappers.<AigcConversation>lambdaQuery()
                        .eq(AigcConversation::getUserId, userId)
                        .orderByDesc(AigcConversation::getCreateTime));
    }

    @Override
    public IPage<AigcConversation> conversationPages(AigcConversation data, Page<AigcConversation> page) {

    	Page<AigcConversation> iPage = aigcConversationMapper.selectPage(page, Wrappers.<AigcConversation>lambdaQuery()
    	        .like(!StrUtil.isBlank(data.getTitle()), AigcConversation::getTitle, data.getTitle())
    	        .eq(data.getAppApiId() != null && data.getAppApiId() > 0, AigcConversation::getAppApiId, data.getAppApiId())
    	        .eq(data.getKnowledgeId() != null, AigcConversation::getKnowledgeId, data.getKnowledgeId()) 
    	        .isNull(data.getKnowledgeId() == null, AigcConversation::getKnowledgeId) // 查询知识库ID为空的记录
    	        .orderByDesc(AigcConversation::getCreateTime));

        if (!iPage.getRecords().isEmpty()) {
            Map<Integer, List<SysUser>> userMap = remoteUserService.getUserList(SecurityConstants.FROM_IN)
                    .getData().stream()
                    .collect(Collectors.groupingBy(SysUser::getId));

            Set<Integer> conversationIds = iPage.getRecords().stream()
                    .map(AigcConversation::getId)
                    .collect(Collectors.toSet());

            // 查询所有会话相关的消息
            List<AigcMessage> messages = aigcMessageMapper.selectList(
                    Wrappers.<AigcMessage>lambdaQuery()
                            .in(AigcMessage::getConversationId, conversationIds)
                            .orderByDesc(AigcMessage::getCreateTime)
            );

            // 查询出这些Conversation对应的appApiId
            Set<Integer> appApiIds = iPage.getRecords().stream()
                    .map(AigcConversation::getAppApiId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            // 查询所有Prompt，并分组
            List<AigcPrompt> prompts = aigcPromptMapper.selectList(Wrappers.<AigcPrompt>lambdaQuery()
                    .in(AigcPrompt::getAppApiId, appApiIds)
                    .groupBy(AigcPrompt::getId, AigcPrompt::getAppApiId)
                    .orderByDesc(AigcPrompt::getCreateTime));
            Map<Integer, List<AigcPrompt>> promptMap = prompts.stream()
                    .collect(Collectors.groupingBy(AigcPrompt::getAppApiId));

            // 组装数据
            iPage.getRecords().forEach(i -> {
                List<SysUser> userList = userMap.get(i.getUserId());
                if (userList != null && !userList.isEmpty()) {
                    i.setUsername(userList.get(0).getUsername());
                }

                List<AigcMessage> messageList = messages.stream()
                        .filter(m -> m.getConversationId() != null && m.getConversationId().equals(i.getId()))
                        .collect(Collectors.toList());

                if (!messageList.isEmpty()) {
                    i.setChatTotal(messageList.size());
                    i.setEndTime(messageList.get(0).getCreateTime());
                    i.setTokenUsed(messageList.stream()
                            .filter(m -> m.getTokens() != null)
                            .mapToInt(AigcMessage::getTokens)
                            .sum());
                }

                // 组合prompt
                List<AigcPrompt> appPrompts = promptMap.get(i.getAppApiId());
                if (appPrompts != null && !appPrompts.isEmpty()) {
                    String combinedPrompt = appPrompts.stream()
                            .map(AigcPrompt::getContent)
                            .filter(StrUtil::isNotBlank)
                            .collect(Collectors.joining("\n\n"));
                    i.setPrompt(combinedPrompt);
                }
            });
        }
        return iPage;
    }


    @Override
    @Transactional
    public AigcConversation addConversation(AigcConversation conversation) {

        if (aigcConversationMapper.insert(conversation) > 0) {
        	return conversation;
        } else {
        	return null;
        }
    }

    @Override
    @Transactional
    public void updateConversation(AigcConversation conversation) {
        aigcConversationMapper.updateById(conversation);
    }

    @Override
    @Transactional
    public void delConversation(Integer conversationId) {
    	aigcMessageMapper.delete(
                Wrappers.<AigcMessage>lambdaQuery()
                        .eq(AigcMessage::getConversationId, conversationId));
    	aigcConversationMapper.deleteById(conversationId);
    }

	@Override
	public AigcConversation findById(Integer id) {
		return aigcConversationMapper.selectById(id);
	}

}

