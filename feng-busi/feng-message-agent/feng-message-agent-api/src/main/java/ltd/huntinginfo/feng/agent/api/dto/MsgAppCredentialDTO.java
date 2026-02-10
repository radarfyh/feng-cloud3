package ltd.huntinginfo.feng.agent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 应用认证凭证数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "应用认证凭证DTO", description = "应用认证凭证数据传输对象，包含应用密钥和访问控制信息")
public class MsgAppCredentialDTO {
    
    @Schema(description = "唯一标识UUID")
    private String id;
    
    @NotBlank(message = "应用标识不能为空")
    @Size(max = 64, message = "应用标识长度不能超过64个字符")
    @Schema(description = "应用唯一标识(全局唯一)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;
    
//    @Size(max = 20, message = "系统编码长度不能超过20个字符")
//    @Schema(description = "系统编码（用于消息中心对接）")
//    private String sysCode;
    
    @NotBlank(message = "应用密钥不能为空")
    @Size(max = 256, message = "应用密钥长度不能超过256个字符")
    @Schema(description = "AES加密后的应用密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appSecret;
    
    @NotBlank(message = "应用类型不能为空")
    @Size(max = 2, message = "应用类型长度不能超过2个字符")
    @Schema(description = "应用类型:01-设备管理平台 02-执法系统 03-标准组件...", 
           allowableValues = {"01","02","03","04","05","06","07"}, 
           requiredMode = Schema.RequiredMode.REQUIRED)
    private String appType;
    
    @NotBlank(message = "应用名称不能为空")
    @Size(max = 100, message = "应用名称长度不能超过100个字符")
    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appName;
    
    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
    private Integer status;
    
    @Size(max = 100, message = "单位编码长度不能超过100个字符")
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @Size(max = 100, message = "区域编码长度不能超过100个字符")
    @Schema(description = "所属功能区域编码")
    private String zoneCode;
    
    @Schema(description = "密钥过期时间(NULL表示永久有效)")
    private Date expireTime;
    
    @Size(max = 100, message = "IP地址长度不能超过100个字符")
    @Schema(description = "部署IP地址")
    private String ipAddress;
    
    @Size(max = 100, message = "端口长度不能超过100个字符")
    @Schema(description = "部署端口")
    private String port;
    
    @Size(max = 100, message = "场所编码长度不能超过100个字符")
    @Schema(description = "部署地址（所属场所）")
    private String placeCode;
    
    @Schema(description = "IP白名单列表(JSON数组格式)")
    private List<String> ipWhitelistList;
    
    @Schema(description = "API调用速率限制(次/分钟)", defaultValue = "1000")
    private Integer rateLimit;
    
    @Size(max = 256, message = "首页地址长度不能超过256个字符")
    @Schema(description = "应用系统所提供的首页地址", example = "http://data-platform:9002/#/home")
    private String homeUrl;
    
    @Size(max = 256, message = "图标地址长度不能超过256个字符")
    @Schema(description = "系统图标地址", example = "http://file-system:9000/123456.png")
    private String appIcon;
    
    @Size(max = 1000, message = "应用描述长度不能超过1000个字符")
    @Schema(description = "应用系统描述")
    private String appDesc;
    
    @Size(max = 500, message = "回调地址长度不能超过500个字符")
    @Schema(description = "消息回调地址")
    private String callbackUrl;
    
    @Size(max = 20, message = "认证模式长度不能超过20个字符")
    @Schema(description = "回调认证模式:standard/legacy", defaultValue = "standard")
    private String callbackAuthMode;
    
    @Size(max = 6, message = "行政区划代码长度不能超过6个字符")
    @Schema(description = "默认申请单位行政区划代码")
    private String defaultSqssdm;
    
//    @Size(max = 12, message = "单位代码长度不能超过12个字符")
//    @Schema(description = "默认申请单位代码")
//    private String defaultSqdwdm;
    
    @Size(max = 200, message = "单位名称长度不能超过200个字符")
    @Schema(description = "默认申请单位名称")
    private String defaultSqdwmc;
    
    @Size(max = 100, message = "申请人姓名长度不能超过100个字符")
    @Schema(description = "默认申请人姓名")
    private String defaultSqrxm;
    
    @Size(max = 18, message = "证件号码长度不能超过18个字符")
    @Schema(description = "默认申请人证件号码")
    private String defaultSqrzjhm;
    
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    @Schema(description = "默认申请人电话")
    private String defaultSqrdh;
    
    @Size(max = 600, message = "Token长度不能超过600个字符")
    @Schema(description = "部级消息中心Token")
    private String centerToken;
    
    @Schema(description = "部级消息中心Token过期时间")
    private Date centerExpireTime;
    
    @Schema(description = "业务系统Token")
    private String appToken;
    
    @Schema(description = "业务系统Token过期时间")
    private Date appExpireTime;
    
    @Schema(description = "创建者")
    private String createBy;
    
    @Schema(description = "更新者")
    private String updateBy;
    
    @Schema(description = "过期时间查询开始时间")
    private Date expireTimeStart;
    
    @Schema(description = "过期时间查询结束时间")
    private Date expireTimeEnd;
    
    @Schema(description = "Token过期时间查询开始时间")
    private Date tokenExpireTimeStart;
    
    @Schema(description = "Token过期时间查询结束时间")
    private Date tokenExpireTimeEnd;
    
    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
    private String delFlag;
}

