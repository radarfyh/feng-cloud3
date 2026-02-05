package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 菜单权限表(SysMenu)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysMenu")
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "菜单ID")
    private Integer id;
    /**
     * 菜单名称
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @NotBlank(message = "菜单名称不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "菜单名称")
    private String menuName;
    /**
     * 按钮权限唯一标识
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @Schema(description = "按钮权限唯一标识")
    private String permission;
    /**
     * 前端路径
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @Schema(description = "前端路径")
    private String path;
    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Integer parentId;
    /**
     * 图标
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @Schema(description = "图标")
    private String icon;
    /**
     * 菜单描述
     */
    @Schema(description = "菜单描述")
    private String menuDescribe;
    /**
     * 排序值
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @Schema(description = "排序值")
    private Integer sort;
    /**
     * 是否开启路由缓冲 0-否 1-是
     */
    @Schema(description = "是否开启路由缓冲 0-否 1-是")
    private String keepAlive;
    /**
     * 类型 0-菜单 1-按钮
     */
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    @Schema(description = "类型 0-菜单 1-按钮")
    private String type;
    /**
     * 平台应用id：用户中心、客户主索引、主数据管理、监控管理等
     */
    @NotNull(message = "平台应用编码不能为空", groups = {ValidGroup.Save.class})
    @Schema(description = "平台应用编码：用户中心等")
    private String applicationCode;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
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