//package ltd.huntinginfo.feng.agent.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "部级Token查询DTO", description = "部级Token管理查询参数")
//public class MsgCenterTokenDTO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "唯一标识UUID")
//    private String id;
//
//    @Schema(description = "应用标识")
//    private String appKey;
//
//    @Schema(description = "系统编码")
//    private String sysCode;
//
//    @Schema(description = "部级消息中心Token")
//    private String centerToken;
//
//    @Schema(description = "Token类型:BEARER")
//    private String tokenType;
//
//    @Schema(description = "状态:0-失效 1-有效")
//    private Integer status;
//
//    @Schema(description = "最小刷新次数")
//    private Integer refreshCountMin;
//
//    @Schema(description = "最大刷新次数")
//    private Integer refreshCountMax;
//
//    @Schema(description = "最小总请求次数")
//    private Integer totalRequestsMin;
//
//    @Schema(description = "最大总请求次数")
//    private Integer totalRequestsMax;
//
//    @Schema(description = "最小成功请求次数")
//    private Integer successRequestsMin;
//
//    @Schema(description = "最大成功请求次数")
//    private Integer successRequestsMax;
//
//    @Schema(description = "上次请求API")
//    private String lastRequestApi;
//
//    @Schema(description = "过期时间开始范围")
//    private Date expireTimeStart;
//
//    @Schema(description = "过期时间结束范围")
//    private Date expireTimeEnd;
//
//    @Schema(description = "创建时间开始范围")
//    private Date createTimeStart;
//
//    @Schema(description = "创建时间结束范围")
//    private Date createTimeEnd;
//
//    @Schema(description = "上次请求时间开始范围")
//    private Date lastRequestTimeStart;
//
//    @Schema(description = "上次请求时间结束范围")
//    private Date lastRequestTimeEnd;
//
//    @Schema(description = "排序字段")
//    private String orderBy;
//
//    @Schema(description = "排序方向:ASC-升序 DESC-降序")
//    private String orderDirection;
//
//    @Schema(description = "是否查询即将过期的Token")
//    private Boolean queryExpiring;
//
//    @Schema(description = "即将过期分钟数")
//    private Integer expiringMinutes;
//
//    @Schema(description = "是否包含已失效的Token")
//    private Boolean includeDisabled;
//
//    @Schema(description = "是否只查询有效的Token")
//    private Boolean onlyValid;
//}