package work.metanet.feng.admin.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysProject;
import work.metanet.feng.admin.service.SysProjectService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目表(SysProject)表控制层
 * <p>
 * 该类提供了项目管理的相关接口，包括项目的增、删、改、查、分页等功能。
 * </p>
 *
 * @author edison
 * @since 2023-10-01
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysProject")
@Tag(name = "项目模块")
public class SysProjectController {

    /**
     * 服务对象
     */
    private final SysProjectService sysProjectService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param sysProject 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('erp:project:query')")
    public R selectAll(Page page, SysProject sysProject) {
        return R.ok(this.sysProjectService.page(page, Wrappers.<SysProject>lambdaQuery()
                .eq(StrUtil.isNotBlank(sysProject.getProjectCode()), SysProject::getProjectCode, sysProject.getProjectCode())
                .like(StrUtil.isNotBlank(sysProject.getProjectName()), SysProject::getProjectName, sysProject.getProjectName())
                .eq(StrUtil.isNotBlank(sysProject.getStatusCode()), SysProject::getStatusCode, sysProject.getStatusCode())
                .orderByDesc(SysProject::getCreateTime)));
    }

    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('erp:project:query')")
    public R<List<SysProject>> list(SysProject sysProject) {
        return R.ok(this.sysProjectService.list(Wrappers.<SysProject>lambdaQuery()
                .eq(StrUtil.isNotBlank(sysProject.getProjectCode()), SysProject::getProjectCode, sysProject.getProjectCode())
                .like(StrUtil.isNotBlank(sysProject.getProjectName()), SysProject::getProjectName, sysProject.getProjectName())
                .eq(StrUtil.isNotBlank(sysProject.getStatusCode()), SysProject::getStatusCode, sysProject.getStatusCode())
                .orderByDesc(SysProject::getCreateTime)));
    }

    /**
     * 根据主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/id")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@RequestParam("id") Integer id) {
        return R.ok(this.sysProjectService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysProject 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('erp:project:add')")
    public R insert(@RequestBody SysProject sysProject) {
        R r = checkSysProject(sysProject, "1");
        if (r.getCode() != 0) return r;
        if (StrUtil.isBlank(sysProject.getProjectCode())) {
            sysProject.setProjectCode(IdUtil.fastSimpleUUID());
        }
        return R.ok(this.sysProjectService.save(sysProject));
    }

    /**
     * 修改数据
     *
     * @param sysProject 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('erp:project:update')")
    public R update(@RequestBody SysProject sysProject) {
        R r = checkSysProject(sysProject, "2");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysProjectService.updateById(sysProject));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('erp:project:delete')")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysProjectService.removeByIds(idList));
    }

    /**
     * 项目校验
     * @param: sysProject
     * @return 校验结果
     */
    private R checkSysProject(SysProject sysProject, String type) {
        if ("1".equals(type)) {
            Long count = sysProjectService.count(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getProjectCode, sysProject.getProjectCode()));
            if (count > 0) {
                return R.failed("项目编码已存在");
            }
        } else {
            Long count = sysProjectService.count(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getProjectCode, sysProject.getProjectCode()).ne(SysProject::getId, sysProject.getId()));
            if (count > 0) {
                return R.failed("项目编码已存在");
            }
        }
        return R.ok();
    }
}

