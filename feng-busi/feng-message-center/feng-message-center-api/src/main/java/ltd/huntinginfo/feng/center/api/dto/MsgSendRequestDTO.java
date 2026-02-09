package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "消息发送请求DTO", description = "MSG-1000接口请求参数")
public class MsgSendRequestDTO {
    @Schema(description = "接入系统代码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "系统代码不能为空")
    private String appKey;
    
    @Schema(description = "业务UUID，对于某个APPKEY，不能重复否则判定重复请求", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务ID不能为空")
    private String bizId;
    
    @Schema(description = "回调配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "回调配置不能为空")
    @Valid
    private CallbackConfigDTO callbackConfig;
    
    @Schema(description = "发送方信息（省级格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发送方信息不能为空")
    @Valid
    private SenderDTO sender;
    
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息内容不能为空")
    @Valid
    private MessageContentDTO message;
    
    @Schema(description = "接收方信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接收方信息不能为空")
    @Valid
    private ReceiverDTO receiver;
    
    @Data
    @Schema(name = "回调配置DTO")
    public static class CallbackConfigDTO {
        @Schema(description = "回调地址", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "回调地址不能为空")
        private String callbackUrl;
        
        @Schema(description = "回调HTTP方法", defaultValue = "POST")
        private String method = "POST";
        
        @Schema(description = "回调认证模式", allowableValues = {"standard", "legacy"}, defaultValue = "standard")
        private String authMode = "standard";
    }
    
    @Data
    @Schema(name = "发送方DTO")
    public static class SenderDTO {
        @Schema(description = "申请单位行政区划代码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "行政区划代码不能为空")
        private String sqssdm;
        
        @Valid
        @NotNull(message = "申请单位信息不能为空")
        private ApplyUnitDTO sqdwxx;
        
        @Valid
        @NotNull(message = "申请人信息不能为空")
        private ApplyPersonDTO sqrxx;
        
        @Data
        @Schema(name = "申请单位DTO")
        public static class ApplyUnitDTO {
            @Schema(description = "申请单位代码", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "申请单位代码不能为空")
            private String sqdwdm;
            
            @Schema(description = "申请单位名称", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "申请单位名称不能为空")
            private String sqdwmc;
        }
        
        @Data
        @Schema(name = "申请人DTO")
        public static class ApplyPersonDTO {
            @Schema(description = "申请人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "申请人姓名不能为空")
            private String sqrxm;
            
            @Schema(description = "申请人证件号码", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "申请人证件号码不能为空")
            private String sqrzjhm;
            
            @Schema(description = "申请人电话", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "申请人电话不能为空")
            private String sqrdh;
        }
    }
    
    @Data
    @Schema(name = "消息内容DTO")
    public static class MessageContentDTO {
        @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息类型不能为空")
        private String xxlx;
        
        @Schema(description = "消息标题", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息标题不能为空")
        private String xxbt;
        
        @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息内容不能为空")
        private String xxnr;
        
        @Schema(description = "处理地址")
        private String cldz;
        
        @Schema(description = "紧急程度")
        private String jjcd;
        
        @Schema(description = "业务参数")
        private String ywcs;
        
        @Schema(description = "图标(base64)")
        private String tb;
    }
    
    @Data
    @Schema(name = "接收方DTO")
    public static class ReceiverDTO {
        @Schema(description = "发送对象", requiredMode = Schema.RequiredMode.REQUIRED, 
                allowableValues = {"1", "2"})
        @NotBlank(message = "发送对象不能为空")
        private String fsdx;
        
        @Schema(description = "接收单位")
        private String jsdw;
        
        @Schema(description = "接收单位代码")
        private String jsdwdm;
        
        @Schema(description = "接收人")
        private String jsr;
        
        @Schema(description = "接收人证件号码")
        private String jsrzjhm;
    }
}