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
 * 字典表(SysDict)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysDict")
@EqualsAndHashCode(callSuper = true)
public class SysDict extends Model<SysDict> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "编号")
    private Integer id;
    /**
     * 字典key：status
     */
    @NotBlank(message = "字典key不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "字典key：status")
    private String dictKey;
    /**
     * 字典描述：状态
     */
    @Schema(description = "字典描述：状态")
    private String description;
    /**
     * 是否是系统字典：0-否 1-是
     */
    @Schema(description = "是否是系统字典：0-否 1-是")
    private String isSystem;
    /**
     * 所属机构编码
     */
    @Schema(description = "所属机构编码")
    private String organCode;
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
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}