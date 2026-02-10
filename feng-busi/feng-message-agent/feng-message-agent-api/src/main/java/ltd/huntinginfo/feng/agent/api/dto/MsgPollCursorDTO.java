//package ltd.huntinginfo.feng.agent.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "轮询游标DTO", description = "轮询游标查询参数")
//public class MsgPollCursorDTO implements Serializable {
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
//    @Schema(description = "游标键")
//    private String cursorKey;
//
//    @Schema(description = "部级游标值")
//    private String ybid;
//
//    @Schema(description = "状态:0-停止 1-运行")
//    private Integer status;
//
//    @Schema(description = "最小轮询间隔")
//    @Min(value = 10, message = "轮询间隔不能小于10秒")
//    private Integer pollIntervalMin;
//
//    @Schema(description = "最大轮询间隔")
//    private Integer pollIntervalMax;
//
//    @Schema(description = "最小轮询次数")
//    private Integer pollCountMin;
//
//    @Schema(description = "最大轮询次数")
//    private Integer pollCountMax;
//
//    @Schema(description = "最小消息数量")
//    private Integer messageCountMin;
//
//    @Schema(description = "最大消息数量")
//    private Integer messageCountMax;
//
//    @Schema(description = "最小错误次数")
//    private Integer errorCountMin;
//
//    @Schema(description = "最大错误次数")
//    private Integer errorCountMax;
//
//    @Schema(description = "上次错误信息")
//    private String lastError;
//
//    @Schema(description = "逻辑删 0-正常 1-删除")
//    private String delFlag;
//
//    // 时间范围查询字段
//    @Schema(description = "上次轮询时间开始范围")
//    private Date lastPollTimeStart;
//
//    @Schema(description = "上次轮询时间结束范围")
//    private Date lastPollTimeEnd;
//
//    @Schema(description = "上次获取消息时间开始范围")
//    private Date lastMessageTimeStart;
//
//    @Schema(description = "上次获取消息时间结束范围")
//    private Date lastMessageTimeEnd;
//
//    @Schema(description = "上次成功时间开始范围")
//    private Date lastSuccessTimeStart;
//
//    @Schema(description = "上次成功时间结束范围")
//    private Date lastSuccessTimeEnd;
//
//    @Schema(description = "创建时间开始范围")
//    private Date createTimeStart;
//
//    @Schema(description = "创建时间结束范围")
//    private Date createTimeEnd;
//
//    @Schema(description = "修改时间开始范围")
//    private Date updateTimeStart;
//
//    @Schema(description = "修改时间结束范围")
//    private Date updateTimeEnd;
//
//    // 特殊查询条件
//    @Schema(description = "是否查询即将过期的游标")
//    private Boolean queryExpiring;
//
//    @Schema(description = "即将过期分钟数")
//    private Integer expiringMinutes;
//
//    @Schema(description = "是否包含已删除的记录")
//    private Boolean includeDeleted;
//
//    @Schema(description = "是否只查询活动的游标")
//    private Boolean onlyActive;
//
//    // 排序字段
//    @Schema(description = "排序字段")
//    private String orderBy;
//
//    @Schema(description = "排序方向:ASC-升序 DESC-降序", allowableValues = {"ASC", "DESC"})
//    private String orderDirection;
//
//    // 分页字段
//    @Schema(description = "页码", defaultValue = "1")
//    private Integer pageNum = 1;
//
//    @Schema(description = "每页大小", defaultValue = "20")
//    private Integer pageSize = 20;
//
//    // 关联查询字段
//    @Schema(description = "应用名称")
//    private String appName;
//
//    @Schema(description = "系统名称")
//    private String sysName;
//}