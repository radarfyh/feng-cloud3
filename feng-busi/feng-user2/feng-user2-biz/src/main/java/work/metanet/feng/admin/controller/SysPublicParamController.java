package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysPublicParam;
import work.metanet.feng.admin.service.SysPublicParamService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 公共参数配置表(SysPublicParam)表控制层
 *
 * @author edison
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysPublicParam")
@Tag(name = "公共参数模块")
public class SysPublicParamController {
    /**
     * 服务对象
     */
    private final SysPublicParamService sysPublicParamService;

    /**
     * 通过key查询公共参数值
     * @param publicKey
     * @return
     */
    @Inner(value = false)
    @Operation(summary = "查询公共参数值", description = "根据key查询公共参数值")
    @GetMapping("/publicValue/{publicKey}")
    public R publicKey(@PathVariable("publicKey") String publicKey) {
        return R.ok(sysPublicParamService.getSysPublicParamKeyToValue(publicKey));
    }

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysPublicParam 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysPublicParam sysPublicParam) {
        return R.ok(this.sysPublicParamService.page(page, new QueryWrapper<>(sysPublicParam)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysPublicParam>> list() {
           return R.ok( this.sysPublicParamService.list());
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
        return R.ok(this.sysPublicParamService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysPublicParam 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysPublicParam sysPublicParam) {
        return R.ok(this.sysPublicParamService.save(sysPublicParam));
    }

    /**
     * 修改数据
     *
     * @param sysPublicParam 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    public R update(@RequestBody SysPublicParam sysPublicParam) {
        return R.ok(this.sysPublicParamService.updateById(sysPublicParam));
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
        return R.ok(this.sysPublicParamService.removeByIds(idList));
    }
}