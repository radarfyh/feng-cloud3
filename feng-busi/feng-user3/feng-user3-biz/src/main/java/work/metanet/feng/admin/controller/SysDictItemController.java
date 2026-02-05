package work.metanet.feng.admin.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.service.SysDictItemService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典项(SysDictItem)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysDictItem")
@Tag(name = "字典项模块")
public class SysDictItemController {
    /**
     * 服务对象
     */
    private final SysDictItemService sysDictItemService;

    /**
     * 通过字典id查询字典项列表
     *
     * @param sysDictItem 字典项
     * @return
     */
    @Operation(summary = "多条件查询字典项列表")
    @PostMapping("/item/list")
    public R getSysDictItemPage(@RequestBody SysDictItem sysDictItem) {
        return R.ok(sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery()
                .like(StringUtils.isNotBlank(sysDictItem.getLabel()), SysDictItem::getLabel, sysDictItem.getLabel())
                .like(StringUtils.isNotBlank(sysDictItem.getDescription()), SysDictItem::getDescription, sysDictItem.getDescription())
                .eq(null != sysDictItem.getDictId(), SysDictItem::getDictId, sysDictItem.getDictId())
                .eq(StrUtil.isNotBlank(sysDictItem.getDictKey()), SysDictItem::getDictKey, sysDictItem.getDictKey())
                .orderByAsc(SysDictItem::getSort)
                .orderByDesc(SysDictItem::getCreateTime)));
    }

    /**
     * 新增字典项
     *
     * @param sysDictItem 字典项
     * @return R
     */
    @Operation(summary = "新增字典项")
    @SysLog("新增字典项")
    @PostMapping("/item")
    @PreAuthorize("@pms.hasPermission('dict_item_add')")
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public R save(@Validated(ValidGroup.Save.class) @RequestBody SysDictItem sysDictItem) {
        R r = checkSysDict(sysDictItem, "0");
        if (r.getCode() != 0) return r;
        return R.ok(sysDictItemService.save(sysDictItem));
    }

    /**
     * 修改字典项
     *
     * @param sysDictItem 字典项
     * @return R
     */
    @Operation(summary = "修改字典项")
    @SysLog("修改字典项")
    @PutMapping("/item")
    @PreAuthorize("@pms.hasPermission('dict_item_edit')")
    public R updateById(@Validated(ValidGroup.Update.class) @RequestBody SysDictItem sysDictItem) {
        R r = checkSysDict(sysDictItem, "1");
        if (r.getCode() != 0) return r;
        return sysDictItemService.updateDictItem(sysDictItem);
    }

    /**
     * 通过id删除字典项
     *
     * @param id id
     * @return R
     */
    @Operation(summary = "通过id删除字典项")
    @SysLog("删除字典项")
    @PreAuthorize("@pms.hasPermission('dict_item_del')")
    @DeleteMapping("/item")
    public R removeDictItemById(@RequestParam("id") Integer id) {
        return sysDictItemService.removeDictItem(id);
    }

    /**
     * 字典校验
     *
     * @param sysDictItem: 字典
     * @param type:        0-新增 1-修改
     * @return: work.metanet.feng.common.core.util.R
     **/
    private R checkSysDict(SysDictItem sysDictItem, String type) {
        Long count = 0L;
        Long count1 = 0L;
        if ("0".equals(type)) {
            count = sysDictItemService.count(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, sysDictItem.getDictId()).eq(SysDictItem::getValue, sysDictItem.getValue()));
        } else {
            count1 = sysDictItemService.count(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, sysDictItem.getDictId()).eq(SysDictItem::getValue, sysDictItem.getValue()).ne(SysDictItem::getId, sysDictItem.getId()));
        }
        if (count > 0 || count1 > 0) {
            return R.failed("字典项值已存在");
        }
        return R.ok();
    }
}