package ltd.huntinginfo.feng.agent.api.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(name = "AppKey认证响应")
public class AppKeyAuthResponse {
    /** 认证结果信息 **/
    @Schema(description = "认证结果", example = "true")
    private boolean success;
    
    @Schema(description = "错误信息")
    private String errorMsg;
    
    @Schema(description = "认证令牌(JWT格式)")
    private String token;
    
    /** 应用基本信息 **/
    @Schema(description = "系统/平台ID")
    private String appId;

    @Schema(description = "App Key")
    private String appKey;
    
    @Schema(description = "系统名称")
    private String appName;
    
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @Schema(description = "所属功能区域编码")
    private String zoneCode;
    
    @Schema(description = "部署IP地址")
    private String ipAddress;
    
    @Schema(description = "部署端口号")
    private String port;
    
    @Schema(description = "部署地址")
    private String placeCode;
    
    @Schema(description = "权限列表", 
           example = "[\"api:device:read\", \"api:video:stream\"]")
    private List<String> permissions;
    
    @Schema(description = "令牌有效期(秒)", example = "7200")
    private Long expiresTime;
}
