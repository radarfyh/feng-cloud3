package work.metanet.feng.admin.api.vo;/**
 * @ClassName: SysRoleVO
 * @Description: TODO
 * @Date: 2022/6/14 16:41
 * @author edison
 */

import work.metanet.feng.admin.api.entity.SysRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *@ClassName SysRoleVO
 *@Deacription TODO
 *@author edison
 *@Date 2022/6/14 16:41
 **/
@Data
@Schema(description = "角色VO对象")
public class SysRoleVO extends SysRole {

    @Schema(description = "机构名称")
    private String organName;
}
