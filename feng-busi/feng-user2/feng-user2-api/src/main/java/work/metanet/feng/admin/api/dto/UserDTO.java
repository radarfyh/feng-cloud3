package work.metanet.feng.admin.api.dto;

import work.metanet.feng.admin.api.entity.SysUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @ClassName UserDTO
 * @author edison
 * @Date 2022/5/13 11:46
 **/
@Data
@Schema(description = "系统用户传输对象")
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends SysUser {

    /**
     * 角色ID
     */
    @Schema(description = "角色id集合")
    private List<Integer> roleList;

    /**
     * 科室id
     */
    @Schema(description = "科室id")
    private Integer deptId;

    /**
     * 新密码
     */
    @Schema(description = "新密码")
    private String newpassword1;
}
