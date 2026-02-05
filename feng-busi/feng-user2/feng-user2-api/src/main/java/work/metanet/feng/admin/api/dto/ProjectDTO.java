package work.metanet.feng.admin.api.dto;

import work.metanet.feng.admin.api.entity.SysProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 项目传输DTO
 *
 * @author edison
 * @since 2023-10-01
 */
@Data
@Schema(description = "项目传输DTO")
public class ProjectDTO {

    /**
     * 操作类型：0-删除 1-新增 2-修改
     */
    @Schema(description = "操作类型：0-删除 1-新增 2-修改")
    private String type;

    /**
     * 项目集合
     */
    @Schema(description = "项目集合")
    private List<SysProject> projectList;
}
