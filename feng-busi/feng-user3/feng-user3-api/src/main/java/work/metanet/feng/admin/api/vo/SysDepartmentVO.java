package work.metanet.feng.admin.api.vo;

import work.metanet.feng.admin.api.entity.SysDepartment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：SysDepartmentVO
 * @author edison
 * @Date: 2022/11/1 11:44
 * @Description: SysDepartmentVO 功能模块
 */
@Data
public class SysDepartmentVO extends SysDepartment {

    @Schema(description = "科室编码+名称")
    private String deptCodeAndName;

    /**
     * 科室属性编码集合
     */
    @Schema(description = "科室属性编码集合")
    private List<String> deptAttributeList;
}
