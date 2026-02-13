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
        /** 分发中 */
        public static final String MESSAGE_DISTRIBUTING = "ump.queue.message.distributing";
        /** 消息已分发到收件箱/广播筒 */
        public static final String MESSAGE_DISTRIBUTED = "ump.queue.message.distributed";
        /** 分发失败 */
        public static final String MESSAGE_DIST_FAILED = "ump.queue.message.dist.failed";
        /** 已推送 */
        public static final String MESSAGE_PUSHED = "ump.queue.message.pushed";
        /** 推送失败 */
        public static final String MESSAGE_PUSH_FAILED = "ump.queue.message.push.failed";
        /** 业务已接收 */
        public static final String MESSAGE_BIZ_RECEIVED = "ump.queue.message.biz.received";
        /** 待拉取 */
        public static final String MESSAGE_POLL = "ump.queue.message.poll";
        /** 业务已拉取 */
        public static final String MESSAGE_BIZ_PULLED = "ump.queue.message.biz.pulled";
        /** 拉取失败 */
        public static final String MESSAGE_POLL_FAILED = "ump.queue.message.poll.failed";
        /** 消息已被接收方阅读 */
        public static final String MESSAGE_READ = "ump.queue.message.read";
        /** 消息已过期 */
        public static final String MESSAGE_EXPIRED = "ump.queue.message.expired";

        // ---------- 异步任务队列 ----------
        /** 分发任务 */
        public static final String MESSAGE_DISTRIBUTE_TASK = "ump.queue.task.distribute";
        /** 推送任务 */
        public static final String MESSAGE_PUSH_TASK = "ump.queue.task.push";
        /** 重试任务（所有需要重试的任务统一入口） */
        public static final String MESSAGE_RETRY_TASK = "ump.queue.task.retry";

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
        public static final String MESSAGE_DISTRIBUTING = "ump.event.message.distributing";
        public static final String MESSAGE_DISTRIBUTED = "ump.event.message.distributed";
        public static final String MESSAGE_DIST_FAILED = "ump.event.message.dist.failed";
        public static final String MESSAGE_PUSHED = "ump.event.message.pushed";
        public static final String MESSAGE_PUSH_FAILED = "ump.event.message.push.failed";
        public static final String MESSAGE_BIZ_RECEIVED = "ump.event.message.biz.received";
        public static final String MESSAGE_POLL = "ump.event.message.poll";
        public static final String MESSAGE_BIZ_PULLED = "ump.event.message.biz.pulled";
        public static final String MESSAGE_POLL_FAILED = "ump.event.message.poll.failed";
        public static final String MESSAGE_READ = "ump.event.message.read";
        public static final String MESSAGE_EXPIRED = "ump.event.message.expired";

        // ---------- 任务路由 ----------
        public static final String TASK_DISTRIBUTED = "ump.task.distribute";
        public static final String TASK_PUSH = "ump.task.push";
        public static final String TASK_RETRY = "ump.task.retry";

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
        /** 分发失败（永久） */
        public static final String DIST_FAILED = "DIST_FAILED";
        /** 已推送（等待业务确认） */
        public static final String PUSHED = "PUSHED";
        /** 推送失败（永久） */
        public static final String PUSH_FAILED = "PUSH_FAILED";
        /** 业务系统已接收 */
        public static final String BIZ_RECEIVED = "BIZ_RECEIVED";
        /** 待拉取 */
        public static final String POLL = "POLL";
        /** 业务系统已拉取 */
        public static final String BIZ_PULLED = "BIZ_PULLED";
        /** 拉取超时/过期（永久） */
        public static final String POLL_FAILED = "POLL_FAILED";
        /** 已读 */
        public static final String READ = "READ";
        /** 已过期 */
        public static final String EXPIRED = "EXPIRED";

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
     * 消息头中使用的键名
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
    
    // ============================ 推送方式（与数据库 ump_app_credential.default_push_mode 一致） ============================
    public static final class PushModes {
        /** 主动推送 */
        public static final String PUSH = "PUSH";
        /** 等待轮询 */
        public static final String POLL = "POLL";

        private PushModes() {}
    }

    // ============================ 接收者类型（与数据库 ump_msg_main.receiver_type 一致） ============================
    public static final class ReceiverTypes {
        /** 个人 */
        public static final String USER = "USER";
        /** 部门 */
        public static final String DEPT = "DEPT";
        /** 组织 */
        public static final String ORG = "ORG";
        /** 区域 */
        public static final String AREA = "AREA";
        /** 全体 */
        public static final String ALL = "ALL";

        private ReceiverTypes() {}
    }

    // ============================ 消息队列表（ump_msg_queue）队列类型 ============================
    public static final class QueueTaskTypes {
        /** 消息分发任务 */
        public static final String DISTRIBUTE = "DISTRIBUTE";
        /** 消息推送任务 */
        public static final String PUSH = "PUSH";
        /** 重试任务 */
        public static final String RETRY = "RETRY";

        private QueueTaskTypes() {}
    }

    // ============================ 消息队列表（ump_msg_queue）队列名称 ============================
    public static final class QueueNames {
        /** 消息分发队列 */
        public static final String MESSAGE_DISTRIBUTE = "message_distribute";
        /** 消息推送队列 */
        public static final String MESSAGE_PUSH = "message_push";
        /** 重试队列 */
        public static final String MESSAGE_RETRY = "message_retry";

        private QueueNames() {}
    }

    // ============================ 收件箱分发方式（ump_msg_inbox.distribute_mode） ============================
    public static final class DistributeModes {
        /** 收件箱模式（写扩散） */
        public static final String INBOX = "INBOX";
        /** 广播模式（读扩散） */
        public static final String BROADCAST = "BROADCAST";

        private DistributeModes() {}
    }

    // ============================ 广播类型（ump_msg_broadcast.broadcast_type） ============================
    public static final class BroadcastTypes {
        public static final String ALL = "ALL";
        public static final String AREA = "AREA";
        public static final String ORG = "ORG";
        public static final String DEPT = "DEPT";
        public static final String CUSTOM = "CUSTOM";

        private BroadcastTypes() {}
    }

    // ============================ 任务优先级（仅作参考，具体数值可配置） ============================
    public static final class TaskPriorities {
        /** 最高优先级（如消息分发） */
        public static final int HIGHEST = 1;
        /** 高优先级 */
        public static final int HIGH = 3;
        /** 默认优先级 */
        public static final int DEFAULT = 5;
        /** 低优先级（如推送任务） */
        public static final int LOW = 7;
        /** 最低优先级 */
        public static final int LOWEST = 10;

        private TaskPriorities() {}
    }

    // ============================ 重试相关默认值 ============================
    public static final class RetryDefaults {
        /** 默认最大重试次数（与系统配置 queue.max.retry 对齐） */
        public static final int MAX_RETRY = 3;
        /** 默认初始重试间隔（毫秒） */
        public static final long INITIAL_INTERVAL = 1000;
        /** 默认重试间隔倍数 */
        public static final double MULTIPLIER = 2.0;
        /** 默认最大重试间隔（毫秒） */
        public static final long MAX_INTERVAL = 10000;

        private RetryDefaults() {}
    }

    // ============================ 任务数据中的字段名 ============================
    public static final class TaskDataKeys {
        public static final String MESSAGE_ID = "messageId";  // 对应消息主表的id
        public static final String INBOX_ID = "inboxId";  // 新增收件箱的记录ID
        public static final String BROADCAST_ID = "broadcastId";  // 新增广播信息筒的记录ID
        public static final String TASK_ID = "taskId";
        public static final String RECEIVER_ID = "receiverId";
        public static final String RECEIVER_TYPE = "receiverType";
        public static final String RECEIVER_NAME = "receiverName";
        public static final String RECEIVER_COUNT = "receiverCount";
        public static final String PUSH_MODE = "pushMode"; // ump_msg_main.push_mode/ump_app_credential.default_push_mode
        public static final String CALLBACK_URL = "callbackUrl";  // 新增APP回调地址，来自ump_msg_main.callback_url/ump_app_credential.callback_url
        public static final String ESTIMATED_COUNT = "estimatedCount";
        public static final String STATUS = "status";
        public static final String DELAYED = "delayed";
        public static final String REASON = "reason";
        public static final String OLD_STATUS = "oldStatus";

        private TaskDataKeys() {}
    }
    
    // ============================ 业务阈值 ============================
    public static final class Thresholds {
        /** 
         * 分发策略阈值：接收者数量超过此值使用广播模式（读扩散）
         * 默认值：1000
         */
        public static final int BROADCAST_THRESHOLD = 1000;

        /** 
         * 部门广播特殊阈值：接收者数量超过此值标记为大范围部门广播
         * 默认值：5000
         */
        public static final int DEPT_MASS_THRESHOLD = 5000;

        private Thresholds() {}
    }

    private MqMessageEventConstants() {}
}