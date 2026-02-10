package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.StatusCodeQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeDetailVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodePageVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeTreeVO;
import ltd.huntinginfo.feng.center.service.UmpStatusCodeService;
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
 * 消息状态码表控制器
 * 提供状态码的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/status-code")
@RequiredArgsConstructor
@Tag(name = "状态码管理", description = "消息状态码的增删改查和状态管理")
public class UmpStatusCodeController {

    private final UmpStatusCodeService umpStatusCodeService;

    @Operation(summary = "创建状态码", description = "创建新的状态码")
    @PostMapping("/create")
    public R<String> createStatusCode(
            @RequestParam String statusCode,
            @RequestParam String statusName,
            @RequestParam String statusDesc,
            @RequestParam String category,
            @RequestParam(required = false) String parentCode,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Integer isFinal,
            @RequestParam(required = false) Integer canRetry) {
        try {
            String id = umpStatusCodeService.createStatusCode(
                    statusCode, statusName, statusDesc, category, parentCode,
                    sortOrder, isFinal, canRetry);
            return R.ok(id, "状态码创建成功");
        } catch (Exception e) {
            log.error("创建状态码失败", e);
            return R.failed("状态码创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新状态码", description = "更新状态码信息")
    @PutMapping("/{id}")
    public R<Boolean> updateStatusCode(
            @Parameter(description = "状态码ID", required = true) 
            @PathVariable String id,
            @RequestParam(required = false) String statusName,
            @RequestParam(required = false) String statusDesc,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Integer isFinal,
            @RequestParam(required = false) Integer canRetry,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpStatusCodeService.updateStatusCode(
                    id, statusName, statusDesc, sortOrder, isFinal, canRetry, status);
            return success ? R.ok(true) : R.failed("状态码更新失败");
        } catch (Exception e) {
            log.error("更新状态码失败", e);
            return R.failed("状态码更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据状态码查询", description = "根据状态码查询详情")
    @GetMapping("/code/{statusCode}")
    public R<StatusCodeDetailVO> getByStatusCode(
            @Parameter(description = "状态码", required = true) 
            @PathVariable String statusCode) {
        StatusCodeDetailVO statusCodeDetail = umpStatusCodeService.getByStatusCode(statusCode);
        if (statusCodeDetail == null) {
            return R.failed("状态码不存在");
        }
        return R.ok(statusCodeDetail);
    }

    @Operation(summary = "分页查询状态码", description = "根据条件分页查询状态码列表")
    @PostMapping("/page")
    public R<Page<StatusCodePageVO>> queryStatusCodePage(@Valid @RequestBody StatusCodeQueryDTO queryDTO) {
        Page<StatusCodePageVO> page = umpStatusCodeService.queryStatusCodePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据分类查询状态码列表", description = "根据分类查询状态码列表")
    @GetMapping("/category/{category}")
    public R<List<StatusCodeDetailVO>> getByCategory(
            @Parameter(description = "分类", required = true) 
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getByCategory(category, enabledOnly);
        return R.ok(statusCodes);
    }

    @Operation(summary = "根据父状态码查询子状态码列表", description = "根据父状态码查询子状态码列表")
    @GetMapping("/parent/{parentCode}")
    public R<List<StatusCodeDetailVO>> getByParentCode(
            @Parameter(description = "父状态码", required = true) 
            @PathVariable String parentCode,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getByParentCode(parentCode, enabledOnly);
        return R.ok(statusCodes);
    }

    @Operation(summary = "查询所有启用的状态码", description = "查询所有启用的状态码列表")
    @GetMapping("/enabled")
    public R<List<StatusCodeDetailVO>> getAllEnabled() {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getAllEnabled();
        return R.ok(statusCodes);
    }

    @Operation(summary = "启用状态码", description = "启用状态码")
    @PutMapping("/enable/{id}")
    public R<Boolean> enableStatusCode(
            @Parameter(description = "状态码ID", required = true) 
            @PathVariable String id) {
        boolean success = umpStatusCodeService.enableStatusCode(id);
        return success ? R.ok(true) : R.failed("启用失败");
    }

    @Operation(summary = "禁用状态码", description = "禁用状态码")
    @PutMapping("/disable/{id}")
    public R<Boolean> disableStatusCode(
            @Parameter(description = "状态码ID", required = true) 
            @PathVariable String id) {
        boolean success = umpStatusCodeService.disableStatusCode(id);
        return success ? R.ok(true) : R.failed("禁用失败");
    }

    @Operation(summary = "批量启用状态码", description = "批量启用状态码")
    @PutMapping("/enable/batch")
    public R<Integer> batchEnableStatusCodes(@RequestBody List<String> ids) {
        int updatedCount = umpStatusCodeService.batchEnableStatusCodes(ids);
        String ret = "成功启用" + String.valueOf(updatedCount) + "个状态码";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量禁用状态码", description = "批量禁用状态码")
    @PutMapping("/disable/batch")
    public R<Integer> batchDisableStatusCodes(@RequestBody List<String> ids) {
        int updatedCount = umpStatusCodeService.batchDisableStatusCodes(ids);
        String ret = "成功禁用" + String.valueOf(updatedCount) + "个状态码";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "检查状态码是否存在", description = "检查状态码是否存在")
    @GetMapping("/exists/{statusCode}")
    public R<Boolean> existsByStatusCode(
            @Parameter(description = "状态码", required = true) 
            @PathVariable String statusCode) {
        boolean exists = umpStatusCodeService.existsByStatusCode(statusCode);
        return R.ok(exists);
    }

    @Operation(summary = "获取状态码统计", description = "获取状态码的统计信息")
    @GetMapping("/statistics")
    public R<StatusCodeStatisticsVO> getStatusCodeStatistics() {
        StatusCodeStatisticsVO statistics = umpStatusCodeService.getStatusCodeStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "获取状态码层级树", description = "获取状态码的层级树结构")
    @GetMapping("/tree")
    public R<List<StatusCodeTreeVO>> getStatusCodeTree(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeTreeVO> tree = umpStatusCodeService.getStatusCodeTree(category, enabledOnly);
        return R.ok(tree);
    }

    @Operation(summary = "获取状态码映射", description = "获取状态码的键值对映射")
    @GetMapping("/map/{category}")
    public R<Map<String, String>> getStatusCodeMap(
            @Parameter(description = "分类", required = true) 
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, String> codeMap = umpStatusCodeService.getStatusCodeMap(category, enabledOnly);
        return R.ok(codeMap);
    }

    @Operation(summary = "检查是否为最终状态", description = "检查状态码是否为最终状态")
    @GetMapping("/check/final/{statusCode}")
    public R<Boolean> isFinalStatusCode(
            @Parameter(description = "状态码", required = true) 
            @PathVariable String statusCode) {
        boolean isFinal = umpStatusCodeService.isFinalStatusCode(statusCode);
        return R.ok(isFinal);
    }

    @Operation(summary = "检查是否可重试", description = "检查状态码是否可重试")
    @GetMapping("/check/retry/{statusCode}")
    public R<Boolean> canRetryStatusCode(
            @Parameter(description = "状态码", required = true) 
            @PathVariable String statusCode) {
        boolean canRetry = umpStatusCodeService.canRetryStatusCode(statusCode);
        return R.ok(canRetry);
    }

    @Operation(summary = "获取有效流转状态码", description = "获取当前状态码可流转的目标状态码列表")
    @GetMapping("/transitions/{currentStatusCode}")
    public R<List<StatusCodeDetailVO>> getValidTransitionStatusCodes(
            @Parameter(description = "当前状态码", required = true) 
            @PathVariable String currentStatusCode) {
        List<StatusCodeDetailVO> transitions = umpStatusCodeService.getValidTransitionStatusCodes(currentStatusCode);
        return R.ok(transitions);
    }

    @Operation(summary = "验证状态流转", description = "验证状态流转是否允许")
    @GetMapping("/validate/transition")
    public R<Boolean> validateStatusTransition(
            @RequestParam String currentStatusCode,
            @RequestParam String targetStatusCode) {
        boolean valid = umpStatusCodeService.validateStatusTransition(currentStatusCode, targetStatusCode);
        return R.ok(valid);
    }
}