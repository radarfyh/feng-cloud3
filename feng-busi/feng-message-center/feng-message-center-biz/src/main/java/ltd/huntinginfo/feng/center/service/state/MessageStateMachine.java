package ltd.huntinginfo.feng.center.service.state;

import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息状态机 - 集中管理 ump_msg_main.status 的所有合法变更
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStateMachine {

    private final UmpMsgMainService msgMainService;

    // ========== 正向流转 ==========

    /** RECEIVED → DISTRIBUTING（开始分发） */
    @Transactional
    public void onDistributeStart(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.DISTRIBUTING);
        log.debug("状态变更: 消息ID={} → DISTRIBUTING", msgId);
    }

    /** DISTRIBUTING → DISTRIBUTED（分发成功） */
    @Transactional
    public void onDistributed(String msgId, int totalReceivers) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.DISTRIBUTED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getDistributeTime, LocalDateTime.now())
                .set(UmpMsgMain::getTotalReceivers, totalReceivers)
                .update();
        log.info("状态变更: 消息ID={} → DISTRIBUTED, 接收者数量={}", msgId, totalReceivers);
    }

    /** DISTRIBUTED → PUSHED（发起推送请求） */
    @Transactional
    public void onPushed(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.PUSHED);
        log.debug("状态变更: 消息ID={} → PUSHED", msgId);
    }

    /** DISTRIBUTED → PULL（转为待拉取） */
    @Transactional
    public void onPullReady(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.PULL);
        log.debug("状态变更: 消息ID={} → PULL", msgId);
    }

    /** PUSHED → BIZ_RECEIVED（业务系统确认接收） */
    @Transactional
    public void onBusinessReceived(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.BIZ_RECEIVED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getSendTime, LocalDateTime.now())
                .update();
        log.info("状态变更: 消息ID={} → BIZ_RECEIVED", msgId);
    }

    /** PULL → BIZ_PULLED（业务系统拉取成功） */
    @Transactional
    public void onBusinessPulled(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.BIZ_PULLED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getSendTime, LocalDateTime.now())
                .update();
        log.info("状态变更: 消息ID={} → BIZ_PULLED", msgId);
    }

    /** BIZ_RECEIVED / BIZ_PULLED → READ（用户已读） */
    @Transactional
    public void onRead(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.READ);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();
        log.info("状态变更: 消息ID={} → READ", msgId);
    }

    // ========== 失败状态（超过重试次数） ==========

    /** 分发失败超过重试次数 → DIST_FAILED */
    @Transactional
    public void onDistributeFailed(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.DIST_FAILED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();
        log.warn("状态变更: 消息ID={} → DIST_FAILED（分发失败超限）", msgId);
    }

    /** 推送失败/业务确认失败超过重试次数 → PUSH_FAILED */
    @Transactional
    public void onPushFailed(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.PUSH_FAILED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();
        log.warn("状态变更: 消息ID={} → PUSH_FAILED（推送失败超限）", msgId);
    }

    /** 拉取超时/过期 → PULL_FAILED */
    @Transactional
    public void onPullFailed(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.PULL_FAILED);
        msgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, msgId)
                .set(UmpMsgMain::getCompleteTime, LocalDateTime.now())
                .update();
        log.warn("状态变更: 消息ID={} → PULL_FAILED（拉取超时）", msgId);
    }

    // ========== 重试回退（未超限） ==========

    /** 分发失败后重试 → 回退到 DISTRIBUTING */
    @Transactional
    public void onRetryDistribute(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.DISTRIBUTING);
        log.info("状态变更: 消息ID={} → DISTRIBUTING（分发重试）", msgId);
    }

    /** 推送失败/业务确认失败后重试 → 回退到 DISTRIBUTED */
    @Transactional
    public void onRetryPush(String msgId) {
        msgMainService.updateMessageStatus(msgId, MqMessageEventConstants.EventTypes.DISTRIBUTED);
        log.info("状态变更: 消息ID={} → DISTRIBUTED（推送重试）", msgId);
    }
}