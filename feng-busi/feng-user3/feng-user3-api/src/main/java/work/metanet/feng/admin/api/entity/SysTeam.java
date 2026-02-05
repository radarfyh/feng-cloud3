package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 小组(SysTeam)实体类
 *
 * @author edison
 * @since 2023-08-02 09:53:53
 */
@Data
@Schema(description = "小组")
@EqualsAndHashCode(callSuper = true)
public class SysTeam extends Model<SysTeam> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 联盟id
     */
    @Schema(description = "联盟id")
    private Integer affiliationId;
    /**
     * 名称
     */
    @Schema(description = "小组名称")
    private String teamName;
    /**
     * 小组编码
     */
    @Schema(description = "小组编码")
    private String teamCode;
    /**
     * 状态:0-启用 1-禁用
     */
    @Schema(description = "状态:0-启用 1-禁用")
    private String status;
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
    private String delFlag;


}
