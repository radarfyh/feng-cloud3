package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 应用系统表(SysApplication)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysApplication")
@EqualsAndHashCode(callSuper = true)
public class SysApplication extends Model<SysApplication> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "应用名称")
    private String appName;
    /**
     * 应用编码
     */
    @NotBlank(message = "应用编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "应用编码")
    private String applicationCode;
    /**
     * 应用英文名称
     */
    @Schema(description = "应用英文名称")
    private String appEnName;
    /**
     * 应用缩写名称
     */
    @Schema(description = "应用缩写名称")
    private String appAbbr;
    /**
     * 状态：0禁用-1-启用
     */
    @Schema(description = "状态：0禁用-1-启用")
    private String status;
    /**
     * 厂商id
     */
    @NotNull(message = "厂商id不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "厂商id")
    private Integer manufacturerId;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String appDesc;
    /**
     * 是否是集成门户：0-否 1-是
     */
    @Schema(description = "是否是集成门户：0-否 1-是")
    private String isFengPortal;
    /**
     * 应用类型：0-内部应用 1-外部应用
     */
    @Schema(description = "应用类型：0-内部应用 1-外部应用")
    private String fengType;
    /**
     * 标志系统的：0 B/S 、 1 CS
     */
    @Schema(description = "标志系统的：0 B/S 、 1 CS")
    private String clientType;
    /**
     * 各个服务鉴权，请求头认证code编码
     */
    @Schema(description = "各个服务鉴权，请求头认证code编码")
    private String securityCode;
    /**
     * oauth授权码编码
     */
    @Schema(description = "oauth授权码编码")
    private String oauthCode;
    /**
     * 集成url
     */
    @Schema(description = "集成url")
    private String integrationUri;
    /**
     * 参数属性
     */
    @Schema(description = "参数属性")
    private String parameterAttribute;
    /**
     * 集成应用图标地址
     */
    @Schema(description = "集成应用图标地址")
    private String appIcon;

    @Schema(description = "")
    private String isMicro;

    @Schema(description = "")
    private String microPrefix;

    @Schema(description = "")
    private String microEntry;
    /**
     * appId
     */
    @Schema(description = "appId")
    private String appId;
    /**
     * appSecret
     */
    @Schema(description = "appSecret")
    private String appSecret;
    /**
     * 管理端是否显示0-否 1-是
     */
    @Schema(description = "管理端是否显示0-否 1-是")
    private String sysIsShow;
    /**
     * 展示形式：0-普通形式1-工作台形式
     */
    @Schema(description = "展示形式：0-普通形式1-工作台形式，默认为0")
    private String displayForm;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}