package work.metanet.feng.admin.api.dto;

import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName：SysStaffDeptDTO
 * @author edison
 * @Date: 2022/12/26 9:42
 * @Description: SysStaffDeptDTO 功能模块
 */
@Data
@Schema(description = "人员科室关联DTO")
public class SysStaffDeptDTO {

    /**
     * 人员id
     */
    @Schema(description = "人员id")
    private Integer staffId;

    /**
     * 科室id集合
     */
    @NotBlank(message = "科室id集合不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "科室id集合逗号隔开【,】")
    private String deptIds;
}
