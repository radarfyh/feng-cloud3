package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "接收者统计VO")
public class ReceiverStatisticsVO {
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "接收者类型")
    private String receiverType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "总消息数")
    private Integer totalCount;
    
    @Schema(description = "未读消息数")
    private Integer unreadCount;
    
    @Schema(description = "已读消息数")
    private Integer readCount;
    
    @Schema(description = "已接收消息数")
    private Integer receivedCount;
    
    @Schema(description = "失败消息数")
    private Integer failedCount;
    
    @Schema(description = "阅读率")
    public Double getReadRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return readCount != null ? (double) readCount / totalCount * 100 : 0.0;
    }
    
    @Schema(description = "接收率")
    public Double getReceiveRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return receivedCount != null ? (double) receivedCount / totalCount * 100 : 0.0;
    }
}