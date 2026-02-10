package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "收件箱详情VO")
public class InboxDetailVO {
    
    @Schema(description = "收件箱记录ID")
    private String id;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "接收者类型")
    private String receiverType;
    
    @Schema(description = "接收者名称")
    private String receiverName;
    
    @Schema(description = "接收者单位代码")
    private String receiverOrgCode;
    
    @Schema(description = "接收者单位名称")
    private String receiverOrgName;
    
    @Schema(description = "分发方式")
    private String distributeMode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "分发时间")
    private LocalDateTime distributeTime;
    
    @Schema(description = "接收状态")
    private String receiveStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "接收时间")
    private LocalDateTime receiveTime;
    
    @Schema(description = "阅读状态")
    private Integer readStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "推送次数")
    private Integer pushCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后推送时间")
    private LocalDateTime lastPushTime;
    
    @Schema(description = "推送状态")
    private String pushStatus;
}