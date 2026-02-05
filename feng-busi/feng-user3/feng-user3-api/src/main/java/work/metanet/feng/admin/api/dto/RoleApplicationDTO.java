package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：RoleApplicationDTO
 * @author edison
 * @Date: 2022/8/9 10:51
 * @Description: RoleApplicationDTO 功能模块
 */
@Data
@Schema(description = "角色外部应用关联DTO")
public class RoleApplicationDTO {

    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "外部应用id集合")
    private List<Integer> applicationIds;
}
