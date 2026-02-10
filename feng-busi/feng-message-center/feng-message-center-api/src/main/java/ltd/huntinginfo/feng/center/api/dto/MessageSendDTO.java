package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "消息发送DTO")
public class MessageSendDTO {
    
    @NotBlank(message = "消息标题不能为空")
    @Size(max = 200, message = "消息标题长度不能超过200个字符")
    @Schema(description = "消息标题", required = true, example = "系统维护通知")
    private String title;
    
    @NotNull(message = "消息内容不能为空")
    @Schema(description = "消息内容(JSON格式)", required = true)
    private Map<String, Object> content;
    
    @Schema(description = "消息编码(xxbm)", example = "XXBM-DE85WY5M2Y6")
    private String msgCode;
    
    @Schema(description = "消息类型", example = "NOTICE", defaultValue = "NOTICE")
    private String msgType = "NOTICE";
    
    @Schema(description = "优先级(1-5)", example = "3", defaultValue = "3")
    private Integer priority = 3;
    
    @NotBlank(message = "发送应用标识不能为空")
    @Schema(description = "发送应用标识", required = true, example = "APP001")
    private String senderAppKey;
    
    @Schema(description = "发送者类型", example = "APP", defaultValue = "APP")
    private String senderType = "APP";
    
    @Schema(description = "发送者ID", example = "user_001")
    private String senderId;
    
    @Schema(description = "发送者名称", example = "系统管理员")
    private String senderName;
    
    @Schema(description = "发送单位代码", example = "130100000000")
    private String senderOrgCode;
    
    @Schema(description = "发送单位名称", example = "石家庄市消防局")
    private String senderOrgName;
    
    @Schema(description = "接收者数量", example = "1", defaultValue = "1")
    private Integer receiverCount = 1;
    
    @Schema(description = "接收者类型", example = "USER", defaultValue = "USER")
    private String receiverType = "USER";
    
    @Schema(description = "接收者范围配置(JSON)")
    private Map<String, Object> receiverScope;
    
    @Schema(description = "回调地址", example = "http://callback.example.com/api/message/status")
    private String callbackUrl;
    
    @Schema(description = "推送方式", example = "PUSH", defaultValue = "PUSH")
    private String pushMode = "PUSH";
    
    @Schema(description = "回调配置(JSON)")
    private Map<String, Object> callbackConfig;
    
    @Schema(description = "扩展参数(JSON)")
    private Map<String, Object> extParams;
    
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
