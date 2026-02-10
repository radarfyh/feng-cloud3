package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemConfig;
import ltd.huntinginfo.feng.center.mapper.UmpSystemConfigMapper;
import ltd.huntinginfo.feng.center.service.UmpSystemConfigService;
import ltd.huntinginfo.feng.center.api.dto.SystemConfigQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigPageVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpSystemConfigServiceImpl extends ServiceImpl<UmpSystemConfigMapper, UmpSystemConfig> implements UmpSystemConfigService {

    private final UmpSystemConfigMapper umpSystemConfigMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createConfig(String configKey, String configValue, String configType,
                              String configDesc, String category) {
        if (!StringUtils.hasText(configKey) || !StringUtils.hasText(configValue) || 
            !StringUtils.hasText(configType)) {
            throw new IllegalArgumentException("配置键、配置值和配置类型不能为空");
        }

        // 检查配置键是否已存在
        if (existsByConfigKey(configKey)) {
            log.warn("配置键已存在，配置键: {}", configKey);
            throw new RuntimeException("配置键已存在");
        }

        // 验证配置类型
        if (!isValidConfigType(configType)) {
            log.warn("无效的配置类型，配置类型: {}", configType);
            throw new RuntimeException("无效的配置类型");
        }

        // 验证配置值是否符合配置类型
        if (!validateConfigValue(configValue, configType)) {
            log.warn("配置值不符合配置类型，配置值: {}, 配置类型: {}", configValue, configType);
            throw new RuntimeException("配置值不符合配置类型");
        }

        // 创建配置
        UmpSystemConfig config = new UmpSystemConfig();
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType.toUpperCase());
        config.setConfigDesc(configDesc);
        config.setCategory(StringUtils.hasText(category) ? category : "COMMON");
        config.setStatus(1); // 默认启用
        config.setCreateTime(LocalDateTime.now());

        if (save(config)) {
            log.info("配置创建成功，配置键: {}, 配置类型: {}", configKey, configType);
            return config.getId();
        } else {
            log.error("配置创建失败，配置键: {}", configKey);
            throw new RuntimeException("配置创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(String configKey, String configValue, String configDesc,
                               String category, String configType, Integer status) {
        if (!StringUtils.hasText(configKey)) {
            throw new IllegalArgumentException("配置键不能为空");
        }

        UmpSystemConfig config = umpSystemConfigMapper.selectByConfigKey(configKey);
        if (config == null) {
            log.warn("配置不存在，配置键: {}", configKey);
            return false;
        }

        boolean updated = false;
        if (StringUtils.hasText(configValue)) {
            // 验证配置值是否符合配置类型
            String type = StringUtils.hasText(configType) ? configType : config.getConfigType();
            if (!validateConfigValue(configValue, type)) {
                log.warn("配置值不符合配置类型，配置值: {}, 配置类型: {}", configValue, type);
                throw new RuntimeException("配置值不符合配置类型");
            }
            config.setConfigValue(configValue);
            updated = true;
        }
        if (StringUtils.hasText(configDesc)) {
            config.setConfigDesc(configDesc);
            updated = true;
        }
        if (StringUtils.hasText(category)) {
            config.setCategory(category);
            updated = true;
        }
        if (StringUtils.hasText(configType)) {
            // 验证配置类型
            if (!isValidConfigType(configType)) {
                log.warn("无效的配置类型，配置类型: {}", configType);
                throw new RuntimeException("无效的配置类型");
            }
            config.setConfigType(configType.toUpperCase());
            updated = true;
        }
        if (status != null) {
            config.setStatus(status);
            updated = true;
        }

        if (updated) {
            config.setUpdateTime(LocalDateTime.now());
            boolean success = updateById(config);
            if (success) {
                log.info("配置更新成功，配置键: {}", configKey);
            }
            return success;
        }
        
        return true;
    }

    @Override
    public SystemConfigDetailVO getByConfigKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            throw new IllegalArgumentException("配置键不能为空");
        }

        UmpSystemConfig config = umpSystemConfigMapper.selectByConfigKey(configKey);
        if (config == null) {
            log.warn("配置不存在，配置键: {}", configKey);
            return null;
        }

        return convertToDetailVO(config);
    }

    @Override
    public Page<SystemConfigPageVO> queryConfigPage(SystemConfigQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpSystemConfig> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getConfigKey())) {
            queryWrapper.like(UmpSystemConfig::getConfigKey, queryDTO.getConfigKey());
        }
        
        if (StringUtils.hasText(queryDTO.getConfigType())) {
            queryWrapper.eq(UmpSystemConfig::getConfigType, queryDTO.getConfigType());
        }
        
        if (StringUtils.hasText(queryDTO.getCategory())) {
            queryWrapper.eq(UmpSystemConfig::getCategory, queryDTO.getCategory());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpSystemConfig::getStatus, queryDTO.getStatus());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByAsc(UmpSystemConfig::getCategory)
                       .orderByAsc(UmpSystemConfig::getConfigKey);
        }

        // 执行分页查询
        Page<UmpSystemConfig> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpSystemConfig> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<SystemConfigPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<SystemConfigPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<SystemConfigDetailVO> getByCategory(String category, boolean enabledOnly) {
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException("配置类别不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByCategory(category, status);
        
        return configs.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(SystemConfigDetailVO::getConfigKey))
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemConfigDetailVO> getByConfigType(String configType, boolean enabledOnly) {
        if (!StringUtils.hasText(configType)) {
            throw new IllegalArgumentException("配置类型不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByConfigType(configType, status);
        
        return configs.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(SystemConfigDetailVO::getCategory)
                        .thenComparing(SystemConfigDetailVO::getConfigKey))
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemConfigDetailVO> getAllEnabled() {
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByCategory(null, 1);
        
        return configs.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(SystemConfigDetailVO::getCategory)
                        .thenComparing(SystemConfigDetailVO::getConfigKey))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableConfig(String configKey) {
        return updateConfigStatus(configKey, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableConfig(String configKey) {
        return updateConfigStatus(configKey, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnableConfigs(List<String> configKeys) {
        if (CollectionUtils.isEmpty(configKeys)) {
            return 0;
        }

        // 获取配置ID列表
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByConfigKeys(configKeys, null);
        if (CollectionUtils.isEmpty(configs)) {
            return 0;
        }

        List<String> ids = configs.stream()
                .map(UmpSystemConfig::getId)
                .collect(Collectors.toList());

        int updatedCount = umpSystemConfigMapper.batchUpdateStatus(ids, 1);
        if (updatedCount > 0) {
            log.info("批量启用配置成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisableConfigs(List<String> configKeys) {
        if (CollectionUtils.isEmpty(configKeys)) {
            return 0;
        }

        // 获取配置ID列表
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByConfigKeys(configKeys, null);
        if (CollectionUtils.isEmpty(configs)) {
            return 0;
        }

        List<String> ids = configs.stream()
                .map(UmpSystemConfig::getId)
                .collect(Collectors.toList());

        int updatedCount = umpSystemConfigMapper.batchUpdateStatus(ids, 0);
        if (updatedCount > 0) {
            log.info("批量禁用配置成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigValue(String configKey, String configValue) {
        if (!StringUtils.hasText(configKey) || !StringUtils.hasText(configValue)) {
            throw new IllegalArgumentException("配置键和配置值不能为空");
        }

        UmpSystemConfig config = umpSystemConfigMapper.selectByConfigKey(configKey);
        if (config == null) {
            log.warn("配置不存在，配置键: {}", configKey);
            return false;
        }

        // 验证配置值是否符合配置类型
        if (!validateConfigValue(configValue, config.getConfigType())) {
            log.warn("配置值不符合配置类型，配置值: {}, 配置类型: {}", configValue, config.getConfigType());
            throw new RuntimeException("配置值不符合配置类型");
        }

        int updated = umpSystemConfigMapper.updateConfigValue(configKey, configValue);
        if (updated > 0) {
            log.info("配置值更新成功，配置键: {}, 新值: {}", configKey, configValue);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean existsByConfigKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }

        return umpSystemConfigMapper.existsByConfigKey(configKey);
    }

    @Override
    public SystemConfigStatisticsVO getConfigStatistics() {
        Map<String, Object> statsMap = umpSystemConfigMapper.selectConfigStatistics();
        
        SystemConfigStatisticsVO statisticsVO = new SystemConfigStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            
            // 分类统计
            Map<String, Long> categoryStats = new HashMap<>();
            if (statsMap.containsKey("category_stats")) {
                // 这里假设category_stats是以JSON格式存储的
                // 实际实现可能需要根据数据库返回的格式进行调整
                try {
                    String categoryStatsJson = (String) statsMap.get("category_stats");
                    Map<String, Object> categoryMap = objectMapper.readValue(categoryStatsJson, Map.class);
                    categoryMap.forEach((key, value) -> categoryStats.put(key, ((Number) value).longValue()));
                } catch (Exception e) {
                    log.warn("解析分类统计失败", e);
                }
            }
            statisticsVO.setCategoryStats(categoryStats);
            
            // 类型统计
            Map<String, Long> typeStats = new HashMap<>();
            if (statsMap.containsKey("type_stats")) {
                try {
                    String typeStatsJson = (String) statsMap.get("type_stats");
                    Map<String, Object> typeMap = objectMapper.readValue(typeStatsJson, Map.class);
                    typeMap.forEach((key, value) -> typeStats.put(key, ((Number) value).longValue()));
                } catch (Exception e) {
                    log.warn("解析类型统计失败", e);
                }
            }
            statisticsVO.setTypeStats(typeStats);
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public Map<String, String> getConfigsByKeys(List<String> configKeys, boolean enabledOnly) {
        if (CollectionUtils.isEmpty(configKeys)) {
            return Collections.emptyMap();
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpSystemConfig> configs = umpSystemConfigMapper.selectByConfigKeys(configKeys, status);
        
        return configs.stream()
                .collect(Collectors.toMap(UmpSystemConfig::getConfigKey, UmpSystemConfig::getConfigValue));
    }

    @Override
    public Map<String, String> getConfigMap(String category, boolean enabledOnly) {
        Integer status = enabledOnly ? 1 : null;
        return umpSystemConfigMapper.selectConfigMap(category, status);
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        if (!StringUtils.hasText(configKey)) {
            return defaultValue;
        }

        UmpSystemConfig config = umpSystemConfigMapper.selectByConfigKey(configKey);
        if (config == null || config.getStatus() != 1) {
            return defaultValue;
        }

        return config.getConfigValue();
    }

    @Override
    public Integer getIntConfigValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的整数，配置键: {}, 配置值: {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Long getLongConfigValue(String configKey, Long defaultValue) {
        String value = getConfigValue(configKey, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的长整数，配置键: {}, 配置值: {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Boolean getBooleanConfigValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }

        value = value.trim().toLowerCase();
        if ("true".equals(value) || "1".equals(value) || "yes".equals(value) || "on".equals(value)) {
            return true;
        } else if ("false".equals(value) || "0".equals(value) || "no".equals(value) || "off".equals(value)) {
            return false;
        } else {
            log.warn("配置值不是有效的布尔值，配置键: {}, 配置值: {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Double getDoubleConfigValue(String configKey, Double defaultValue) {
        String value = getConfigValue(configKey, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的浮点数，配置键: {}, 配置值: {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            throw new IllegalArgumentException("配置键不能为空");
        }

        LambdaQueryWrapper<UmpSystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpSystemConfig::getConfigKey, configKey);
        
        boolean success = remove(queryWrapper);
        if (success) {
            log.info("配置删除成功，配置键: {}", configKey);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteConfigs(List<String> configKeys) {
        if (CollectionUtils.isEmpty(configKeys)) {
            return 0;
        }

        LambdaQueryWrapper<UmpSystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UmpSystemConfig::getConfigKey, configKeys);
        
        boolean success = remove(queryWrapper);
        if (success) {
            int count = configKeys.size();
            log.info("批量删除配置成功，数量: {}", count);
            return count;
        }
        
        return 0;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpSystemConfig> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "configKey":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemConfig::getConfigKey);
                } else {
                    queryWrapper.orderByDesc(UmpSystemConfig::getConfigKey);
                }
                break;
            case "configType":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemConfig::getConfigType);
                } else {
                    queryWrapper.orderByDesc(UmpSystemConfig::getConfigType);
                }
                break;
            case "category":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemConfig::getCategory);
                } else {
                    queryWrapper.orderByDesc(UmpSystemConfig::getCategory);
                }
                break;
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemConfig::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpSystemConfig::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemConfig::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpSystemConfig::getUpdateTime);
                }
                break;
            default:
                queryWrapper.orderByAsc(UmpSystemConfig::getCategory)
                           .orderByAsc(UmpSystemConfig::getConfigKey);
                break;
        }
    }

    private boolean updateConfigStatus(String configKey, Integer status) {
        if (!StringUtils.hasText(configKey) || status == null) {
            throw new IllegalArgumentException("配置键和状态不能为空");
        }

        UmpSystemConfig config = umpSystemConfigMapper.selectByConfigKey(configKey);
        if (config == null) {
            log.warn("配置不存在，配置键: {}", configKey);
            return false;
        }

        if (config.getStatus().equals(status)) {
            log.debug("配置状态未改变，配置键: {}, 状态: {}", configKey, status);
            return true;
        }

        config.setStatus(status);
        config.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(config);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("配置{}成功，配置键: {}", action, configKey);
        }
        
        return success;
    }

    private boolean isValidConfigType(String configType) {
        return Arrays.asList("STRING", "NUMBER", "BOOLEAN", "JSON").contains(configType.toUpperCase());
    }

    private boolean validateConfigValue(String configValue, String configType) {
        if (!StringUtils.hasText(configType)) {
            return false;
        }

        switch (configType.toUpperCase()) {
            case "STRING":
                return true; // 字符串类型总是有效
            case "NUMBER":
                try {
                    Double.parseDouble(configValue);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "BOOLEAN":
                String lowerValue = configValue.toLowerCase();
                return "true".equals(lowerValue) || "false".equals(lowerValue) || 
                       "1".equals(lowerValue) || "0".equals(lowerValue) ||
                       "yes".equals(lowerValue) || "no".equals(lowerValue) ||
                       "on".equals(lowerValue) || "off".equals(lowerValue);
            case "JSON":
                try {
                    objectMapper.readTree(configValue);
                    return true;
                } catch (JsonProcessingException e) {
                    return false;
                }
            default:
                return false;
        }
    }

    private SystemConfigDetailVO convertToDetailVO(UmpSystemConfig config) {
        SystemConfigDetailVO vo = new SystemConfigDetailVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    private SystemConfigPageVO convertToPageVO(UmpSystemConfig config) {
        SystemConfigPageVO vo = new SystemConfigPageVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}