package work.metanet.feng.admin.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysAffiliation;
import work.metanet.feng.admin.service.SysAffiliationService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.count;

/**
 * 联盟信息表(SysAffiliation)表控制层
 *
 * @author edison
 * @since 2023-08-02
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysAffiliation")
@Tag(name = "联盟模块")
public class SysAffiliationController {
    /**
     * 服务对象
     */
    private final SysAffiliationService sysAffiliationService;

    /**
     * 分页查询所有数据
     *
     * @param page           分页对象
     * @param sysAffiliation 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:query')")
    public R selectAll(Page page, SysAffiliation sysAffiliation) {
        return R.ok(this.sysAffiliationService.page(page, Wrappers.<SysAffiliation>lambdaQuery().eq(StrUtil.isNotBlank(sysAffiliation.getAffiliationCode()), SysAffiliation::getAffiliationCode, sysAffiliation.getAffiliationCode()).like(StrUtil.isNotBlank(sysAffiliation.getAffiliationName()), SysAffiliation::getAffiliationName, sysAffiliation.getAffiliationName()).eq(StrUtil.isNotBlank(sysAffiliation.getStatus()), SysAffiliation::getStatus, sysAffiliation.getStatus()).orderByDesc(SysAffiliation::getCreateTime)));
    }

    /**
     * 根据机构编码查询联盟列表
     *
     * @return Response对象
     */
    @GetMapping("/getSysAffiliationByOrganCode")
    @Operation(summary = "根据机构编码查询联盟列表")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:query')")
    public R<List<SysAffiliation>> getSysAffiliationByOrganCode(@RequestHeader(value = "organCode", required = false) String organCode) {
        return sysAffiliationService.getOrganCodeBySysAffiliationList(organCode);
    }

    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:query')")
    public R<List<SysAffiliation>> list(SysAffiliation sysAffiliation) {
        return R.ok(this.sysAffiliationService.list(Wrappers.<SysAffiliation>lambdaQuery().eq(StrUtil.isNotBlank(sysAffiliation.getAffiliationCode()), SysAffiliation::getAffiliationCode, sysAffiliation.getAffiliationCode()).like(StrUtil.isNotBlank(sysAffiliation.getAffiliationName()), SysAffiliation::getAffiliationName, sysAffiliation.getAffiliationName()).eq(StrUtil.isNotBlank(sysAffiliation.getStatus()), SysAffiliation::getStatus, sysAffiliation.getStatus()).orderByDesc(SysAffiliation::getCreateTime)));
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
        return R.ok(this.sysAffiliationService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysAffiliation 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:add')")
    public R insert(@RequestBody SysAffiliation sysAffiliation) {
        R r = checkSysAffiliation(sysAffiliation, "1");
        if (r.getCode() != 0) return r;
        if (StrUtil.isBlank(sysAffiliation.getAffiliationCode())) {
            sysAffiliation.setAffiliationCode(IdUtil.fastSimpleUUID());
        }
        return R.ok(this.sysAffiliationService.save(sysAffiliation));
    }

    /**
     * 修改数据
     *
     * @param sysAffiliation 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:update')")
    public R update(@RequestBody SysAffiliation sysAffiliation) {
        R r = checkSysAffiliation(sysAffiliation, "2");
        if (r.getCode() != 0) return r;
        return R.ok(this.sysAffiliationService.updateById(sysAffiliation));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('erp:affiliation:delete')")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysAffiliationService.removeByIds(idList));
    }

    private R checkSysAffiliation(SysAffiliation sysAffiliation, String type) {
        if ("1".equals(type)) {
            Long count = count(Wrappers.<SysAffiliation>lambdaQuery().eq(SysAffiliation::getAffiliationName, sysAffiliation.getAffiliationName()));
            if (count > 0) {
                return R.failed("联盟名称已存在");
            }
        } else {
            Long count = count(Wrappers.<SysAffiliation>lambdaQuery().eq(SysAffiliation::getAffiliationCode, sysAffiliation.getAffiliationCode()).ne(SysAffiliation::getId, sysAffiliation.getId()));
            if (count > 0) {
                return R.failed("联盟code已存在");
            }
        }
        return R.ok();
    }
}
