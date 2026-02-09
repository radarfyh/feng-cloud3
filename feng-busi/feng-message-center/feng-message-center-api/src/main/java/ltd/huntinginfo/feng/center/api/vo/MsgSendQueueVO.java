//package ltd.huntinginfo.feng.center.api.vo;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "消息发送队列VO", description = "消息发送队列响应对象")
//public class MsgSendQueueVO implements Serializable {
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
//    @Schema(description = "系统编码")
//    private String sysCode;
//
//    @Schema(description = "队列类型:SEND-发送消息 CALLBACK-回调业务系统 RETRY-重试")
//    private String queueType;
//
//    @Schema(description = "队列状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败")
//    private String queueStatus;
//
//    @Schema(description = "优先级1-10，数字越小优先级越高")
//    private Integer priority;
//
//    @Schema(description = "执行时间")
//    private Date executeTime;
//
//    @Schema(description = "最大重试次数")
//    private Integer maxRetry;
//
//    @Schema(description = "当前重试次数")
//    private Integer currentRetry;
//
//    @Schema(description = "任务数据JSON")
//    private Object taskData;
//
//    @Schema(description = "结果代码")
//    private String resultCode;
//
//    @Schema(description = "结果消息")
//    private String resultMessage;
//
//    @Schema(description = "执行开始时间")
//    private Date executeStartTime;
//
//    @Schema(description = "执行结束时间")
//    private Date executeEndTime;
//
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @Schema(description = "修改时间")
//    private Date updateTime;
//}