package work.metanet.feng.admin.api.vo;

import work.metanet.feng.admin.api.entity.SysStaff;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：人员角色科室基本信息
 * @author edison
 * @Date: 2022/9/20 15:56
 * @Description: StaffRoleDeptVO 功能模块
 */
@Data
public class StaffRoleDeptVO {

    @Schema(description = "人员的基本信息")
    private SysStaff sysStaff;

    @Schema(description = "角色集合信息")
    private List<SysRoleVO> sysRoleVOS;

    @Schema(description = "科室基本信息")
    private SysDepartmentVO sysDepartmentVO;

}
