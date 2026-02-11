package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "权限详情VO")
public class PermissionDetailVO {
    
    @Schema(description = "权限ID")
    private String id;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "资源代码")
    private String resourceCode;
    
    @Schema(description = "资源描述")
    private String resourceName;
    
    @Schema(description = "操作")
    private String operation;
    
    @Schema(description = "状态")
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}