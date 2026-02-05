package work.metanet.feng.admin.api.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import work.metanet.feng.admin.api.entity.SysRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author edison
 * @date 2017/10/29
 */
@Data
@Schema(description = "前端用户展示对象")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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
    private String sexCode;
    
    /**
     * 性别名称
     */
    @Schema(description = "性别名称")
    private String sexName;
    
    /**
     * 随机盐
     */
    @Schema(description = "随机盐")
    private String salt;
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
     * 人员名称
     */
    @Schema(description = "人员名称")
    private String staffName;
    /**
     * 科室ID
     */
    @Schema(description = "科室ID")
    private Integer deptId;
    /**
     * 所属机构编码
     */
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
    
	/**
	 * 微信openid
	 */
	@Schema(description = "微信open id")
	private String wxOpenid;

	/**
	 * QQ openid
	 */
	@Schema(description = "qq open id")
	private String qqOpenid;

	/**
	 * gitee openid
	 */
	@Schema(description = "gitee open id")
	private String giteeOpenId;

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
    /**
     * 科室名称
     */
    @Schema(description = "科室名称")
    private String deptName;
    /**
     * 角色列表
     */
    @Schema(description = "拥有的角色列表")
    private List<SysRole> roles;

    /**
     * 角色id集合
     */
    @Schema(description = "角色id集合")
    private List<Integer> roleList;


}
