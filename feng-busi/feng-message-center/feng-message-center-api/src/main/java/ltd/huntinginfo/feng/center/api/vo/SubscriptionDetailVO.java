package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "订阅详情VO")
public class SubscriptionDetailVO {
    
    @Schema(description = "订阅ID")
    private String id;
    
    @Schema(description = "主题代码")
    private String topicCode;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "订阅配置")
    private Map<String, Object> subscriptionConfig;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "推送方式")
    private String pushMode;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "接收消息数量")
    private Integer messageCount;
    
    @Schema(description = "订阅时长（天）")
    private Long subscriptionDays;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订阅时间")
    private LocalDateTime subscribeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "取消订阅时间")
    private LocalDateTime unsubscribeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
}