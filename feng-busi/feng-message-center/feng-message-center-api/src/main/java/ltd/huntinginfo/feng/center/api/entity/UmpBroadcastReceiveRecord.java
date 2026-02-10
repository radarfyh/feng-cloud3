package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广播消息接收记录表实体类
 * 对应表：ump_broadcast_receive_record
 * 作用：记录重要广播消息的精准送达与阅读状态，用于关键消息审计
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_broadcast_receive_record", autoResultMap = true)
@Schema(description = "广播消息接收记录表实体")
public class UmpBroadcastReceiveRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "广播ID")
    @TableField("broadcast_id")
    private String broadcastId;

    @Schema(description = "接收者ID")
    @TableField("receiver_id")
    private String receiverId;

    @Schema(description = "接收者类型:USER/DEPT/ORG/AREA")
    @TableField("receiver_type")
    private String receiverType;

    @Schema(description = "接收状态:PENDING-待送达 DELIVERED-已送达 FAILED-送达失败")
    @TableField("receive_status")
    private String receiveStatus;

    @Schema(description = "接收/送达时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @Schema(description = "阅读状态:0-未读 1-已读")
    @TableField("read_status")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("read_time")
    private LocalDateTime readTime;

    @Schema(description = "记录创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "最后状态更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}