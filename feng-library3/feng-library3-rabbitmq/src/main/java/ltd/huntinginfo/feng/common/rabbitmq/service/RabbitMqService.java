package ltd.huntinginfo.feng.common.rabbitmq.service;

import ltd.huntinginfo.feng.common.rabbitmq.constant.RabbitMessageEvent;
import ltd.huntinginfo.feng.common.rabbitmq.dto.RabbitMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ消息服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqService {
    
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;
    
    /**
     * 发送消息到指定队列
     *
     * @param exchange 交换器名称
     * @param routingKey 路由键
     * @param message 消息内容
     */
    public <T> void sendMessage(String exchange, String routingKey, RabbitMessage<T> message) {
        try {
            // 设置重试次数
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }
            
            // 转换消息
            Message amqpMessage = messageConverter.toMessage(message, new MessageProperties());
            
            // 设置消息头
            amqpMessage.getMessageProperties().setHeader("message-id", message.getMessageId());
            amqpMessage.getMessageProperties().setHeader("message-type", message.getMessageType());
            amqpMessage.getMessageProperties().setHeader("business-type", message.getBusinessType());
            amqpMessage.getMessageProperties().setHeader("send-time", LocalDateTime.now().toString());
            amqpMessage.getMessageProperties().setHeader("retry-count", message.getRetryCount());
            
            // 设置消息持久化
            amqpMessage.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
            
            // 发送消息
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage);
            
            log.debug("消息发送成功，消息ID: {}, 交换器: {}, 路由键: {}", 
                    message.getMessageId(), exchange, routingKey);
        } catch (AmqpException e) {
            log.error("消息发送失败，消息ID: {}, 交换器: {}, 路由键: {}", 
                    message.getMessageId(), exchange, routingKey, e);
            throw new RuntimeException("消息发送失败", e);
        }
    }
    
    /**
     * 发送延迟消息
     *
     * @param exchange 交换器名称
     * @param routingKey 路由键
     * @param message 消息内容
     * @param delayMillis 延迟毫秒数
     */
    public <T> void sendDelayedMessage(String exchange, String routingKey, 
                                      RabbitMessage<T> message, long delayMillis) {
        try {
            // 设置重试次数
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }
            
            // 转换消息
            Message amqpMessage = messageConverter.toMessage(message, new MessageProperties());
            
            // 设置消息头
            amqpMessage.getMessageProperties().setHeader("message-id", message.getMessageId());
            amqpMessage.getMessageProperties().setHeader("message-type", message.getMessageType());
            amqpMessage.getMessageProperties().setHeader("business-type", message.getBusinessType());
            amqpMessage.getMessageProperties().setHeader("send-time", LocalDateTime.now().toString());
            amqpMessage.getMessageProperties().setHeader("retry-count", message.getRetryCount());
            
            // 设置延迟时间（RabbitMQ延迟消息插件需要的头信息）
            amqpMessage.getMessageProperties().setHeader("x-delay", delayMillis);
            
            // 设置消息持久化
            amqpMessage.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
            
            // 发送消息
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage);
            
            log.debug("延迟消息发送成功，消息ID: {}, 交换器: {}, 路由键: {}, 延迟: {}ms", 
                    message.getMessageId(), exchange, routingKey, delayMillis);
        } catch (AmqpException e) {
            log.error("延迟消息发送失败，消息ID: {}, 交换器: {}, 路由键: {}", 
                    message.getMessageId(), exchange, routingKey, e);
            throw new RuntimeException("延迟消息发送失败", e);
        }
    }
    
    /**
     * 发送到延迟交换器
     *
     * @param payload 消息内容
     * @param delayMillis 延迟毫秒数
     */
    public <T> void sendToDelayedExchange(T payload, long delayMillis) {
        RabbitMessage<T> message = RabbitMessage.create("DELAYED_MESSAGE", payload);
        
        sendDelayedMessage(RabbitMessageEvent.EXCHANGE_DELAYED, 
                          RabbitMessageEvent.ROUTING_KEY_DELAYED, 
                          message, delayMillis);
    }
    
    /**
     * 发送消息创建事件
     *
     * @param payload 消息内容
     */
    public <T> void sendMessageCreatedEvent(T payload) {
        RabbitMessage<T> message = RabbitMessage.create(
                RabbitMessageEvent.MESSAGE_CREATED, payload);
        
        sendMessage(RabbitMessageEvent.EXCHANGE_MESSAGE, 
                   RabbitMessageEvent.ROUTING_KEY_MESSAGE_CREATED, message);
    }
    
    /**
     * 发送消息状态更新事件
     *
     * @param payload 消息内容
     */
    public <T> void sendMessageStatusUpdatedEvent(T payload) {
        RabbitMessage<T> message = RabbitMessage.create(
                RabbitMessageEvent.MESSAGE_STATUS_UPDATED, payload);
        
        sendMessage(RabbitMessageEvent.EXCHANGE_MESSAGE, 
                   RabbitMessageEvent.ROUTING_KEY_MESSAGE_STATUS, message);
    }
    
    /**
     * 发送消息分发事件
     *
     * @param payload 消息内容
     */
    public <T> void sendMessageDistributedEvent(T payload) {
        RabbitMessage<T> message = RabbitMessage.create(
                RabbitMessageEvent.MESSAGE_DISTRIBUTED, payload);
        
        sendMessage(RabbitMessageEvent.EXCHANGE_MESSAGE, 
                   RabbitMessageEvent.ROUTING_KEY_MESSAGE_DISTRIBUTE, message);
    }
    
    /**
     * 发送消息过期事件
     *
     * @param payload 消息内容
     */
    public <T> void sendMessageExpiredEvent(T payload) {
        RabbitMessage<T> message = RabbitMessage.create(
                RabbitMessageEvent.MESSAGE_EXPIRED, payload);
        
        sendMessage(RabbitMessageEvent.EXCHANGE_MESSAGE, 
                   RabbitMessageEvent.ROUTING_KEY_MESSAGE_EXPIRE, message);
    }
    
    /**
     * 发送延迟的消息分发事件
     *
     * @param payload 消息内容
     * @param delayMillis 延迟毫秒数
     */
    public <T> void sendDelayedMessageDistributedEvent(T payload, long delayMillis) {
        RabbitMessage<T> message = RabbitMessage.create(
                RabbitMessageEvent.MESSAGE_DISTRIBUTED, payload);
        
        sendDelayedMessage(RabbitMessageEvent.EXCHANGE_MESSAGE, 
                          RabbitMessageEvent.ROUTING_KEY_MESSAGE_DISTRIBUTE, 
                          message, delayMillis);
    }
}