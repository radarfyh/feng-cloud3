package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ltd.huntinginfo.feng.ai.api.dto.ChatReq;
import ltd.huntinginfo.feng.ai.api.entity.AigcApp;
import ltd.huntinginfo.feng.ai.api.entity.AigcAppApi;
import ltd.huntinginfo.feng.ai.api.vo.CompletionReq;
import ltd.huntinginfo.feng.ai.api.vo.CompletionRes;
import ltd.huntinginfo.feng.ai.config.AppChannelStore;
import ltd.huntinginfo.feng.ai.config.AppStore;
import ltd.huntinginfo.feng.ai.exception.ServiceException;
import ltd.huntinginfo.feng.ai.service.LangChatService;
import ltd.huntinginfo.feng.ai.utils.AiEmitter;
import ltd.huntinginfo.feng.common.core.constant.AppConst;
import ltd.huntinginfo.feng.common.core.util.StreamEmitter;
import ltd.huntinginfo.feng.common.security.annotation.OpenapiAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 渠道访问端点 （APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
@Tag(name = "渠道对话模块")
public class AppApiChatEndpoint {

    private final LangChatService langChatService;
    private final AppStore appStore;
    private final AppChannelStore appChannelStore;

    @OpenapiAuth(AppConst.CHANNEL_API)
    @PostMapping(value = "/chat/completions")
    @Operation(summary = "完成请求")
    public SseEmitter completions(@RequestBody CompletionReq req) {
        StreamEmitter emitter = new StreamEmitter();
        AigcAppApi appApi = appChannelStore.getApiChannel();

        return handler(emitter, appApi.getAppId(), req.getMessages());
    }

    private SseEmitter handler(StreamEmitter emitter, Integer appId, List<CompletionReq.Message> messages) {
        if (messages == null || messages.isEmpty() || appId <= 0) {
            throw new RuntimeException("聊天消息为空，或者没有配置模型信息");
        }
        CompletionReq.Message message = messages.get(0);

        AigcApp app = appStore.get(appId);
        AigcAppApi appApi = appChannelStore.getApiChannel();
        if (app == null) {
            throw new ServiceException("没有配置应用信息");
        }
        ChatReq req = new ChatReq()
                .setMessage(message.getContent())
                .setRole(message.getRole())
                .setModelId(app.getModelId())
                .setPrompts(appApi.getPrompts())
                .setKnowledgeIds(app.getKnowledgeIds());
        
        AiEmitter aiEmitter =  new AiEmitter();
        aiEmitter.setEmitter(emitter);
        
        langChatService
                .singleChat(req, aiEmitter)
                .onNext(token -> {
                    CompletionRes res = CompletionRes.process(token);
                    emitter.send(res);
                }).onComplete(c -> {
                    CompletionRes res = CompletionRes.end(c);
                    emitter.send(res);
                    emitter.complete();
                }).onError(e -> {
                    emitter.error(e.getMessage());
                }).start();

        return emitter.get();
    }
}
