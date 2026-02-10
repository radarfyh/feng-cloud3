package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "消息详情VO")
public class MessageDetailVO {
    
    @Schema(description = "消息ID")
    private String id;
    
    @Schema(description = "消息编码")
    private String msgCode;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "消息标题")
    private String title;
    
    @Schema(description = "消息内容")
    private Map<String, Object> content;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "发送应用标识")
    private String senderAppKey;
    
    @Schema(description = "发送者名称")
    private String senderName;
    
    @Schema(description = "发送单位名称")
    private String senderOrgName;
    
    @Schema(description = "代理平台标识")
    private String agentAppKey;
    
    @Schema(description = "接收者数量")
    private Integer receiverCount;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "推送方式")
    private String pushMode;
    
    @Schema(description = "消息状态")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "分发时间")
    private LocalDateTime distributeTime;
    
    @Schema(description = "总接收人数")
    private Integer totalReceivers;
    
    @Schema(description = "已接收人数")
    private Integer receivedCount;
    
    @Schema(description = "已读人数")
    private Integer readCount;
    
    @Schema(description = "已读率")
    private Double readRate;
}
