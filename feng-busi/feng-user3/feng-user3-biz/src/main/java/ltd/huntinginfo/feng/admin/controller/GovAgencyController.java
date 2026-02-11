package ltd.huntinginfo.feng.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.admin.service.dict.GovAgencyService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 政府机关/机构字典管理控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/gov/agency")
@Tag(name = "政府机关管理模块", description = "GovAgency")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class GovAgencyController {

    private final GovAgencyService govAgencyService;

    /**
     * 根据 code 查询机构信息
     *
     * @param code 机构代码
     * @return 机构信息
     */
    @GetMapping("/{code}")
    @Operation(summary = "根据 code 查询机构信息", description = "通过机构代码查询机构详情")
    public R<GovAgency> getByCode(@PathVariable String code) {
        return R.ok(govAgencyService.getByCode(code));
    }

    /**
     * 根据父级 code 查询下级机构
     *
     * @param parentCode 父级机构代码
     * @return 下级机构列表
     */
    @GetMapping("/parent/{parentCode}")
    @Operation(summary = "查询下级机构", description = "根据父级机构代码查询下级机构")
    public R<List<GovAgency>> getByParentCode(@PathVariable String parentCode) {
        return R.ok(govAgencyService.getByParentCode(parentCode));
    }

    /**
     * 根据级别查询机构
     *
     * @param level 机构级别
     * @return 机构列表
     */
    @GetMapping("/level/{level}")
    @Operation(summary = "按级别查询机构", description = "根据机构级别查询机构列表")
    public R<List<GovAgency>> getByLevel(@PathVariable Integer level) {
        return R.ok(govAgencyService.getByLevel(level));
    }

    /**
     * 查询全部有效机构
     *
     * @return 机构列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部机构", description = "查询全部机构字典数据")
    public R<List<GovAgency>> listAll() {
        return R.ok(govAgencyService.getAllValidItems());
    }

    /**
     * 获取机构树
     *
     * @return 树形机构数据
     */
    @GetMapping("/tree")
    @Operation(summary = "获取机构树", description = "返回完整的政府机构树形结构")
    public R<List<GovAgency>> getAgencyTree() {
        return R.ok(govAgencyService.getAgencyTree());
    }

    /**
     * 新增机构（谨慎开放）
     */
    @SysLog("新增政府机构")
    @PostMapping
    @HasPermission("gov_agency_add")
    @Operation(summary = "新增机构", description = "新增政府机构信息")
    public R<Boolean> save(@Valid @RequestBody GovAgency govAgency) {
        return R.ok(govAgencyService.save(govAgency));
    }

    /**
     * 修改机构
     */
    @SysLog("修改政府机构")
    @PutMapping
    @HasPermission("gov_agency_edit")
    @Operation(summary = "修改机构", description = "修改政府机构信息")
    public R<Boolean> update(@Valid @RequestBody GovAgency govAgency) {
        return R.ok(govAgencyService.updateById(govAgency));
    }

    /**
     * 删除机构（逻辑删除）
     */
    @SysLog("删除政府机构")
    @DeleteMapping("/{id}")
    @HasPermission("gov_agency_del")
    @Operation(summary = "删除机构", description = "根据ID删除政府机构")
    public R<Boolean> delete(@PathVariable String id) {
        return R.ok(govAgencyService.removeById(id));
    }

    /**
     * 刷新机构缓存
     */
    @SysLog("刷新政府机构缓存")
    @PostMapping("/refresh-cache")
    @HasPermission("gov_agency_cache_refresh")
    @Operation(summary = "刷新缓存", description = "清空并重新加载政府机构缓存")
    public R<Void> refreshCache() {
        govAgencyService.refreshCache();
        return R.ok();
    }
}

