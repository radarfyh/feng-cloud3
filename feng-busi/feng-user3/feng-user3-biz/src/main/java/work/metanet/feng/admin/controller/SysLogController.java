package work.metanet.feng.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import work.metanet.feng.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.SysLogPageDTO;
import work.metanet.feng.admin.api.entity.SysLog;
import work.metanet.feng.admin.service.SysLogService;
import work.metanet.feng.common.security.annotation.Inner;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * 日志表(SysLog)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysLog")
@Tag(name = "日志模块")
public class SysLogController {
    /**
     * 服务对象
     */
    private final SysLogService sysLogService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysLog 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysLog sysLog) {
        return R.ok(this.sysLogService.page(page, new QueryWrapper<>(sysLog)));
    }

    /**
     * 远程分页查询所有数据
     *
     * @param query 查询对象
     * @return 所有数据
     */
    @Inner
    @PostMapping("/pageRemote")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(@RequestBody SysLogPageDTO query) {
        return R.ok(this.sysLogService.page(query.getPage(), new QueryWrapper<>(query.getSysLog())));
    }
    
    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysLog>> list() {
           return R.ok( this.sysLogService.list());
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
        return R.ok(this.sysLogService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysLog 实体对象
     * @return 新增结果
     */
    @Inner
    @PostMapping
    @Operation(summary = "新增数据")
    public R saveLog(@RequestBody SysLog sysLog) {
        return R.ok(this.sysLogService.save(sysLog));
    }

    /**
     * 修改数据
     *
     * @param sysLog 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysLog sysLog) {
        return R.ok(this.sysLogService.updateById(sysLog));
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
        return R.ok(this.sysLogService.removeByIds(idList));
    }
}