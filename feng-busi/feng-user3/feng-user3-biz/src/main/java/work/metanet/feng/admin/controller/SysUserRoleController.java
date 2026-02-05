package work.metanet.feng.admin.controller;


import work.metanet.feng.admin.api.dto.UserRoleDTO;
import work.metanet.feng.admin.service.SysUserRoleService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户角色表(SysUserRole)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysUserRole")
@Tag(name = "用户角色模块")
public class SysUserRoleController {
    /**
     * 服务对象
     */
    private final SysUserRoleService sysUserRoleService;

    /**
     * 批量用户分配多角色
     *
     * @param userRoleDTO 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "批量用户分配多角色")
    public R insert(@RequestBody UserRoleDTO userRoleDTO) {
        List<Integer> userIds = userRoleDTO.getUserIds();
        List<Integer> roleIds = userRoleDTO.getRoleIds();
        if ((userIds != null && userIds.size() > 0) && (roleIds != null && roleIds.size() > 0)) {
            for (Integer userId : userIds) {
                this.sysUserRoleService.batchSave(userId, roleIds);
            }
        }
        return R.ok();
    }

}