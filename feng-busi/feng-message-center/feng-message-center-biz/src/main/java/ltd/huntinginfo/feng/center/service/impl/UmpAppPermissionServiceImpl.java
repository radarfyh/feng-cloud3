package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.mapper.UmpAppPermissionMapper;
import ltd.huntinginfo.feng.center.service.UmpAppPermissionService;
import ltd.huntinginfo.feng.center.api.dto.PermissionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.PermissionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionPageVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionStatisticsVO;
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
 * 应用权限表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpAppPermissionServiceImpl extends ServiceImpl<UmpAppPermissionMapper, UmpAppPermission> implements UmpAppPermissionService {

    private final UmpAppPermissionMapper umpAppPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPermission(String appKey, String resourceCode, String resourceName, String operation) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(resourceCode) || 
            !StringUtils.hasText(operation)) {
            throw new IllegalArgumentException("应用标识、资源代码和操作类型不能为空");
        }

        // 检查权限是否已存在
        boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(appKey, resourceCode);
        if (exists) {
            log.warn("权限已存在，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            throw new RuntimeException("权限已存在");
        }

        // 创建权限
        UmpAppPermission permission = new UmpAppPermission();
        permission.setAppKey(appKey);
        permission.setResourceCode(resourceCode);
        permission.setResourceName(StringUtils.hasText(resourceName) ? resourceName : resourceCode);
        permission.setOperation(operation);
        permission.setStatus(1); // 默认启用
        permission.setCreateTime(LocalDateTime.now());

        if (save(permission)) {
            log.info("权限创建成功，应用标识: {}, 资源代码: {}, 操作: {}", appKey, resourceCode, operation);
            return permission.getId();
        } else {
            log.error("权限创建失败，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            throw new RuntimeException("权限创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreatePermissions(String appKey, List<Map<String, String>> permissions) {
        if (!StringUtils.hasText(appKey) || CollectionUtils.isEmpty(permissions)) {
            return 0;
        }

        List<UmpAppPermission> permissionList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map<String, String> permissionMap : permissions) {
            String resourceCode = permissionMap.get("resourceCode");
            String resourceName = permissionMap.get("resourceName");
            String operation = permissionMap.get("operation");

            if (!StringUtils.hasText(resourceCode) || !StringUtils.hasText(operation)) {
                log.warn("权限数据不完整，跳过: {}", permissionMap);
                continue;
            }

            // 检查权限是否已存在
            boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(appKey, resourceCode);
            if (exists) {
                log.debug("权限已存在，跳过: 应用标识: {}, 资源代码: {}", appKey, resourceCode);
                continue;
            }

            UmpAppPermission permission = new UmpAppPermission();
            permission.setAppKey(appKey);
            permission.setResourceCode(resourceCode);
            permission.setResourceName(StringUtils.hasText(resourceName) ? resourceName : resourceCode);
            permission.setOperation(operation);
            permission.setStatus(1); // 默认启用
            permission.setCreateTime(now);

            permissionList.add(permission);
        }

        if (CollectionUtils.isEmpty(permissionList)) {
            return 0;
        }

        int insertedCount = umpAppPermissionMapper.batchInsert(permissionList);
        if (insertedCount > 0) {
            log.info("批量创建权限成功，应用标识: {}, 数量: {}", appKey, insertedCount);
        }
        
        return insertedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(String permissionId, String resourceName, String operation, Integer status) {
        if (!StringUtils.hasText(permissionId)) {
            throw new IllegalArgumentException("权限ID不能为空");
        }

        UmpAppPermission permission = getById(permissionId);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", permissionId);
            return false;
        }

        if (StringUtils.hasText(resourceName)) {
            permission.setResourceName(resourceName);
        }
        if (StringUtils.hasText(operation)) {
            permission.setOperation(operation);
        }
        if (status != null) {
            permission.setStatus(status);
        }
        permission.setUpdateTime(LocalDateTime.now());

        boolean success = updateById(permission);
        if (success) {
            log.info("权限更新成功，权限ID: {}, 应用标识: {}, 资源代码: {}", 
                    permissionId, permission.getAppKey(), permission.getResourceCode());
        }
        
        return success;
    }

    @Override
    public PermissionDetailVO getPermissionByKeyAndResource(String appKey, String resourceCode) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(resourceCode)) {
            throw new IllegalArgumentException("应用标识和资源代码不能为空");
        }

        UmpAppPermission permission = umpAppPermissionMapper.selectByAppKeyAndResourceCode(appKey, resourceCode);
        if (permission == null) {
            log.warn("权限不存在，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            return null;
        }

        return convertToDetailVO(permission);
    }

    @Override
    public Page<PermissionPageVO> queryPermissionPage(PermissionQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpAppPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpAppPermission::getDelFlag, 0); // 只查询未删除的记录

        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpAppPermission::getAppKey, queryDTO.getAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getResourceCode())) {
            queryWrapper.like(UmpAppPermission::getResourceCode, queryDTO.getResourceCode());
        }
        
        if (StringUtils.hasText(queryDTO.getResourceName())) {
            queryWrapper.like(UmpAppPermission::getResourceName, queryDTO.getResourceName());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpAppPermission::getStatus, queryDTO.getStatus());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
        }

        // 执行分页查询
        Page<UmpAppPermission> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpAppPermission> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<PermissionPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<PermissionPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<PermissionDetailVO> getPermissionsByAppKey(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectByAppKey(appKey);
        return permissions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDetailVO> getAvailablePermissions(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return Collections.emptyList();
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectAvailablePermissions(appKey);
        return permissions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enablePermission(String permissionId) {
        return updatePermissionStatus(permissionId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disablePermission(String permissionId) {
        return updatePermissionStatus(permissionId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnablePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }

        int updatedCount = umpAppPermissionMapper.batchUpdateStatus(permissionIds, 1);
        if (updatedCount > 0) {
            log.info("批量启用权限成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisablePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }

        int updatedCount = umpAppPermissionMapper.batchUpdateStatus(permissionIds, 0);
        if (updatedCount > 0) {
            log.info("批量禁用权限成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    public boolean checkPermission(String appKey, String resourceCode, String operation) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(resourceCode)) {
            return false;
        }

        UmpAppPermission permission = umpAppPermissionMapper.selectByAppKeyAndResourceCode(appKey, resourceCode);
        if (permission == null) {
            log.warn("权限不存在，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            return false;
        }

        // 检查权限状态
        if (permission.getStatus() != 1 || permission.getDelFlag() != 0) {
            log.warn("权限不可用，应用标识: {}, 资源代码: {}, 状态: {}, 删除标记: {}", 
                    appKey, resourceCode, permission.getStatus(), permission.getDelFlag());
            return false;
        }

        // 检查操作权限
        return validateOperationPermission(permission.getOperation(), operation);
    }

    @Override
    public PermissionStatisticsVO getPermissionStatistics() {
        Map<String, Object> statsMap = umpAppPermissionMapper.selectPermissionStatistics();
        
        PermissionStatisticsVO statisticsVO = new PermissionStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            statisticsVO.setReadCount(((Number) statsMap.getOrDefault("read_count", 0)).longValue());
            statisticsVO.setWriteCount(((Number) statsMap.getOrDefault("write_count", 0)).longValue());
            statisticsVO.setAllOperationCount(((Number) statsMap.getOrDefault("all_operation_count", 0)).longValue());
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
            
            // 计算操作类型分布
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setReadRate((double) statisticsVO.getReadCount() / statisticsVO.getTotalCount() * 100);
                statisticsVO.setWriteRate((double) statisticsVO.getWriteCount() / statisticsVO.getTotalCount() * 100);
                statisticsVO.setAllOperationRate((double) statisticsVO.getAllOperationCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(String permissionId) {
        if (!StringUtils.hasText(permissionId)) {
            throw new IllegalArgumentException("权限ID不能为空");
        }

        UmpAppPermission permission = getById(permissionId);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", permissionId);
            return false;
        }

        // 逻辑删除
        permission.setDelFlag(1);
        permission.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(permission);
        if (success) {
            log.info("权限删除成功，权限ID: {}, 应用标识: {}, 资源代码: {}", 
                    permissionId, permission.getAppKey(), permission.getResourceCode());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeletePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }

        List<UmpAppPermission> permissions = listByIds(permissionIds);
        if (CollectionUtils.isEmpty(permissions)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        for (UmpAppPermission permission : permissions) {
            permission.setDelFlag(1);
            permission.setUpdateTime(now);
        }

        boolean success = updateBatchById(permissions);
        if (success) {
            log.info("批量删除权限成功，数量: {}", permissions.size());
            return permissions.size();
        }
        
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePermissionsByAppKey(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return 0;
        }

        LambdaQueryWrapper<UmpAppPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpAppPermission::getAppKey, appKey)
                   .eq(UmpAppPermission::getDelFlag, 0);

        List<UmpAppPermission> permissions = list(queryWrapper);
        if (CollectionUtils.isEmpty(permissions)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        for (UmpAppPermission permission : permissions) {
            permission.setDelFlag(1);
            permission.setUpdateTime(now);
        }

        boolean success = updateBatchById(permissions);
        if (success) {
            log.info("删除应用所有权限成功，应用标识: {}, 数量: {}", appKey, permissions.size());
            return permissions.size();
        }
        
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyPermissionsToApp(String sourceAppKey, String targetAppKey) {
        if (!StringUtils.hasText(sourceAppKey) || !StringUtils.hasText(targetAppKey)) {
            return 0;
        }

        List<UmpAppPermission> sourcePermissions = umpAppPermissionMapper.selectByAppKey(sourceAppKey);
        if (CollectionUtils.isEmpty(sourcePermissions)) {
            return 0;
        }

        List<UmpAppPermission> targetPermissions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (UmpAppPermission sourcePermission : sourcePermissions) {
            if (sourcePermission.getDelFlag() != 0 || sourcePermission.getStatus() != 1) {
                continue;
            }

            // 检查目标应用是否已存在该权限
            boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(
                targetAppKey, sourcePermission.getResourceCode());
            if (exists) {
                continue;
            }

            UmpAppPermission targetPermission = new UmpAppPermission();
            BeanUtils.copyProperties(sourcePermission, targetPermission);
            targetPermission.setId(null);
            targetPermission.setAppKey(targetAppKey);
            targetPermission.setCreateTime(now);
            targetPermission.setUpdateTime(null);
            targetPermission.setUpdateBy(null);

            targetPermissions.add(targetPermission);
        }

        if (CollectionUtils.isEmpty(targetPermissions)) {
            return 0;
        }

        int insertedCount = umpAppPermissionMapper.batchInsert(targetPermissions);
        if (insertedCount > 0) {
            log.info("复制权限成功，源应用: {}, 目标应用: {}, 数量: {}", 
                    sourceAppKey, targetAppKey, insertedCount);
        }
        
        return insertedCount;
    }

    @Override
    public Map<String, Object> getResourceTree(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return Collections.emptyMap();
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectByAppKey(appKey);
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptyMap();
        }

        // 按资源代码分组
        Map<String, List<UmpAppPermission>> groupedPermissions = permissions.stream()
                .filter(p -> p.getDelFlag() == 0 && p.getStatus() == 1)
                .collect(Collectors.groupingBy(UmpAppPermission::getResourceCode));

        // 构建资源树
        Map<String, Object> resourceTree = new HashMap<>();
        resourceTree.put("appKey", appKey);
        resourceTree.put("totalResources", groupedPermissions.size());

        List<Map<String, Object>> resources = new ArrayList<>();
        for (Map.Entry<String, List<UmpAppPermission>> entry : groupedPermissions.entrySet()) {
            String resourceCode = entry.getKey();
            List<UmpAppPermission> resourcePermissions = entry.getValue();

            Map<String, Object> resource = new HashMap<>();
            resource.put("resourceCode", resourceCode);
            resource.put("resourceName", 
                    resourcePermissions.get(0).getResourceName());
            
            List<String> operations = resourcePermissions.stream()
                    .map(UmpAppPermission::getOperation)
                    .distinct()
                    .collect(Collectors.toList());
            resource.put("operations", operations);

            resources.add(resource);
        }

        resourceTree.put("resources", resources);
        return resourceTree;
    }

    @Override
    public boolean validateOperation(String appKey, String resourceCode, String operation) {
        return checkPermission(appKey, resourceCode, operation);
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpAppPermission> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getUpdateTime);
                }
                break;
            case "resourceCode":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getResourceCode);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getResourceCode);
                }
                break;
            case "resourceName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getResourceName);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getResourceName);
                }
                break;
            case "operation":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getOperation);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getOperation);
                }
                break;
            case "status":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getStatus);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getStatus);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
                break;
        }
    }

    private boolean updatePermissionStatus(String permissionId, Integer status) {
        if (!StringUtils.hasText(permissionId) || status == null) {
            throw new IllegalArgumentException("权限ID和状态不能为空");
        }

        UmpAppPermission permission = getById(permissionId);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", permissionId);
            return false;
        }

        if (permission.getStatus().equals(status)) {
            log.debug("权限状态未改变，权限ID: {}, 状态: {}", permissionId, status);
            return true;
        }

        permission.setStatus(status);
        permission.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(permission);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("权限{}成功，权限ID: {}, 应用标识: {}, 资源代码: {}", 
                    action, permissionId, permission.getAppKey(), permission.getResourceCode());
        }
        
        return success;
    }

    private boolean validateOperationPermission(String allowedOperation, String requestedOperation) {
        if ("*".equals(allowedOperation)) {
            return true;
        }

        if ("READ".equals(allowedOperation) && "READ".equals(requestedOperation)) {
            return true;
        }

        if ("WRITE".equals(allowedOperation) && "WRITE".equals(requestedOperation)) {
            return true;
        }

        if ("ALL".equals(allowedOperation)) {
            return true;
        }

        return false;
    }

    private PermissionDetailVO convertToDetailVO(UmpAppPermission permission) {
        PermissionDetailVO vo = new PermissionDetailVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private PermissionPageVO convertToPageVO(UmpAppPermission permission) {
        PermissionPageVO vo = new PermissionPageVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}