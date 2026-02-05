package work.metanet.feng.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import work.metanet.feng.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysUserDepartment;
import work.metanet.feng.admin.service.SysUserDepartmentService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * 用户科室表(SysUserDepartment)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysUserDepartment")
@Tag(name = "用户部门模块")
public class SysUserDepartmentController {
    /**
     * 服务对象
     */
    private final SysUserDepartmentService sysUserDepartmentService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysUserDepartment 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysUserDepartment sysUserDepartment) {
        return R.ok(this.sysUserDepartmentService.page(page, new QueryWrapper<>(sysUserDepartment)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysUserDepartment>> list() {
           return R.ok( this.sysUserDepartmentService.list());
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
        return R.ok(this.sysUserDepartmentService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysUserDepartment 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysUserDepartment sysUserDepartment) {
        return R.ok(this.sysUserDepartmentService.save(sysUserDepartment));
    }

    /**
     * 修改数据
     *
     * @param sysUserDepartment 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysUserDepartment sysUserDepartment) {
        return R.ok(this.sysUserDepartmentService.updateById(sysUserDepartment));
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
        return R.ok(this.sysUserDepartmentService.removeByIds(idList));
    }
}