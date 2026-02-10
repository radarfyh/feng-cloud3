package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTemplate;
import ltd.huntinginfo.feng.center.mapper.UmpMsgTemplateMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgTemplateService;
import ltd.huntinginfo.feng.center.api.dto.TemplateQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TemplateDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TemplatePageVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateUsageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息模板表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgTemplateServiceImpl extends ServiceImpl<UmpMsgTemplateMapper, UmpMsgTemplate> implements UmpMsgTemplateService {

    private final UmpMsgTemplateMapper umpMsgTemplateMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTemplate(String templateCode, String templateName, String templateType,
                                String titleTemplate, String contentTemplate, Map<String, Object> variables,
                                Integer defaultPriority, String defaultPushMode, String defaultCallbackUrl) {
        if (!StringUtils.hasText(templateCode) || !StringUtils.hasText(templateName) || 
            !StringUtils.hasText(templateType) || !StringUtils.hasText(titleTemplate) || 
            !StringUtils.hasText(contentTemplate)) {
            throw new IllegalArgumentException("模板代码、模板名称、模板类型、标题模板和内容模板不能为空");
        }

        // 检查模板代码是否已存在
        if (existsByTemplateCode(templateCode)) {
            log.warn("模板代码已存在，模板代码: {}", templateCode);
            throw new RuntimeException("模板代码已存在");
        }

        // 创建模板
        UmpMsgTemplate template = new UmpMsgTemplate();
        template.setTemplateCode(templateCode);
        template.setTemplateName(templateName);
        template.setTemplateType(templateType);
        template.setTitleTemplate(titleTemplate);
        template.setContentTemplate(contentTemplate);
        template.setVariables(variables);
        template.setDefaultPriority(defaultPriority != null ? defaultPriority : 3);
        template.setDefaultPushMode(defaultPushMode);
        template.setDefaultCallbackUrl(defaultCallbackUrl);
        template.setStatus(1); // 默认启用
        template.setDelFlag(0);
        template.setCreateTime(LocalDateTime.now());

        if (save(template)) {
            log.info("模板创建成功，模板代码: {}, 模板名称: {}", templateCode, templateName);
            return template.getId();
        } else {
            log.error("模板创建失败，模板代码: {}", templateCode);
            throw new RuntimeException("模板创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTemplate(String id, String templateName, String titleTemplate,
                                 String contentTemplate, Map<String, Object> variables,
                                 Integer defaultPriority, String defaultPushMode,
                                 String defaultCallbackUrl, Integer status) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("模板ID不能为空");
        }

        UmpMsgTemplate template = getById(id);
        if (template == null) {
            log.warn("模板不存在，模板ID: {}", id);
            return false;
        }

        boolean updated = false;
        if (StringUtils.hasText(templateName)) {
            template.setTemplateName(templateName);
            updated = true;
        }
        if (StringUtils.hasText(titleTemplate)) {
            template.setTitleTemplate(titleTemplate);
            updated = true;
        }
        if (StringUtils.hasText(contentTemplate)) {
            template.setContentTemplate(contentTemplate);
            updated = true;
        }
        if (variables != null) {
            template.setVariables(variables);
            updated = true;
        }
        if (defaultPriority != null) {
            template.setDefaultPriority(defaultPriority);
            updated = true;
        }
        if (StringUtils.hasText(defaultPushMode)) {
            template.setDefaultPushMode(defaultPushMode);
            updated = true;
        }
        if (defaultCallbackUrl != null) {
            template.setDefaultCallbackUrl(defaultCallbackUrl);
            updated = true;
        }
        if (status != null) {
            template.setStatus(status);
            updated = true;
        }

        if (updated) {
            template.setUpdateTime(LocalDateTime.now());
            boolean success = updateById(template);
            if (success) {
                log.info("模板更新成功，模板ID: {}, 模板代码: {}", id, template.getTemplateCode());
            }
            return success;
        }
        
        return true;
    }

    @Override
    public TemplateDetailVO getByTemplateCode(String templateCode) {
        if (!StringUtils.hasText(templateCode)) {
            throw new IllegalArgumentException("模板代码不能为空");
        }

        UmpMsgTemplate template = umpMsgTemplateMapper.selectByTemplateCode(templateCode);
        if (template == null) {
            log.warn("模板不存在，模板代码: {}", templateCode);
            return null;
        }

        return convertToDetailVO(template);
    }

    @Override
    public Page<TemplatePageVO> queryTemplatePage(TemplateQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgTemplate::getDelFlag, 0); // 只查询未删除的记录

        if (StringUtils.hasText(queryDTO.getTemplateName())) {
            queryWrapper.like(UmpMsgTemplate::getTemplateName, queryDTO.getTemplateName());
        }
        
        if (StringUtils.hasText(queryDTO.getTemplateType())) {
            queryWrapper.eq(UmpMsgTemplate::getTemplateType, queryDTO.getTemplateType());
        }
        
        if (StringUtils.hasText(queryDTO.getTemplateCode())) {
            queryWrapper.eq(UmpMsgTemplate::getTemplateCode, queryDTO.getTemplateCode());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpMsgTemplate::getStatus, queryDTO.getStatus());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgTemplate::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgTemplate> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgTemplate> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<TemplatePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<TemplatePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<TemplateDetailVO> getByTemplateType(String templateType, boolean enabledOnly) {
        if (!StringUtils.hasText(templateType)) {
            throw new IllegalArgumentException("模板类型不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpMsgTemplate> templates = umpMsgTemplateMapper.selectByTemplateType(templateType, status);
        
        return templates.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(TemplateDetailVO::getTemplateName))
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateDetailVO> getAllEnabled() {
        List<UmpMsgTemplate> templates = umpMsgTemplateMapper.selectAllEnabled();
        
        return templates.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(TemplateDetailVO::getTemplateType)
                        .thenComparing(TemplateDetailVO::getTemplateName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableTemplate(String id) {
        return updateTemplateStatus(id, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableTemplate(String id) {
        return updateTemplateStatus(id, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnableTemplates(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int updatedCount = umpMsgTemplateMapper.batchUpdateStatus(ids, 1);
        if (updatedCount > 0) {
            log.info("批量启用模板成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisableTemplates(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int updatedCount = umpMsgTemplateMapper.batchUpdateStatus(ids, 0);
        if (updatedCount > 0) {
            log.info("批量禁用模板成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        if (!StringUtils.hasText(templateCode)) {
            return false;
        }

        return umpMsgTemplateMapper.existsByTemplateCode(templateCode);
    }

    @Override
    public TemplateStatisticsVO getTemplateStatistics() {
        Map<String, Object> statsMap = umpMsgTemplateMapper.selectTemplateStatistics();
        
        TemplateStatisticsVO statisticsVO = new TemplateStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            
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
    public Map<String, TemplateDetailVO> getTemplatesByCodes(List<String> templateCodes, boolean enabledOnly) {
        if (CollectionUtils.isEmpty(templateCodes)) {
            return Collections.emptyMap();
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpMsgTemplate> templates = umpMsgTemplateMapper.selectByTemplateCodes(templateCodes, status);
        
        return templates.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toMap(TemplateDetailVO::getTemplateCode, vo -> vo));
    }

    @Override
    public List<TemplateDetailVO> searchTemplates(String keyword, boolean enabledOnly) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpMsgTemplate> templates = umpMsgTemplateMapper.searchTemplates(keyword, status);
        
        return templates.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(TemplateDetailVO::getTemplateType)
                        .thenComparing(TemplateDetailVO::getTemplateName))
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateUsageVO> getTemplateUsageStatistics(String templateCode) {
        List<Map<String, Object>> usageStats = umpMsgTemplateMapper.selectTemplateUsageStatistics(templateCode);
        
        return usageStats.stream()
                .map(this::convertToUsageVO)
                .sorted(Comparator.comparing(TemplateUsageVO::getUsageDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> renderTemplate(String templateCode, Map<String, Object> variables) {
        if (!StringUtils.hasText(templateCode)) {
            throw new IllegalArgumentException("模板代码不能为空");
        }

        UmpMsgTemplate template = umpMsgTemplateMapper.selectByTemplateCode(templateCode);
        if (template == null || template.getStatus() != 1) {
            log.warn("模板不存在或未启用，模板代码: {}", templateCode);
            throw new RuntimeException("模板不存在或未启用");
        }

        try {
            // 验证变量
            Map<String, Object> validationResult = validateTemplateVariables(templateCode, variables);
            if (!Boolean.TRUE.equals(validationResult.get("valid"))) {
                throw new RuntimeException("模板变量验证失败: " + validationResult.get("message"));
            }

            // 渲染模板
            Map<String, String> result = new HashMap<>();
            
            // 渲染标题
            if (StringUtils.hasText(template.getTitleTemplate())) {
                StringSubstitutor titleSubstitutor = new StringSubstitutor(variables);
                result.put("title", titleSubstitutor.replace(template.getTitleTemplate()));
            }
            
            // 渲染内容
            if (StringUtils.hasText(template.getContentTemplate())) {
                StringSubstitutor contentSubstitutor = new StringSubstitutor(variables);
                result.put("content", contentSubstitutor.replace(template.getContentTemplate()));
            }
            
            // 添加模板信息
            result.put("templateCode", templateCode);
            result.put("templateName", template.getTemplateName());
            result.put("templateType", template.getTemplateType());
            result.put("defaultPriority", String.valueOf(template.getDefaultPriority()));
            result.put("defaultPushMode", template.getDefaultPushMode());
            result.put("defaultCallbackUrl", template.getDefaultCallbackUrl());
            
            log.debug("模板渲染成功，模板代码: {}", templateCode);
            return result;
        } catch (Exception e) {
            log.error("模板渲染失败，模板代码: {}", templateCode, e);
            throw new RuntimeException("模板渲染失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateTemplateVariables(String templateCode, Map<String, Object> variables) {
        if (!StringUtils.hasText(templateCode)) {
            throw new IllegalArgumentException("模板代码不能为空");
        }

        UmpMsgTemplate template = umpMsgTemplateMapper.selectByTemplateCode(templateCode);
        if (template == null) {
            return Map.of("valid", false, "message", "模板不存在");
        }

        Map<String, Object> result = new HashMap<>();
        List<String> missingVariables = new ArrayList<>();
        List<String> extraVariables = new ArrayList<>();

        // 获取模板定义的变量
        Map<String, Object> definedVariables = template.getVariables();
        if (definedVariables != null) {
            // 检查必填变量
            for (Map.Entry<String, Object> entry : definedVariables.entrySet()) {
                String varName = entry.getKey();
                Object varDef = entry.getValue();
                
                if (varDef instanceof Map) {
                    Map<String, Object> varDefMap = (Map<String, Object>) varDef;
                    Boolean required = (Boolean) varDefMap.get("required");
                    
                    if (Boolean.TRUE.equals(required) && 
                        (variables == null || !variables.containsKey(varName))) {
                        missingVariables.add(varName);
                    }
                }
            }

            // 检查额外变量
            if (variables != null) {
                for (String varName : variables.keySet()) {
                    if (!definedVariables.containsKey(varName)) {
                        extraVariables.add(varName);
                    }
                }
            }
        }

        result.put("valid", missingVariables.isEmpty());
        result.put("missingVariables", missingVariables);
        result.put("extraVariables", extraVariables);
        result.put("message", missingVariables.isEmpty() ? "变量验证通过" : "缺少必填变量: " + missingVariables);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyTemplate(String sourceTemplateCode, String targetTemplateCode, String targetTemplateName) {
        if (!StringUtils.hasText(sourceTemplateCode) || !StringUtils.hasText(targetTemplateCode) || 
            !StringUtils.hasText(targetTemplateName)) {
            throw new IllegalArgumentException("源模板代码、目标模板代码和目标模板名称不能为空");
        }

        // 检查源模板是否存在
        UmpMsgTemplate sourceTemplate = umpMsgTemplateMapper.selectByTemplateCode(sourceTemplateCode);
        if (sourceTemplate == null) {
            log.warn("源模板不存在，模板代码: {}", sourceTemplateCode);
            throw new RuntimeException("源模板不存在");
        }

        // 检查目标模板代码是否已存在
        if (existsByTemplateCode(targetTemplateCode)) {
            log.warn("目标模板代码已存在，模板代码: {}", targetTemplateCode);
            throw new RuntimeException("目标模板代码已存在");
        }

        // 复制模板
        UmpMsgTemplate newTemplate = new UmpMsgTemplate();
        BeanUtils.copyProperties(sourceTemplate, newTemplate, "id", "templateCode", "templateName", 
                               "createTime", "updateTime", "createBy", "updateBy");
        
        newTemplate.setId(null); // 清除ID，让数据库生成新的
        newTemplate.setTemplateCode(targetTemplateCode);
        newTemplate.setTemplateName(targetTemplateName);
        newTemplate.setCreateTime(LocalDateTime.now());
        newTemplate.setDelFlag(0);
        newTemplate.setStatus(1); // 默认启用

        if (save(newTemplate)) {
            log.info("模板复制成功，源模板: {}, 目标模板: {}", sourceTemplateCode, targetTemplateCode);
            return newTemplate.getId();
        } else {
            log.error("模板复制失败，源模板: {}, 目标模板: {}", sourceTemplateCode, targetTemplateCode);
            throw new RuntimeException("模板复制失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemplate(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("模板ID不能为空");
        }

        UmpMsgTemplate template = getById(id);
        if (template == null) {
            log.warn("模板不存在，模板ID: {}", id);
            return false;
        }

        // 逻辑删除
        template.setDelFlag(1);
        template.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(template);
        if (success) {
            log.info("模板删除成功，模板ID: {}, 模板代码: {}", id, template.getTemplateCode());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteTemplates(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        List<UmpMsgTemplate> templates = listByIds(ids);
        if (CollectionUtils.isEmpty(templates)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        for (UmpMsgTemplate template : templates) {
            template.setDelFlag(1);
            template.setUpdateTime(now);
        }

        boolean success = updateBatchById(templates);
        if (success) {
            log.info("批量删除模板成功，数量: {}", templates.size());
            return templates.size();
        }
        
        return 0;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgTemplate> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getUpdateTime);
                }
                break;
            case "templateName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getTemplateName);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getTemplateName);
                }
                break;
            case "templateCode":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getTemplateCode);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getTemplateCode);
                }
                break;
            case "templateType":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getTemplateType);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getTemplateType);
                }
                break;
            case "defaultPriority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTemplate::getDefaultPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTemplate::getDefaultPriority);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgTemplate::getCreateTime);
                break;
        }
    }

    private boolean updateTemplateStatus(String id, Integer status) {
        if (!StringUtils.hasText(id) || status == null) {
            throw new IllegalArgumentException("模板ID和状态不能为空");
        }

        UmpMsgTemplate template = getById(id);
        if (template == null) {
            log.warn("模板不存在，模板ID: {}", id);
            return false;
        }

        if (template.getStatus().equals(status)) {
            log.debug("模板状态未改变，模板ID: {}, 状态: {}", id, status);
            return true;
        }

        template.setStatus(status);
        template.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(template);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("模板{}成功，模板ID: {}, 模板代码: {}", action, id, template.getTemplateCode());
        }
        
        return success;
    }

    private TemplateDetailVO convertToDetailVO(UmpMsgTemplate template) {
        TemplateDetailVO vo = new TemplateDetailVO();
        BeanUtils.copyProperties(template, vo);
        
        // 解析变量定义
        if (template.getVariables() != null) {
            try {
                Map<String, Map<String, Object>> parsedVariables = new HashMap<>();
                for (Map.Entry<String, Object> entry : template.getVariables().entrySet()) {
                    if (entry.getValue() instanceof Map) {
                        parsedVariables.put(entry.getKey(), (Map<String, Object>) entry.getValue());
                    }
                }
                vo.setParsedVariables(parsedVariables);
            } catch (Exception e) {
                log.warn("解析模板变量失败，模板代码: {}", template.getTemplateCode(), e);
            }
        }
        
        return vo;
    }

    private TemplatePageVO convertToPageVO(UmpMsgTemplate template) {
        TemplatePageVO vo = new TemplatePageVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }

    private TemplateUsageVO convertToUsageVO(Map<String, Object> usageMap) {
        TemplateUsageVO vo = new TemplateUsageVO();
        vo.setTemplateCode((String) usageMap.get("template_code"));
        vo.setTemplateName((String) usageMap.get("template_name"));
        vo.setUsageDate((String) usageMap.get("usage_date"));
        vo.setUsageCount(((Number) usageMap.getOrDefault("usage_count", 0)).longValue());
        vo.setSuccessCount(((Number) usageMap.getOrDefault("success_count", 0)).longValue());
        vo.setFailedCount(((Number) usageMap.getOrDefault("failed_count", 0)).longValue());
        
        // 计算成功率
        if (vo.getUsageCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getUsageCount() * 100);
        }
        
        return vo;
    }
}