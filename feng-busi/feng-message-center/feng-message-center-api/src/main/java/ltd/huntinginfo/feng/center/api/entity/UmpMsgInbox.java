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
 * 收件箱表实体类
 * 对应表：ump_msg_inbox
 * 作用：存储个人或小范围消息的分发记录，采用写扩散模式
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_inbox", autoResultMap = true)
@Schema(description = "收件箱表实体")
public class UmpMsgInbox implements Serializable {

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

    @Schema(description = "接收者类型:USER/DEPT/ORG/AREA")
    @TableField("receiver_type")
    private String receiverType;

    @Schema(description = "接收者名称")
    @TableField("receiver_name")
    private String receiverName;

    @Schema(description = "接收者单位代码")
    @TableField("receiver_org_code")
    private String receiverOrgCode;

    @Schema(description = "接收者单位名称")
    @TableField("receiver_org_name")
    private String receiverOrgName;

    @Schema(description = "分发方式:INBOX-收件箱 BROADCAST-广播")
    @TableField("distribute_mode")
    private String distributeMode;

    @Schema(description = "分发时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("distribute_time")
    private LocalDateTime distributeTime;

    @Schema(description = "接收状态:PENDING-待接收 RECEIVED-已接收 FAILED-接收失败")
    @TableField("receive_status")
    private String receiveStatus;

    @Schema(description = "接收时间")
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

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "推送次数")
    @TableField("push_count")
    private Integer pushCount;

    @Schema(description = "最后推送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_push_time")
    private LocalDateTime lastPushTime;

    @Schema(description = "推送状态")
    @TableField("push_status")
    private String pushStatus;
}