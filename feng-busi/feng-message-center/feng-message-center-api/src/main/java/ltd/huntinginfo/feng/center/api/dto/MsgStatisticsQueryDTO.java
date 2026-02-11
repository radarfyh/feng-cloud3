package ltd.huntinginfo.feng.center.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "消息统计查询DTO")
public class MsgStatisticsQueryDTO {
    
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "20", defaultValue = "20")
    private Long size = 20L;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计日期开始")
    private LocalDate statDateStart;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计日期结束")
    private LocalDate statDateEnd;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "排序字段")
    private String sortField;
    
    @Schema(description = "排序方向", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortOrder = "desc";
}