package ltd.huntinginfo.feng.common.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

/**
 * RabbitMQ 队列、交换机、绑定配置（Spring Boot 4 + Jackson 3）
 * <p>
 * 设计原则：
 * - 队列名、交换机名、路由键严格使用 MqMessageEventConstants 常量
 * - 移除所有队列级 TTL，改为消息级 TTL（生产者设置）
 * - 移除队列 maxLength 硬编码，由 RabbitMQ 磁盘/内存阈值控制
 * - 独立死信交换机，各业务队列绑定 DLX + 明确路由键
 * - 废弃专用延迟交换机，使用主交换机 + x-delay 头 + 业务延迟队列
 * </p>
 */
@Configuration
public class RabbitQueueConfig {

    // -------------------- 1. 交换机 --------------------
    @Bean
    public TopicExchange messageExchange() {
        return ExchangeBuilder.topicExchange(MqMessageEventConstants.Exchanges.MESSAGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(MqMessageEventConstants.Exchanges.DLX)
                .durable(true)
                .build();
    }

    // -------------------- 2. 消息状态队列 --------------------
    @Bean
    public Queue messageReceivedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_RECEIVED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageDistributedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageSentQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_SENT)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageReadQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_READ)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageExpiredQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageFailedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_FAILED)
                .build();
    }

    // -------------------- 3. 异步任务队列 --------------------
    @Bean
    public Queue messageSendTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_SEND_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.TASK_RETRY)
                .build();
    }

    @Bean
    public Queue messageCallbackTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_CALLBACK_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.TASK_RETRY)
                .build();
    }

    @Bean
    public Queue messageRetryTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue broadcastDispatchTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.BROADCAST_DISPATCH_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.TASK_RETRY)
                .build();
    }

    // -------------------- 4. 延迟队列 --------------------
    @Bean
    public Queue delayedSendQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.DELAYED_SEND)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    @Bean
    public Queue delayedExpireQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.DELAYED_EXPIRE)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED)
                .build();
    }

    // -------------------- 5. 绑定关系 --------------------
    @Bean
    public Binding messageReceivedBinding(Queue messageReceivedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageReceivedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_RECEIVED);
    }

    @Bean
    public Binding messageDistributedBinding(Queue messageDistributedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageDistributedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_DISTRIBUTED);
    }

    @Bean
    public Binding messageSentBinding(Queue messageSentQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageSentQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_SENT);
    }

    @Bean
    public Binding messageReadBinding(Queue messageReadQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageReadQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_READ);
    }

    @Bean
    public Binding messageExpiredBinding(Queue messageExpiredQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageExpiredQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_EXPIRED);
    }

    @Bean
    public Binding messageFailedBinding(Queue messageFailedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageFailedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.MESSAGE_FAILED);
    }

    @Bean
    public Binding messageSendTaskBinding(Queue messageSendTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageSendTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.TASK_SEND);
    }

    @Bean
    public Binding messageCallbackTaskBinding(Queue messageCallbackTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageCallbackTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.TASK_CALLBACK);
    }

    @Bean
    public Binding messageRetryTaskBinding(Queue messageRetryTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageRetryTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.TASK_RETRY);
    }

    @Bean
    public Binding broadcastDispatchTaskBinding(Queue broadcastDispatchTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(broadcastDispatchTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.TASK_BROADCAST_DISPATCH);
    }

    @Bean
    public Binding delayedSendBinding(Queue delayedSendQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(delayedSendQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.DELAYED_SEND);
    }

    @Bean
    public Binding delayedExpireBinding(Queue delayedExpireQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(delayedExpireQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.DELAYED_EXPIRE);
    }
}