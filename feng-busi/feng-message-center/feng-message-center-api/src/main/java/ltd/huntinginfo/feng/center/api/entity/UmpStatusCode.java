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
 * 消息状态码表实体类
 * 对应表：ump_status_code
 * 作用：统一管理系统中所有状态码，便于状态的管理和国际化
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ump_status_code")
@Schema(description = "消息状态码表实体")
public class UmpStatusCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "状态码")
    @TableField("status_code")
    private String statusCode;

    @Schema(description = "状态名称")
    @TableField("status_name")
    private String statusName;

    @Schema(description = "状态描述")
    @TableField("status_desc")
    private String statusDesc;

    @Schema(description = "分类:MESSAGE-消息 CALLBACK-回调 QUEUE-队列")
    @TableField("category")
    private String category;

    @Schema(description = "父状态码")
    @TableField("parent_code")
    private String parentCode;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "是否为最终状态:0-否 1-是")
    @TableField("is_final")
    private Integer isFinal;

    @Schema(description = "是否可重试:0-否 1-是")
    @TableField("can_retry")
    private Integer canRetry;

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}