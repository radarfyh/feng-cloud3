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
 * 回调记录表实体类
 * 对应表：ump_msg_callback
 * 作用：记录所有回调请求的执行情况，确保回调的可靠性和可追溯性
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_callback", autoResultMap = true)
@Schema(description = "回调记录表实体")
public class UmpMsgCallback implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息ID")
    @TableField("msg_id")
    private String msgId;

    @Schema(description = "接收者ID")
    @TableField("receiver_id")
    private String receiverId;

    @Schema(description = "回调地址")
    @TableField("callback_url")
    private String callbackUrl;

    @Schema(description = "回调方法")
    @TableField("callback_method")
    private String callbackMethod;

    @Schema(description = "回调数据(JSON)")
    @TableField(value = "callback_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> callbackData;

    @Schema(description = "回调签名")
    @TableField("signature")
    private String signature;

    @Schema(description = "回调ID")
    @TableField("callback_id")
    private String callbackId;

    @Schema(description = "状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败")
    @TableField("status")
    private String status;

    @Schema(description = "HTTP状态码")
    @TableField("http_status")
    private Integer httpStatus;

    @Schema(description = "响应内容")
    @TableField("response_body")
    private String responseBody;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @Schema(description = "响应时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("response_time")
    private LocalDateTime responseTime;

    @Schema(description = "耗时(ms)")
    @TableField("cost_time")
    private Integer costTime;

    @Schema(description = "重试次数")
    @TableField("retry_count")
    private Integer retryCount;

    @Schema(description = "下次重试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("next_retry_time")
    private LocalDateTime nextRetryTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}