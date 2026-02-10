package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "广播接收记录详情VO")
public class BroadcastReceiveRecordDetailVO {
    
    @Schema(description = "广播ID")
    private String broadcastId;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "接收者类型")
    private String receiverType;
    
    @Schema(description = "接收状态")
    private String receiveStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "接收/送达时间")
    private LocalDateTime receiveTime;
    
    @Schema(description = "阅读状态")
    private Integer readStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后状态更新时间")
    private LocalDateTime updateTime;
}