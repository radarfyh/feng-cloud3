package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysDict;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.service.SysDictItemService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 字典表(SysDict)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysDict")
@Tag(name = "字典模块")
public class SysDictController {
    /**
     * 服务对象
     */
    private final SysDictService sysDictService;

    private final SysDictItemService sysDictItemService;

    /**
     * 分页查询字典信息
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @Operation(summary = "分页查询字典信息")
    @GetMapping("/page")
    public R<IPage> getDictPage(Page page, SysDict sysDict) {
        return R.ok(sysDictService.page(page, Wrappers.<SysDict>lambdaQuery().like(StringUtils.isNotBlank(sysDict.getDictKey()), SysDict::getDictKey, sysDict.getDictKey()).like(StringUtils.isNotBlank(sysDict.getIsSystem()), SysDict::getIsSystem, sysDict.getIsSystem()).like(StringUtils.isNotBlank(sysDict.getDescription()), SysDict::getDescription, sysDict.getDescription()).like(StringUtils.isNotBlank(sysDict.getRemarks()), SysDict::getRemarks, sysDict.getRemarks()).orderByDesc(SysDict::getCreateTime)));
    }

    /**
     * 通过字典类型查找字典项
     *
     * @param type 类型
     * @return 同类型字典
     */
    @Operation(summary = "通过字典类型查找字典项")
    @GetMapping("/dictKey/{type}")
    public R getDictByType(@PathVariable String type) {
        return sysDictService.getDictByType(type);
    }


    /**
     * 查询字典列表
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典列表")
    public R<List<SysDict>> list() {
        return R.ok(this.sysDictService.list());
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
        return R.ok(this.sysDictService.getById(id));
    }

    /**
     * 添加字典
     *
     * @param sysDict 字典信息
     * @return success、false
     */
    @Operation(summary = "添加字典")
    @SysLog("添加字典")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('dict_add')")
    public R save(@Validated(ValidGroup.Save.class) @RequestBody SysDict sysDict) {
        R r = checkSysDict(sysDict, "0");
        if (r.getCode() != 0) return r;
        return R.ok(sysDictService.save(sysDict));
    }

    /**
     * 修改字典
     *
     * @param sysDict 字典信息
     * @return success/false
     */
    @Operation(summary = "修改字典")
    @PutMapping
    @SysLog("修改字典")
    @PreAuthorize("@pms.hasPermission('dict_edit')")
    public R updateById(@Validated(ValidGroup.Update.class) @RequestBody SysDict sysDict) {
        R r = checkSysDict(sysDict, "1");
        if (r.getCode() != 0) return r;
        return sysDictService.updateDict(sysDict);
    }

    /**
     * 删除字典，并且清除字典缓存
     *
     * @param id ID
     * @return R
     */
    @Operation(summary = "删除字典，并且清除字典缓存")
    @SysLog("删除字典")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('dict_del')")
    public R removeById(@RequestParam("id") Integer id) {
        return sysDictService.removeDict(id);
    }

    /**
     * 字典校验
     *
     * @param sysDict: 字典
     * @param type:    0-新增 1-修改
     * @return: work.metanet.feng.common.core.util.R
     **/
    private R checkSysDict(SysDict sysDict, String type) {
        Long count = 0L;
        Long count1 = 0L;
        if ("0".equals(type)) {
            count = sysDictService.count(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictKey, sysDict.getDictKey()));
        } else {
            count1 = sysDictService.count(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictKey, sysDict.getDictKey()).ne(SysDict::getId, sysDict.getId()));
        }
        if (count > 0 || count1 > 0) {
            return R.failed("字典编码已存在");
        }
        return R.ok();
    }
}