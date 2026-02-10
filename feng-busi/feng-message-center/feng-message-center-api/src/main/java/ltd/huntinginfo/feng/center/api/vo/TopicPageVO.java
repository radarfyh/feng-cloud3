package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "主题分页VO")
public class TopicPageVO {
    
    @Schema(description = "主题ID")
    private String id;
    
    @Schema(description = "主题代码")
    private String topicCode;
    
    @Schema(description = "主题名称")
    private String topicName;
    
    @Schema(description = "主题类型")
    private String topicType;
    
    @Schema(description = "默认消息类型")
    private String defaultMsgType;
    
    @Schema(description = "默认优先级")
    private Integer defaultPriority;
    
    @Schema(description = "订阅者数量")
    private Integer subscriberCount;
    
    @Schema(description = "最大订阅者数量")
    private Integer maxSubscribers;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "订阅率")
    private Double subscribeRate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}