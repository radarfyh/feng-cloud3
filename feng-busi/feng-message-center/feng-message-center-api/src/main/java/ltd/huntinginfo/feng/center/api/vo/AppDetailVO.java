package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "应用详情VO")
public class AppDetailVO {
    
    @Schema(description = "应用ID")
    private String id;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "应用名称")
    private String appName;
    
    @Schema(description = "应用类型")
    private String appType;
    
    @Schema(description = "应用描述")
    private String appDesc;
    
    @Schema(description = "应用图标地址")
    private String appIcon;
    
    @Schema(description = "应用首页地址")
    private String homeUrl;
    
    @Schema(description = "默认推送方式")
    private String defaultPushMode;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调认证模式")
    private String callbackAuthMode;
    
    @Schema(description = "API调用速率限制(次/分钟)")
    private Integer rateLimit;
    
    @Schema(description = "最大消息大小(字节)")
    private Integer maxMsgSize;
    
    @Schema(description = "IP白名单")
    private List<String> ipWhitelist;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "密钥剩余天数")
    private Long secretRemainingDays;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "密钥过期时间")
    private LocalDateTime secretExpireTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}