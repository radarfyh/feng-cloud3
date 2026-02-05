package work.metanet.feng.admin.api.dto;

import work.metanet.feng.admin.api.entity.SysDepartment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "科室传输DTO")
public class DepartmentAttributeDTO extends SysDepartment {

    /**
     * 科室属性编码集合
     */
    @Schema(description = "科室属性编码集合")
    private List<String> deptAttributeList;
}
