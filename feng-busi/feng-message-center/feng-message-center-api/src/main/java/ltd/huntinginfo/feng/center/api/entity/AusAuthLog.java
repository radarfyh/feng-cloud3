package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一认证系统认证日志表实体类
 */
@Data
@TableName("msg_aus_auth_log")
@EqualsAndHashCode(callSuper = true)
@Schema(name = "统一认证日志", description = "统一认证日志表实体类")
public class AusAuthLog extends Model<AusAuthLog> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID，自增主键")
    private Long id;

    // 请求基本信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "请求时间", requiredMode = RequiredMode.REQUIRED)
    private Date requestTime;
    
    @Schema(description = "请求URL", requiredMode = RequiredMode.REQUIRED)
    private String requestUrl;
    
    @Schema(description = "请求方法（GET/POST等）", requiredMode = RequiredMode.REQUIRED)
    private String requestMethod;
    
    @Schema(description = "请求内容类型", requiredMode = RequiredMode.REQUIRED)
    private String requestContentType;

    // 请求体参数
    @Schema(description = "应用代码")
    private String requestAppKey;
    
    @Schema(description = "应用密钥")
    private String requestAppSecret;
    
    @Schema(description = "应用凭证")
    private String requestAppToken;
    
    @Schema(description = "登录类型：0 个人，1 单位", requiredMode = RequiredMode.REQUIRED)
    private String requestLoginType;
    
    @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED)
    private String requestUsername;
    
    @Schema(description = "密码")
    private String requestPassword;
    
    @Schema(description = "重定向路径")
    private String requestRedirectPath;
    
    @Schema(description = "子应用类型 01-设备管理（子）平台 02-执法办案管理系统 03-标准组件 04-采集设备 05-远程讯问视音频设备 06-同步录音录像设备 07-签摁终端")
    private String requestAppType;
    
    @Schema(description = "请求参数-访问令牌")
    private String requestAccessToken;
    
    @Schema(description = "请求参数-刷新令牌")
    private String requestRefreshToken;

    // 响应信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "响应时间")
    private Date responseTime;
    
    @Schema(description = "响应状态码")
    private String status;
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "结果代码")
    private String code;

    // 返回数据
    @Schema(description = "本地令牌", requiredMode = RequiredMode.REQUIRED)
    private String localToken;
    
    @Schema(description = "响应参数-访问令牌", requiredMode = RequiredMode.REQUIRED)
    private String accessToken;
     
    @Schema(description = "响应参数-刷新令牌")
    private String refreshToken;
    
    @Schema(description = "过期时间", example = "2023-08-15T10:00:00")
    private Date expiresTime;
    
    @Schema(description = "用户账号", example = "admin", requiredMode = RequiredMode.REQUIRED)
    private String userId;
    
    @Schema(description = "统一认证系统返回的用户唯一ID", example = "12345")
    private String ausId;

    @Schema(description = "省份编号，备用", example = "33")
    private String proId;
    
    @Schema(description = "用户所属省份代码", example = "FJ")
    private String proCode;

    @Schema(description = "省份名称", example = "浙江省")
    private String proName;

    @Schema(description = "城市编号，备用", example = "3301")
    private String cityId;
    
    @Schema(description = "用户所属城市代码", example = "QZ")
    private String cityCode;

    @Schema(description = "城市名称", example = "杭州市")
    private String cityName;

    @Schema(description = "区域编号，备用", example = "330106")
    private String regId;

    @Schema(description = "用户所属区域代码", example = "BA")
    private String regCode;

    @Schema(description = "区域名称", example = "西湖区")
    private String regName;

    @Schema(description = "用户所属单位代码", example = "ZXJD")
    private String unitNo;

    @Schema(description = "机构编号，备用", example = "ORG001")
    private String orgId;

    @Schema(description = "用户所属机构代码", example = "FZ313")
    private String orgCode;

    @Schema(description = "机构名称", example = "XX市公安局")
    private String orgName;

    
    @Schema(description = "用户账号，和loginId相同", example = "admin")
    private String username;

    @Schema(description = "用户昵称，备用", example = "系统管理员")
    private String nickname;
    
    @Schema(description = "用户姓名", example = "系统管理员")
    private String name;

    @Schema(description = "身份证号", example = "330102199001011234")
    private String idCard;

    @Schema(description = "用户登录ip", example = "127.0.0.1")
    private String clientIp;

    @Schema(description = "用户邮箱，备用", example = "user@example.com")
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

    @Schema(description = "用户性别(0-未知 1-男 2-女)，备用",  example = "1")
    private Integer sex;

    @Schema(description = "用户头像，备用", example = "/avatar/default.jpg")
    private String avatar;

    @Schema(description = "最后登录IP，备用",  example = "192.168.1.1")
    private String loginIp;

    @Schema(description = "最后登录时间，备用",  example = "2023-08-15T10:00:00")
    private Date loginDate;

    // 处理信息
    @Schema(description = "处理耗时（毫秒）")
    private Integer processTime;
    
    @Schema(description = "错误信息")
    private String errorMessage;

    // 系统审计字段
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
    
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者")
    private String updateBy;
    
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private Date updateTime;
    
    @TableLogic
    @Schema(description = "逻辑删除标记（0-正常 1-删除）")
    private String delFlag;
}
