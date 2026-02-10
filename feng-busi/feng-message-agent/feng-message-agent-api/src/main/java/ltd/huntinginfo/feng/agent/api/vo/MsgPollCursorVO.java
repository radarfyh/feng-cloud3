//package ltd.huntinginfo.feng.agent.api.vo;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "轮询游标视图VO", description = "轮询游标返回数据")
//public class MsgPollCursorVO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
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
//    @Schema(description = "游标键")
//    private String cursorKey;
//
//    @Schema(description = "部级游标值")
//    private String ybid;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "上次轮询时间")
//    private Date lastPollTime;
//
//    @Schema(description = "轮询间隔(秒)")
//    private Integer pollInterval;
//
//    @Schema(description = "轮询次数")
//    private Integer pollCount;
//
//    @Schema(description = "获取消息总数")
//    private Integer messageCount;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "上次获取消息时间")
//    private Date lastMessageTime;
//
//    @Schema(description = "状态:0-停止 1-运行")
//    private Integer status;
//
//    @Schema(description = "状态描述")
//    private String statusDesc;
//
//    @Schema(description = "连续错误次数")
//    private Integer errorCount;
//
//    @Schema(description = "上次错误信息")
//    private String lastError;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "上次成功时间")
//    private Date lastSuccessTime;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "修改时间")
//    private Date updateTime;
//
//    @Schema(description = "逻辑删 0-正常 1-删除")
//    private String delFlag;
//
//    @Schema(description = "备注信息")
//    private String remark;
//
//    // 计算字段
//    @Schema(description = "健康状态", allowableValues = {"HEALTHY", "WARNING", "ERROR"})
//    private String healthStatus;
//
//    @Schema(description = "健康状态描述")
//    private String healthStatusDesc;
//
//    @Schema(description = "下次轮询时间")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date nextPollTime;
//
//    @Schema(description = "是否已到轮询时间")
//    private Boolean readyToPoll;
//
//    @Schema(description = "轮询是否超时")
//    private Boolean pollOverdue;
//
//    @Schema(description = "超时秒数")
//    private Long overdueSeconds;
//
//    @Schema(description = "距离上次轮询的分钟数")
//    private Long minutesSinceLastPoll;
//
//    @Schema(description = "是否活跃")
//    private Boolean isActive;
//
//    @Schema(description = "成功率")
//    private String successRate;
//
//    @Schema(description = "平均每次轮询获取消息数")
//    private String avgMessagesPerPoll;
//
//    @Schema(description = "今日轮询次数")
//    private Integer todayPollCount;
//
//    @Schema(description = "今日获取消息数")
//    private Integer todayMessageCount;
//
//    @Schema(description = "本周轮询次数")
//    private Integer weekPollCount;
//
//    @Schema(description = "本月轮询次数")
//    private Integer monthPollCount;
//
//    @Schema(description = "需要关注的错误")
//    private Boolean needsAttention;
//
//    // 配置信息
//    @Schema(description = "最大允许错误次数")
//    private Integer maxErrorCount;
//
//    @Schema(description = "最小轮询间隔")
//    private Integer minPollInterval;
//
//    @Schema(description = "最大轮询间隔")
//    private Integer maxPollInterval;
//
//    // 状态常量
//    public static final class Status {
//        public static final int STOPPED = 0; // 停止
//        public static final int RUNNING = 1; // 运行
//    }
//
//    // 健康状态常量
//    public static final class HealthStatus {
//        public static final String HEALTHY = "HEALTHY";
//        public static final String WARNING = "WARNING";
//        public static final String ERROR = "ERROR";
//    }
//
//    // 状态映射
//    public static class StatusMapping {
//        public static String getStatusDesc(Integer status) {
//            if (status == null) {
//                return "未知";
//            }
//            switch (status) {
//                case Status.STOPPED:
//                    return "停止";
//                case Status.RUNNING:
//                    return "运行";
//                default:
//                    return "未知";
//            }
//        }
//    }
//
//    // 健康状态映射
//    public static class HealthStatusMapping {
//        public static String getHealthStatusDesc(String healthStatus) {
//            if (healthStatus == null) {
//                return "未知";
//            }
//            switch (healthStatus) {
//                case HealthStatus.HEALTHY:
//                    return "健康";
//                case HealthStatus.WARNING:
//                    return "警告";
//                case HealthStatus.ERROR:
//                    return "错误";
//                default:
//                    return "未知";
//            }
//        }
//    }
//}