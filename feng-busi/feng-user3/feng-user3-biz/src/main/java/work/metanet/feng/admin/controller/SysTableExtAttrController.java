package work.metanet.feng.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import work.metanet.feng.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysTableExtAttr;
import work.metanet.feng.admin.service.SysTableExtAttrService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * 用户扩展属性配置(SysTableExtAttr)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysUserExtConfig")
@Tag(name = "用户扩展配置模块")
public class SysTableExtAttrController {
    /**
     * 服务对象
     */
    private final SysTableExtAttrService sysExtConfigService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysUserExtConfig 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysTableExtAttr sysUserExtConfig) {
        return R.ok(this.sysExtConfigService.page(page, new QueryWrapper<>(sysUserExtConfig)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysTableExtAttr>> list() {
           return R.ok( this.sysExtConfigService.list());
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
        return R.ok(this.sysExtConfigService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysUserExtConfig 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysTableExtAttr sysUserExtConfig) {
        return R.ok(this.sysExtConfigService.save(sysUserExtConfig));
    }

    /**
     * 修改数据
     *
     * @param sysUserExtConfig 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysTableExtAttr sysUserExtConfig) {
        return R.ok(this.sysExtConfigService.updateById(sysUserExtConfig));
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
        return R.ok(this.sysExtConfigService.removeByIds(idList));
    }
}