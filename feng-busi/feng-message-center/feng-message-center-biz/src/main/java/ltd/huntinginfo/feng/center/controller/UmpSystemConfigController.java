package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.SystemConfigQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigPageVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpSystemConfigService;
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
 * 系统配置表控制器
 * 提供系统配置的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置的增删改查和状态管理")
public class UmpSystemConfigController {

    private final UmpSystemConfigService umpSystemConfigService;

    @Operation(summary = "创建配置", description = "创建新的系统配置")
    @PostMapping("/create")
    public R<String> createConfig(
            @RequestParam String configKey,
            @RequestParam String configValue,
            @RequestParam String configType,
            @RequestParam(required = false) String configDesc,
            @RequestParam(required = false) String category) {
        try {
            String configId = umpSystemConfigService.createConfig(
                    configKey, configValue, configType, configDesc, category);
            return R.ok(configId, "配置创建成功");
        } catch (Exception e) {
            log.error("创建配置失败", e);
            return R.failed("配置创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新配置", description = "更新系统配置信息")
    @PutMapping("/{configKey}")
    public R<Boolean> updateConfig(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) String configValue,
            @RequestParam(required = false) String configDesc,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String configType,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpSystemConfigService.updateConfig(
                    configKey, configValue, configDesc, category, configType, status);
            return success ? R.ok(true) : R.failed("配置更新失败");
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return R.failed("配置更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据配置键查询配置", description = "根据配置键查询配置详情")
    @GetMapping("/key/{configKey}")
    public R<SystemConfigDetailVO> getByConfigKey(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey) {
        SystemConfigDetailVO config = umpSystemConfigService.getByConfigKey(configKey);
        if (config == null) {
            return R.failed("配置不存在");
        }
        return R.ok(config);
    }

    @Operation(summary = "分页查询配置", description = "根据条件分页查询配置列表")
    @PostMapping("/page")
    public R<Page<SystemConfigPageVO>> queryConfigPage(@Valid @RequestBody SystemConfigQueryDTO queryDTO) {
        Page<SystemConfigPageVO> page = umpSystemConfigService.queryConfigPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据类别查询配置列表", description = "根据类别查询配置列表")
    @GetMapping("/category/{category}")
    public R<List<SystemConfigDetailVO>> getByCategory(
            @Parameter(description = "配置类别", required = true) 
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<SystemConfigDetailVO> configs = umpSystemConfigService.getByCategory(category, enabledOnly);
        return R.ok(configs);
    }

    @Operation(summary = "根据配置类型查询配置列表", description = "根据配置类型查询配置列表")
    @GetMapping("/type/{configType}")
    public R<List<SystemConfigDetailVO>> getByConfigType(
            @Parameter(description = "配置类型", required = true) 
            @PathVariable String configType,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<SystemConfigDetailVO> configs = umpSystemConfigService.getByConfigType(configType, enabledOnly);
        return R.ok(configs);
    }

    @Operation(summary = "查询所有启用的配置", description = "查询所有启用的配置列表")
    @GetMapping("/enabled")
    public R<List<SystemConfigDetailVO>> getAllEnabled() {
        List<SystemConfigDetailVO> configs = umpSystemConfigService.getAllEnabled();
        return R.ok(configs);
    }

    @Operation(summary = "启用配置", description = "启用系统配置")
    @PutMapping("/enable/{configKey}")
    public R<Boolean> enableConfig(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey) {
        boolean success = umpSystemConfigService.enableConfig(configKey);
        return success ? R.ok(true) : R.failed("启用失败");
    }

    @Operation(summary = "禁用配置", description = "禁用系统配置")
    @PutMapping("/disable/{configKey}")
    public R<Boolean> disableConfig(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey) {
        boolean success = umpSystemConfigService.disableConfig(configKey);
        return success ? R.ok(true) : R.failed("禁用失败");
    }

    @Operation(summary = "批量启用配置", description = "批量启用系统配置")
    @PutMapping("/enable/batch")
    public R<Integer> batchEnableConfigs(@RequestBody List<String> configKeys) {
        int updatedCount = umpSystemConfigService.batchEnableConfigs(configKeys);
        String ret = "成功启用" + String.valueOf(updatedCount) + "个配置";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量禁用配置", description = "批量禁用系统配置")
    @PutMapping("/disable/batch")
    public R<Integer> batchDisableConfigs(@RequestBody List<String> configKeys) {
        int updatedCount = umpSystemConfigService.batchDisableConfigs(configKeys);
        String ret = "成功禁用" + String.valueOf(updatedCount) + "个配置";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "更新配置值", description = "更新配置值")
    @PutMapping("/value/{configKey}")
    public R<Boolean> updateConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam String configValue) {
        boolean success = umpSystemConfigService.updateConfigValue(configKey, configValue);
        return success ? R.ok(true) : R.failed("更新失败");
    }

    @Operation(summary = "检查配置键是否存在", description = "检查配置键是否存在")
    @GetMapping("/exists/{configKey}")
    public R<Boolean> existsByConfigKey(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey) {
        boolean exists = umpSystemConfigService.existsByConfigKey(configKey);
        return R.ok(exists);
    }

    @Operation(summary = "获取配置统计", description = "获取系统配置的统计信息")
    @GetMapping("/statistics")
    public R<SystemConfigStatisticsVO> getConfigStatistics() {
        SystemConfigStatisticsVO statistics = umpSystemConfigService.getConfigStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "根据配置键列表查询配置", description = "根据配置键列表查询配置映射")
    @PostMapping("/batch/keys")
    public R<Map<String, String>> getConfigsByKeys(
            @RequestBody List<String> configKeys,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, String> configs = umpSystemConfigService.getConfigsByKeys(configKeys, enabledOnly);
        return R.ok(configs);
    }

    @Operation(summary = "获取配置键值映射", description = "获取配置的键值对映射")
    @GetMapping("/map")
    public R<Map<String, String>> getConfigMap(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, String> configMap = umpSystemConfigService.getConfigMap(category, enabledOnly);
        return R.ok(configMap);
    }

    @Operation(summary = "获取配置值", description = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public R<String> getConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) String defaultValue) {
        String value = umpSystemConfigService.getConfigValue(configKey, defaultValue);
        return R.ok(value);
    }

    @Operation(summary = "获取整数配置值", description = "根据配置键获取整数配置值")
    @GetMapping("/value/int/{configKey}")
    public R<Integer> getIntConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) Integer defaultValue) {
        Integer value = umpSystemConfigService.getIntConfigValue(configKey, defaultValue);
        return R.ok(value);
    }

