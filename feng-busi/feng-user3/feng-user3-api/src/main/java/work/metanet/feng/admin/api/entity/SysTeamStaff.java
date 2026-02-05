package work.metanet.feng.admin.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小组人员关联表(TeamStaff)实体类
 *
 * @author edison
 * @since 2023-08-02 09:54:37
 */
@Data
@Schema(description = "小组人员关联表")
@EqualsAndHashCode(callSuper = true)
public class SysTeamStaff extends Model<SysTeamStaff> {

    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**
     * 小组id
     */
    @Schema(description = "小组id")
    private Integer teamId;
    /**
     * 人员id
     */
    @Schema(description = "人员id")
    private Integer staffId;


}
