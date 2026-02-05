package work.metanet.feng.admin.controller;


import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.MailConditionDTO;
import work.metanet.feng.admin.api.dto.UserBaseDTO;
import work.metanet.feng.admin.api.dto.UserDTO;
import work.metanet.feng.admin.api.dto.UserFirstLogin;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.service.SysTenantService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.enums.OperationTypeEnum;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 用户表(SysUser)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysUser")
@Tag(name = "用户模块")
@Slf4j
public class SysUserController {
	private static final String STATUS_UNLOCK = "0";
    /**
     * 服务对象
     */
    private final SysUserService sysUserService;

    private final RedisTemplate<String, String> redisTemplate;

    private final KeyStrResolver tenantKeyStrResolver;
    
    private final SysTenantService tenantService;
    
    private final CacheManager cacheManager;

    /**
     * 分页查询所有用户数据
     *
     * @param page    分页对象
     * @param userDTO 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有用户数据")
    public R getUsersWithRolePage(Page page, UserDTO userDTO) {
    	IPage resultIPage = this.sysUserService.getUsersWithRolePage(page, userDTO);
    	log.debug("getUsersWithRolePage --> resultIPage: {}", resultIPage.toString());
    	
        return R.ok(resultIPage);
    }

    /**
     * 获取指定用户全部信息
     *
     * @return 用户信息
     */
    @Inner
    @GetMapping("/info/{username}")
    @Operation(summary = "获取指定用户全部信息【内部接口】")
    public R info(@PathVariable String username) {
        SysUser user = sysUserService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed(null, String.format("用户信息为空 %s", username));
        }
        UserInfo resultUserInfo = sysUserService.findUserInfo(user);
        
        log.debug("info --> username: {}, resultUserInfo: {}", username, resultUserInfo.toString());
        if (resultUserInfo == null || resultUserInfo.getSysUser().getId() == null || resultUserInfo.getSysUser().getId() <= 0) {
//        	return R.failed("获取指定用户全部信息失败");
        }
        
