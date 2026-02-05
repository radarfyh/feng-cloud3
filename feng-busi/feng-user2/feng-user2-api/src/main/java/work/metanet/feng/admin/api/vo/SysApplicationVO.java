package work.metanet.feng.admin.api.vo;/**
 * @ClassName: SysApplicationVO
 * @Description: TODO
 * @Date: 2022/5/23 12:18
 * @author edison
 */

import work.metanet.feng.admin.api.entity.SysApplication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName SysApplicationVO
 * @Deacription 应用VO
 * @author edison
 * @Date 2022/5/23 12:18
 **/
@Data
@Schema(description = "应用VO")
public class SysApplicationVO extends SysApplication {

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "角色id")
    private Integer roleId;
}
