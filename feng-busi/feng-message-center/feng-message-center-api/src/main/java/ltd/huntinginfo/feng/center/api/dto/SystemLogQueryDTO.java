package ltd.huntinginfo.feng.center.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "系统日志查询DTO")
public class SystemLogQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "20", defaultValue = "20")
    private Long size = 20L;
    
    @Schema(description = "日志类型")
    private String logType;
    
    @Schema(description = "日志级别")
    private String logLevel;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "操作者")
    private String operator;
    
    @Schema(description = "操作名称")
    private String operation;
    
    @Schema(description = "API路径")
    private String apiPath;
    
    @Schema(description = "响应代码")
    private String responseCode;
    
    @Schema(description = "认证状态")
    private Integer authStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "关键词")
    private String keyword;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortOrder = "desc";
}