        return R.ok(resultUserInfo);
    }

    /**
     * 获取当前用户全部信息
     *
     * @return 用户信息
     */
    @GetMapping(value = {"/info"})
    @Operation(summary = "获取当前登录用户全部信息")
    public R info() {
        String username = SecurityUtils.getUser().getUsername();
        SysUser user = sysUserService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed(null, "获取当前用户信息失败");
        }
        UserInfo resultUserInfo = sysUserService.findUserInfo(user);
        
        log.debug("info --> resultUserInfo: {}", resultUserInfo.toString());
        
        if (resultUserInfo == null || resultUserInfo.getSysUser().getId() == null || resultUserInfo.getSysUser().getId() <= 0) {
        	return R.failed("获取当前登录用户全部信息失败");
        }
        
        return R.ok(resultUserInfo);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @GetMapping("/details/{username}")
    @Operation(summary = "根据用户名查询用户信息")
    public R user(@PathVariable String username) {
        SysUser condition = new SysUser();
        condition.setUsername(username);
        
        SysUser resultSysUser = sysUserService.getOne(new QueryWrapper<>(condition));
        log.debug("user --> resultSysUser: {}", resultSysUser.toString());
        
        if (resultSysUser == null || resultSysUser.getId() == null || resultSysUser.getId() <= 0) {
//        	return R.failed("根据用户名查询用户信息失败");
        }
        
        return R.ok(resultSysUser);
    }

    /**
     * @param username 用户名称
     * @return 查询上级科室对应的所有用户信息
     */
    @GetMapping("/ancestor/{username}")
    @Operation(summary = "查询上级科室对应的所有用户信息")
    public R listAncestorUsers(@PathVariable String username) {
    	List<SysUser> list = sysUserService.listAncestorUsers(username);
    	
    	if (list == null) {
    		return R.failed("上级科室对应的所有用户信息查询失败");
    	}
    	
        return R.ok();
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@PathVariable Serializable id) {
    	SysUser sysUser = this.sysUserService.getById(id);
    	log.debug("selectOne --> sysUser: {}", sysUser);
    	
        return R.ok(sysUser);
    }

    /**
     * 通过用户id查询详情
     *
     * @param userId 主键
     * @return 单条数据
     */
    @Inner
    @GetMapping("/getUserById")
    @Operation(summary = "通过用户id查询详情")
    public R getUserById(@RequestParam("userId") Integer userId) {
    	SysUser sysUser = this.sysUserService.getById(userId);
    	log.debug("selectOne --> sysUser: {}", sysUser);
    	
        return R.ok(sysUser);
    }

    /**
     * 通过用户id查询对应人员信息
     *
     * @param userId 主键
     * @return 单条数据
     */
    @Inner
    @GetMapping("/getStaffByUserId")
    @Operation(summary = "通过用户id查询对应人员信息")
    public R<SysStaff> getStaffByUserId(@RequestParam("userId") Integer userId) {
    	R<SysStaff> sysStaff = sysUserService.getStaffByUserId(userId);
    	log.debug("getStaffByUserId --> sysStaff: {}", sysStaff.getData().toString());
    	
        return sysStaff;
    }

    /**
     * 添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
    @SysLog("添加用户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('user_add')")
    @Operation(summary = "添加用户")
    public R user(@Validated(ValidGroup.Save.class) @RequestBody UserDTO userDto) {
        return sysUserService.saveUser(userDto);
    }

    /**
     * 删除用户信息
     *
     * @param id ID
     * @return R
     */
    @SysLog("删除用户信息")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('user_del')")
    @Operation(summary = "删除用户")
    public R userDel(@RequestParam("id") Integer id) {
        SysUser sysUser = sysUserService.getById(id);
        
        return sysUserService.deleteUserById(sysUser);
    }


    /**
     * 更新指定用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
    @SysLog("更新用户信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('user_edit')")
    @Operation(summary = "更新指定用户信息")
    public R updateUser(@Validated(ValidGroup.Update.class) @RequestBody UserDTO userDto) {
        return sysUserService.updateUser(userDto);
    }

    /**
     * 重置密码
     *
     * @return R
     */
    @Operation(summary = "重置密码")
    @SysLog("重置密码")
    @PutMapping("/reset/password")
    @PreAuthorize("@pms.hasPermission('user_reset')")
    public R resetPassword(@Validated(ValidGroup.Update.class) @RequestBody SysUser sysUser) {
        return sysUserService.resetPassword(sysUser);
    }


    /**
     * 修改个人信息
     *
     * @param userDto userDto
     * @return success/false
     */
    @SysLog("修改自己的密码和其他信息")
    @PutMapping("/update/password")
    @Operation(summary = "修改自己的密码和其他信息")
    public R updatePassword(@Validated(ValidGroup.Update.class) @RequestBody UserDTO userDto, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String token = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StrUtil.EMPTY).trim();
        return sysUserService.updatePassword(userDto, token);
    }

    /**
     * 更新用户信息
     *
     * @param userBaseDTO 用户信息
     * @return R
     */
    @Operation(summary = "更新用户基本信息，修改用户头像，手机号等等")
    @SysLog("更新用户基本信息")
    @PutMapping("/avatar")
    public R updateUser(@Validated(ValidGroup.Update.class) @RequestBody UserBaseDTO userBaseDTO) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userBaseDTO, sysUser);
        // 查重
        Integer ret = sysUserService.sysUserCheck(sysUser, OperationTypeEnum.UPDATE);
        switch (ret) {
        case 1:
        	return R.failed("用户名重复");
        case 2:
        	return R.failed("该员工已有账号");
        case 8:
        case 9:
        	return R.failed("用户名或者工号不符合规则：用户名和工号不能为空，工号不能小于等于0");
        }

        if (!sysUserService.updateById(sysUser)) {
        	return R.failed("修改用户基本信息失败");
        }

        return R.ok();
    }

    /**
     * 用户账号解锁
     *
     * @param sysUser sysUser
     * @return success/false
     */
    @Operation(summary = "用户账号解锁")
    @PutMapping("/usernameUnlock")
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#sysUser.username")
    public R usernameUnlock(@RequestBody SysUser sysUser) {
    	String username = sysUser.getUsername();
        if (StrUtil.isBlank(username)) {
            return R.failed("用户名不能为空");
        }
        String key = String.format("%s:%s:%s", CacheConstants.LOGIN_ERROR_TIMES, tenantKeyStrResolver.key(), username);
        if (isKeyExists(key)) {
	        if (!redisTemplate.delete(key)) {
	        	return R.failed("缓存删除失败");
	        }
        }
        key = CacheConstants.USER_DETAILS + StrUtil.COLON + StrUtil.COLON + username;
        if (isKeyExists(key)) {
	        if (!redisTemplate.delete(key)) {
	        	return R.failed("缓存删除失败");
	        }
        }
//        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
//        ValueWrapper value = cache.get(username);
//        if (cache != null &&  value != null) {
//            FengUser user = (FengUser) cache.get(username).get();
//            user.setAccountNonLocked(true); 
//            UserDetails userDetails = user;
//            if (null != cache) {
//                cache.put(username, userDetails);
//            }
//        } 

        return R.ok();
    }

    public Boolean isKeyExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists; // 处理可能的null值
    }
    
    /**
     * 切换当前默认租户
     *
     * @param jsonObject tenantId默认租户ID，switchCode第二租户ID
     * @return success/false
     */
    @Operation(summary = "租户切换")
    @PutMapping("/switch")
    public R switchTenant(@RequestBody JSONObject jsonObject) {
    	Integer tenantId = (Integer) jsonObject.get("tenantId");
        if (Objects.isNull(tenantId)) {
            return R.failed("租户ID不能为空");
        }
        
        // 获取当前用户（含有租户ID）
        String switchCode = (String) jsonObject.get("switchCode");
        FengUser user = SecurityUtils.getUser();
        
        boolean success = sysUserService.update(Wrappers.<SysUser>lambdaUpdate()
        		.eq(SysUser::getUsername, user.getUsername())
        			.set(SysUser::getTenantId, tenantId)
        			.set(SysUser::getSwitchCode, switchCode));
        
        if (!success) return R.failed("修改租户失败");
        
        // 返回当前租户的信息
        return R.ok(tenantService.getById(tenantId));
    }

    /**
     * 锁定指定用户
     *
     * @param username 用户名
     * @return R
     */
    @Inner
    @PutMapping("/lock/{username}")
    public R lockUser(@PathVariable String username) {
        return sysUserService.lockUser(username);
    }

    /**
     * 条件获取站内信用户id集合【内部接口】
     *
     * @param mailConditionDTO
     * @return
     */
    @Operation(summary = "条件获取站内信用户集合")
    @PostMapping("/mailUserList")
    public R<List<SysUser>> mailUserList(@RequestBody MailConditionDTO mailConditionDTO) {
        return sysUserService.mailUserList(mailConditionDTO);
    }

    /**
     * 通过机构编码和用户名获取用户基本信息
     *
     * @param username 用户名
     * @return R
     */
    @Inner
    @Operation(summary = "通过机构编码和用户名获取用户基本信息")
    @GetMapping("/getUserByOrganCodeAndUsername")
    public R<SysUser> getUserByOrganCodeAndUsername(@RequestParam("organCode") String organCode, @RequestParam("username") String username) {
    	SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrganCode, organCode).eq(SysUser::getUsername, username));
    	log.debug("selectOne --> sysUser: {}", sysUser);
    	
        return R.ok(sysUser);
    }

    /**
     * 首次登录修改用户基本信息
     *
     * @param firstLogin
     * @return
     */
    @Operation(summary = "首次登录修改用户基本信息")
    @PostMapping("/firstLogin")
    public R firstLogin(@Validated(ValidGroup.Update.class) @RequestBody UserFirstLogin firstLogin, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String token = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StrUtil.EMPTY).trim();
        return sysUserService.firstLogin(firstLogin, token);
    }

    /**
     * 查询用户列表
     *
     * @param sysUser
     * @return 用户列表
     */
    @Inner
    @GetMapping("/list")
    @Operation(summary = "查询用户列表")
    public R<List<SysUser>> list() {
        return R.ok(sysUserService.list(Wrappers.<SysUser>lambdaQuery().orderByDesc(SysUser::getId)));
    }
    
    @Inner
    @GetMapping("/getCount")
    @Operation(summary = "查询用户数量")
    public R<Dict> getCount() {
    	return R.ok(sysUserService.getCount());
    }
}