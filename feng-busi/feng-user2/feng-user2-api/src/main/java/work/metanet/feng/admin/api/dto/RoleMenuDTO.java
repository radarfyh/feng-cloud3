package work.metanet.feng.admin.api.dto;

import work.metanet.feng.common.core.util.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoleMenuDTO {

    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "平台应用编码：用户中心等")
    private String applicationCode;

    /**
     * 菜单列表
     */
//    @NotBlank(message = "menuIds不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "菜单列表逗号隔开【,】")
    private String menuIds;
}
