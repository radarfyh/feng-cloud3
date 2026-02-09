package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(name = "消息状态更新请求DTO", description = "MSG-1020接口请求参数")
public class MsgStatusUpdateRequestDTO {
    
    @Schema(description = "业务系统编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "系统编码不能为空")
    private String sysCode;
    
    @Schema(description = "消息编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息编码不能为空")
    private String xxbm;
    
    @Schema(description = "处理单位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理单位不能为空")
    private String cldw;
    
    @Schema(description = "处理单位代码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理单位代码不能为空")
    private String cldwdm;
    
    @Schema(description = "处理人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理人不能为空")
    private String clr;
    
    @Schema(description = "处理人证件号码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理人证件号码不能为空")
    private String clrzjhm;
    
    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, 
            allowableValues = {"1"})
    @NotBlank(message = "处理状态不能为空")
    private String clzt;
}