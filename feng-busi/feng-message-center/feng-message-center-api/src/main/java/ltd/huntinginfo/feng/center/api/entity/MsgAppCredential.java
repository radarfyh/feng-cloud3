package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_app_credential")
@Schema(name = "应用认证凭证", description = "应用认证凭证实体，包含应用密钥和访问控制信息")
public class MsgAppCredential implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用唯一标识(全局唯一)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;
    
//    @Schema(description = "系统编码（用于消息中心对接）")
//    private String sysCode;

    @Schema(description = "AES加密后的应用密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appSecret;

    @Schema(description = "应用类型:01-设备管理平台 02-执法系统 03-标准组件...", 
           allowableValues = {"01","02","03","04","05","06","07"})
    private String appType;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appName;

    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
    private Integer status;
    
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @Schema(description = "所属功能区域编码")
    private String zoneCode;

    @Schema(description = "密钥过期时间(NULL表示永久有效)")
    private Date expireTime;

    @Schema(description = "部署IP地址")
    private String ipAddress;
    
    @Schema(description = "部署端口")
    private String port;
    
    @Schema(description = "部署地址（所属场所）")
    private String placeCode;
    
    @Schema(description = "IP白名单(JSON数组格式,空表示不限制)")
    private String ipWhitelist;

    @Schema(description = "API调用速率限制(次/分钟)", defaultValue = "1000")
    private Integer rateLimit;
    
    @Schema(description = "应用系统所提供的首页地址", example = "http://data-platform:9002/#/home")
    private String homeUrl;
    
    @Schema(description = "系统图标地址", example = "http://file-system:9000/123456.png")
    private String appIcon;
    
    @Schema(description = "应用系统描述")
    private String appDesc;
    
    @Schema(description = "消息回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调认证模式:standard/legacy", defaultValue = "standard")
    private String callbackAuthMode;
    
    @Schema(description = "默认申请单位行政区划代码")
    private String defaultSqssdm;
    
//    @Schema(description = "默认申请单位代码")
//    private String defaultSqdwdm;
    
    @Schema(description = "默认申请单位名称")
    private String defaultSqdwmc;
    
    @Schema(description = "默认申请人姓名")
    private String defaultSqrxm;
    
    @Schema(description = "默认申请人证件号码")
    private String defaultSqrzjhm;
    
    @Schema(description = "默认申请人电话")
    private String defaultSqrdh;
    
    @Schema(description = "部级消息中心Token")
    private String centerToken;
    
    @Schema(description = "部级消息中心Token过期时间")
    private Date centerExpireTime;
    
    @Schema(description = "业务系统Token")
    private String appToken;
    
    @Schema(description = "业务系统Token过期时间")
    private Date appExpireTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
    private String delFlag;
}