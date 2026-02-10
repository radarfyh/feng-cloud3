//package ltd.huntinginfo.feng.agent.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Schema(name = "消息发送队列DTO", description = "消息发送队列请求参数")
//public class MsgSendQueueDTO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Schema(description = "代理平台消息ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "代理平台消息ID不能为空")
//    private String proxyMsgId;
//
//    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "应用标识不能为空")
//    private String appKey;
//
//    @Schema(description = "系统编码", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "系统编码不能为空")
//    private String sysCode;
//
//    @Schema(description = "队列类型:SEND-发送消息 CALLBACK-回调业务系统 RETRY-重试", 
//            requiredMode = Schema.RequiredMode.REQUIRED, 
//            allowableValues = {"SEND", "CALLBACK", "RETRY"})
//    @NotBlank(message = "队列类型不能为空")
//    private String queueType;
//
//    @Schema(description = "优先级1-10，数字越小优先级越高", defaultValue = "5")
//    private Integer priority = 5;
//
//    @Schema(description = "执行时间", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "执行时间不能为空")
//    private Date executeTime;
//
//    @Schema(description = "最大重试次数", defaultValue = "3")
//    private Integer maxRetry = 3;
//
//    @Schema(description = "任务数据JSON", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "任务数据不能为空")
//    private String taskData;
//}