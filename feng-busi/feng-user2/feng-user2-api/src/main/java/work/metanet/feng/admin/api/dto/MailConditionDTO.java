package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName：分组条件
 * @author edison
 * @Date: 2022/9/21 15:55
 * @Description: MailConditionDTO 功能模块
 */
@Data
public class MailConditionDTO {

    /**
     * 通讯录分组
     */
    @Schema(description = "通讯录分组")
    private String groupCode;

    /**
     * 机构编码
     */
    @Schema(description = "机构编码")
    private String organCode;

    /**
     * 科室编码
     */
    @Schema(description = "科室编码")
    private String deptCode;

    /**
     * 岗位类别：员工
     */
    @Schema(description = "岗位类别：员工")
    private String jobCategory;
}
