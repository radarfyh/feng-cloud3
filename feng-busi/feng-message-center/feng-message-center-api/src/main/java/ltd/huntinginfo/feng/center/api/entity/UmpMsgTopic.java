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
 * 消息主题表实体类
 * 对应表：ump_msg_topic
 * 作用：管理消息主题，用于消息的分类和路由
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_topic", autoResultMap = true)
@Schema(description = "消息主题表实体")
public class UmpMsgTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主题ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "主题代码")
    @TableField("topic_code")
    private String topicCode;

    @Schema(description = "主题名称")
    @TableField("topic_name")
    private String topicName;

    @Schema(description = "主题类型:SYSTEM-系统主题 CUSTOM-自定义主题")
    @TableField("topic_type")
    private String topicType;

    @Schema(description = "主题描述")
    @TableField("description")
    private String description;

    @Schema(description = "默认消息类型")
    @TableField("default_msg_type")
    private String defaultMsgType;

    @Schema(description = "默认优先级")
    @TableField("default_priority")
    private Integer defaultPriority;

    @Schema(description = "路由规则配置(JSON)")
    @TableField(value = "routing_rules", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> routingRules;

    @Schema(description = "订阅者数量")
    @TableField("subscriber_count")
    private Integer subscriberCount;

    @Schema(description = "最大订阅者数量")
    @TableField("max_subscribers")
    private Integer maxSubscribers;

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记:0-正常 1-删除")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}