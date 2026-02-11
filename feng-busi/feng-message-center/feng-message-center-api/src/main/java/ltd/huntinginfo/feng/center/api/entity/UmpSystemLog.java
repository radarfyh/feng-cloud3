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
 * 系统日志表实体类
 * 对应表：ump_system_log
 * 作用：合并认证日志和操作日志，统一记录系统行为
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_system_log", autoResultMap = true)
@Schema(description = "系统日志表实体")
public class UmpSystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID(UUID)")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "日志类型:AUTH-认证日志 OPERATION-操作日志 SYSTEM-系统日志")
    @TableField("log_type")
    private String logType;

    @Schema(description = "日志级别:DEBUG/INFO/WARN/ERROR")
    @TableField("log_level")
    private String logLevel;

    @Schema(description = "应用标识")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "操作者")
    @TableField("operator")
    private String operator;

    @Schema(description = "操作名称")
    @TableField("operation")
    private String operation;

    @Schema(description = "请求ID")
    @TableField("request_id")
    private String requestId;

    @Schema(description = "API路径")
    @TableField("api_path")
    private String apiPath;

    @Schema(description = "HTTP方法")
    @TableField("http_method")
    private String httpMethod;

    @Schema(description = "请求参数(JSON)")
    @TableField(value = "request_params", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> requestParams;

    @Schema(description = "认证类型:APPKEY/TOKEN")
    @TableField("auth_type")
    private String authType;

    @Schema(description = "认证状态:0-失败 1-成功")
    @TableField("auth_status")
    private Integer authStatus;

    @Schema(description = "认证错误码")
    @TableField("auth_error_code")
    private String authErrorCode;

    @Schema(description = "响应代码")
    @TableField("response_code")
    private String responseCode;

    @Schema(description = "响应消息")
    @TableField("response_message")
    private String responseMessage;

    @Schema(description = "响应数据(JSON)")
    @TableField(value = "response_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> responseData;

    @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @Schema(description = "用户代理")
    @TableField("user_agent")
    private String userAgent;

    @Schema(description = "服务器主机")
    @TableField("server_host")
    private String serverHost;

    @Schema(description = "耗时(ms)")
    @TableField("cost_time")
    private Integer costTime;

    @Schema(description = "内存使用(KB)")
    @TableField("memory_usage")
    private Integer memoryUsage;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "错误堆栈")
    @TableField("error_stack")
    private String errorStack;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}