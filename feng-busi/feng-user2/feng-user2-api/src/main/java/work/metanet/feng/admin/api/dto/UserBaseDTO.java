package work.metanet.feng.admin.api.dto;

import work.metanet.feng.common.core.util.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName UserBaseDTO
 * @author edison
 * @Date 2022/5/20 16:03
 **/
@Data
public class UserBaseDTO implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "用户ID不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "用户ID")
    private Integer id;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;
    /**
     * 人员ID
     */
    @Schema(description = "人员ID")
    private Integer staffId;
    /**
     * 科室ID
     */
    @Schema(description = "科室ID")
    private Integer deptId;

    /**
     * 切换机构编码
     */
    @Schema(description = "切换机构编码")
    private String switchCode;

    /**
     * 过期时间，定时任务条件查询修改过期状态
     */
    @Schema(description = "过期时间，定时任务条件查询修改过期状态")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    /**
     * 帐户是否过期（0-false 1-true）
     */
    @Schema(description = "帐户是否过期（0-false 1-true）")
    private String expiredFlag;
    /**
     * 账号状态:0-启用 1-禁用
     */
    @Schema(description = "账号状态:0-启用 1-禁用")
    private String status;
    /**
     * 账号锁定:0-正常 1-锁定
     */
    @Schema(description = "账号锁定:0-正常 1-锁定")
    private String lockFlag;
}
