package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * AUS认证日志数据传输对象
 */
@Data
@Schema(name = "AUS认证日志DTO", description = "AUS认证日志数据传输对象")
public class AusAuthLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID，自增主键", example = "123456")
    private Long id;

    // 请求体参数
    @Schema(description = "请求时间", example = "2025-07-12 10:10:10", requiredMode = RequiredMode.REQUIRED)
    private Date requestTime;
    
    @Schema(description = "请求URL", example = "GET", requiredMode = RequiredMode.REQUIRED)
    private String requestUrl;
    
    @Schema(description = "请求方法（GET/POST等）", example = "GET", requiredMode = RequiredMode.REQUIRED)
    private String requestMethod;
    
    @Schema(description = "请求内容类型", example = "application/json", requiredMode = RequiredMode.REQUIRED)
    private String requestContentType;
    
    @Schema(description = "应用代码", example = "REMOTE_AV_PLATFORM")
    private String requestAppKey;
    
    @Schema(description = "应用密钥", example = "Rapc4d2e6f8a9b7c5d3e1f2b3e5f12345")
    private String requestAppSecret;
    
    @Schema(description = "应用凭证", example = "ee07ec55-5474-4f82-ab2b-774ede7f8caf")
    private String requestAppToken;
    
    @Schema(description = "登录类型：0 个人，1 单位", example = "0", requiredMode = RequiredMode.REQUIRED)
    private String requestLoginType;
    
    @Schema(description = "用户名", example = "13585823603", requiredMode = RequiredMode.REQUIRED)
    private String requestUsername;
    
    @Schema(description = "重定向路径", example = "http://10.226.118.234:8888/login")
    private String requestRedirectPath;
    
    @Schema(description = "子应用类型 01-设备管理（子）平台 02-执法办案管理系统 03-标准组件 04-采集设备 05-远程讯问视音频设备 06-同步录音录像设备 07-签摁终端"
    		, example = "01")
    private String requestAppType;

    // 返回数据
    @Schema(description = "本地令牌", example = "ee07ec55-5474-4f82-ab2b-774ede7f8caf", requiredMode = RequiredMode.REQUIRED)
    private String localToken;
    
    @Schema(description = "响应参数-访问令牌", example = "ee07ec55-5474-4f82-ab2b-774ede7f8caf", requiredMode = RequiredMode.REQUIRED)
    private String accessToken;
     
    @Schema(description = "响应参数-刷新令牌", example = "ee07ec55-5474-4f82-ab2b-774ede7f8caf")
    private String refreshToken;
    
    @Schema(description = "过期时间", example = "2023-08-15T10:00:00")
    private Date expiresTime;
    
    @Schema(description = "用户账号", example = "zhangsan", requiredMode = RequiredMode.REQUIRED)
    private String userId;
    
    @Schema(description = "aus返回的用户唯一ID", example = "12345")
     private String ausId;

    @Schema(description = "用户所属省份代码", example = "FJ")
    private String proCode;

    @Schema(description = "省份名称", example = "浙江省")
    private String proName;
    
    @Schema(description = "用户所属城市代码", example = "QZ")
    private String cityCode;

    @Schema(description = "城市名称", example = "杭州市")
    private String cityName;

    @Schema(description = "用户所属区域代码", example = "BA")
    private String regCode;

    @Schema(description = "区域名称", example = "西湖区")
    private String regName;

    @Schema(description = "用户所属单位代码", example = "ZXJD")
    private String unitNo;

    @Schema(description = "用户所属机构代码", example = "FZ313")
    private String orgCode;

    @Schema(description = "机构名称", example = "XX市公安局")
    private String orgName;

    
    @Schema(description = "用户账号，和aus loginId相同", example = "zhangsan")
    private String username;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;
    
    @Schema(description = "用户姓名", example = "张麻子")
    private String name;

    @Schema(description = "身份证号", example = "330102199001011234")
    private String idCard;

    @Schema(description = "用户登录ip", example = "127.0.0.1")
    private String clientIp;

    @Schema(description = "用户邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobile;

    @Schema(description = "用户拥有的公共角色以及在当前应用下拥有的角色", 
            example = "app_admin,common")
    private String ausRole;

    
    @Schema(description = "用户类型(0-个人用户 1-单位用户)", example = "1", requiredMode = RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "单位账号是否激活 0:否,1:是", example = "1")
    private String isActive;
    
    @Schema(description = "警种代码")
    private String policeClassification;
    
    @Schema(description = "警种名称")
    private String classificationName;    
    
    @Schema(description = "所属公安机关上级代码")
    private String parentRegCode;
    
    @Schema(description = "所属公安机关上级名称")
    private String parentRegName;

    @Schema(description = "用户性别(0-未知 1-男 2-女)",  example = "1")
    private Integer sex;
}

