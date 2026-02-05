package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.Dict;
import work.metanet.feng.admin.api.dto.MailConditionDTO;
import work.metanet.feng.admin.api.dto.UserDTO;
import work.metanet.feng.admin.api.dto.UserFirstLogin;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.vo.UserVO;
import work.metanet.feng.common.core.constant.enums.OperationTypeEnum;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 用户表(SysUser)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 查询用户信息
     *
     * @param sysUser 用户
     * @return userInfo
     */
    UserInfo findUserInfo(SysUser sysUser);

    /**
     * 分页查询用户信息（含有角色信息）
     *
     * @param page    分页对象
     * @param userDTO 参数列表
     * @return
     */
    IPage getUsersWithRolePage(Page page, UserDTO userDTO);

    /**
     * 删除用户
     *
     * @param sysUser 用户
     * @return boolean
     */
    R deleteUserById(SysUser sysUser);

    /*
     *
     * @Description: 重置密码
     * @author edison
     * @date 2021/3/26
     * @param: username 用户名
     * @param: password 密码
     * @return
     */
    R resetPassword(SysUser sysUser);

    /**
     * 修改自己的密码
     *
     * @param userDto 用户信息
     * @return Boolean
     */
    R updatePassword(UserDTO userDto, String token);

    /**
     * 更新指定用户信息
     *
     * @param userDto 用户信息
     * @return
     */
    R updateUser(UserDTO userDto);

    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO selectUserVoById(Integer id);

    /**
     * 查询上级科室对应的所有用户信息
     *
     * @param username 用户名
     * @return R
     */
    List<SysUser> listAncestorUsers(String username);

    /**
     * 保存用户信息
     *
     * @param userDto DTO 对象
     * @return success/fail
     */
    R saveUser(UserDTO userDto);


    /*
     *
     * @Description:通过角色id获取所有的用户集合
     * @author edison
     * @date 2021/1/22
     * @param: roleId
     * @return
     */
    List<SysUser> findRoleByUsers(Integer roleId);

    /**
     * 锁定用户
     *
     * @param username
     * @return
     */
    R lockUser(String username);
    /**
     * 修改头像和性别
     *
     * @param staffId 人员ID
     * @param sysStaff 包含头像和性别的人员信息
     * @return
     */
    R updateUserByStaffId(Integer staffId, SysStaff sysStaff);
    
    /**
     * 条件获取站内信用户id集合【内部接口】
     *
     * @param mailConditionDTO
     * @return
     */
    R<List<SysUser>> mailUserList(MailConditionDTO mailConditionDTO);

    /**
     * 用户首次登录
     *
     * @param firstLogin:
     * @param token:
     * @return R
     */
    R firstLogin(UserFirstLogin firstLogin, String token);

    /**
     * 通过用户id查询对应人员信息
     *
     * @param userId
     * @return
     */
    R<SysStaff> getStaffByUserId(Integer userId);
    
    Dict getCount();

	/**
	 * 用户参数校验
	 * <p>
	 * 校验用户名称和员工ID是否已存在，避免重复数据。
	 * </p>
	 *
	 * @param sysUser 用户实体
	 * @param type    操作类型：OperationTypeEnum
	 * @return 校验结果：0-没有错误， 1-用户名重复，2-员工ID已存在用户名，8-sysUser传入错误，9-类型传入错误
	 */
	Integer sysUserCheck(SysUser sysUser, OperationTypeEnum type);
}