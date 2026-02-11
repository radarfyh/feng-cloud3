package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.PermissionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.PermissionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionPageVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpAppPermissionService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 应用权限表控制器
 * 提供应用权限的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/app/permission")
@RequiredArgsConstructor
@Tag(name = "应用权限管理", description = "应用权限的增删改查和状态管理")
public class UmpAppPermissionController {

    private final UmpAppPermissionService umpAppPermissionService;

    @Operation(summary = "创建应用权限", description = "创建新的应用权限")
    @PostMapping("/create")
    public R<String> createPermission(
            @RequestParam String appKey,
            @RequestParam String resourceCode,
            @RequestParam(required = false) String resourceName,
            @RequestParam String operation) {
        try {
            String permissionId = umpAppPermissionService.createPermission(
                    appKey, resourceCode, resourceName, operation);
            String ret = "权限创建成功";
            return R.ok(permissionId, ret);
        } catch (Exception e) {
            log.error("创建权限失败", e);
            return R.failed("权限创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量创建应用权限", description = "批量创建应用权限")
    @PostMapping("/batch-create")
    public R<Integer> batchCreatePermissions(
            @RequestParam String appKey,
            @RequestBody List<Map<String, String>> permissions) {
        try {
            int createdCount = umpAppPermissionService.batchCreatePermissions(appKey, permissions);
            String ret = "成功创建" + String.valueOf(createdCount) + "个权限";
            return R.ok(createdCount, ret);
        } catch (Exception e) {
            log.error("批量创建权限失败", e);
            return R.failed("批量创建权限失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新应用权限", description = "更新应用权限信息")
    @PutMapping("/{permissionId}")
    public R<Boolean> updatePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable String permissionId,
            @RequestParam(required = false) String resourceName,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpAppPermissionService.updatePermission(
                    permissionId, resourceName, operation, status);
            return success ? R.ok(true) : R.failed("权限更新失败");
        } catch (Exception e) {
            log.error("更新权限失败", e);
            return R.failed("权限更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据应用标识和资源代码查询权限", description = "根据应用标识和资源代码查询权限详情")
    @GetMapping("/detail")
    public R<PermissionDetailVO> getPermissionByKeyAndResource(
            @RequestParam String appKey,
            @RequestParam String resourceCode) {
        PermissionDetailVO permission = umpAppPermissionService.getPermissionByKeyAndResource(appKey, resourceCode);
        if (permission == null) {
            return R.failed("权限不存在");
        }
        return R.ok(permission);
    }

    @Operation(summary = "分页查询应用权限", description = "根据条件分页查询应用权限列表")
    @PostMapping("/page")
    public R<Page<PermissionPageVO>> queryPermissionPage(@Valid @RequestBody PermissionQueryDTO queryDTO) {
        Page<PermissionPageVO> page = umpAppPermissionService.queryPermissionPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据应用标识查询权限列表", description = "查询指定应用的所有权限")
    @GetMapping("/app/{appKey}")
    public R<List<PermissionDetailVO>> getPermissionsByAppKey(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey) {
        List<PermissionDetailVO> permissions = umpAppPermissionService.getPermissionsByAppKey(appKey);
        return R.ok(permissions);
    }

    @Operation(summary = "查询可用的应用权限列表", description = "查询指定应用的可用权限列表")
    @GetMapping("/available/{appKey}")
    public R<List<PermissionDetailVO>> getAvailablePermissions(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey) {
        List<PermissionDetailVO> permissions = umpAppPermissionService.getAvailablePermissions(appKey);
        return R.ok(permissions);
    }

    @Operation(summary = "启用权限", description = "启用应用权限")
    @PutMapping("/enable/{permissionId}")
    public R<Boolean> enablePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.enablePermission(permissionId);
        return success ? R.ok(true) : R.failed("启用失败");
    }

    @Operation(summary = "禁用权限", description = "禁用应用权限")
    @PutMapping("/disable/{permissionId}")
    public R<Boolean> disablePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.disablePermission(permissionId);
        return success ? R.ok(true) : R.failed("禁用失败");
    }

    @Operation(summary = "批量启用权限", description = "批量启用应用权限")
    @PutMapping("/enable/batch")
    public R<Integer> batchEnablePermissions(@RequestBody List<String> permissionIds) {
        int updatedCount = umpAppPermissionService.batchEnablePermissions(permissionIds);
        String ret = "成功启用" + String.valueOf(updatedCount) + "个权限";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量禁用权限", description = "批量禁用应用权限")
    @PutMapping("/disable/batch")
    public R<Integer> batchDisablePermissions(@RequestBody List<String> permissionIds) {
        int updatedCount = umpAppPermissionService.batchDisablePermissions(permissionIds);
        String ret = "成功禁用" + String.valueOf(updatedCount) + "个权限";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "检查应用权限", description = "检查应用是否有权限访问资源")
    @GetMapping("/check")
    public R<Boolean> checkPermission(
            @RequestParam String appKey,
            @RequestParam String resourceCode,
            @RequestParam(required = false) String operation) {
        boolean hasPermission = umpAppPermissionService.checkPermission(
                appKey, resourceCode, operation != null ? operation : "READ");
        return R.ok(hasPermission);
    }

    @Operation(summary = "验证操作权限", description = "验证应用对资源的操作权限")
    @PostMapping("/validate")
    public R<Boolean> validateOperation(
            @RequestParam String appKey,
            @RequestParam String resourceCode,
            @RequestParam String operation) {
        boolean valid = umpAppPermissionService.validateOperation(appKey, resourceCode, operation);
        return R.ok(valid);
    }

    @Operation(summary = "获取权限统计", description = "获取权限的统计信息")
    @GetMapping("/statistics")
    public R<PermissionStatisticsVO> getPermissionStatistics() {
        PermissionStatisticsVO statistics = umpAppPermissionService.getPermissionStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "删除权限", description = "删除应用权限")
    @DeleteMapping("/{permissionId}")
    public R<Boolean> deletePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.deletePermission(permissionId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "批量删除权限", description = "批量删除应用权限")
    @DeleteMapping("/batch")
    public R<Integer> batchDeletePermissions(@RequestBody List<String> permissionIds) {
        int deletedCount = umpAppPermissionService.batchDeletePermissions(permissionIds);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个权限";
        return R.ok(deletedCount, ret);
    }

    @Operation(summary = "根据应用标识删除权限", description = "删除指定应用的所有权限")
    @DeleteMapping("/app/{appKey}")
    public R<Integer> deletePermissionsByAppKey(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey) {
        int deletedCount = umpAppPermissionService.deletePermissionsByAppKey(appKey);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个权限";
        return R.ok(deletedCount, ret);
    }

    @Operation(summary = "复制权限到其他应用", description = "将源应用的权限复制到目标应用")
    @PostMapping("/copy")
    public R<Integer> copyPermissionsToApp(
            @RequestParam String sourceAppKey,
            @RequestParam String targetAppKey) {
        int copiedCount = umpAppPermissionService.copyPermissionsToApp(sourceAppKey, targetAppKey);
        String ret = "成功复制" + String.valueOf(copiedCount) + "个权限";
        return R.ok(copiedCount, ret);
    }

    @Operation(summary = "获取应用资源树", description = "获取应用的可访问资源树结构")
    @GetMapping("/resource-tree/{appKey}")
    public R<Map<String, Object>> getResourceTree(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey) {
        Map<String, Object> resourceTree = umpAppPermissionService.getResourceTree(appKey);
        return R.ok(resourceTree);
    }
}