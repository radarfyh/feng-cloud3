package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.ProjectStaffDTO;
import work.metanet.feng.admin.api.entity.SysProjectStaff;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.service.SysProjectStaffService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目成员表(SysProjectStaff)控制层
 * <p>
 * 该控制层提供了项目成员的增、删、改、查功能接口，包括根据项目ID、角色ID等信息查询项目成员。
 * 还支持分页查询、角色验证等功能。
 * </p>
 *
 * @author edison
 * @date 2023-01-31
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysProjectStaff")
@Tag(name = "项目成员模块")
public class SysProjectStaffController {
    /**
     * 服务对象
     */
    private final SysProjectStaffService projectStaffService;
    private final SysStaffMapper staffMapper;

    /**
     * 获取当前登录用户的项目id集合
     * <p>
     * 该方法根据员工工号查询所属项目id集合，用于返回给前端。
     * </p>
     *
     * @param staffNo 员工工号
     * @return 项目id集合
     */
    @Inner
    @GetMapping("/getProIdListByStaffId")
    @Operation(summary = "获取当前登录用户的项目id集合,内部feign接口")
    public R<List<SysProjectStaff>> getProIdListByStaffId(@RequestParam("staffNo") String staffNo) {
        return R.ok(this.projectStaffService.list(Wrappers.<SysProjectStaff>lambdaQuery()
                .eq(SysProjectStaff::getStaffNo, staffNo)));
    }

    /**
     * 查询项目角色id集合
     * <p>
     * 该方法根据组织编码查询项目角色id集合，用于角色管理。
     * </p>
     *
     * @param organCode 组织编码
     * @return 角色id集合
     */
    @GetMapping("/getRoleIdsByCode")
    @Operation(summary = "查询项目角色id集合")
    public R<List<Integer>> getRoleIdsByCode(@RequestParam("organCode") String organCode) {
        return R.ok(projectStaffService.getRoleIdsByProjectRoleCode(organCode));
    }

    /**
     * 判断当前用户是否为项目组长，是则加入当前项目成员列表中
     * <p>
     * 该方法根据组织编码和项目ID判断当前用户是否为项目组长。
     * </p>
     *
     * @param organCode 组织编码
     * @param projectId 项目ID
     * @return 是否是项目组长
     */
    @GetMapping("/isProjectLeader")
    @Operation(summary = "判断当前用户是否为项目组长，是则加入当前项目成员列表中")
    public R isProjectLeader(@RequestParam("organCode") String organCode, @RequestParam("projectId") Integer projectId) {
        return R.ok(projectStaffService.isProjectLeader(organCode, projectId));
    }

    /**
     * 分页条件查询根据项目id和角色id查询人员信息
     * <p>
     * 该方法通过分页条件查询根据项目ID和角色ID查询人员信息。
     * </p>
     *
     * @param page              分页信息
     * @param projectStaffDTO   项目成员DTO
     * @return 项目成员列表
     */
    @GetMapping("/getStaffListByProId")
    @Operation(summary = "分页条件查询根据项目id和角色id查询人员信息")
    public R getStaffListByProId(Page page, ProjectStaffDTO projectStaffDTO) {
        return R.ok(this.projectStaffService.getStaffListByProjectId(page, projectStaffDTO.getOrganCode(),
                projectStaffDTO.getProjectId(), projectStaffDTO.getRoleId()));
    }

    /**
     * 根据项目id和人员工号获取菜单权限
     * <p>
     * 该方法根据项目ID和员工工号获取对应的菜单权限。
     * </p>
     *
     * @param organCode       组织编码
     * @param applicationCode 应用编码
     * @param projectId       项目ID
     * @param staffNo         员工工号
     * @return 菜单权限
     */
    @GetMapping("/getRoleIdByMenu")
    @Operation(summary = "根据项目id和人员工号获取菜单权限")
    public R getRoleIdByMenu(@RequestParam("organCode") String organCode, @RequestParam("applicationCode") String applicationCode,
                              @RequestParam("projectId") Integer projectId, @RequestParam("staffNo") String staffNo) {
        return R.ok(projectStaffService.getRoleIdByMenu(organCode, applicationCode, projectId, staffNo));
    }

