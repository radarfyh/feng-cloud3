package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 项目成员数据传输对象(ProjectStaffDTO)
 * <p>
 * 该类用于传递项目成员的相关信息，包括项目ID、角色ID、所属机构编码、人员备注、人员工号等信息。
 * </p>
 * 
 * @author edison
 * @date 2023-01-31
 */
@Data
public class ProjectStaffDTO {

    /**
     * 项目id
     * <p>
     * 该字段表示所属的项目ID，通常用于唯一标识一个项目。
     * </p>
     */
    @NotNull(message = "项目ID不能为空")
    @Schema(description = "项目id")
    private Integer projectId;

    /**
     * 角色id
     * <p>
     * 该字段表示项目中的角色ID，用于标识用户在项目中的角色。
     * </p>
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色id")
    private Integer roleId;

    /**
     * 所属机构编码
     * <p>
     * 该字段表示项目成员所属的机构或组织的编码。
     * </p>
     */
    @NotNull(message = "机构编码不能为空")
    @Schema(description = "所属机构编码")
    private String organCode;

    /**
     * 人员备注
     * <p>
     * 该字段用于记录项目成员的备注信息，提供更多的个性化描述。
     * </p>
     */
    @Schema(description = "人员备注")
    private String notes;

    /**
     * 人员工号集合
     * <p>
     * 该字段包含多个员工工号，通常用于批量处理项目成员的情况。
     * </p>
     */
    @NotNull(message = "工号列表不能为空")
    @Schema(description = "人员工号集合")
    private List<String> staffNoList;

    /**
     * 检查是否有人员工号
     * <p>
     * 该方法用于检查人员工号集合是否为空。
     * </p>
     *
     * @return true 如果工号集合不为空，false 如果为空
     */
    public boolean hasStaffNo() {
        return staffNoList != null && !staffNoList.isEmpty();
    }
}
