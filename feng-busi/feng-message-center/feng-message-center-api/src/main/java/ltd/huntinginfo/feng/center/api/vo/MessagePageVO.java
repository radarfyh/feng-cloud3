package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "消息分页VO")
public class MessagePageVO {
    
    @Schema(description = "消息ID")
    private String id;
    
    @Schema(description = "消息编码")
    private String msgCode;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "消息标题")
    private String title;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "发送应用标识")
    private String senderAppKey;
    
    @Schema(description = "发送者名称")
    private String senderName;
    
    @Schema(description = "消息状态")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @Schema(description = "总接收人数")
    private Integer totalReceivers;
    
    @Schema(description = "已读人数")
    private Integer readCount;
    
    @Schema(description = "已读率")
    private Double readRate;
}