package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应用认证凭证视图对象
 */
@Data
@Schema(name = "应用认证凭证VO", description = "应用认证凭证视图对象，包含应用密钥和访问控制信息")
public class MsgAppCredentialVO {
    
    @Schema(description = "唯一标识UUID")
    private String id;
    
    @Schema(description = "应用唯一标识(全局唯一)")
    private String appKey;
    
//    @Schema(description = "系统编码（用于消息中心对接）")
//    private String sysCode;
    
    @Schema(description = "应用类型")
    private String appType;
    
    @Schema(description = "应用类型名称")
    private String appTypeName;
    
    @Schema(description = "应用名称")
    private String appName;
    
    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "状态描述")
    private String statusDesc;
    
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @Schema(description = "所属单位名称")
    private String agencyName;
    
    @Schema(description = "所属功能区域编码")
    private String zoneCode;
    
    @Schema(description = "密钥过期时间")
    private Date expireTime;
    
    @Schema(description = "是否已过期")
    private Boolean expired;
    
    @Schema(description = "部署IP地址")
    private String ipAddress;
    
    @Schema(description = "部署端口")
    private String port;
    
    @Schema(description = "部署地址（所属场所）")
    private String placeCode;
    
    @Schema(description = "IP白名单列表")
    private List<String> ipWhitelist;
    
    @Schema(description = "API调用速率限制(次/分钟)")
    private Integer rateLimit;
    
    @Schema(description = "首页地址")
    private String homeUrl;
    
    @Schema(description = "系统图标地址")
    private String appIcon;
    
    @Schema(description = "应用系统描述")
    private String appDesc;
    
    @Schema(description = "消息回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调认证模式")
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
    
    @Schema(description = "部级消息中心Token是否有效")
    private Boolean tokenValid;
    
    @Schema(description = "部级消息中心Token是否即将过期")
    private Boolean tokenWillExpire;
    
    @Schema(description = "部级消息中心Token是否已过期")
    private Boolean tokenExpired;
    
    @Schema(description = "部级消息中心Token过期时间")
    private Date centerExpireTime;
    
    @Schema(description = "部级消息中心Token是否需要刷新")
    private Boolean needRefresh;
    
    @Schema(description = "业务系统Token")
    private String appToken;
    
    @Schema(description = "业务系统Token过期时间")
    private Date appExpireTime;
    
    @Schema(description = "创建者")
    private String createBy;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "更新者")
    private String updateBy;
    
    @Schema(description = "修改时间")
    private Date updateTime;
    
    @Schema(description = "应用类型映射")
    @Data
    public static class AppTypeMapping {
        public static final String DEVICE_MANAGEMENT = "01";     // 设备管理平台
        public static final String LAW_ENFORCEMENT = "02";      // 执法系统
        public static final String STANDARD_COMPONENT = "03";   // 标准组件
        public static final String SYNC_AV = "04";              // 同步录音录像管理子平台
        public static final String REMOTE_AV = "05";            // 远程讯问视音频管理子平台
        public static final String SIGN_PRESS = "06";           // 签捺管理子平台
        public static final String BIOMETRIC = "07";            // 生物识别管理子平台
        
        public static String getAppTypeName(String appType) {
            switch (appType) {
                case DEVICE_MANAGEMENT: return "设备管理平台";
                case LAW_ENFORCEMENT: return "执法系统";
                case STANDARD_COMPONENT: return "标准组件";
                case SYNC_AV: return "同步录音录像管理子平台";
                case REMOTE_AV: return "远程讯问视音频管理子平台";
                case SIGN_PRESS: return "签捺管理子平台";
                case BIOMETRIC: return "生物识别管理子平台";
                default: return "未知类型";
            }
        }
    }
}
