package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 字典项(SysDictItem)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysDictItem")
@EqualsAndHashCode(callSuper = true)
public class SysDictItem extends Model<SysDictItem> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "编号")
    private Integer id;
    /**
     * 字典id
     */
    @NotNull(message = "字典id不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "字典id")
    private Integer dictId;
    /**
     * 字典项value:0、1、2、3
     */
    @Schema(description = "字典项value:0、1、2、3")
    private String value;
    /**
     * 字典项Value备注
     */
    @Schema(description = "字典项Value备注")
    private String label;
    /**
     * 所属字典key
     */
    @Schema(description = "所属字典key")
    private String dictKey;
    /**
     * 字典项描述
     */
    @Schema(description = "字典项描述")
    private String description;
    /**
     * 排序（升序）
     */
    @Schema(description = "排序（升序）")
    private Integer sort;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;
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