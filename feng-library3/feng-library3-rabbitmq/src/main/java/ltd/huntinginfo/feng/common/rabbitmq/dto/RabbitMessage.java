package ltd.huntinginfo.feng.common.rabbitmq.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * RabbitMQ消息实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMessage<T> {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 消息体
     */
    private T payload;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 扩展参数
     */
    private Map<String, Object> extParams;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 创建消息
     */
    public static <T> RabbitMessage<T> create(String messageType, T payload) {
        return RabbitMessage.<T>builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .messageType(messageType)
                .payload(payload)
                .sendTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }
}