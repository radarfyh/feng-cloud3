package ltd.huntinginfo.feng.agent.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("msg_auth_log")
@Schema(name = "认证日志", description = "记录应用认证请求的日志信息")
public class MsgAuthLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "认证类型:APPKEY/TOKEN")
    private String authType;

    @Schema(description = "请求IP")
    private String requestIp;

    @Schema(description = "客户端UA")
    private String userAgent;

    @Schema(description = "状态:0-失败 1-成功")
    private Integer status;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "耗时(ms)")
    private Integer costTime;

    @Schema(description = "请求唯一ID")
    private String requestId;

    @Schema(description = "请求时间戳")
    private Long timestamp;

    @Schema(description = "防重放随机值")
    private String nonce;

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