    @Operation(summary = "获取长整数配置值", description = "根据配置键获取长整数配置值")
    @GetMapping("/value/long/{configKey}")
    public R<Long> getLongConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) Long defaultValue) {
        Long value = umpSystemConfigService.getLongConfigValue(configKey, defaultValue);
        return R.ok(value);
    }

    @Operation(summary = "获取布尔配置值", description = "根据配置键获取布尔配置值")
    @GetMapping("/value/boolean/{configKey}")
    public R<Boolean> getBooleanConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) Boolean defaultValue) {
        Boolean value = umpSystemConfigService.getBooleanConfigValue(configKey, defaultValue);
        return R.ok(value);
    }

    @Operation(summary = "获取浮点数配置值", description = "根据配置键获取浮点数配置值")
    @GetMapping("/value/double/{configKey}")
    public R<Double> getDoubleConfigValue(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey,
            @RequestParam(required = false) Double defaultValue) {
        Double value = umpSystemConfigService.getDoubleConfigValue(configKey, defaultValue);
        return R.ok(value);
    }

    @Operation(summary = "删除配置", description = "删除系统配置")
    @DeleteMapping("/{configKey}")
    public R<Boolean> deleteConfig(
            @Parameter(description = "配置键", required = true) 
            @PathVariable String configKey) {
        boolean success = umpSystemConfigService.deleteConfig(configKey);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "批量删除配置", description = "批量删除系统配置")
    @DeleteMapping("/batch")
    public R<Integer> batchDeleteConfigs(@RequestBody List<String> configKeys) {
        int deletedCount = umpSystemConfigService.batchDeleteConfigs(configKeys);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个配置";
        return R.ok(deletedCount, ret);
    }
}