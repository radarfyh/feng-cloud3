//package ltd.huntinginfo.feng.center.api.vo;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "部级Token视图VO", description = "部级Token管理返回数据")
//public class MsgCenterTokenVO implements Serializable {
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
//    @Schema(description = "部级消息中心Token")
//    private String centerToken;
//
//    @Schema(description = "Token类型:BEARER")
//    private String tokenType;
//
//    @Schema(description = "Token类型描述")
//    private String tokenTypeDesc;
//
//    @Schema(description = "Token过期时间")
//    private Date expireTime;
//
//    @Schema(description = "刷新次数")
//    private Integer refreshCount;
//
//    @Schema(description = "总请求次数")
//    private Integer totalRequests;
//
//    @Schema(description = "成功请求次数")
//    private Integer successRequests;
//
//    @Schema(description = "请求成功率")
//    private String successRate;
//
//    @Schema(description = "上次请求时间")
//    private Date lastRequestTime;
//
//    @Schema(description = "上次请求API")
//    private String lastRequestApi;
//
//    @Schema(description = "API描述")
//    private String lastRequestApiDesc;
//
//    @Schema(description = "状态:0-失效 1-有效")
//    private Integer status;
//
//    @Schema(description = "状态描述")
//    private String statusDesc;
//
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @Schema(description = "修改时间")
//    private Date updateTime;
//
//    @Schema(description = "备注信息")
//    private String remark;
//
//    @Schema(description = "Token是否已过期")
//    private Boolean expired;
//
//    @Schema(description = "Token剩余有效分钟数（-1表示永不过期）")
//    private Long remainMinutes;
//
//    @Schema(description = "Token是否即将过期（5分钟内）")
//    private Boolean willExpire;
//
//    @Schema(description = "是否需要刷新Token")
//    private Boolean needRefresh;
//
//    @Schema(description = "Token有效性")
//    private Boolean tokenValid;
//
//    @Schema(description = "使用时长（天）")
//    private Long usageDays;
//
//    @Schema(description = "平均每天请求次数")
//    private String avgDailyRequests;
//
//    @Schema(description = "最后活跃时间")
//    private Date lastActiveTime;
//
//    @Schema(description = "创建人")
//    private String createBy;
//
//    @Schema(description = "修改人")
//    private String updateBy;
//
//    // 扩展字段，用于展示相关统计信息
//    @Schema(description = "今日请求次数")
//    private Integer todayRequests;
//
//    @Schema(description = "今日成功次数")
//    private Integer todaySuccess;
//
//    @Schema(description = "今日成功率")
//    private String todaySuccessRate;
//
//    @Schema(description = "本周请求次数")
//    private Integer weekRequests;
//
//    @Schema(description = "本月请求次数")
//    private Integer monthRequests;
//
//    // Token使用统计
//    @Schema(description = "Token使用分析")
//    private TokenUsageAnalysis usageAnalysis;
//
//    @Data
//    @Schema(name = "Token使用分析", description = "Token使用情况分析")
//    public static class TokenUsageAnalysis implements Serializable {
//        private static final long serialVersionUID = 1L;
//
//        @Schema(description = "高频使用时间段")
//        private String peakUsagePeriod;
//
//        @Schema(description = "平均响应时间(ms)")
//        private Long avgResponseTime;
//
//        @Schema(description = "最大响应时间(ms)")
//        private Long maxResponseTime;
//
//        @Schema(description = "最小响应时间(ms)")
//        private Long minResponseTime;
//
//        @Schema(description = "错误类型分布")
//        private String errorTypeDistribution;
//
//        @Schema(description = "建议优化点")
//        private String optimizationSuggestions;
//    }
//
//    // Token状态常量
//    public static final class Status {
//        public static final int DISABLED = 0; // 失效
//        public static final int ENABLED = 1;  // 有效
//    }
//
//    // Token类型常量
//    public static final class TokenType {
//        public static final String BEARER = "BEARER";
//        public static final String BASIC = "BASIC";
//        public static final String API_KEY = "API_KEY";
//        public static final String JWT = "JWT";
//        public static final String OAUTH2 = "OAUTH2";
//    }
//
//    // Token类型映射
//    public static class TokenTypeMapping {
//        public static String getTokenTypeDesc(String tokenType) {
//            if (tokenType == null) {
//                return "未知";
//            }
//            switch (tokenType.toUpperCase()) {
//                case TokenType.BEARER:
//                    return "Bearer令牌";
//                case TokenType.BASIC:
//                    return "Basic认证";
//                case TokenType.API_KEY:
//                    return "API密钥";
//                case TokenType.JWT:
//                    return "JWT令牌";
//                case TokenType.OAUTH2:
//                    return "OAuth2令牌";
//                default:
//                    return tokenType;
//            }
//        }
//    }
//}