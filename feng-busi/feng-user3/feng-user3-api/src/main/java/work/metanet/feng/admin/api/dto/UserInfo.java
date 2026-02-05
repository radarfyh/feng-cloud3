package work.metanet.feng.admin.api.dto;

import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.common.core.constant.enums.JobCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserInfo
 * @author edison
 * @Date 2022/4/28 14:36
 **/
@Data
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 用户基本信息
     */
    @Schema(description = "用户基本信息")
    private SysUser sysUser;

    /**
     * 权限标识集合
     */
    @Schema(description = "权限标识集合")
    private String[] permissions;

    /**
     * 角色集合
     */
    @Schema(description = "角色标识集合")
    private Integer[] roles;

    /**
     * 人员岗位类别
     */
    @Schema(description = "人员岗位类别")
    private JobCategory jobCategory;

    /**
     * 科室编码
     */
    @Schema(description = "科室编码")
    private String deptCode;


}
