package work.metanet.feng.admin.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import work.metanet.feng.admin.api.entity.SysTeam;
import work.metanet.feng.admin.service.SysTeamService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.count;

/**
 * 小组(SysTeam)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysTeam")
@Tag(name = "小组模块")
public class SysTeamController {
    /**
     * 服务对象
     */
    private final SysTeamService sysTeamService;


    /**
     * 根据联盟id查询小组集合
     *
     * @return Response对象
     */
    @GetMapping("/getSysTeamListByAffiliationId")
    @Operation(summary = "根据联盟id查询小组集合")
    @PreAuthorize("@pms.hasPermission('erp:team:query')")
    public R<List<SysTeam>> getSysTeamListByAffiliationId(@RequestParam("affiliationId") Integer affiliationId) {
        return R.ok(this.sysTeamService.list(Wrappers.<SysTeam>lambdaQuery().eq(SysTeam::getAffiliationId, affiliationId)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/id")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@RequestParam("id") Integer id) {
        return R.ok(this.sysTeamService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysTeam 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('erp:team:add')")
    public R insert(@RequestBody SysTeam sysTeam) {
        R r = checkSysTeam(sysTeam, "1");
        if (r.getCode() != 0) return r;
        if (StrUtil.isBlank(sysTeam.getTeamCode())) {
            sysTeam.setTeamCode(IdUtil.fastSimpleUUID());
        }
        return R.ok(this.sysTeamService.save(sysTeam));
    }

    /**
     * 修改数据
     *
     * @param sysTeam 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('erp:team:update')")
    public R update(@RequestBody SysTeam sysTeam) {
        R r = checkSysTeam(sysTeam, "2");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysTeamService.updateById(sysTeam));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('erp:team:delete')")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysTeamService.removeByIds(idList));
    }

    private R checkSysTeam(SysTeam sysTeam, String type) {
        if ("1".equals(type)) {
            Long count = count(Wrappers.<SysTeam>lambdaQuery().eq(SysTeam::getTeamName, sysTeam.getTeamName()));
            if (count > 0) {
                return R.failed("小组名称已存在");
            }
        } else {
            Long count = count(Wrappers.<SysTeam>lambdaQuery().eq(SysTeam::getTeamCode, sysTeam.getTeamCode()).ne(SysTeam::getId, sysTeam.getId()));
            if (count > 0) {
                return R.failed("小组code已存在");
            }
        }
        return R.ok();
    }
}
