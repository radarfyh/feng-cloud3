package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(name = "消息映射VO", description = "消息映射视图对象")
public class MsgAgentMappingVO {
    
    @Schema(description = "唯一标识UUID")
    private String id;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "业务系统流水号")
    private String bizId;
    
    @Schema(description = "部级消息编码")
    private String xxbm;
    
    @Schema(description = "消息标题")
    private String msgTitle;
    
    @Schema(description = "消息内容摘要")
    private String contentSummary;
    
    @Schema(description = "消息状态")
    private String status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "状态码")
    private String statusCode;
    
    @Schema(description = "发送时间")
    private Date sendTime;
    
    @Schema(description = "发送时间格式化")
    private String sendTimeStr;
    
    @Schema(description = "完成时间")
    private Date completeTime;
    
    @Schema(description = "完成时间格式化")
    private String completeTimeStr;
    
    @Schema(description = "发送人姓名")
    private String senderName;
    
    @Schema(description = "接收人姓名")
    private String receiverName;
    
    @Schema(description = "部级处理状态")
    private String centerClzt;
    
    @Schema(description = "部级处理状态文本")
    private String centerClztText;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "创建时间格式化")
    private String createTimeStr;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "重试次数")
    private Integer retryCount;
    
    @Schema(description = "部级消息类型")
    private String centerXxlx;
    
    @Schema(description = "部级消息类型名称")
    private String centerXxlxName;
    
    @Schema(description = "部级紧急程度")
    private String centerJjcd;
    
    @Schema(description = "部级紧急程度文本")
    private String centerJjcdText;
    
    @Schema(description = "业务接收者ID")
    private String bizReceiverId;
    
    @Schema(description = "业务接收者名称")
    private String bizReceiverName;
    
    // 用于前端展示的扩展字段
    @Schema(description = "是否可重试")
    private Boolean canRetry;
    
    @Schema(description = "是否可回调")
    private Boolean canCallback;
    
    @Schema(description = "是否可标记已读")
    private Boolean canMarkRead;
}