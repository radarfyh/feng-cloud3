//package ltd.huntinginfo.feng.center.api.vo;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import java.util.Date;
//
//// MSG-1011 响应VO
//
//@Data
//@Schema(name = "消息查询响应VO", description = "MSG-1011接口响应参数")
//public class MsgQueryResponseVO {
//    
//    @Schema(description = "发送单位")
//    private String fsdw;
//    
//    @Schema(description = "发送单位代码")
//    private String fsdwdm;
//    
//    @Schema(description = "发送人")
//    private String fsr;
//    
//    @Schema(description = "发送人证件号码")
//    private String fsrzjhm;
//    
//    @Schema(description = "发送对象")
//    private String fsdx;
//    
//    @Schema(description = "接收单位")
//    private String jsdw;
//    
//    @Schema(description = "接收单位代码")
//    private String jsdwdm;
//    
//    @Schema(description = "接收人")
//    private String jsr;
//    
//    @Schema(description = "接收人证件号码")
//    private String jsrzjhm;
//    
//    @Schema(description = "消息编码")
//    private String xxbm;
//    
//    @Schema(description = "消息类型")
//    private String xxlx;
//    
//    @Schema(description = "消息标题")
//    private String xxbt;
//    
//    @Schema(description = "消息内容")
//    private String xxnr;
//    
//    @Schema(description = "处理地址")
//    private String cldz;
//    
//    @Schema(description = "紧急程度")
//    private String jjcd;
//    
//    @Schema(description = "业务参数")
//    private String ywcs;
//    
//    @Schema(description = "图标")
//    private String tb;
//    
//    @Schema(description = "处理状态：0-未读 1-已读")
//    private String clzt;
//    
//    @Schema(description = "发送时间")
//    private Date sendTime;
//    
//    @Schema(description = "接收时间")
//    private Date receiveTime;
//}