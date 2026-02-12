package ltd.huntinginfo.feng.common.core.mq;

/**
 * RabbitMQ 核心常量定义
 * <p>
 * 设计原则：
 * 1. 与 Kafka Topic 命名风格统一，所有资源名称采用小写点分格式，以 "ump" 为顶级域。
 * 2. 队列与消息状态一一对应，一个队列只处理一种状态的消息。
 * 3. 事件类型与数据库 ump_msg_main.status 字段值完全一致。
 * 4. 所有常量均以 "ump." 为前缀，避免与第三方系统冲突。
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
public final class MqMessageEventConstants {

    // ============================ 交换机名称 ============================
    public static final class Exchanges {
        /**
         * 主业务交换机（Topic类型）
         * 所有业务消息均通过此交换机发送，根据路由键分发到对应队列
         */
        public static final String MESSAGE = "ump.exchange.message";

        /**
         * 死信交换机（Topic类型）
         * 消息重试超过最大次数、或被拒绝且不重新入队时，转发至此交换机
         */
        public static final String DLX = "ump.exchange.dlx";

        private Exchanges() {}
    }

    // ============================ 队列名称 ============================
    public static final class Queues {
        // ---------- 消息状态流转队列（与 ump_msg_main.status 一一对应）----------
        /** 消息已接收，待验证/分发 */
        public static final String MESSAGE_RECEIVED = "ump.queue.message.received";
        /** 消息已分发到收件箱/广播筒 */
        public static final String MESSAGE_DISTRIBUTED = "ump.queue.message.distributed";
        /** 消息已发送给接收方（推送成功） */
        public static final String MESSAGE_SENT = "ump.queue.message.sent";
        /** 消息已被接收方阅读 */
        public static final String MESSAGE_READ = "ump.queue.message.read";
        /** 消息已过期 */
        public static final String MESSAGE_EXPIRED = "ump.queue.message.expired";
        /** 消息处理失败（最终状态） */
        public static final String MESSAGE_FAILED = "ump.queue.message.failed";

        // ---------- 异步任务队列 ----------
        /** 消息推送任务（调用接收方回调） */
        public static final String MESSAGE_SEND_TASK = "ump.queue.task.send";
        /** 回调执行任务（调用业务系统回调地址） */
        public static final String MESSAGE_CALLBACK_TASK = "ump.queue.task.callback";
        /** 重试任务（所有需要重试的任务统一入口） */
        public static final String MESSAGE_RETRY_TASK = "ump.queue.task.retry";
        /** 广播分发任务（大范围消息写广播筒） */
        public static final String BROADCAST_DISPATCH_TASK = "ump.queue.task.broadcast.dispatch";

        // ---------- 延迟队列（按业务细分）----------
        /** 延迟发送队列（如定时推送） */
        public static final String DELAYED_SEND = "ump.queue.delayed.send";
        /** 延迟过期处理队列 */
        public static final String DELAYED_EXPIRE = "ump.queue.delayed.expire";

        private Queues() {}
    }

    // ============================ 路由键 ============================
    public static final class RoutingKeys {
        // ---------- 事件路由（与队列绑定）----------
        public static final String MESSAGE_RECEIVED = "ump.event.message.received";
        public static final String MESSAGE_DISTRIBUTED = "ump.event.message.distributed";
        public static final String MESSAGE_SENT = "ump.event.message.sent";
        public static final String MESSAGE_READ = "ump.event.message.read";
        public static final String MESSAGE_EXPIRED = "ump.event.message.expired";
        public static final String MESSAGE_FAILED = "ump.event.message.failed";

        // ---------- 任务路由 ----------
        public static final String TASK_SEND = "ump.task.send";
        public static final String TASK_CALLBACK = "ump.task.callback";
        public static final String TASK_RETRY = "ump.task.retry";
        public static final String TASK_BROADCAST_DISPATCH = "ump.task.broadcast.dispatch";

        // ---------- 延迟路由 ----------
        /** 延迟发送，配合 x-delay 头使用，通过主交换机发送 */
        public static final String DELAYED_SEND = "ump.event.delayed.send";
        /** 延迟过期处理 */
        public static final String DELAYED_EXPIRE = "ump.event.delayed.expire";

        private RoutingKeys() {}
    }

    // ============================ 事件类型 ============================
    /**
     * 消息事件类型，与数据库 ump_msg_main.status 字段值完全一致
     * 用于 RabbitMessage.messageType 字段
     */
    public static final class EventTypes {
        /** 已接收 */
        public static final String RECEIVED = "RECEIVED";
        /** 分发中 */
        public static final String DISTRIBUTING = "DISTRIBUTING";
        /** 已分发 */
        public static final String DISTRIBUTED = "DISTRIBUTED";
        /** 发送中 */
        public static final String SENDING = "SENDING";
        /** 已发送 */
        public static final String SENT = "SENT";
        /** 已读 */
        public static final String READ = "READ";
        /** 已过期 */
        public static final String EXPIRED = "EXPIRED";
        /** 失败 */
        public static final String FAILED = "FAILED";

        private EventTypes() {}
    }

    // ============================ 业务类型 ============================
    /**
     * 消息业务类型，对应数据库 ump_msg_main.msg_type 字段
     */
    public static final class BusinessTypes {
        /** 通知 */
        public static final String NOTICE = "NOTICE";
        /** 提醒 */
        public static final String ALERT = "ALERT";
        /** 业务消息 */
        public static final String BIZ = "BIZ";
        /** 代理消息 */
        public static final String AGENT = "AGENT";

        private BusinessTypes() {}
    }

    // ============================ 配置键 ============================
    /**
     * 配置中心键名，用于动态获取值，避免硬编码
     */
    public static final class ConfigKeys {
        /** MQ切换 */
        public static final String MQ_TYPE = "mc.mq.type";

        private ConfigKeys() {}
    }

    // ============================ 消息头字段 ============================
    /**
     * RabbitMQ 消息头中使用的键名
     */
    public static final class Headers {
        /** 消息ID（全局唯一） */
        public static final String MESSAGE_ID = "ump_message_id";
        /** 事件类型（RECEIVED/SENT等） */
        public static final String EVENT_TYPE = "ump_event_type";
        /** 业务类型（NOTICE/ALERT等） */
        public static final String BUSINESS_TYPE = "ump_business_type";
        /** 重试次数 */
        public static final String RETRY_COUNT = "ump_retry_count";
        /** 延迟毫秒数（x-delay 插件专用） */
        public static final String X_DELAY = "x-delay";

        private Headers() {}
    }
    
    // ============================ 消费者组 ============================
    public static final class ConsumerGroups {
        /** 消息状态处理组 */
        public static final String MESSAGE_STATE = "ump-consumer-message-state";
        /** 异步任务处理组 */
        public static final String TASK = "ump-consumer-task";
        /** 延迟任务处理组 */
        public static final String DELAYED = "ump-consumer-delayed";

        private ConsumerGroups() {}
    }

    private MqMessageEventConstants() {}
}