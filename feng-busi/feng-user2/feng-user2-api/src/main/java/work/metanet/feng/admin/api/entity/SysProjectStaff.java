package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目员工表实体类
 * <p>
 * 此类表示项目员工表，用于存储项目与员工之间的关联信息，包括项目ID、角色ID、员工工号以及员工备注等信息。
 * </p>
 * 
 * @author edison
 * @since 2023-01-31 09:46:50
 */
@Data
@Schema(description = "项目员工表，包含项目ID、角色ID、员工工号及备注等信息")
@EqualsAndHashCode(callSuper = true)
public class SysProjectStaff extends Model<SysProjectStaff> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    /**
     * 项目ID
     * <p>
     * 表示该员工所在的项目的唯一标识符。
     * </p>
     */
    @Schema(description = "项目ID", requiredMode = RequiredMode.REQUIRED)
    private Integer projectId;

    /**
     * 角色ID
     * <p>
     * 表示员工在该项目中的角色，通常与角色表关联。
     * </p>
     */
    @Schema(description = "角色ID", requiredMode = RequiredMode.REQUIRED)
    private Integer roleId;

    /**
     * 员工ID
     * <p>
     * 员工在公司或项目中的内部存储标识符。
     * </p>
     */
    @Schema(description = "员工ID", requiredMode = RequiredMode.REQUIRED)
    private Integer staffId;
    
    /**
     * 员工工号
     * <p>
     * 员工在公司或项目中的唯一编码。
     * </p>
     */
    @Schema(description = "员工工号", requiredMode = RequiredMode.REQUIRED)
    private String staffNo;

    /**
     * 备注
     * <p>
     * 用于存储备注，可以为空。
     * </p>
     */
    @Schema(description = "备注")
    private String notes;

}
