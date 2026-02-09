//、package ltd.huntinginfo.feng.center.api.vo;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "消息日志视图VO", description = "消息日志返回数据")
//public class MsgAgentLogVO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
//
//    @Schema(description = "代理平台消息ID")
//    private String proxyMsgId;
//
//    @Schema(description = "应用标识")
//    private String appKey;
//
//    @Schema(description = "应用名称")
//    private String appName;
//
//    @Schema(description = "系统编码")
//    private String sysCode;
//
//    @Schema(description = "系统名称")
//    private String sysName;
//
//    @Schema(description = "日志类型")
//    private String logType;
//
//    @Schema(description = "日志类型描述")
//    private String logTypeDesc;
//
//    @Schema(description = "日志级别")
//    private String logLevel;
//
//    @Schema(description = "日志级别描述")
//    private String logLevelDesc;
//
//    @Schema(description = "日志内容")
//    private String logContent;
//
//    @Schema(description = "日志详情")
//    private String logDetail;
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
//    @Schema(description = "HTTP状态描述")
//    private String httpStatusDesc;
//
//    @Schema(description = "响应时间(ms)")
//    private Integer responseTime;
//
//    @Schema(description = "响应时间描述")
//    private String responseTimeDesc;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @Schema(description = "是否重要日志")
//    private Boolean important;
//
//    @Schema(description = "是否可以重试")
//    private Boolean retryable;
//
//    @Schema(description = "已重试次数")
//    private Integer retryCount;
//
//    // 扩展字段
//    @Schema(description = "时间戳")
//    private Long timestamp;
//
//    @Schema(description = "格式化时间")
//    private String formattedTime;
//
//    @Schema(description = "日志摘要")
//    private String summary;
//
//    // 用于前端展示的字段
//    @Schema(description = "是否展开详情")
//    private Boolean expanded = false;
//
//    @Schema(description = "是否选中")
//    private Boolean selected = false;
//
//    @Schema(description = "标签列表")
//    private String[] tags;
//
//    // 状态常量
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
//    public static final class LogLevel {
//        public static final String DEBUG = "DEBUG";
//        public static final String INFO = "INFO";
//        public static final String WARN = "WARN";
//        public static final String ERROR = "ERROR";
//    }
//
//    // 日志类型映射
//    public static class LogTypeMapping {
//        public static String getLogTypeDesc(String logType) {
//            if (logType == null) {
//                return "未知";
//            }
//            switch (logType) {
//                case LogType.SEND:
//                    return "发送";
//                case LogType.CALLBACK:
//                    return "回调";
//                case LogType.RETRY:
//                    return "重试";
//                case LogType.ERROR:
//                    return "错误";
//                case LogType.POLL:
//                    return "轮询";
//                case LogType.TOKEN:
//                    return "令牌";
//                case LogType.STATUS:
//                    return "状态更新";
//                case LogType.DEBUG:
//                    return "调试";
//                case LogType.INFO:
//                    return "信息";
//                case LogType.WARN:
//                    return "警告";
//                default:
//                    return logType;
//            }
//        }
//    }
//
//    // 日志级别映射
//    public static class LogLevelMapping {
//        public static String getLogLevelDesc(String logLevel) {
//            if (logLevel == null) {
//                return "未知";
//            }
//            switch (logLevel) {
//                case LogLevel.DEBUG:
//                    return "调试";
//                case LogLevel.INFO:
//                    return "信息";
//                case LogLevel.WARN:
//                    return "警告";
//                case LogLevel.ERROR:
//                    return "错误";
//                default:
//                    return logLevel;
//            }
//        }
//    }
//}