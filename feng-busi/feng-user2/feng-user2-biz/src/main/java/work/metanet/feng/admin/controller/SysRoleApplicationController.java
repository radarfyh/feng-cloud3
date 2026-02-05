package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import work.metanet.feng.admin.api.dto.RoleApplicationDTO;
import work.metanet.feng.admin.api.entity.SysRoleApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.admin.service.SysRoleApplicationService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色外部应用关联表(RoleApplication)表控制层
 *
 * @author edison
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysRoleApplication")
@Tag(name = "角色应用模块")
public class SysRoleApplicationController {
    /**
     * 服务对象
     */
    private final SysRoleApplicationService roleApplicationService;

    /**
     * 获取当前登录用的所有应用列表(内部应用+外部应用)
     *
     * @return 新增结果
     */
    @GetMapping
    @Operation(summary = "获取门户当前登录用的所有应用列表(内部应用+外部应用)")
    public R<List<SysApplicationVO>> getApplicationList(String isCollect,String appMac) {
        return this.roleApplicationService.getApplicationList(isCollect,appMac);
    }


    /**
     * 根据角色id获取外部应用权限列表
     *
     * @param roleId 角色id
     * @return 新增结果
     */
    @GetMapping("/getApplicationIdsByRoleId")
    @Operation(summary = "根据角色id获取外部应用权限列表")
    public R getApplicationIdsByRoleId(String roleId) {
        List<Integer> applicationIdList = null;
        List<SysRoleApplication> list = this.roleApplicationService.list(Wrappers.<SysRoleApplication>lambdaQuery()
        		.eq(SysRoleApplication::getRoleId, roleId));
        if (null != list && list.size() > 0) {
            applicationIdList = list.stream().map(SysRoleApplication::getApplicationId).collect(Collectors.toList());
        }
        return R.ok(applicationIdList);
    }

    /**
     * 根据角色id配置外部应用权限
     *
     * @param roleApplicationDTO 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "根据角色id配置外部应用限")
    public R insert(@RequestBody RoleApplicationDTO roleApplicationDTO) {
        return R.ok(this.roleApplicationService.insert(roleApplicationDTO));
    }
}
