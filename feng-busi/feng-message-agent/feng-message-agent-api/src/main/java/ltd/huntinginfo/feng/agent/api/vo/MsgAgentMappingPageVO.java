package ltd.huntinginfo.feng.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "消息映射分页VO")
public class MsgAgentMappingPageVO {
    
    @Schema(description = "主键ID")
    private String id;
    
    @Schema(description = "代理平台消息ID")
    private String msgId;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "业务ID")
    private String bizId;
    
    @Schema(description = "部级消息编码")
    private String xxbm;
    
    @Schema(description = "消息标题")
    private String msgTitle;
    
    @Schema(description = "消息状态")
    private String status;
    
    @Schema(description = "状态码")
    private String statusCode;
    
    @Schema(description = "发送时间")
    private Date sendTime;
    
    @Schema(description = "发送时间(格式化)")
    private String sendTimeStr;
    
    @Schema(description = "完成时间")
    private Date completeTime;
    
    @Schema(description = "完成时间(格式化)")
    private String completeTimeStr;
    
    @Schema(description = "发送人姓名")
    private String senderName;
    
    @Schema(description = "接收人姓名")
    private String receiverName;
    
    @Schema(description = "部级处理状态")
    private String centerClzt;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "创建时间(格式化)")
    private String createTimeStr;
}