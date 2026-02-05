package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 系统角色表(SysRole)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysRole")
@EqualsAndHashCode(callSuper = true)
public class SysRole extends Model<SysRole> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "Id")
    private Integer id;
    
    @Schema(description = "所属租户ID")
    private Integer tenantId;
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String roleCode;
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String roleDesc;
    /**
     * 数据权限类型：0-全部 1-自定义
     */
    @Schema(description = "数据权限类型：0-全部 1-自定义")
    private Integer dsType;
    /**
     * 数据权限作用范围：科室id逗号隔开
     */
    @Schema(description = "数据权限作用范围：科室id逗号隔开")
    private String dsScope;
    /**
     * 角色类型:0-系统角色 1-自定义角色
     */
    @NotBlank(message = "角色类型不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "角色类型:0-系统角色 1-自定义角色")
    private String type;
    /**
     * 所属机构编码
     */
//    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 角色有效开始时间
     */
    @Schema(description = "角色有效开始时间")
    private LocalDateTime roleStartTime;
    /**
     * 角色有效结束时间
     */
    @Schema(description = "角色有效结束时间")
    private LocalDateTime roleEndTime;
    /**
     * 是否内置角色：0-否 1-是
     */
    @Schema(description = "是否内置角色：0-否 1-是")
    private String isDefault;
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
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}
