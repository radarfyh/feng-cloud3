//package ltd.huntinginfo.feng.center.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import jakarta.validation.constraints.NotBlank;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "部级配置DTO", description = "部级配置查询参数")
//public class MsgCenterConfigDTO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
//
//    @NotBlank(message = "配置键不能为空")
//    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String configKey;
//
//    @NotBlank(message = "配置值不能为空")
//    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String configValue;
//
//    @Schema(description = "配置类型:STRING/NUMBER/BOOLEAN/JSON", defaultValue = "STRING")
//    private String configType = "STRING";
//
//    @Schema(description = "配置描述")
//    private String configDesc;
//
//    @Schema(description = "配置类别:COMMON-通用 API-接口地址 TOKEN-令牌配置", defaultValue = "COMMON")
//    private String category = "COMMON";
//
//    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
//    private Integer status = 1;
//
//    @Schema(description = "创建者")
//    private String createBy;
//
//    @Schema(description = "更新时间")
//    private String updateBy;
//
//    // 查询条件字段
//    @Schema(description = "创建时间开始范围")
//    private Date createTimeStart;
//
//    @Schema(description = "创建时间结束范围")
//    private Date createTimeEnd;
//
//    @Schema(description = "更新时间开始范围")
//    private Date updateTimeStart;
//
//    @Schema(description = "更新时间结束范围")
//    private Date updateTimeEnd;
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
//    // 验证字段
//    @Schema(description = "是否验证配置")
//    private Boolean validate = true;
//
//    @Schema(description = "是否加密敏感配置")
//    private Boolean encryptSensitive = true;
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
//}