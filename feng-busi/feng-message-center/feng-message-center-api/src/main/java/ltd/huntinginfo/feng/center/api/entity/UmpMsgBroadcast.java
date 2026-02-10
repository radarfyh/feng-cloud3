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
 * 广播信息筒表实体类
 * 对应表：ump_msg_broadcast
 * 作用：存储广播消息的分发记录，采用读扩散模式
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_broadcast", autoResultMap = true)
@Schema(description = "广播信息筒表实体")
public class UmpMsgBroadcast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "广播ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息ID")
    @TableField("msg_id")
    private String msgId;

    @Schema(description = "广播类型:ALL-全体 DEPT-部门 ORG-组织 AREA-区域 CUSTOM-自定义")
    @TableField("broadcast_type")
    private String broadcastType;

    @Schema(description = "目标范围配置(JSON)")
    @TableField(value = "target_scope", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> targetScope;

    @Schema(description = "目标范围描述")
    @TableField("target_description")
    private String targetDescription;

    @Schema(description = "总接收人数")
    @TableField("total_receivers")
    private Integer totalReceivers;

    @Schema(description = "已分发数量")
    @TableField("distributed_count")
    private Integer distributedCount;

    @Schema(description = "已接收数量")
    @TableField("received_count")
    private Integer receivedCount;

    @Schema(description = "已读人数")
    @TableField("read_count")
    private Integer readCount;

    @Schema(description = "状态:DISTRIBUTING-分发中 COMPLETED-完成 PARTIAL-部分完成")
    @TableField("status")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}