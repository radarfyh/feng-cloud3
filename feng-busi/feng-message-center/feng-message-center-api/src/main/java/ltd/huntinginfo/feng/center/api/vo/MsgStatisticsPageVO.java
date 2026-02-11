package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "消息统计分页VO")
public class MsgStatisticsPageVO {
    
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
    
    @Schema(description = "接收数量")
    private Integer receiveCount;
    
    @Schema(description = "阅读数量")
    private Integer readCount;
    
    @Schema(description = "错误数量")
    private Integer errorCount;
    
    @Schema(description = "发送成功率")
    private Double sendSuccessRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
}