package work.metanet.feng.admin.api.vo;

import work.metanet.feng.admin.api.entity.SysStaff;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 人员信息VO（视图对象）
 * <p>
 * 该类用于封装人员信息的视图层数据，继承自 SysStaff 实体类，并添加了额外的字段如账号开通状态、科室名称和科室编码。
 * </p>
 * 
 * @author edison
 * @date 2022/5/25
 */
@Data
@Schema(description = "人员信息VO")
public class SysStaffVO extends SysStaff {

    /**
     * 是否开通账号
     * <p>
     * 该字段表示用户账号是否已经开通，0表示未开通，1表示已开通。
     * </p>
     */
    @Schema(description = "是否开通账号:0-否 1-是")
    private String accountOpened;

    /**
     * 科室名称
     * <p>
     * 该字段表示用户所属的科室名称。
     * </p>
     */
    @Schema(description = "科室名称")
    private String deptName;

    /**
     * 科室编码
     * <p>
     * 该字段表示用户所属的科室编码，用于标识科室。
     * </p>
     */
    @Schema(description = "科室编码")
    private String deptCode;
}
