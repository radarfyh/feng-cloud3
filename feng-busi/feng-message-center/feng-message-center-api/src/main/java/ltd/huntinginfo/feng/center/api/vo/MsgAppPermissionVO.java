package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Schema(name = "应用权限VO", description = "应用权限视图对象，定义应用可访问的资源")
public class MsgAppPermissionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    private String id;

    @Schema(description = "关联应用标识")
    private String appKey;

    @Schema(description = "资源标识符(格式:服务:资源:操作)", 
           example = "api:device:read")
    private String resourceCode;

    @Schema(description = "资源描述")
    private String resourceName;

    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
    private Integer status;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "修改时间")
    private Date updateTime;
}
