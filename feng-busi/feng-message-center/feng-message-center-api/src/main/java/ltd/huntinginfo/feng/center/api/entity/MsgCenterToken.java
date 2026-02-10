//package ltd.huntinginfo.feng.center.api.entity;
//
//import com.baomidou.mybatisplus.annotation.*;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@TableName("msg_center_token")
//@Schema(name = "部级Token管理", description = "部级消息中心Token管理实体")
//public class MsgCenterToken implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @TableId
//    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String id;
//
//    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String appKey;
//    
//    @Schema(description = "部级消息中心Token", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String centerToken;
//    
//    @Schema(description = "Token类型:BEARER", defaultValue = "BEARER")
//    private String tokenType;
//    
//    @Schema(description = "Token过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
//    private Date expireTime;
//    
//    @Schema(description = "刷新次数", defaultValue = "0")
//    private Long refreshCount;
//    
//    @Schema(description = "总请求次数", defaultValue = "0")
//    private Integer totalRequests;
//    
//    @Schema(description = "成功请求次数", defaultValue = "0")
//    private Integer successRequests;
//    
//    @Schema(description = "上次请求时间")
//    private Date lastRequestTime;
//    
//    @Schema(description = "上次请求API")
//    private String lastRequestApi;
//    
//    @Schema(description = "状态:0-失效 1-有效", defaultValue = "1")
//    private Integer status;
//
//    @TableField(fill = FieldFill.INSERT)
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @TableField(fill = FieldFill.UPDATE)
//    @Schema(description = "修改时间")
//    private Date updateTime;
//}