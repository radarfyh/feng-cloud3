package ltd.huntinginfo.feng.common.core.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * RabbitMQ 消息统一封装体
 * <p>
 * 特性：
 * - 泛型 payload，支持任意业务对象
 * - 自动生成全局消息ID
 * - 包含事件类型（与数据库状态码一致）、业务类型（NOTICE/ALERT等）
 * - 支持扩展参数与重试计数（重试计数建议由消费者维护，生产者一般传0）
 * </p>
 *
 * @author feng-cloud3
 * @since 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqMessage<T> {

    /**
     * 全局唯一消息ID，用于链路追踪与幂等
     */
    private String messageId;

    /**
     * 事件类型，与数据库 ump_msg_main.status 字段值一致
     * 取值见 {@link RabbitMessageEvent.EventTypes}
     */
    private String messageType;

    /**
     * 业务类型，对应数据库 ump_msg_main.msg_type 字段
     * 取值见 {@link RabbitMessageEvent.BusinessTypes}
     */
    private String businessType;

    /**
     * 业务消息体
     */
    private T payload;

    /**
     * 消息创建时间（ISO-8601 字符串，由 Jackson 自动序列化）
     */
    private LocalDateTime sendTime;

    /**
     * 扩展属性，用于传递额外元数据
     */
    private Map<String, Object> extParams;

    /**
     * 重试次数（仅用于消费者重试传递，生产者通常填0）
     */
    private Integer retryCount;

    /**
     * 快速构造消息（仅事件类型 + 业务数据）
     * 业务类型默认为 null，由生产者按需补充
     */
    public static <T> MqMessage<T> create(String messageType, T payload) {
        return MqMessage.<T>builder()
                .messageId(UUID.randomUUID().toString())
                .messageType(messageType)
                .payload(payload)
                .sendTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }

    /**
     * 完整构造消息（事件类型 + 业务类型 + 业务数据）
     */
    public static <T> MqMessage<T> create(String messageType, String businessType, T payload) {
        return MqMessage.<T>builder()
                .messageId(UUID.randomUUID().toString())
                .messageType(messageType)
                .businessType(businessType)
                .payload(payload)
                .sendTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }
}