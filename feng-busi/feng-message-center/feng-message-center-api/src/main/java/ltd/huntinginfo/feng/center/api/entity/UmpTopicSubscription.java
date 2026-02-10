package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 主题订阅表实体类
 * 对应表：ump_topic_subscription
 * 作用：记录业务系统对消息主题的订阅关系
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_topic_subscription", autoResultMap = true)
@Schema(description = "主题订阅表实体")
public class UmpTopicSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订阅ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "主题代码")
    @TableField("topic_code")
    private String topicCode;

    @Schema(description = "应用标识")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "订阅配置(JSON)")
    @TableField(value = "subscription_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> subscriptionConfig;

    @Schema(description = "回调地址")
    @TableField("callback_url")
    private String callbackUrl;

    @Schema(description = "推送方式:PUSH-推送 POLL-轮询")
    @TableField("push_mode")
    private String pushMode;

    @Schema(description = "状态:0-取消订阅 1-已订阅")
    @TableField("status")
    private Integer status;

    @Schema(description = "订阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("subscribe_time")
    private LocalDateTime subscribeTime;

    @Schema(description = "取消订阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("unsubscribe_time")
    private LocalDateTime unsubscribeTime;

    @Schema(description = "接收消息数量")
    @TableField("message_count")
    private Integer messageCount;

    @Schema(description = "最后消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    @Schema(description = "逻辑删除标记:0-正常 1-删除")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}