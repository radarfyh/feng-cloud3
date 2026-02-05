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
 * 系统配置表(SysConfig)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysConfig")
@EqualsAndHashCode(callSuper = true)
public class SysConfig extends Model<SysConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 配置编号
     */
    @NotNull(message = "配置编号不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "配置编号")
    private Integer no;
    /**
     * 配置编码
     */
    @NotBlank(message = "配置编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "配置编码")
    private String code;
    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "配置值")
    private String value;
    /**
     * 配置描述
     */
    @Schema(description = "配置描述")
    private String desc;
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