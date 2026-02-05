package work.metanet.feng.ai.service.impl;

import work.metanet.feng.ai.api.dto.ChatReq;
import work.metanet.feng.ai.api.dto.ChatRes;
import work.metanet.feng.ai.api.dto.ImageR;
import work.metanet.feng.ai.api.entity.AigcAppApi;
import work.metanet.feng.ai.api.entity.AigcMessage;
import work.metanet.feng.ai.api.entity.AigcOss;
import work.metanet.feng.ai.config.AppChannelStore;
import work.metanet.feng.ai.service.AigcMessageService;
import work.metanet.feng.ai.service.ChatService;
import work.metanet.feng.ai.service.LangChatService;
import work.metanet.feng.ai.utils.AiEmitter;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.util.ServletUtil;
import work.metanet.feng.common.core.util.StreamEmitter;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.github.xiaoymin.knife4j.core.util.StrUtil;

/**
 * AI对话请求服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final LangChatService langChatService;
    private final AigcMessageService aigcMessageService;
    private final AppChannelStore appChannelStore;

    @Override
    public void chat(ChatReq req, AiEmitter emit) {
        StreamEmitter emitter = emit.getEmitter();
        if (emitter == null) {
        	throw new RuntimeException("chat--》内部错误，emitter为空");
        }
        long startTime = System.currentTimeMillis();
        StringBuilder text = new StringBuilder();

        AigcAppApi appApi = appChannelStore.getApiChannel();
        if (appApi != null) {
            if (req.getModelId() == null || req.getModelId() <= 0) {
            	req.setModelId(appApi.getApp().getModelId());
            	req.setModelName(appApi.getApp().getModel().getName());
            }
//            if (req.getPrompts() == null || req.getPrompts().size() == 0) {
//            	req.setPrompts(appApi.getPrompts());
//            }
//            if (req.getKnowledgeId() == null || req.getKnowledgeId() <= 0) {
//            	
//            }
//            if (req.getKnowledgeIds() == null || req.getKnowledgeIds().size() == 0) {
//            	req.setKnowledgeIds(appApi.getApp().getKnowledgeIds());
//            }
            if (req.getAppId() != null && req.getAppId() > 0) {
            	if (req.getAppId() != appApi.getAppId() ||
            			req.getAppApiId() != appApi.getId()) {
            		throw new RuntimeException("应用ID或者渠道ID不一致，请确认渠道API KEY是否正确");
            	}
            }
            req.setAppId(appApi.getAppId());
            req.setAppApiId(appApi.getId());
        }

        // 保存用户消息
        req.setRole(BuiltInRoleEnum.USER.getCode());
        saveMessage(req, 0, 0);
        
        try {
            langChatService
                    .chat(req, emit)
                    .onNext(e -> {
                        text.append(e);
                        ChatRes res = new ChatRes(e);
                        emitter.send(res);
                        log.debug("onNext-->{}", res);
                    })
                    .onComplete((e) -> {
                        TokenUsage tokenUsage = e.tokenUsage();
                        ChatRes res = new ChatRes(tokenUsage.totalTokenCount(), startTime);
                        emitter.send(res);
                        log.debug("onComplete-->{}", res);
                        emitter.complete();

                        // 保存助手消息
                        req.setMessage(text.toString());
                        req.setRole(BuiltInRoleEnum.ASSISTANT.getCode());
                        saveMessage(req, tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount());
                    })
                    .onError((e) -> {
                    	// 发生错误也要记录日志，并发送中断消息到客户端
                        log.debug("onError-->{}", e.getMessage());
                        
                        // 保存系统消息
                        if (StrUtil.isNotBlank(text)) {
	                        req.setMessage(text.toString());
                        }
                        else {
                        	req.setMessage(e.getMessage());
                        }
                        req.setRole(BuiltInRoleEnum.SYSTEM.getCode());
                        saveMessage(req, 0, 0);
                        
                        emitter.error(e.getMessage());
                    })
                    .start();
        } catch (Exception e) {
            emitter.error(e.getMessage());
        } 
    }
    
    private void saveMessage(ChatReq req, Integer inputToken, Integer outputToken) {
        if (req.getConversationId() != null) {
            AigcMessage message = new AigcMessage();
            BeanUtils.copyProperties(req, message);
            
            if (StrUtil.isBlank(message.getIp())) {
            	message.setIp(ServletUtil.getIpAddr());
            }
        
            message.setPromptTokens(inputToken);
            message.setTokens(outputToken);
            
            message.setStatus("0");
            message.setCreateBy(req.getUsername());
            
            message.setCreateTime(LocalDateTime.now());
            
            aigcMessageService.addMessage(message);
        }
    }

    @Override
    public String text(ChatReq req, AiEmitter emit) {
        String text;
        try {
            text = langChatService.text(req, emit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return text;
    }

    @Override
    public AigcOss image(ImageR req, AiEmitter emit) {
        Response<Image> res = langChatService.image(req, emit);

        String path = res.content().url().toString();
        AigcOss oss = new AigcOss();
        oss.setUrl(path);
        return oss;
    }
}
