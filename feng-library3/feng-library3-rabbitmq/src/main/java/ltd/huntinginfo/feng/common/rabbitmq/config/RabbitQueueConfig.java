package ltd.huntinginfo.feng.common.rabbitmq.config;

import ltd.huntinginfo.feng.common.rabbitmq.constant.RabbitMessageEvent;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ队列和交换器配置
 */
@Configuration
public class RabbitQueueConfig {

    /**
     * 消息创建队列
     */
    @Bean
    public Queue messageCreatedQueue() {
        return QueueBuilder.durable(RabbitMessageEvent.QUEUE_MESSAGE_CREATED)
                // 设置消息TTL（24小时）
                .ttl(24 * 60 * 60 * 1000)
                // 设置死信交换器
                .deadLetterExchange(RabbitMessageEvent.EXCHANGE_MESSAGE)
                .deadLetterRoutingKey(RabbitMessageEvent.ROUTING_KEY_MESSAGE_EXPIRE)
                // 设置最大长度
                .maxLength(10000)
                .build();
    }

    /**
     * 消息状态更新队列
     */
    @Bean
    public Queue messageStatusQueue() {
        return QueueBuilder.durable(RabbitMessageEvent.QUEUE_MESSAGE_STATUS)
                .ttl(12 * 60 * 60 * 1000)
                .deadLetterExchange(RabbitMessageEvent.EXCHANGE_MESSAGE)
                .deadLetterRoutingKey(RabbitMessageEvent.ROUTING_KEY_MESSAGE_EXPIRE)
                .maxLength(5000)
                .build();
    }

    /**
     * 消息分发队列
     */
    @Bean
    public Queue messageDistributeQueue() {
        return QueueBuilder.durable(RabbitMessageEvent.QUEUE_MESSAGE_DISTRIBUTE)
                .ttl(6 * 60 * 60 * 1000)
                .deadLetterExchange(RabbitMessageEvent.EXCHANGE_MESSAGE)
                .deadLetterRoutingKey(RabbitMessageEvent.ROUTING_KEY_MESSAGE_EXPIRE)
                .maxLength(20000)
                .build();
    }

    /**
     * 消息过期队列
     */
    @Bean
    public Queue messageExpireQueue() {
        return QueueBuilder.durable(RabbitMessageEvent.QUEUE_MESSAGE_EXPIRE)
                .ttl(72 * 60 * 60 * 1000)
                .maxLength(1000)
                .build();
    }

    /**
     * 消息交换器（主题交换器）
     */
    @Bean
    public TopicExchange messageExchange() {
        return ExchangeBuilder.topicExchange(RabbitMessageEvent.EXCHANGE_MESSAGE)
                .durable(true)
                .build();
    }

    /**
     * 绑定消息创建队列
     */
    @Bean
    public Binding messageCreatedBinding(Queue messageCreatedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageCreatedQueue)
                .to(messageExchange)
                .with(RabbitMessageEvent.ROUTING_KEY_MESSAGE_CREATED);
    }

    /**
     * 绑定消息状态队列
     */
    @Bean
    public Binding messageStatusBinding(Queue messageStatusQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageStatusQueue)
                .to(messageExchange)
                .with(RabbitMessageEvent.ROUTING_KEY_MESSAGE_STATUS);
    }

    /**
     * 绑定消息分发队列
     */
    @Bean
    public Binding messageDistributeBinding(Queue messageDistributeQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageDistributeQueue)
                .to(messageExchange)
                .with(RabbitMessageEvent.ROUTING_KEY_MESSAGE_DISTRIBUTE);
    }

    /**
     * 绑定消息过期队列
     */
    @Bean
    public Binding messageExpireBinding(Queue messageExpireQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageExpireQueue)
                .to(messageExchange)
                .with(RabbitMessageEvent.ROUTING_KEY_MESSAGE_EXPIRE);
    }
}