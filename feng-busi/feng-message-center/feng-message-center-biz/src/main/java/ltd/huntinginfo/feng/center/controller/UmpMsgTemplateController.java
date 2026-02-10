package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.TemplateQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TemplateDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TemplatePageVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateUsageVO;
import ltd.huntinginfo.feng.center.service.UmpMsgTemplateService;
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
 * 消息模板表控制器
 * 提供消息模板的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/template")
@RequiredArgsConstructor
@Tag(name = "消息模板管理", description = "消息模板的增删改查和状态管理")
public class UmpMsgTemplateController {

    private final UmpMsgTemplateService umpMsgTemplateService;

    @Operation(summary = "创建模板", description = "创建新的消息模板")
    @PostMapping("/create")
    public R<String> createTemplate(
            @RequestParam String templateCode,
            @RequestParam String templateName,
            @RequestParam String templateType,
            @RequestParam String titleTemplate,
            @RequestParam String contentTemplate,
            @RequestBody(required = false) Map<String, Object> variables,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String defaultCallbackUrl) {
        try {
            String templateId = umpMsgTemplateService.createTemplate(
                    templateCode, templateName, templateType, titleTemplate, contentTemplate,
                    variables, defaultPriority, defaultPushMode, defaultCallbackUrl);
            return R.ok(templateId, "模板创建成功");
        } catch (Exception e) {
            log.error("创建模板失败", e);
            return R.failed("模板创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新模板", description = "更新消息模板信息")
    @PutMapping("/{id}")
    public R<Boolean> updateTemplate(
            @Parameter(description = "模板ID", required = true) 
            @PathVariable String id,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String titleTemplate,
            @RequestParam(required = false) String contentTemplate,
            @RequestBody(required = false) Map<String, Object> variables,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String defaultCallbackUrl,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpMsgTemplateService.updateTemplate(
                    id, templateName, titleTemplate, contentTemplate, variables,
                    defaultPriority, defaultPushMode, defaultCallbackUrl, status);
            return success ? R.ok(true) : R.failed("模板更新失败");
        } catch (Exception e) {
            log.error("更新模板失败", e);
            return R.failed("模板更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据模板代码查询模板", description = "根据模板代码查询模板详情")
    @GetMapping("/code/{templateCode}")
    public R<TemplateDetailVO> getByTemplateCode(
            @Parameter(description = "模板代码", required = true) 
            @PathVariable String templateCode) {
        TemplateDetailVO template = umpMsgTemplateService.getByTemplateCode(templateCode);
        if (template == null) {
            return R.failed("模板不存在");
        }
        return R.ok(template);
    }

    @Operation(summary = "分页查询模板", description = "根据条件分页查询模板列表")
    @PostMapping("/page")
    public R<Page<TemplatePageVO>> queryTemplatePage(@Valid @RequestBody TemplateQueryDTO queryDTO) {
        Page<TemplatePageVO> page = umpMsgTemplateService.queryTemplatePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据模板类型查询模板列表", description = "根据模板类型查询模板列表")
    @GetMapping("/type/{templateType}")
    public R<List<TemplateDetailVO>> getByTemplateType(
            @Parameter(description = "模板类型", required = true) 
            @PathVariable String templateType,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<TemplateDetailVO> templates = umpMsgTemplateService.getByTemplateType(templateType, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "查询所有启用的模板", description = "查询所有启用的模板列表")
    @GetMapping("/enabled")
    public R<List<TemplateDetailVO>> getAllEnabled() {
        List<TemplateDetailVO> templates = umpMsgTemplateService.getAllEnabled();
        return R.ok(templates);
    }

    @Operation(summary = "启用模板", description = "启用消息模板")
    @PutMapping("/enable/{id}")
    public R<Boolean> enableTemplate(
            @Parameter(description = "模板ID", required = true) 
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.enableTemplate(id);
        return success ? R.ok(true) : R.failed("启用失败");
    }

    @Operation(summary = "禁用模板", description = "禁用消息模板")
    @PutMapping("/disable/{id}")
    public R<Boolean> disableTemplate(
            @Parameter(description = "模板ID", required = true) 
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.disableTemplate(id);
        return success ? R.ok(true) : R.failed("禁用失败");
    }

    @Operation(summary = "批量启用模板", description = "批量启用消息模板")
    @PutMapping("/enable/batch")
    public R<Integer> batchEnableTemplates(@RequestBody List<String> ids) {
        int updatedCount = umpMsgTemplateService.batchEnableTemplates(ids);
        String ret = "成功启用" + String.valueOf(updatedCount) + "个模板";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量禁用模板", description = "批量禁用消息模板")
    @PutMapping("/disable/batch")
    public R<Integer> batchDisableTemplates(@RequestBody List<String> ids) {
        int updatedCount = umpMsgTemplateService.batchDisableTemplates(ids);
        String ret = "成功禁用" + String.valueOf(updatedCount) + "个模板";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "检查模板代码是否存在", description = "检查模板代码是否存在")
    @GetMapping("/exists/{templateCode}")
    public R<Boolean> existsByTemplateCode(
            @Parameter(description = "模板代码", required = true) 
            @PathVariable String templateCode) {
        boolean exists = umpMsgTemplateService.existsByTemplateCode(templateCode);
        return R.ok(exists);
    }

    @Operation(summary = "获取模板统计", description = "获取消息模板的统计信息")
    @GetMapping("/statistics")
    public R<TemplateStatisticsVO> getTemplateStatistics() {
        TemplateStatisticsVO statistics = umpMsgTemplateService.getTemplateStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "根据模板代码列表查询模板", description = "根据模板代码列表查询模板映射")
    @PostMapping("/batch/codes")
    public R<Map<String, TemplateDetailVO>> getTemplatesByCodes(
            @RequestBody List<String> templateCodes,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, TemplateDetailVO> templates = umpMsgTemplateService.getTemplatesByCodes(templateCodes, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "搜索模板", description = "根据关键词搜索模板")
    @GetMapping("/search")
    public R<List<TemplateDetailVO>> searchTemplates(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<TemplateDetailVO> templates = umpMsgTemplateService.searchTemplates(keyword, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "获取模板使用统计", description = "获取模板的使用统计信息")
    @GetMapping("/usage/statistics")
    public R<List<TemplateUsageVO>> getTemplateUsageStatistics(
            @RequestParam(required = false) String templateCode) {
        List<TemplateUsageVO> usageStats = umpMsgTemplateService.getTemplateUsageStatistics(templateCode);
        return R.ok(usageStats);
    }

    @Operation(summary = "渲染模板", description = "根据模板代码和变量渲染模板")
    @PostMapping("/render/{templateCode}")
    public R<Map<String, String>> renderTemplate(
            @Parameter(description = "模板代码", required = true) 
            @PathVariable String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            Map<String, String> result = umpMsgTemplateService.renderTemplate(templateCode, variables);
            return R.ok(result, "模板渲染成功");
        } catch (Exception e) {
            log.error("渲染模板失败", e);
            return R.failed("模板渲染失败: " + e.getMessage());
        }
    }

    @Operation(summary = "验证模板变量", description = "验证模板变量是否符合要求")
    @PostMapping("/validate/variables/{templateCode}")
    public R<Map<String, Object>> validateTemplateVariables(
            @Parameter(description = "模板代码", required = true) 
            @PathVariable String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        Map<String, Object> result = umpMsgTemplateService.validateTemplateVariables(templateCode, variables);
        return R.ok(result);
    }

    @Operation(summary = "复制模板", description = "复制现有模板创建新模板")
    @PostMapping("/copy")
    public R<String> copyTemplate(
            @RequestParam String sourceTemplateCode,
            @RequestParam String targetTemplateCode,
            @RequestParam String targetTemplateName) {
        try {
            String newTemplateId = umpMsgTemplateService.copyTemplate(
                    sourceTemplateCode, targetTemplateCode, targetTemplateName);
            return R.ok(newTemplateId, "模板复制成功");
        } catch (Exception e) {
            log.error("复制模板失败", e);
            return R.failed("模板复制失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除模板", description = "删除消息模板")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteTemplate(
            @Parameter(description = "模板ID", required = true) 
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.deleteTemplate(id);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "批量删除模板", description = "批量删除消息模板")
    @DeleteMapping("/batch")
    public R<Integer> batchDeleteTemplates(@RequestBody List<String> ids) {
        int deletedCount = umpMsgTemplateService.batchDeleteTemplates(ids);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个模板";
        return R.ok(deletedCount, ret);
    }
}