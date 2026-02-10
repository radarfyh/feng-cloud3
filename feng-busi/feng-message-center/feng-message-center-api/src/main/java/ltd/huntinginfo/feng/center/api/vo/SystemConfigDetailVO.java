package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "系统配置详情VO")
public class SystemConfigDetailVO {
    
    @Schema(description = "配置ID")
    private String id;
    
    @Schema(description = "配置键")
    private String configKey;
    
    @Schema(description = "配置值")
    private String configValue;
    
    @Schema(description = "配置类型")
    private String configType;
    
    @Schema(description = "配置描述")
    private String configDesc;
    
    @Schema(description = "配置类别")
    private String category;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "创建者")
    private String createBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新者")
    private String updateBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}