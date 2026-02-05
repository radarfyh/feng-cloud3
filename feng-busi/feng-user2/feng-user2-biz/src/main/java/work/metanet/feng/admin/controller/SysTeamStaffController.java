package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.TeamStaffDTO;
import work.metanet.feng.admin.api.entity.SysTeamStaff;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.service.SysTeamStaffService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小组人员关联表(TeamStaff)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("teamStaff")
@Tag(name = "小组人员模块")
public class SysTeamStaffController {
    /**
     * 服务对象
     */
    private final SysTeamStaffService teamStaffService;

    /**
     * 分页查询小组人员列表
     *
     * @param page                分页对象
     * @param teamStaff 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询小组人员列表")
    public R selectPage(Page page, SysTeamStaff teamStaff) {
        return R.ok(this.teamStaffService.selectPage(page, teamStaff));
    }


    /**
     * 根据小组id查询小组人员列表
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "根据小组id查询小组人员列表")
    public R<List<SysStaff>> getStaffListByTeamId(@RequestParam("teamId") Integer teamId) {
        return R.ok(this.teamStaffService.getStaffListByTeamId(teamId));
    }

    /**
     * 新增数据
     *
     * @param teamStaffDTO 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody TeamStaffDTO teamStaffDTO) {
        List<SysTeamStaff> teamStaffs = null;
        teamStaffDTO.getStaffIds().forEach(staffId -> {
        	SysTeamStaff teamStaff = new SysTeamStaff();
            teamStaff.setTeamId(teamStaff.getTeamId());
            teamStaff.setStaffId(staffId);
            teamStaffs.add(teamStaff);
        });
        return R.ok(this.teamStaffService.saveBatch(teamStaffs));
    }


    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.teamStaffService.removeByIds(idList));
    }
}
