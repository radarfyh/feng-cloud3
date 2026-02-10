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
//@Schema(name = "部级配置视图VO", description = "部级配置返回数据")
//public class MsgCenterConfigVO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
//
//    @Schema(description = "配置键")
//    private String configKey;
//
//    @Schema(description = "配置值")
//    private String configValue;
//
//    @Schema(description = "配置类型")
//    private String configType;
//
//    @Schema(description = "配置类型描述")
//    private String configTypeDesc;
//
//    @Schema(description = "配置描述")
//    private String configDesc;
//
//    @Schema(description = "配置类别")
//    private String category;
//
//    @Schema(description = "配置类别描述")
//    private String categoryDesc;
//
//    @Schema(description = "状态:0-禁用 1-启用")
//    private Integer status;
//
//    @Schema(description = "状态描述")
//    private String statusDesc;
//
//    @Schema(description = "创建者")
//    private String createBy;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @Schema(description = "更新者")
//    private String updateBy;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Schema(description = "更新时间")
//    private Date updateTime;
//
//    // 扩展字段
//    @Schema(description = "是否敏感配置")
//    private Boolean sensitive;
//
//    @Schema(description = "是否需要加密")
//    private Boolean needEncryption;
//
//    @Schema(description = "配置值掩码（用于敏感信息显示）")
//    private String maskedValue;
//
//    @Schema(description = "配置值长度")
//    private Integer valueLength;
//
//    @Schema(description = "是否来自缓存")
//    private Boolean fromCache = false;
//
//    @Schema(description = "配置重要性级别:1-低 2-中 3-高")
//    private Integer importanceLevel;
//
//    @Schema(description = "配置验证状态")
//    private String validationStatus;
//
//    // 配置类型常量
//    public static final class ConfigType {
//        public static final String STRING = "STRING";
//        public static final String NUMBER = "NUMBER";
//        public static final String BOOLEAN = "BOOLEAN";
//        public static final String JSON = "JSON";
//        public static final String ARRAY = "ARRAY";
//        public static final String PASSWORD = "PASSWORD";
//    }
//
//    // 配置类别常量
//    public static final class Category {
//        public static final String COMMON = "COMMON";
//        public static final String API = "API";
//        public static final String TOKEN = "TOKEN";
//        public static final String POLL = "POLL";
//        public static final String LOG = "LOG";
//        public static final String SECURITY = "SECURITY";
//        public static final String MONITOR = "MONITOR";
//        public static final String NOTIFICATION = "NOTIFICATION";
//        public static final String BACKUP = "BACKUP";
//        public static final String SYSTEM = "SYSTEM";
//    }
//
//    // 配置类型映射
//    public static class ConfigTypeMapping {
//        public static String getConfigTypeDesc(String configType) {
//            if (configType == null) {
//                return "未知";
//            }
//            switch (configType) {
//                case ConfigType.STRING:
//                    return "字符串";
//                case ConfigType.NUMBER:
//                    return "数字";
//                case ConfigType.BOOLEAN:
//                    return "布尔";
//                case ConfigType.JSON:
//                    return "JSON";
//                case ConfigType.ARRAY:
//                    return "数组";
//                case ConfigType.PASSWORD:
//                    return "密码";
//                default:
//                    return configType;
//            }
//        }
//    }
//
//    // 配置类别映射
//    public static class CategoryMapping {
//        public static String getCategoryDesc(String category) {
//            if (category == null) {
//                return "未知";
//            }
//            switch (category) {
//                case Category.COMMON:
//                    return "通用";
//                case Category.API:
//                    return "接口地址";
//                case Category.TOKEN:
//                    return "令牌配置";
//                case Category.POLL:
//                    return "轮询配置";
//                case Category.LOG:
//                    return "日志配置";
//                case Category.SECURITY:
//                    return "安全配置";
//                case Category.MONITOR:
//                    return "监控配置";
//                case Category.NOTIFICATION:
//                    return "通知配置";
//                case Category.BACKUP:
//                    return "备份配置";
//                case Category.SYSTEM:
//                    return "系统配置";
//                default:
//                    return category;
//            }
//        }
//    }
//}