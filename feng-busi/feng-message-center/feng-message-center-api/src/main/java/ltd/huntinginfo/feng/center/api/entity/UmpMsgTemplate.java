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
 * 消息模板表实体类
 * 对应表：ump_msg_template
 * 作用：管理消息模板，支持模板化消息发送
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_template", autoResultMap = true)
@Schema(description = "消息模板表实体")
public class UmpMsgTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "模板代码")
    @TableField("template_code")
    private String templateCode;

    @Schema(description = "模板名称")
    @TableField("template_name")
    private String templateName;

    @Schema(description = "模板类型")
    @TableField("template_type")
    private String templateType;

    @Schema(description = "标题模板")
    @TableField("title_template")
    private String titleTemplate;

    @Schema(description = "内容模板")
    @TableField("content_template")
    private String contentTemplate;

    @Schema(description = "模板变量定义(JSON)")
    @TableField(value = "variables", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> variables;

    @Schema(description = "默认优先级")
    @TableField("default_priority")
    private Integer defaultPriority;

    @Schema(description = "默认推送方式")
    @TableField("default_push_mode")
    private String defaultPushMode;

    @Schema(description = "默认回调地址")
    @TableField("default_callback_url")
    private String defaultCallbackUrl;

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

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记:0-正常 1-删除")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}