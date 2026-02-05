package work.metanet.feng.admin.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：UserRoleDTO
 * @author edison
 * @Date: 2022/8/12 16:52
 * @Description: UserRoleDTO 功能模块
 */
@Data
@Schema(description = "批量用户分配多角色DTO")
public class UserRoleDTO {

    /**
     * 用户id集合
     */
    @Schema(description = "用户id集合")
    private List<Integer> userIds;

    /**
     * 角色id集合
     */
    @Schema(description = "角色id集合")
    private List<Integer> roleIds;
}
