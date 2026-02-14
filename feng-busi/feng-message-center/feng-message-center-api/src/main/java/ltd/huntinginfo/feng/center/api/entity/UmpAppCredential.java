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
import java.util.List;
import java.util.Map;

/**
 * 应用认证凭证表实体类
 * 对应表：ump_app_credential
 * 作用：管理所有接入统一消息平台的业务系统，包括直接接入和通过代理平台接入的应用
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_app_credential", autoResultMap = true)
@Schema(description = "应用认证凭证表实体")
public class UmpAppCredential implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "应用唯一标识")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "AES加密后的应用密钥")
    @TableField("app_secret")
    private String appSecret;

    @Schema(description = "应用名称")
    @TableField("app_name")
    private String appName;

    @Schema(description = "应用类型:DIRECT-直接接入 AGENT-代理接入")
    @TableField("app_type")
    private String appType;

    @Schema(description = "应用描述")
    @TableField("app_desc")
    private String appDesc;

    @Schema(description = "应用图标地址")
    @TableField("app_icon")
    private String appIcon;

    @Schema(description = "应用首页地址")
    @TableField("home_url")
    private String homeUrl;

    @Schema(description = "默认推送方式:PUSH-推送 POLL-轮询")
    @TableField("default_push_mode")
    private String defaultPushMode;

    @Schema(description = "默认回调地址")
    @TableField("callback_url")
    private String callbackUrl;
    
    @Schema(description = "回调配置(JSON)")
    @TableField(value = "callback_config")
    private String callbackConfig;

    @Schema(description = "回调认证模式")
    @TableField("callback_auth_mode")
    private String callbackAuthMode;

    @Schema(description = "API调用速率限制(次/分钟)")
    @TableField("rate_limit")
    private Integer rateLimit;

    @Schema(description = "最大消息大小(字节)")
    @TableField("max_msg_size")
    private Integer maxMsgSize;

    @Schema(description = "IP白名单(JSON数组)")
    @TableField(value = "ip_whitelist", typeHandler = JacksonTypeHandler.class)
    private List<String> ipWhitelist;

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "密钥过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("secret_expire_time")
    private LocalDateTime secretExpireTime;

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