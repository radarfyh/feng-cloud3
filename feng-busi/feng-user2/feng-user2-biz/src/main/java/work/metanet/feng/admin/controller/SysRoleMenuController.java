package work.metanet.feng.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import work.metanet.feng.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.entity.SysRoleMenu;
import work.metanet.feng.admin.service.SysRoleMenuService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * 角色菜单表(SysRoleMenu)表控制层
 *
 * @author edison
 * @since 2023-05-11
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sysRoleMenu")
@Tag(name = "角色菜单模块")
public class SysRoleMenuController {
    /**
     * 服务对象
     */
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysRoleMenu 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysRoleMenu sysRoleMenu) {
        return R.ok(this.sysRoleMenuService.page(page, new QueryWrapper<>(sysRoleMenu)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysRoleMenu>> list() {
    	List<SysRoleMenu> list = this.sysRoleMenuService.list();
    	log.debug("list-->返回结果：{}", JSONUtil.toJsonStr(list));
    	
        return R.ok(list );
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
        return R.ok(this.sysRoleMenuService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysRoleMenu 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysRoleMenu sysRoleMenu) {
        return R.ok(this.sysRoleMenuService.save(sysRoleMenu));
    }

    /**
     * 修改数据
     *
     * @param sysRoleMenu 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysRoleMenu sysRoleMenu) {
        return R.ok(this.sysRoleMenuService.updateById(sysRoleMenu));
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
        return R.ok(this.sysRoleMenuService.removeByIds(idList));
    }
}