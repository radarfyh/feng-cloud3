package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import work.metanet.feng.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysDeptAttribute;
import work.metanet.feng.admin.service.SysDeptAttributeService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

/**
 * 科室属性关联表(SysDeptAttribute)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysDeptAttribute")
@Tag(name = "部门属性模块")
public class SysDeptAttributeController {
    /**
     * 服务对象
     */
    private final SysDeptAttributeService sysDeptAttributeService;

    /**
     * 分页查询所有数据
     *
     * @param page             分页对象
     * @param sysDeptAttribute 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysDeptAttribute sysDeptAttribute) {
        return R.ok(this.sysDeptAttributeService.page(page, new QueryWrapper<>()));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysDeptAttribute>> list() {
        return R.ok(this.sysDeptAttributeService.list());
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
        return R.ok(this.sysDeptAttributeService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysDeptAttribute 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysDeptAttribute sysDeptAttribute) {
        return R.ok(this.sysDeptAttributeService.save(sysDeptAttribute));
    }

    /**
     * 修改数据
     *
     * @param sysDeptAttribute 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysDeptAttribute sysDeptAttribute) {
        return R.ok(this.sysDeptAttributeService.updateById(sysDeptAttribute));
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
        return R.ok(this.sysDeptAttributeService.removeByIds(idList));
    }
}
