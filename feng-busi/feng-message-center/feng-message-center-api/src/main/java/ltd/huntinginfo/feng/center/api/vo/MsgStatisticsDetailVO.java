package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "消息统计详情VO")
public class MsgStatisticsDetailVO {
    
    @Schema(description = "统计ID")
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计日期")
    private LocalDate statDate;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "发送数量")
    private Integer sendCount;
    
    @Schema(description = "发送成功数量")
    private Integer sendSuccessCount;
    
    @Schema(description = "发送失败数量")
    private Integer sendFailedCount;
    
    @Schema(description = "接收数量")
    private Integer receiveCount;
    
    @Schema(description = "阅读数量")
    private Integer readCount;
    
    @Schema(description = "平均处理时间(ms)")
    private Integer avgProcessTime;
    
    @Schema(description = "平均接收时间(ms)")
    private Integer avgReceiveTime;
    
    @Schema(description = "平均阅读时间(ms)")
    private Integer avgReadTime;
    
    @Schema(description = "错误数量")
    private Integer errorCount;
    
    @Schema(description = "重试数量")
    private Integer retryCount;
    
    @Schema(description = "发送成功率")
    private Double sendSuccessRate;
    
    @Schema(description = "发送失败率")
    private Double sendFailedRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
    
    @Schema(description = "错误率")
    private Double errorRate;
    
    @Schema(description = "重试率")
    private Double retryRate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}