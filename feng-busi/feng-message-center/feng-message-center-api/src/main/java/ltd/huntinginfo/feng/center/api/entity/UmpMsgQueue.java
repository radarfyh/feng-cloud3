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
 * 消息队列表实体类
 * 对应表：ump_msg_queue
 * 作用：存储待处理的异步任务，实现消息的异步处理和削峰填谷
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_queue", autoResultMap = true)
@Schema(description = "消息队列表实体")
public class UmpMsgQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "队列类型:SEND-发送 DISTRIBUTE-分发 CALLBACK-回调 RETRY-重试")
    @TableField("queue_type")
    private String queueType;

    @Schema(description = "队列名称")
    @TableField("queue_name")
    private String queueName;

    @Schema(description = "消息ID")
    @TableField("msg_id")
    private String msgId;

    @Schema(description = "任务数据(JSON)")
    @TableField(value = "task_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> taskData;

    @Schema(description = "优先级1-10,数字越小优先级越高")
    @TableField("priority")
    private Integer priority;

    @Schema(description = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("execute_time")
    private LocalDateTime executeTime;

    @Schema(description = "最大重试次数")
    @TableField("max_retry")
    private Integer maxRetry;

    @Schema(description = "当前重试次数")
    @TableField("current_retry")
    private Integer currentRetry;

    @Schema(description = "状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败")
    @TableField("status")
    private String status;

    @Schema(description = "工作者ID")
    @TableField("worker_id")
    private String workerId;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "结果代码")
    @TableField("result_code")
    private String resultCode;

    @Schema(description = "结果消息")
    @TableField("result_message")
    private String resultMessage;

    @Schema(description = "错误堆栈")
    @TableField("error_stack")
    private String errorStack;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}