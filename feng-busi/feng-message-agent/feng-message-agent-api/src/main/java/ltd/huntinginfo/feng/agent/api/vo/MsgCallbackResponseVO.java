package ltd.huntinginfo.feng.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(name = "消息回调响应VO", description = "业务系统对MSG-1010的回调响应")
public class MsgCallbackResponseVO {
    
    @Schema(description = "是否成功接收", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean received;
    
    @Schema(description = "业务处理消息的时间")
    private Date processTime;
}