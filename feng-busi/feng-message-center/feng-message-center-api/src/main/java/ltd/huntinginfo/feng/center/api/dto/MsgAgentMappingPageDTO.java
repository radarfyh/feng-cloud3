package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "消息映射DTO", description = "消息映射数据传输对象")
public class MsgAgentMappingPageDTO {
    
    // ==================== 基础标识字段 ====================
    
    @Schema(description = "唯一标识UUID")
    private String id;
    
    @NotBlank(message = "应用标识不能为空")
    @Size(max = 64, message = "应用标识长度不能超过64个字符")
    @Schema(description = "应用标识（业务系统标识）", requiredMode = Schema.RequiredMode.REQUIRED, 
           example = "DEVICE_MGMT_PLATFORM")
    private String appKey;
    
    @Size(max = 50, message = "业务系统流水号长度不能超过50个字符")
    @Schema(description = "业务系统流水号（用于幂等性检查）", example = "APPROVAL202401290001")
    private String bizId;
    
    // ==================== 部级消息标识 ====================
    
    @Size(max = 32, message = "部级消息编码长度不能超过32个字符")
    @Schema(description = "部级消息编码（xxbm）", example = "XXBM-DE85WY5M2Y6")
    private String xxbm;
    
    @Size(max = 50, message = "部级消息ID长度不能超过50个字符")
    @Schema(description = "部级消息ID（center_msg_id）")
    private String centerMsgId;
    
    // ==================== 消息内容摘要 ====================
    
    @Size(max = 10, message = "消息类型长度不能超过10个字符")
    @Schema(description = "代理平台消息类型")
    private String msgType;
    
    @Size(max = 200, message = "消息标题长度不能超过200个字符")
    @Schema(description = "消息标题", example = "公文审批通知")
    private String msgTitle;
    
    @Schema(description = "消息内容")
    private String content;
    
    // ==================== 代理平台发送方信息 ====================
    
    @Size(max = 50, message = "发送者单位代码长度不能超过50个字符")
    @Schema(description = "发送者单位代码")
    private String senderOrgCode;
    
    @Size(max = 100, message = "发送者单位名称长度不能超过100个字符")
    @Schema(description = "发送者单位名称")
    private String senderOrgName;
    
    @Size(max = 50, message = "发送者证件号码长度不能超过50个字符")
    @Schema(description = "发送者证件号码")
    private String senderIdcard;
    
    @Size(max = 100, message = "发送者姓名长度不能超过100个字符")
    @Schema(description = "发送者姓名")
    private String senderName;
    
    // ==================== 代理平台接收方信息 ====================
    
    @Size(max = 50, message = "接收者单位代码长度不能超过50个字符")
    @Schema(description = "接收者单位代码")
    private String receiverOrgCode;
    
    @Size(max = 100, message = "接收者单位名称长度不能超过100个字符")
    @Schema(description = "接收者单位名称")
    private String receiverOrgName;
    
    @Size(max = 50, message = "接收者证件号码长度不能超过50个字符")
    @Schema(description = "接收者证件号码")
    private String receiverIdcard;
    
    @Size(max = 100, message = "接收者姓名长度不能超过100个字符")
    @Schema(description = "接收者姓名")
    private String receiverName;
}