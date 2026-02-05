package work.metanet.feng.admin.api.dto;

import work.metanet.feng.common.core.constant.enums.Gender;
import work.metanet.feng.common.core.util.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName：UserFirstLogin
 * @author edison
 * @Date: 2022/11/11 15:12
 * @Description: 首次登录修改用户基本信息 功能模块
 */
@Data
public class UserFirstLogin {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "用户名")
    private String username;

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "原密码")
    private String password;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "新密码")
    private String newpassword1;

    /**
     * 所属机构编码
     */
    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "所属机构编码")
    private String organCode;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String staffName;

    /**
     * 性别编码
     */
    @Schema(description = "性别编码")
    private Gender sexCode;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String identificationNo;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    private String birthdate;

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


}
