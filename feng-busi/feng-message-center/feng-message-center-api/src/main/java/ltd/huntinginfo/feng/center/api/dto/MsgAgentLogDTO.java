//package ltd.huntinginfo.feng.center.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import jakarta.validation.constraints.NotBlank;
//import java.io.Serializable;
//import java.util.Date;
//import java.util.Map;
//
//@Data
//@Schema(name = "消息日志DTO", description = "消息日志查询参数")
//public class MsgAgentLogDTO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
//
//    @NotBlank(message = "代理平台消息ID不能为空")
//    @Schema(description = "代理平台消息ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String proxyMsgId;
//
//    @NotBlank(message = "应用标识不能为空")
//    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String appKey;
//
//    @NotBlank(message = "系统编码不能为空")
//    @Schema(description = "系统编码", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String sysCode;
//
//    @NotBlank(message = "日志类型不能为空")
//    @Schema(description = "日志类型:SEND-发送 CALLBACK-回调 RETRY-重试 ERROR-错误 POLL-轮询 TOKEN-令牌 STATUS-状态更新", 
//           requiredMode = Schema.RequiredMode.REQUIRED)
//    private String logType;
//
//    @Schema(description = "日志级别:DEBUG/INFO/WARN/ERROR", defaultValue = "INFO")
//    private String logLevel = "INFO";
//
//    @NotBlank(message = "日志内容不能为空")
//    @Schema(description = "日志内容", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String logContent;
//
//    @Schema(description = "日志详情JSON字符串")
//    private String logDetail;
//
//    @Schema(description = "日志详情Map")
//    private Map<String, Object> logDetailMap;
//
//    @Schema(description = "操作名称")
//    private String operation;
//
//    @Schema(description = "API地址")
//    private String apiUrl;
//
//    @Schema(description = "HTTP方法")
//    private String httpMethod;
//
//    @Schema(description = "HTTP状态码")
//    private Integer httpStatus;
//
//    @Schema(description = "响应时间(ms)")
//    private Integer responseTime;
//
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    // 查询条件字段
//    @Schema(description = "创建时间开始范围")
//    private Date createTimeStart;
//
//    @Schema(description = "创建时间结束范围")
//    private Date createTimeEnd;
//
//    @Schema(description = "最小响应时间")
//    private Integer responseTimeMin;
//
//    @Schema(description = "最大响应时间")
//    private Integer responseTimeMax;
//
//    @Schema(description = "关键词搜索")
//    private String keyword;
//
//    @Schema(description = "排序字段")
//    private String orderBy;
//
//    @Schema(description = "排序方向:ASC-升序 DESC-降序")
//    private String orderDirection;
//
//    @Schema(description = "限制数量")
//    private Integer limit;
//
//    // 关联字段
//    @Schema(description = "应用名称")
//    private String appName;
//
//    @Schema(description = "系统名称")
//    private String sysName;
//
//    // 日志类型常量
//    public static final class LogType {
//        public static final String SEND = "SEND";
//        public static final String CALLBACK = "CALLBACK";
//        public static final String RETRY = "RETRY";
//        public static final String ERROR = "ERROR";
//        public static final String POLL = "POLL";
//        public static final String TOKEN = "TOKEN";
//        public static final String STATUS = "STATUS";
//        public static final String DEBUG = "DEBUG";
//        public static final String INFO = "INFO";
//        public static final String WARN = "WARN";
//    }
//
//    // 日志级别常量
//    public static final class LogLevel {
//        public static final String DEBUG = "DEBUG";
//        public static final String INFO = "INFO";
//        public static final String WARN = "WARN";
//        public static final String ERROR = "ERROR";
//    }
//}