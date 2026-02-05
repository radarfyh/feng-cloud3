package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cn.hutool.core.lang.Dict;

import java.util.List;

@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteUserService {

    /**
     * 通过用户名查询用户、角色信息
     *
     * @param username 用户名
     * @param from     调用标志
     * @return R
     */
    @GetMapping("/sysUser/info/{username}")
    R<UserInfo> info(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM) String fromIn);

    /**
     * 通过社交账号或手机号查询用户、角色信息
     *
     * @param phone appid@code
     * @param from  调用标志
     * @return
     */
    @GetMapping("/social/info/{phone}")
    R<UserInfo> social(@PathVariable("phone") String phone, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询上级部门的用户信息
     *
     * @param username 用户名
     * @return R
     */
    @GetMapping("/sysUser/ancestor/{username}")
    R<List<SysUser>> ancestorUsers(@PathVariable("username") String username);

    /**
     * 锁定用户
     *
     * @param username 用户名
     * @param from     调用标识
     * @return
     */
    @PutMapping("/sysUser/lock/{username}")
    R<Boolean> lockUser(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM) String from);


    /**
     * 通过机构编码和用户名获取用户基本信息
     *
     * @param organCode 机构编码
     * @param username  用户名
     * @return R
     */
    @GetMapping("/sysUser/getUserByOrganCodeAndUsername")
    R<SysUser> getUserByOrganCodeAndUsername(@RequestParam("organCode") String organCode, @RequestParam("username") String username, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过用户id查询详情
     *
     * @param userId 用户id
     * @param from   调用标识
     * @return
     */
    @GetMapping("/sysUser/getUserById")
    R<SysUser> getUserById(@RequestParam("userId") Integer userId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过用户id查询对应人员信息
     * @param userId
     * @param from
     * @return
     */
    @GetMapping("/sysUser/getStaffByUserId")
    R<SysStaff> getStaffByUserId(@RequestParam("userId") Integer userId, @RequestHeader(SecurityConstants.FROM) String from);
    
    /**
     * 通过用户列表
     *
     * @param userId 用户id
     * @param from   调用标识
     * @return
     */
    @GetMapping("/sysUser/list")
    R<List<SysUser>> getUserList(@RequestHeader(SecurityConstants.FROM) String fromIn);
    
    @GetMapping("/sysUser/getCount")
    R<Dict> getCount(@RequestHeader(SecurityConstants.FROM) String fromIn);
}
