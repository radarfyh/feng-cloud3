package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日志查询传输对象
 */
@Data
@Schema(description = "日志查询对象")
public class SysLogDTO {

    @Schema(description = "id")
    private Long id;
    
    @Schema(description = "操作分类")
    private String type;
    
    @Schema(description = "操作名称")
    private String title;
    
    // 请求信息
    
    @Schema(description = "访问服务名")
    private String serviceId;

    @Schema(description = "访问IP地址")
    private String remoteAddr;

    @Schema(description = "请求方式:User-Agent会告诉网站服务器，访问者是通过什么工具来请求的")
    private String userAgent;

    @Schema(description = "请求路径")
    private String requestUri;

    @Schema(description = "请求类型：POST GET PUT")
    private String method;

    @Schema(description = "请求参数（路径带的参数）")
    private String params;
    
    @Schema(description = "请求头")
    private String requestHeader;
    
    @Schema(description = "请求体JSON对象")
    private String requestData;
    
    @Schema(description = "租户ID")
	private Integer tenantId;
    
    @Schema(description = "请求之应用代码")
    private String appKey;
    
    @Schema(description = "请求之应用密钥")
    private String appSecret;
    
    @Schema(description = "请求之应用凭证")
    private String appToken;

    // 性能信息
    
    @Schema(description = "执行时间")
    private Long time;
    
    // 响应信息

    @Schema(description = "响应头")
    private String header;
    
    @Schema(description = "异常信息")
    private String exception;
    
    @Schema(description = "响应时间")
    private LocalDateTime responseTime;
    
    @Schema(description = "响应状态码")
    private String status;
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "响应结果JSON对象")
    private String data;

    @Schema(description = "响应结果代码 0成功 1失败")
    private String code;
    
    @Schema(description = "应用凭证令牌")
    private String token;

    @Schema(description = "响应状态码文本")
    private String statusText;
    
    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
