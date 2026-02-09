package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(name = "消息发送响应VO", description = "MSG-1000接口响应参数")
public class MsgSendResponseVO {
    
    @Schema(description = "代理平台消息ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String msgId;
    
    @Schema(description = "业务流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bizId;
    
    @Schema(description = "省级消息编码")
    private String xxbm;
    
    @Schema(description = "消息发送时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date sendTime;
    
    @Schema(description = "消息状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
    
    @Schema(description = "消息状态码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusCode;
}
