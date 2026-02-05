package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import work.metanet.feng.common.core.constant.enums.Gender;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.LocalDateTime;

/**
 * 用户表(SysUser)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Data
@Schema(description = "SysUser")
@EqualsAndHashCode(callSuper = true)
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @Schema(description = "所属租户ID")
    private Integer tenantId;
    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "用户名")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 性别编码
     */
    @Schema(description = "性别编码")
    private Gender sexCode;
    /**
     * 随机盐
     */
    @Schema(description = "随机盐")
    private String salt;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空", groups = {ValidGroup.Save.class})
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
     * 所属机构编码
     */
    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Save.class})
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 切换机构编码
     */
    @Schema(description = "切换机构编码")
    private String switchCode;
    /**
     * 过期时间，定时任务条件查询修改过期状态
     */
    @Schema(description = "过期时间，定时任务条件查询修改过期状态")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireTime;
    /**
     * 帐户是否过期（0-false 1-true）
     */
    @Schema(description = "帐户是否过期（0-false 1-true）")
    private String expiredFlag;
    /**
     * 账号状态:0-启用 1-禁用
     */
    @Schema(description = "账号状态:0-禁用 1-启用")
    private String status;
    /**
     * 账号锁定:0-锁定 1-正常
     */
    @Schema(description = "账号锁定:0-锁定 1-正常")
    private String lockFlag;
    /**
     * 是否首次登录：0-否/1-是 默认1
     */
    @Schema(description = "是否首次登录：0-否/1-是 默认1")
    private String firstLogin;
    
	/**
	 * 微信openid
	 */
	@Schema(description = "微信openid")
	private String wxOpenid;

	/**
	 * 微信小程序openId
	 */
	@Schema(description = "微信小程序openid")
	private String miniOpenid;

	/**
	 * QQ openid
	 */
	@Schema(description = "QQ openid")
	private String qqOpenid;

	/**
	 * 码云唯一标识
	 */
	@Schema(description = "码云唯一标识")
	private String giteeLogin;

	/**
	 * 开源中国唯一标识
	 */
	@Schema(description = "开源中国唯一标识")
	private String oscId;
    
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}