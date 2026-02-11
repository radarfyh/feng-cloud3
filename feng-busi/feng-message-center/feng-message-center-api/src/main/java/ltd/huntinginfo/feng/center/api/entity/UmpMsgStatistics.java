package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消息统计表实体类
 * 对应表：ump_msg_statistics
 * 作用：按天统计消息发送和接收情况，用于业务分析和监控
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ump_msg_statistics")
@Schema(description = "消息统计表实体")
public class UmpMsgStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "统计ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "统计日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("stat_date")
    private LocalDate statDate;

    @Schema(description = "应用标识")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "消息类型")
    @TableField("msg_type")
    private String msgType;

    @Schema(description = "发送数量")
    @TableField("send_count")
    private Integer sendCount;

    @Schema(description = "发送成功数量")
    @TableField("send_success_count")
    private Integer sendSuccessCount;

    @Schema(description = "发送失败数量")
    @TableField("send_failed_count")
    private Integer sendFailedCount;

    @Schema(description = "接收数量")
    @TableField("receive_count")
    private Integer receiveCount;

    @Schema(description = "阅读数量")
    @TableField("read_count")
    private Integer readCount;

    @Schema(description = "平均处理时间(毫秒)")
    @TableField("avg_process_time")
    private Integer avgProcessTime;

    @Schema(description = "平均接收时间(毫秒)")
    @TableField("avg_receive_time")
    private Integer avgReceiveTime;

    @Schema(description = "平均阅读时间(毫秒)")
    @TableField("avg_read_time")
    private Integer avgReadTime;

    @Schema(description = "错误数量")
    @TableField("error_count")
    private Integer errorCount;

    @Schema(description = "重试数量")
    @TableField("retry_count")
    private Integer retryCount;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}