    /**
     * 根据项目id/角色id查询所有人员对应的userId【内部feign接口】
     * <p>
     * 该方法通过项目ID和角色ID查询所有人员对应的userId。
     * </p>
     *
     * @param organCode  组织编码
     * @param projectId  项目ID
     * @param roleId     角色ID
     * @return 用户ID集合
     */
    @Inner
    @GetMapping("/getUserIdByProAndRole")
    @Operation(summary = "根据项目id/角色id查询所有人员对应的userId【内部feign接口】")
    public R<List<Integer>> getUserIdByProAndRole(@RequestParam("organCode") String organCode,
                                                   @RequestParam("projectId") Integer projectId, @RequestParam("roleId") Integer roleId) {
        return R.ok(projectStaffService.getUserIdByProjectAndRole(organCode, projectId, roleId));
    }

    /**
     * 根据项目id查询所有人员对应的userId【内部feign接口】
     * <p>
     * 该方法通过项目ID查询所有人员对应的userId。
     * </p>
     *
     * @param organCode  组织编码
     * @param projectId  项目ID
     * @return 用户ID集合
     */
    @Inner
    @GetMapping("/getUserIdByProId")
    @Operation(summary = "根据项目id查询所有人员对应的userId【内部feign接口】")
    public R<List<Integer>> getUserIdByProId(@RequestParam("organCode") String organCode, @RequestParam("projectId") Integer projectId) {
        return R.ok(projectStaffService.getUserIdByProjectId(organCode, projectId));
    }

    /**
     * 项目成员-根据角色添加成员
     * <p>
     * 该方法用于根据角色和员工工号添加项目成员，避免重复添加。
     * </p>
     *
     * @param projectStaffDTO 项目成员信息DTO
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "项目成员-根据角色添加成员")
    public R insert(@RequestBody ProjectStaffDTO projectStaffDTO) {
        projectStaffDTO.getStaffNoList().forEach(staffNo -> {
            long count = projectStaffService.count(Wrappers.<SysProjectStaff>lambdaQuery()
                    .eq(SysProjectStaff::getProjectId, projectStaffDTO.getProjectId())
                    .eq(SysProjectStaff::getRoleId, projectStaffDTO.getRoleId())
                    .eq(SysProjectStaff::getStaffNo, staffNo));
            if (count == 0) { // 如果该成员还没有加入此角色，则添加
                SysProjectStaff projectStaff = new SysProjectStaff();
                projectStaff.setProjectId(projectStaffDTO.getProjectId());
                projectStaff.setRoleId(projectStaffDTO.getRoleId());
                List<SysStaff> staffs = staffMapper.selectList(Wrappers.<SysStaff>lambdaQuery()
                    .eq(SysStaff::getStaffNo, staffNo));
                projectStaff.setStaffId(staffs.get(0).getId());
                projectStaff.setStaffNo(staffNo);
                projectStaff.setNotes(projectStaffDTO.getNotes());
                projectStaffService.save(projectStaff); // 保存项目成员
            }
        });
        return R.ok();
    }

    /**
     * 删除项目所有角色成员
     * <p>
     * 该方法用于删除指定项目的所有角色成员。
     * </p>
     *
     * @param projectId 项目ID
     * @return 删除结果
     */
    @Inner
    @DeleteMapping("/deleteProject")
    @Operation(summary = "删除项目所有角色成员【内部feign接口】")
    public R deleteProject(@RequestParam("projectId") Integer projectId) {
        try {
            boolean success = projectStaffService.remove(Wrappers.<SysProjectStaff>lambdaQuery()
                    .eq(SysProjectStaff::getProjectId, projectId));
            if (success) {
                return R.ok();
            } else {
                return R.failed("删除失败");
            }
        } catch (Exception e) {
            return R.failed("操作失败: " + e.getMessage());
        }
    }

    /**
     * 删除项目成员
     * <p>
     * 该方法用于根据项目ID、角色ID和员工工号删除项目成员。
     * </p>
     *
     * @param projectId 项目ID
     * @param roleId    角色ID
     * @param staffNo   员工工号
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    public R delete(@RequestParam("projectId") Integer projectId, @RequestParam("roleId") Integer roleId,
                    @RequestParam("staffNo") String staffNo) {
        return R.ok(this.projectStaffService.remove(Wrappers.<SysProjectStaff>lambdaQuery()
                .eq(SysProjectStaff::getProjectId, projectId)
                .eq(SysProjectStaff::getRoleId, roleId)
                .eq(SysProjectStaff::getStaffNo, staffNo)));
    }
}
