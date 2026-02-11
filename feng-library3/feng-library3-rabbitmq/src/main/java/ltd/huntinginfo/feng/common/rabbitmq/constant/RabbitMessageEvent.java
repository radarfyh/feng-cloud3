package ltd.huntinginfo.feng.common.rabbitmq.constant;

/**
 * RabbitMQ消息事件常量
 */
public class RabbitMessageEvent {
    
    // 消息中心相关事件
    public static final String MESSAGE_CREATED = "MESSAGE_CREATED";
    public static final String MESSAGE_STATUS_UPDATED = "MESSAGE_STATUS_UPDATED";
    public static final String MESSAGE_DISTRIBUTED = "MESSAGE_DISTRIBUTED";
    public static final String MESSAGE_SENT = "MESSAGE_SENT";
    public static final String MESSAGE_READ = "MESSAGE_READ";
    public static final String MESSAGE_EXPIRED = "MESSAGE_EXPIRED";
    
    // 队列配置
    public static final String QUEUE_MESSAGE_CREATED = "queue.message.created";
    public static final String QUEUE_MESSAGE_STATUS = "queue.message.status";
    public static final String QUEUE_MESSAGE_DISTRIBUTE = "queue.message.distribute";
    public static final String QUEUE_MESSAGE_EXPIRE = "queue.message.expire";
    
    public static final String EXCHANGE_MESSAGE = "exchange.message";
    public static final String ROUTING_KEY_MESSAGE_CREATED = "routing.key.message.created";
    public static final String ROUTING_KEY_MESSAGE_STATUS = "routing.key.message.status";
    public static final String ROUTING_KEY_MESSAGE_DISTRIBUTE = "routing.key.message.distribute";
    public static final String ROUTING_KEY_MESSAGE_EXPIRE = "routing.key.message.expire";
    
    // 重试次数
    public static final int MAX_RETRY_COUNT = 5;
    
    // 延迟消息配置
    public static final String EXCHANGE_DELAYED = "delayed.exchange";
    public static final String QUEUE_DELAYED_MESSAGE = "queue.delayed.message";
    public static final String ROUTING_KEY_DELAYED = "delayed.routing.key";
}