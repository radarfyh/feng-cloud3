package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.MailConditionDTO;
import work.metanet.feng.admin.api.dto.UserDTO;
import work.metanet.feng.admin.api.dto.UserFirstLogin;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.*;
import work.metanet.feng.admin.api.feign.RemoteTokenService;
import work.metanet.feng.admin.api.vo.UserVO;
import work.metanet.feng.admin.mapper.*;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.admin.service.SysMenuService;
import work.metanet.feng.admin.service.SysUserRoleService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.enums.JobCategory;
import work.metanet.feng.common.core.constant.enums.MailListGroupEnum;
import work.metanet.feng.common.core.constant.enums.OperationTypeEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.data.datascope.DataScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表(SysUser)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private final SysMenuService sysMenuService;

    private final SysRoleMapper sysRoleMapper;

    private final SysDepartmentMapper sysDepartmentMapper;

    private final SysUserRoleService sysUserRoleService;

    private final RemoteTokenService remoteTokenService;

    private final SysOrganMapper sysOrganMapper;

    private final SysStaffMapper sysStaffMapper;
    
    private final SysDictService dictService;

    /**
     * 保存用户信息
     *
     * @param userDto DTO 对象
     * @return success/fail
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveUser(UserDTO userDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
        
        // 设置初始密码为机构定义的初始密码
        String password = userDto.getPassword();
        if (StrUtil.isBlank(password)) {
            SysOrgan sysOrgan = sysOrganMapper.selectOne(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, userDto.getOrganCode()));
            password = sysOrgan.getDefaultPassword();
        }
        sysUser.setPassword(ENCODER.encode(password));
        
        //设置过期时间为1个月
        LocalDateTime expireTime = LocalDateTimeUtil.offset(LocalDateTime.now(), 1, ChronoUnit.MONTHS);
        sysUser.setExpireTime(expireTime);
        
        // 查重
        Integer ret = sysUserCheck(sysUser, OperationTypeEnum.CREATE);
        switch (ret) {
        case 1:
        	return R.failed("用户名重复");
        case 2:
        	return R.failed("该员工已有账号");
        case 8:
        case 9:
        	return R.failed("用户名或者工号不符合规则：用户名和工号不能为空，工号不能小于等于0");
        }
        
        int count = baseMapper.insert(sysUser);
        if (count <= 0) return R.failed("用户信息记录插入错误");
        
        // 处理角色
        List<SysUserRole> userRoleList = userDto.getRoleList().stream().map(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(sysUser.getId());
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());
        
        boolean batchFlag = sysUserRoleService.saveBatch(userRoleList);
        if (!batchFlag) return R.failed("用户角色调整失败");
        
        return R.ok();
    }

    @Override
    public List<SysUser> findRoleByUsers(Integer roleId) {
        return baseMapper.findRoleByUsers(roleId);
    }

    /**
     * 锁定用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#username")
    public R<Boolean> lockUser(String username) {
        SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        sysUser.setLockFlag(CommonConstants.STATUS_LOCK);
        baseMapper.updateById(sysUser);
        return R.ok();
    }
    /**
     * 修改头像和性别
     *
     * @param staffId 人员ID
     * @param sysStaff 包含头像和性别的人员信息
     * @return
     */
    @Override
    public R updateUserByStaffId(Integer staffId, SysStaff sysStaff) {
        SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getStaffId, staffId));
        sysUser.setAvatar(sysStaff.getPhotograph());
        sysUser.setSexCode(sysStaff.getGenderCode());
        if (baseMapper.updateById(sysUser) > 0) {
        	return R.ok();
        }
        else {
        	return R.failed("按员工ID修改用户失败");
        }
    }
    
    @Override
    public R<List<SysUser>> mailUserList(MailConditionDTO mailConditionDTO) {
        //针对员工的通讯录获取：userId、email、phone等
        String groupCode = mailConditionDTO.getGroupCode();
        List<SysUser> sysUsers = null;
        if ((MailListGroupEnum.GROUP).equals(groupCode)) {
            //groupCode->全单位->当前机构的用户推送
            sysUsers = this.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrganCode, mailConditionDTO.getOrganCode()));
        } else if ((MailListGroupEnum.BRANCH).equals(groupCode)) {
            //groupCode->分支->当前机构的用户推送
            sysUsers = this.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrganCode, mailConditionDTO.getOrganCode()));
        } else if ((MailListGroupEnum.DEPT).equals(groupCode)) {
            //groupCode->科室->当前机构下所有科室的用户推送
            sysUsers = baseMapper.getListByDeptCode(mailConditionDTO.getOrganCode(), mailConditionDTO.getDeptCode());
        } else if ((MailListGroupEnum.STAFF).equals(groupCode)) {
            //groupCode->员工->当前机构下所有岗位类别为员工的用户推送
            sysUsers = baseMapper.getListByJobCategory(mailConditionDTO.getOrganCode(), mailConditionDTO.getJobCategory());
        }
        
        log.debug("mailUserList --> sysUsers: {}", sysUsers);
        return R.ok(sysUsers);
    }

    /**
     * 用户首次登录
     *
     * @param firstLogin:
     * @param token:
     * @return R
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R firstLogin(UserFirstLogin firstLogin, String token) {
        //修改当前用户密码
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(firstLogin.getUsername());
        userDTO.setPassword(firstLogin.getPassword());
        userDTO.setNewpassword1(firstLogin.getNewpassword1());
        userDTO.setOrganCode(firstLogin.getOrganCode());
        userDTO.setNickName(firstLogin.getStaffName());
        userDTO.setFirstLogin("0");
        userDTO.setAvatar(firstLogin.getAvatar());
        userDTO.setSexCode(firstLogin.getSexCode());
        userDTO.setPhone(firstLogin.getPhone());
        userDTO.setEmail(firstLogin.getEmail());
        updatePassword(userDTO, token);
        //修改基本信息
        SysStaff sysStaff = new SysStaff();
        sysStaff.setStaffNo(firstLogin.getUsername());
        sysStaff.setStaffName(firstLogin.getStaffName());
        sysStaff.setOrganCode(firstLogin.getOrganCode());
        sysStaff.setGenderCode(firstLogin.getSexCode());
        sysStaff.setTelephone(firstLogin.getPhone());
        sysStaff.setIdentificationNo(firstLogin.getIdentificationNo());
        if (StrUtil.isNotBlank(firstLogin.getBirthdate())) {
            Date birthdate = DateUtil.parse(firstLogin.getBirthdate(), "yyyy-MM-dd");
            sysStaff.setBirthdate(birthdate);
        }
        sysStaffMapper.update(sysStaff, Wrappers.<SysStaff>lambdaUpdate().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffNo, sysStaff.getStaffNo()));
        return R.ok();
    }

    @Override
    public R<SysStaff> getStaffByUserId(Integer userId) {
        SysUser sysUser = this.getById(userId);
        if (null != sysUser.getStaffId()) {
            SysStaff sysStaff = sysStaffMapper.selectById(sysUser.getStaffId());
            return R.ok(sysStaff);
        }
        return R.failed();
    }

    /**
     * 通过查用户的全部信息
     *
     * @param sysUser 用户
     * @return
     */
    @Override
    public UserInfo findUserInfo(SysUser sysUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setSysUser(sysUser);
        // 设置角色列表 （ID）
        List<Integer> roleIds = sysRoleMapper.listRolesByUserId(sysUser.getId()).stream().map(SysRole::getId).collect(Collectors.toList());
        userInfo.setRoles(ArrayUtil.toArray(roleIds, Integer.class));

        // 设置权限列表（menu.permission）
        Set<String> permissions = new HashSet<>();
        roleIds.forEach(roleId -> {
            List<String> permissionList = sysMenuService.findMenuByRoleId(roleId).stream().filter(menu -> StrUtil.isNotEmpty(menu.getPermission())).map(SysMenu::getPermission).collect(Collectors.toList());
            permissions.addAll(permissionList);
        });
        //查询人员岗位类别
        // 解决问题：Cause: org.postgresql.util.PSQLException: ERROR: operator does not exist: character varying = integer 2025/5/20
        SysStaff sysStaff = sysStaffMapper.selectOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysUser.getOrganCode()).eq(SysStaff::getStaffNo, sysUser.getStaffId().toString()));
        if (Objects.nonNull(sysStaff) && Objects.nonNull(sysStaff.getDeptId())) {
            SysDepartment sysDepartment = sysDepartmentMapper.selectById(sysStaff.getDeptId());
            if (Objects.nonNull(sysDepartment)){
                userInfo.setDeptCode(sysDepartment.getDeptCode());
            }
        }
        if (Objects.nonNull(sysStaff)) {
            userInfo.setJobCategory(sysStaff.getJobCategory());
        } else {
            //人员岗位类别，默认为OTHER
            userInfo.setJobCategory(JobCategory.OTHER);
        }
        userInfo.setPermissions(ArrayUtil.toArray(permissions, String.class));
        return userInfo;
    }

    /**
     * 分页查询用户信息（含有角色信息）
     *
     * @param page    分页对象
     * @param userDTO 参数列表
     * @return
     */
    @Override
    public IPage getUsersWithRolePage(Page page, UserDTO userDTO) {
    	IPage<UserVO> ret = baseMapper.getUserVosPage(page, userDTO, new DataScope());
        return ret.convert(this::convertToVO);
    }
    
    UserVO convertToVO(UserVO user) {
    	UserVO vo = new UserVO();
    	BeanUtil.copyProperties(user, vo);
        // 处理性别名称
        if (user.getSexCode() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("GENDER");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), user.getSexCode()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setSexName(label);
        }    	
    	
    	return vo;
    }

    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO selectUserVoById(Integer id) {
        return convertToVO(baseMapper.getUserVoById(id));
    }

    /**
     * 删除用户
     *
     * @param sysUser 用户
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#sysUser.username")
    public R deleteUserById(SysUser sysUser) {
    	Integer ret  = sysUserCheck(sysUser, OperationTypeEnum.DELETE);
//        switch (ret) {
//        case 1:
//        	return R.failed("用户名重复");
//        case 2:
//        	return R.failed("该员工已有账号");
//        case 8:
//        case 9:
//        	return R.failed("用户名或者工号不符合规则：用户名和工号不能为空，工号不能小于等于0");
//        }
    	
        boolean roleFlag = sysUserRoleService.deleteByUserId(sysUser.getId());
        // if (!roleFlag) return Boolean.FALSE;
        
        roleFlag = this.removeById(sysUser.getId());
        if (!roleFlag) return R.failed("删除用户失败");
        
        return R.ok();
    }

    /*
     * @Description: 重置密码
     * @param: id 用户ID
     * @param: password 新密码
     */
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#sysUser.username")
    @Override
    public R resetPassword(SysUser sysUser) {
        if (null != sysUser.getId() && StrUtil.isNotBlank(sysUser.getOrganCode())) {
            SysOrgan sysOrgan = sysOrganMapper.selectOne(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, sysUser.getOrganCode()));
            // 如果机构设置了默认密码则置为机构默认密码，否则采用参数提供的密码
            String password = StrUtil.isNotBlank(sysOrgan.getDefaultPassword()) ? sysOrgan.getDefaultPassword() : sysUser.getPassword();
            if (StrUtil.isNotBlank(password)) {
            	// 重建入参，防止污染
            	SysUser newUser = new SysUser();
            	newUser.setId(sysUser.getId());
            	newUser.setPassword(ENCODER.encode(password));
                return R.ok(this.updateById(sysUser));
            }
        }
        return R.failed("参数不能为空");
    }

    /**
     * 修改自己的密码和其他信息（注意有扩展）
     * @param id 用户ID， password 原密码， newPassword 新密码，nickName昵称，avatar头像，sexCode性别代码，phone电话，email邮箱，firstLogin首次登录标记
     * @param token 登录令牌
     */
    @Override
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
    public R updatePassword(UserDTO userDto, String token) {
        SysUser sysUser;
        if (null != userDto.getId()) {
        	// 提供ID则按ID查询用户 表
            sysUser = getById(userDto.getId());
        } else {
        	// 不提供ID则按用户名和机构代码查询用户表
            sysUser = getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, userDto.getUsername()).eq(SysUser::getOrganCode, userDto.getOrganCode()));
            if (sysUser == null || sysUser.getId() == null || sysUser.getId() <= 0) {
            	return R.failed("用户ID无法获取");
            }
        }
        
        // 校验现有密码是否正确
        if (!ENCODER.matches(userDto.getPassword(), sysUser.getPassword())) {
            log.info("原密码错误，修改个人信息失败:{}", userDto.getUsername());
            return R.failed("原密码错误，修改个人信息失败");
        }
        
        //  设置允许修改的扩展信息（nickName昵称，avatar头像，sexCode性别代码，phone电话，email邮箱，firstLogin首次登录标记）
        
        if (StrUtil.isNotBlank(userDto.getNewpassword1())) {
            sysUser.setPassword(ENCODER.encode(userDto.getNewpassword1()));
        }
        if (StrUtil.isNotBlank(userDto.getNickName())) {
            sysUser.setNickName(userDto.getNickName());
        }
        if (StrUtil.isNotBlank(userDto.getAvatar())) {
            sysUser.setAvatar(userDto.getAvatar());
        }
        if (userDto.getSexCode() != null) {
            sysUser.setSexCode(userDto.getSexCode());
        }
        if (StrUtil.isNotBlank(userDto.getPhone())) {
            sysUser.setPhone(userDto.getPhone());
        }
        if (StrUtil.isNotBlank(userDto.getEmail())) {
            sysUser.setEmail(userDto.getEmail());
        }
        if (StrUtil.isNotBlank(userDto.getFirstLogin())) {
            sysUser.setFirstLogin(userDto.getFirstLogin());
        }
        
        boolean ret = this.updateById(sysUser);
        if (!ret) return R.failed("密码修改和扩展信息修改失败");
        
        // 删除token，重新登录
        return remoteTokenService.removeTokenById(token, SecurityConstants.FROM_IN);
    }

    /**
     * 更新指定用户信息
     *
     * @param userDto 用户信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
    public R updateUser(UserDTO userDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setUpdateTime(LocalDateTime.now());
        
        //该接口不能更改用户密码
        sysUser.setPassword(null);

        // 查重
        Integer ret = sysUserCheck(sysUser, OperationTypeEnum.UPDATE);
        switch (ret) {
        case 1:
        	return R.failed("用户名重复");
        case 2:
        	return R.failed("该员工已有账号");
        case 8:
        case 9:
        	return R.failed("用户名或者工号不符合规则：用户名和工号不能为空，工号不能小于等于0");
        }
        
        // 没有重复则修改记录
        boolean updateFlag = this.updateById(sysUser);
        if (!updateFlag) return R.failed("修改用户失败"); // 修改失败
        
        // 重新建立角色映射关系
        if (null != userDto.getRoleList() && userDto.getRoleList().size() > 0) {
            sysUserRoleService.remove(Wrappers.<SysUserRole>update().lambda().eq(SysUserRole::getUserId, userDto.getId()));
            userDto.getRoleList().forEach(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(sysUser.getId());
                userRole.setRoleId(roleId);
                userRole.insert();
            });
        }
        return R.ok();
    }

    /**
     * 查询上级科室对应的所有用户信息
     *
     * @param username 用户名
     * @return R
     */
    @Override
    public List<SysUser> listAncestorUsers(String username) {
        SysUser sysUser = this.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        SysDepartment sysDepartment = sysDepartmentMapper.selectById(sysUser.getDeptId());
        if (Objects.nonNull(sysDepartment) && !sysDepartment.getParentCode().equals("0")) {
            SysDepartment parentDepartment = sysDepartmentMapper.selectOne(Wrappers.<SysDepartment>query().lambda().eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode()).eq(SysDepartment::getDeptCode, sysDepartment.getParentCode()));
            return this.list(Wrappers.<SysUser>query().lambda().eq(SysUser::getDeptId, parentDepartment.getId()));
        }
        return null;
    }

    /**
     * 用户参数校验
     * <p>
     * 校验用户名称和员工ID是否已存在，避免重复数据。
     * </p>
     *
     * @param sysUser 用户实体
     * @param type    操作类型：OperationTypeEnum
     * @return 校验结果：0-没有错误， 1-用户名重复，2-员工ID已存在超过3个用户名，8-sysUser传入错误，9-类型传入错误
     */
    @Override
    public Integer sysUserCheck(SysUser sysUser, OperationTypeEnum type) {
    	if (ObjUtil.isNull(sysUser) 
    			|| sysUser.getUsername().isBlank() 
    			|| sysUser.getStaffId() == null 
    			|| sysUser.getStaffId() <= 0) {
    		return 8; // sysUser传入错误，用户名和工号不正确，工号应非空，而且大于0
    	}
        if (type == OperationTypeEnum.CREATE) {
        	// 用户名查重
        	LambdaQueryWrapper<SysUser> queryWrapper1 = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sysUser.getUsername());
    		Long count1 = baseMapper.selectCount(queryWrapper1);
    		if (count1 > 0) return 1; // 用户名重复
    		// 员工ID查重
    		LambdaQueryWrapper<SysUser> queryWrapper2 = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getStaffId, sysUser.getStaffId());
    		Long count2 = baseMapper.selectCount(queryWrapper2);
    		if (count2 > 0) return 2; // 员工ID重

        } else if (type == OperationTypeEnum.UPDATE) {
        	// 用户名查重
        	LambdaQueryWrapper<SysUser> queryWrapper1 = Wrappers.<SysUser>lambdaQuery()
        			.eq(SysUser::getUsername, sysUser.getUsername())
        			.ne(SysUser::getId, sysUser.getId());
    		Long count1 = baseMapper.selectCount(queryWrapper1);
    		if (count1 > 0) return 1; // 用户名重复
    		// 员工ID查重
    		LambdaQueryWrapper<SysUser> queryWrapper2 = Wrappers.<SysUser>lambdaQuery()
    				.eq(SysUser::getStaffId, sysUser.getStaffId())
    				.ne(SysUser::getId, sysUser.getId())
    				;
    		Long count2 = baseMapper.selectCount(queryWrapper2);
    		if (count2 > 0) return 2; // 员工ID重

        } else if (type == OperationTypeEnum.DELETE) {
            
        } else if (type == OperationTypeEnum.READ) {
            
        } else {
        	return 9; // 类型传入错误
        }
        return 0; // 无错误
    }
    
    @Override
    public Dict getCount() {
    	return baseMapper.getCount();
    }

}