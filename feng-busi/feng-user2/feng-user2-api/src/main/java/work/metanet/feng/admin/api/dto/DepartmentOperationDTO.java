package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "科室传输DTO")
public class DepartmentOperationDTO {

    /**
     * 操作类型：0-删除 1-新增/修改
     */
    @Schema(description = "操作类型：0-删除 1-新增/修改")
    private String type;

    /**
     * 科室集合
     */
    @Schema(description = "科室集合")
    private List<DepartmentAttributeDTO> departmentDTOList;
}
