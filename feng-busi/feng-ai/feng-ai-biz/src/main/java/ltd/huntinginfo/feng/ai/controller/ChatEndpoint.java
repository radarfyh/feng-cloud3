package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.ai.api.dto.ChatReq;
import ltd.huntinginfo.feng.ai.api.dto.ChatRes;
import ltd.huntinginfo.feng.ai.api.dto.ImageR;
import ltd.huntinginfo.feng.ai.api.entity.AigcApp;
import ltd.huntinginfo.feng.ai.api.entity.AigcAppApi;
import ltd.huntinginfo.feng.ai.api.entity.AigcConversation;
import ltd.huntinginfo.feng.ai.api.entity.AigcMessage;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.api.entity.AigcPrompt;
import ltd.huntinginfo.feng.ai.service.AigcAppApiService;
import ltd.huntinginfo.feng.ai.service.AigcAppService;
import ltd.huntinginfo.feng.ai.service.AigcConversationService;
import ltd.huntinginfo.feng.ai.service.AigcMessageService;
import ltd.huntinginfo.feng.ai.service.AigcModelService;
import ltd.huntinginfo.feng.ai.service.ChatService;
import ltd.huntinginfo.feng.ai.service.impl.PersistentChatMemoryStore;
import ltd.huntinginfo.feng.ai.utils.AiEmitter;
import ltd.huntinginfo.feng.common.core.constant.PromptConst;
import ltd.huntinginfo.feng.common.core.constant.enums.BuiltInRoleEnum;
import ltd.huntinginfo.feng.common.core.properties.ChatProps;
import ltd.huntinginfo.feng.common.core.util.PromptUtil;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.StreamEmitter;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.util.SecurityUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.http.HttpServletRequest;

/**
 * AI对话端点（请求和应答APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RequestMapping("/")
@RestController
@AllArgsConstructor
@Tag(name = "AI对话请求应答模块")
public class ChatEndpoint {

    private final ChatService chatService;
    private final AigcMessageService messageService;
    private final AigcModelService modelService;
    private final AigcAppService appService;
    private final AigcAppApiService appApiService;
    private final ChatProps chatProps;

    @SysLog("聊天请求")
    @PostMapping("/chat/completions")
    @PreAuthorize("@pms.hasPermission('chat:completions')")
    @Operation(summary = "聊天请求")
    public SseEmitter chat(@RequestBody ChatReq req, HttpServletRequest request) {
    	// 保存当前的请求属性
        RequestAttributes attributes = new ServletRequestAttributes(request);
        // 第二个参数表示在子线程中共享，**很重要**。
        RequestContextHolder.setRequestAttributes(attributes, true);
        
    	// 填充子会话ID
    	if (StrUtil.isEmpty(req.getChatId())) {
    		String newChatId = StrUtil.uuid();
    		req.setChatId(newChatId);
    	}
    	
    	// 传递emitter和executor
        StreamEmitter emitter = new StreamEmitter();
        AiEmitter aiEmitter =  new AiEmitter();
        aiEmitter.setEmitter(emitter);
//        req.setUserId(SecurityUtils.getUser().getId());
//        req.setUsername(SecurityUtils.getUser().getUsername());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        aiEmitter.setExecutor(executor);
        return emitter.streaming(executor, () -> {
            chatService.chat(req, aiEmitter);
        });
    }

    @GetMapping("/app/info")
    @Operation(summary = "查询应用信息")
    public R<AigcApp> appInfo(@RequestParam Integer appId, Integer conversationId) {
        AigcApp app = appService.getById(appId);
        if (conversationId == null || conversationId <= 0) {
            conversationId = app.getId();
        }

        AigcAppApi appApi = appApiService.getByAppId(appId);
        List<AigcPrompt> prompts = appApi.getPrompts();

        if (prompts != null && !prompts.isEmpty()) {
            // 过滤 content 不为空的提示语
            List<String> promptContents = prompts.stream()
                    .map(AigcPrompt::getContent)
                    .filter(StrUtil::isNotBlank)
                    .toList();

            if (!promptContents.isEmpty()) {
                // 用 \n\n 组合多个提示语
                String systemPrompt = String.join("\n\n", promptContents);
                SystemMessage message = new SystemMessage(systemPrompt);
                PersistentChatMemoryStore.init(conversationId, message);
            }
        }

        return R.ok(app);
    }



    @GetMapping("/chat/list/{conversationId}")
    @Operation(summary = "按会话ID，查询聊天会话的消息")
    public R<List<AigcMessage>> messages(@PathVariable Integer conversationId) {
        List<AigcMessage> list = messageService.getMessages(conversationId, SecurityUtils.getUser().getId());

        // initialize chat memory
        List<ChatMessage> chatMessages = new ArrayList<>();
        list.forEach(item -> {
            if (chatMessages.size() >= chatProps.getMemoryMaxMessage()) {
                return;
            }
            if (item.getRole().equals(BuiltInRoleEnum.ASSISTANT.getCode())) {
                chatMessages.add(new AiMessage(item.getMessage()));
            } else {
                chatMessages.add(new UserMessage(item.getMessage()));
            }
        });
        PersistentChatMemoryStore.init(conversationId, chatMessages);
        return R.ok(list);
    }
    
    @GetMapping("/chat/page")
    @Operation(summary = "分页查询消息, 需在data中传递会话ID")
    public R<IPage<AigcMessage>> list(Page<AigcMessage> page, AigcMessage data) {
        LambdaQueryWrapper<AigcMessage> queryWrapper = Wrappers.<AigcMessage>lambdaQuery()
                .eq(data.getConversationId() != null && data.getConversationId() > 0, AigcMessage::getConversationId, data.getConversationId())
                .orderByDesc(AigcMessage::getId, AigcMessage::getCreateTime);
        IPage<AigcMessage> iPage = messageService.page(page, queryWrapper);
        return R.ok(iPage);
    }
    
    @SysLog("按会话ID清除会话消息")
    @DeleteMapping("/chat/messages/clean/{conversationId}")
    @PreAuthorize("@pms.hasPermission('chat:messages:clean')")
    @Operation(summary = "按会话ID，清除会话消息")
    public R cleanMessage(@PathVariable Integer conversationId) {
        messageService.clearMessage(conversationId);

        // clean chat memory
        PersistentChatMemoryStore.clean(conversationId);
        return R.ok();
    }

    @SysLog("生成思维导图")
    @PostMapping("/chat/mindmap")
    @Operation(summary = "生成思维导图")
    public R mindmap(@RequestBody ChatReq req) {
    	AiEmitter aiEmitter =  new AiEmitter();
    	aiEmitter.setPrompt(PromptUtil.build(req.getMessage(), PromptConst.MINDMAP));
        return R.ok(new ChatRes(chatService.text(req, aiEmitter)));
    }

    @SysLog("生成图片")
    @PostMapping("/chat/image")
    @Operation(summary = "生成图片")
    public R image(@RequestBody ImageR req) {
    	AiEmitter aiEmitter =  new AiEmitter();
    	aiEmitter.setPrompt(PromptUtil.build(req.getMessage(), PromptConst.IMAGE));
        return R.ok(chatService.image(req, aiEmitter));
    }

    @GetMapping("/chat/getImageModels")
    @Operation(summary = "查询图形模型列表")
    public R<List<AigcModel>> getImageModels() {
        List<AigcModel> list = modelService.getImageModels();
        list.forEach(i -> {
            i.setApiKey(null);
            i.setSecretKey(null);
        });
        return R.ok(list);
    }
}
