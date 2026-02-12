package ltd.huntinginfo.feng.center.service.consumer;

import ltd.huntinginfo.feng.center.service.processor.MessageDistributionProcessor;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.consumer.MqMessageConsumer;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RabbitMQ 消息消费者
 * <p>
 * 职责：监听各业务队列，将消息转换为业务对象，调用对应的业务处理器。
 * 不包含任何业务逻辑，所有业务处理委托给 {@link MessageDistributionProcessor}。
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@RequiredArgsConstructor
public class MessageRabbitConsumer implements MqMessageConsumer {

    private final MessageDistributionProcessor messageDistributionProcessor;

    // ==================== 消息状态事件监听（与 ump_msg_main.status 一一对应） ====================

    /**
     * 监听【消息已接收】事件（RECEIVED）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_RECEIVED
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_RECEIVED)
    public void handleMessageReceived(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息已接收事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        // 调用处理器：执行消息接收后的业务逻辑（如创建分发任务）
        messageDistributionProcessor.handleMessageReceived(messageId, MqMessage.getPayload());
    }

    /**
     * 监听【消息已分发】事件（DISTRIBUTED）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED)
    public void handleMessageDistributed(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息已分发事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        messageDistributionProcessor.handleMessageDistributed(messageId, MqMessage.getPayload());
    }

    /**
     * 监听【消息已发送】事件（SENT）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_SENT
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_SENT)
    public void handleMessageSent(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息已发送事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        messageDistributionProcessor.handleMessageSent(messageId, MqMessage.getPayload());
    }

    /**
     * 监听【消息已读】事件（READ）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_READ
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_READ)
    public void handleMessageRead(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息已读事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        messageDistributionProcessor.handleMessageRead(messageId, MqMessage.getPayload());
    }

    /**
     * 监听【消息已过期】事件（EXPIRED）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_EXPIRED
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_EXPIRED)
    public void handleMessageExpired(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息已过期事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        messageDistributionProcessor.handleMessageExpired(messageId, MqMessage.getPayload());
    }

    /**
     * 监听【消息失败】事件（FAILED）
     * 对应队列：MqMessageEventConstants.Queues.MESSAGE_FAILED
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_FAILED)
    public void handleMessageFailed(MqMessage<Map<String, Object>> MqMessage) {
        String messageId = extractMessageId(MqMessage);
        log.info("接收到消息失败事件，消息ID: {}, 重试次数: {}", messageId, MqMessage.getRetryCount());

        messageDistributionProcessor.handleMessageFailed(messageId, MqMessage.getPayload());
    }

    // ==================== 异步任务队列监听 ====================

    /**
     * 监听消息推送任务队列
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_SEND_TASK)
    public void handleSendTask(MqMessage<Map<String, Object>> MqMessage) {
        String taskId = (String) MqMessage.getPayload().get("taskId");
        log.info("接收到消息推送任务，任务ID: {}, 重试次数: {}", taskId, MqMessage.getRetryCount());

        messageDistributionProcessor.processSendTask(MqMessage.getPayload());
    }

    /**
     * 监听回调任务队列
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_CALLBACK_TASK)
    public void handleCallbackTask(MqMessage<Map<String, Object>> MqMessage) {
        String taskId = (String) MqMessage.getPayload().get("taskId");
        log.info("接收到回调任务，任务ID: {}, 重试次数: {}", taskId, MqMessage.getRetryCount());

        messageDistributionProcessor.processCallbackTask(MqMessage.getPayload());
    }

    /**
     * 监听重试任务队列
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK)
    public void handleRetryTask(MqMessage<Map<String, Object>> MqMessage) {
        String taskId = (String) MqMessage.getPayload().get("taskId");
        log.info("接收到重试任务，任务ID: {}, 重试次数: {}", taskId, MqMessage.getRetryCount());

        messageDistributionProcessor.processRetryTask(MqMessage.getPayload());
    }

    /**
     * 监听广播分发任务队列
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.BROADCAST_DISPATCH_TASK)
    public void handleBroadcastDispatchTask(MqMessage<Map<String, Object>> MqMessage) {
        String taskId = (String) MqMessage.getPayload().get("taskId");
        log.info("接收到广播分发任务，任务ID: {}, 重试次数: {}", taskId, MqMessage.getRetryCount());

        messageDistributionProcessor.processBroadcastDispatchTask(MqMessage.getPayload());
    }

    // ==================== 延迟队列监听 ====================
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.DELAYED_SEND)
    public void handleDelayedSendTask(MqMessage<Map<String, Object>> MqMessage) {
        log.info("接收到延迟发送任务，重试次数: {}", MqMessage.getRetryCount());
        messageDistributionProcessor.processDelayedSend(MqMessage.getPayload());
    }

    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.DELAYED_EXPIRE)
    public void handleDelayedExpireTask(MqMessage<Map<String, Object>> MqMessage) {
        log.info("接收到延迟过期处理任务，重试次数: {}", MqMessage.getRetryCount());
        messageDistributionProcessor.processDelayedExpire(MqMessage.getPayload());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从 MqMessage 中提取消息ID
     * 优先从 payload 中获取 messageId，若不存在则使用 MqMessage 自身的 messageId
     */
    private String extractMessageId(MqMessage<Map<String, Object>> MqMessage) {
        Map<String, Object> payload = MqMessage.getPayload();
        if (payload != null && payload.containsKey("messageId")) {
            return (String) payload.get("messageId");
        }
        return MqMessage.getMessageId();
    }
}