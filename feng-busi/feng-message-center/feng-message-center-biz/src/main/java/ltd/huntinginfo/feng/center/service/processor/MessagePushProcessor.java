package ltd.huntinginfo.feng.center.service.processor;

import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.common.rabbitmq.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import cn.hutool.core.bean.BeanUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息推送处理器
 * 负责处理主动推送模式的消息
 * 主要业务逻辑参见数据库表脚本和UmpMsgQueueService的注释
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePushProcessor {
    
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpAppCredentialService umpAppCredentialService;
    private final RabbitMqService rabbitMqService;
    private final RestTemplate restTemplate;
    
    /**
     * 推送消息给接收者
     */
    public boolean pushMessageToReceiver(UmpMsgInbox inbox) {
        String inboxId = inbox.getId();
        String msgId = inbox.getMsgId();
        String receiverId = inbox.getReceiverId();
        
        try {
            log.info("开始推送消息，收件箱ID: {}, 消息ID: {}, 接收者ID: {}", 
                    inboxId, msgId, receiverId);
            
            // 1. 更新推送状态为处理中
            umpMsgInboxService.updatePushStatus(inboxId, "PROCESSING", null);
            
            // 2. 获取应用凭证信息（用于回调）
            // 这里需要根据消息ID获取发送应用信息，简化处理
            String appKey = getAppKeyByMessageId(msgId);
            if (!StringUtils.hasText(appKey)) {
                throw new RuntimeException("无法获取应用信息");
            }
            
            AppDetailVO appCredentialVO = umpAppCredentialService.getAppByKey(appKey);
            
            UmpAppCredential appCredential = new UmpAppCredential();
            BeanUtil.copyProperties(appCredential, appCredentialVO);
            String appSecret = umpAppCredentialService.getAppSecret(appCredentialVO.getId());
            appCredential.setAppSecret(appSecret);
            
            if (appCredential == null) {
                throw new RuntimeException("应用凭证不存在");
            }
            
            // 3. 构建推送数据
            Map<String, Object> pushData = buildPushData(inbox, appCredential);
            
            // 4. 发送推送请求
            boolean pushSuccess = sendPushRequest(appCredential, pushData);
            
            // 5. 更新推送状态
            if (pushSuccess) {
                umpMsgInboxService.updatePushStatus(inboxId, "SUCCESS", null);
                // 标记为已接收
                umpMsgInboxService.markAsReceived(inboxId);
                log.info("消息推送成功，收件箱ID: {}", inboxId);
                return true;
            } else {
                umpMsgInboxService.updatePushStatus(inboxId, "FAILED", "推送请求失败");
                log.error("消息推送失败，收件箱ID: {}", inboxId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("消息推送异常，收件箱ID: {}", inboxId, e);
            umpMsgInboxService.updatePushStatus(inboxId, "FAILED", e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建推送数据
     */
    private Map<String, Object> buildPushData(UmpMsgInbox inbox, UmpAppCredential appCredential) {
        Map<String, Object> pushData = new HashMap<>();
        
        // 基础信息
        pushData.put("inboxId", inbox.getId());
        pushData.put("msgId", inbox.getMsgId());
        pushData.put("receiverId", inbox.getReceiverId());
        pushData.put("receiverType", inbox.getReceiverType());
        pushData.put("receiverName", inbox.getReceiverName());
        pushData.put("distributeTime", inbox.getDistributeTime());
        
        // 消息内容（需要从消息主表获取，这里简化）
        pushData.put("message", buildMessageContent(inbox.getMsgId()));
        
        // 回调信息
        if (StringUtils.hasText(appCredential.getCallbackUrl())) {
            pushData.put("callbackUrl", appCredential.getCallbackUrl());
            pushData.put("callbackAuthMode", appCredential.getCallbackAuthMode());
        }
        
        // 签名信息
        if ("SIGNATURE".equals(appCredential.getCallbackAuthMode())) {
            pushData.put("signature", generateSignature(pushData, appCredential.getAppSecret()));
        }
        
        pushData.put("timestamp", LocalDateTime.now().toString());
        
        return pushData;
    }
    
    /**
     * 发送推送请求
     */
    private boolean sendPushRequest(UmpAppCredential appCredential, Map<String, Object> pushData) {
        String callbackUrl = appCredential.getCallbackUrl();
        if (!StringUtils.hasText(callbackUrl)) {
            log.warn("应用未配置回调地址，无法推送，应用标识: {}", appCredential.getAppKey());
            return false;
        }
        
        try {
            // 发送HTTP POST请求
            // 这里可以根据需要设置超时时间、重试策略等
            Map<String, Object> response = restTemplate.postForObject(callbackUrl, pushData, Map.class);
            
            if (response != null && "success".equals(response.get("status"))) {
                return true;
            }
            
            log.warn("推送请求响应失败，响应: {}", response);
            return false;
            
        } catch (Exception e) {
            log.error("推送请求异常，回调地址: {}", callbackUrl, e);
            return false;
        }
    }
    
    // ============ 辅助方法 ============
    
    private String getAppKeyByMessageId(String msgId) {
        // TODO: 根据消息ID获取应用标识
        // 可以从消息主表查询
        return "DEFAULT_APP_KEY";
    }
    
    private Map<String, Object> buildMessageContent(String msgId) {
        // TODO: 根据消息ID获取消息内容
        Map<String, Object> content = new HashMap<>();
        content.put("msgId", msgId);
        content.put("title", "测试消息");
        content.put("content", "这是消息内容");
        content.put("timestamp", LocalDateTime.now().toString());
        return content;
    }
    
    private String generateSignature(Map<String, Object> data, String appSecret) {
        // TODO: 生成签名
        return "SIGNATURE_" + System.currentTimeMillis();
    }
}
