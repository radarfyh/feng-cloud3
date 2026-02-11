package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import ltd.huntinginfo.feng.center.mapper.UmpSystemLogMapper;
import ltd.huntinginfo.feng.center.service.UmpSystemLogService;
import ltd.huntinginfo.feng.center.api.dto.SystemLogQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统日志表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpSystemLogServiceImpl extends ServiceImpl<UmpSystemLogMapper, UmpSystemLog> implements UmpSystemLogService {

    private final UmpSystemLogMapper umpSystemLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordOperationLog(String logType, String logLevel, String appKey, String operator,
                                    String operation, String requestId, String apiPath, String httpMethod,
                                    Map<String, Object> requestParams, String responseCode,
                                    String responseMessage, Map<String, Object> responseData,
                                    String ipAddress, String userAgent, String serverHost,
                                    Integer costTime, Integer memoryUsage, String errorMessage,
                                    String errorStack) {
        try {
            UmpSystemLog umpSystemLog = new UmpSystemLog();
            umpSystemLog.setLogType(logType != null ? logType : "OPERATION");
            umpSystemLog.setLogLevel(logLevel != null ? logLevel : "INFO");
            umpSystemLog.setAppKey(appKey);
            umpSystemLog.setOperator(operator);
            umpSystemLog.setOperation(operation);
            umpSystemLog.setRequestId(requestId);
            umpSystemLog.setApiPath(apiPath);
            umpSystemLog.setHttpMethod(httpMethod);
            umpSystemLog.setRequestParams(requestParams);
            umpSystemLog.setResponseCode(responseCode);
            umpSystemLog.setResponseMessage(responseMessage);
            umpSystemLog.setResponseData(responseData);
            umpSystemLog.setIpAddress(ipAddress);
            umpSystemLog.setUserAgent(userAgent);
            umpSystemLog.setServerHost(serverHost);
            umpSystemLog.setCostTime(costTime);
            umpSystemLog.setMemoryUsage(memoryUsage);
            umpSystemLog.setErrorMessage(errorMessage);
            umpSystemLog.setErrorStack(errorStack);
            umpSystemLog.setCreateTime(LocalDateTime.now());

            if (save(umpSystemLog)) {
                if ("ERROR".equals(logLevel)) {
                    log.error("操作日志记录成功 - 错误日志，请求ID: {}, 操作: {}, 错误: {}", 
                              requestId, operation, errorMessage);
                } else {
                    log.debug("操作日志记录成功，请求ID: {}, 操作: {}", requestId, operation);
                }
                return umpSystemLog.getId();
            } else {
                log.error("操作日志记录失败，请求ID: {}, 操作: {}", requestId, operation);
                return null;
            }
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordAuthLog(String logLevel, String appKey, String operator, String requestId,
                               String apiPath, String httpMethod, Map<String, Object> requestParams,
                               String authType, Integer authStatus, String authErrorCode,
                               String ipAddress, String userAgent, String serverHost, Integer costTime) {
        try {
            UmpSystemLog umpSystemLog = new UmpSystemLog();
            umpSystemLog.setLogType("AUTH");
            umpSystemLog.setLogLevel(logLevel != null ? logLevel : authStatus == 1 ? "INFO" : "ERROR");
            umpSystemLog.setAppKey(appKey);
            umpSystemLog.setOperator(operator);
            umpSystemLog.setOperation("认证");
            umpSystemLog.setRequestId(requestId);
            umpSystemLog.setApiPath(apiPath);
            umpSystemLog.setHttpMethod(httpMethod);
            umpSystemLog.setRequestParams(requestParams);
            umpSystemLog.setAuthType(authType);
            umpSystemLog.setAuthStatus(authStatus);
            umpSystemLog.setAuthErrorCode(authErrorCode);
            umpSystemLog.setIpAddress(ipAddress);
            umpSystemLog.setUserAgent(userAgent);
            umpSystemLog.setServerHost(serverHost);
            umpSystemLog.setCostTime(costTime);
            umpSystemLog.setCreateTime(LocalDateTime.now());

            if (save(umpSystemLog)) {
                log.debug("认证日志记录成功，请求ID: {}, 应用标识: {}, 认证状态: {}", 
                         requestId, appKey, authStatus);
                return umpSystemLog.getId();
            } else {
                log.error("认证日志记录失败，请求ID: {}, 应用标识: {}", requestId, appKey);
                return null;
            }
        } catch (Exception e) {
            log.error("记录认证日志异常", e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordSystemLog(String logLevel, String operation, String responseMessage,
                                 String errorMessage, String errorStack) {
        try {
            UmpSystemLog umpSystemLog = new UmpSystemLog();
            umpSystemLog.setLogType("SYSTEM");
            umpSystemLog.setLogLevel(logLevel != null ? logLevel : "INFO");
            umpSystemLog.setOperation(operation);
            umpSystemLog.setResponseMessage(responseMessage);
            umpSystemLog.setErrorMessage(errorMessage);
            umpSystemLog.setErrorStack(errorStack);
            umpSystemLog.setCreateTime(LocalDateTime.now());

            if (save(umpSystemLog)) {
                if ("ERROR".equals(logLevel)) {
                    log.error("系统日志记录成功 - 错误日志，操作: {}, 错误: {}", operation, errorMessage);
                } else {
                    log.debug("系统日志记录成功，操作: {}", operation);
                }
                return umpSystemLog.getId();
            } else {
                log.error("系统日志记录失败，操作: {}", operation);
                return null;
            }
        } catch (Exception e) {
            log.error("记录系统日志异常", e);
            return null;
        }
    }

    @Override
    public SystemLogDetailVO getByRequestId(String requestId) {
        if (!StringUtils.hasText(requestId)) {
            throw new IllegalArgumentException("请求ID不能为空");
        }

        UmpSystemLog umpSystemLog = umpSystemLogMapper.selectByRequestId(requestId);
        if (umpSystemLog == null) {
            return null;
        }

        return convertToDetailVO(umpSystemLog);
    }

    @Override
    public Page<SystemLogPageVO> queryLogPage(SystemLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpSystemLog> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getLogType())) {
            queryWrapper.eq(UmpSystemLog::getLogType, queryDTO.getLogType());
        }
        
        if (StringUtils.hasText(queryDTO.getLogLevel())) {
            queryWrapper.eq(UmpSystemLog::getLogLevel, queryDTO.getLogLevel());
        }
        
        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpSystemLog::getAppKey, queryDTO.getAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getOperator())) {
            queryWrapper.eq(UmpSystemLog::getOperator, queryDTO.getOperator());
        }
        
        if (StringUtils.hasText(queryDTO.getOperation())) {
            queryWrapper.like(UmpSystemLog::getOperation, queryDTO.getOperation());
        }
        
        if (StringUtils.hasText(queryDTO.getApiPath())) {
            queryWrapper.like(UmpSystemLog::getApiPath, queryDTO.getApiPath());
        }
        
        if (StringUtils.hasText(queryDTO.getResponseCode())) {
            queryWrapper.eq(UmpSystemLog::getResponseCode, queryDTO.getResponseCode());
        }
        
        if (queryDTO.getAuthStatus() != null) {
            queryWrapper.eq(UmpSystemLog::getAuthStatus, queryDTO.getAuthStatus());
        }

        // 时间范围查询
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpSystemLog::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpSystemLog::getCreateTime, queryDTO.getEndTime());
        }

        // 关键词查询
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                .like(UmpSystemLog::getRequestId, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getOperation, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getApiPath, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getResponseMessage, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getErrorMessage, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getIpAddress, queryDTO.getKeyword())
            );
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
        }

        // 执行分页查询
        Page<UmpSystemLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpSystemLog> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<SystemLogPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<SystemLogPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<SystemLogDetailVO> getByLogType(String logType, Integer limit) {
        if (!StringUtils.hasText(logType)) {
            throw new IllegalArgumentException("日志类型不能为空");
        }

        List<UmpSystemLog> logs = umpSystemLogMapper.selectByLogType(logType, limit);
        
        return logs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByLogLevel(String logLevel, Integer limit) {
        if (!StringUtils.hasText(logLevel)) {
            throw new IllegalArgumentException("日志级别不能为空");
        }

        List<UmpSystemLog> logs = umpSystemLogMapper.selectByLogLevel(logLevel, limit);
        
        return logs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByAppKey(String appKey, Integer limit) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpSystemLog> logs = umpSystemLogMapper.selectByAppKey(appKey, limit);
        
        return logs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByOperator(String operator, Integer limit) {
        if (!StringUtils.hasText(operator)) {
            throw new IllegalArgumentException("操作者不能为空");
        }

        List<UmpSystemLog> logs = umpSystemLogMapper.selectByOperator(operator, limit);
        
        return logs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public SystemLogStatisticsVO getLogStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                                 String logType, String appKey) {
        Map<String, Object> statsMap = umpSystemLogMapper.selectLogStatistics(startTime, endTime, logType, appKey);
        
        SystemLogStatisticsVO statisticsVO = new SystemLogStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setAuthCount(((Number) statsMap.getOrDefault("auth_count", 0)).longValue());
            statisticsVO.setOperationCount(((Number) statsMap.getOrDefault("operation_count", 0)).longValue());
            statisticsVO.setSystemCount(((Number) statsMap.getOrDefault("system_count", 0)).longValue());
            statisticsVO.setInfoCount(((Number) statsMap.getOrDefault("info_count", 0)).longValue());
            statisticsVO.setWarnCount(((Number) statsMap.getOrDefault("warn_count", 0)).longValue());
            statisticsVO.setErrorCount(((Number) statsMap.getOrDefault("error_count", 0)).longValue());
            statisticsVO.setDebugCount(((Number) statsMap.getOrDefault("debug_count", 0)).longValue());
            statisticsVO.setSuccessAuthCount(((Number) statsMap.getOrDefault("success_auth_count", 0)).longValue());
            statisticsVO.setFailedAuthCount(((Number) statsMap.getOrDefault("failed_auth_count", 0)).longValue());
            
            // 计算成功率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setErrorRate((double) statisticsVO.getErrorCount() / statisticsVO.getTotalCount() * 100);
            }
            
            // 计算认证成功率
            long totalAuth = statisticsVO.getSuccessAuthCount() + statisticsVO.getFailedAuthCount();
            if (totalAuth > 0) {
                statisticsVO.setAuthSuccessRate((double) statisticsVO.getSuccessAuthCount() / totalAuth * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<ErrorLogStatisticsVO> getErrorLogStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> errorStats = umpSystemLogMapper.selectErrorLogStatistics(startTime, endTime);
        
        return errorStats.stream()
                .map(this::convertToErrorLogStatisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    public LogPerformanceStatisticsVO getPerformanceStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> perfStats = umpSystemLogMapper.selectPerformanceStatistics(startTime, endTime);
        
        LogPerformanceStatisticsVO statisticsVO = new LogPerformanceStatisticsVO();
        
        if (perfStats != null) {
            statisticsVO.setTotalRequests(((Number) perfStats.getOrDefault("total_requests", 0)).longValue());
            statisticsVO.setAvgCostTime(((Number) perfStats.getOrDefault("avg_cost_time", 0)).doubleValue());
            statisticsVO.setMaxCostTime(((Number) perfStats.getOrDefault("max_cost_time", 0)).intValue());
            statisticsVO.setMinCostTime(((Number) perfStats.getOrDefault("min_cost_time", 0)).intValue());
            statisticsVO.setP95CostTime(((Number) perfStats.getOrDefault("p95_cost_time", 0)).intValue());
            statisticsVO.setP99CostTime(((Number) perfStats.getOrDefault("p99_cost_time", 0)).intValue());
            statisticsVO.setAvgMemoryUsage(((Number) perfStats.getOrDefault("avg_memory_usage", 0)).doubleValue());
            statisticsVO.setMaxMemoryUsage(((Number) perfStats.getOrDefault("max_memory_usage", 0)).intValue());
            
            // 计算慢请求比例（假设超过1000ms为慢请求）
            long slowRequests = ((Number) perfStats.getOrDefault("slow_requests", 0)).longValue();
            if (statisticsVO.getTotalRequests() > 0) {
                statisticsVO.setSlowRequestRate((double) slowRequests / statisticsVO.getTotalRequests() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<ApiCallStatisticsVO> getApiCallStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> apiStats = umpSystemLogMapper.selectApiCallStatistics(startTime, endTime, limit);
        
        return apiStats.stream()
                .map(this::convertToApiCallStatisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperatorStatisticsVO> getOperatorStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> operatorStats = umpSystemLogMapper.selectOperatorStatistics(startTime, endTime, limit);
        
        return operatorStats.stream()
                .map(this::convertToOperatorStatisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppStatisticsVO> getAppStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> appStats = umpSystemLogMapper.selectAppStatistics(startTime, endTime, limit);
        
        return appStats.stream()
                .map(this::convertToAppStatisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredLogs(Integer days) {
        if (days == null || days <= 0) {
            throw new IllegalArgumentException("保留天数必须大于0");
        }

        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        int cleanedCount = umpSystemLogMapper.cleanExpiredLogs(beforeTime);
        
        if (cleanedCount > 0) {
            log.info("清理过期日志成功，清理{}天前的日志，共清理{}条", days, cleanedCount);
        }
        
        return cleanedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteLogs(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int deletedCount = umpSystemLogMapper.batchDeleteLogs(ids);
        if (deletedCount > 0) {
            log.info("批量删除日志成功，共删除{}条", deletedCount);
        }
        
        return deletedCount;
    }

    @Override
    public List<LogTrendVO> getLogTrendStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                                 String interval, String logType, String logLevel) {
        if (startTime == null || endTime == null || !StringUtils.hasText(interval)) {
            throw new IllegalArgumentException("开始时间、结束时间和时间间隔不能为空");
        }

        List<Map<String, Object>> trendStats = umpSystemLogMapper.selectLogTrendStatistics(
                startTime, endTime, interval, logType, logLevel);
        
        return trendStats.stream()
                .map(this::convertToLogTrendVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getErrorLogs(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<UmpSystemLog> errorLogs = umpSystemLogMapper.selectErrorLogs(startTime, endTime, limit);
        
        return errorLogs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> searchLogs(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        // 构建查询条件
        LambdaQueryWrapper<UmpSystemLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like(UmpSystemLog::getRequestId, keyword)
            .or().like(UmpSystemLog::getOperation, keyword)
            .or().like(UmpSystemLog::getApiPath, keyword)
            .or().like(UmpSystemLog::getResponseMessage, keyword)
            .or().like(UmpSystemLog::getErrorMessage, keyword)
            .or().like(UmpSystemLog::getIpAddress, keyword)
            .or().like(UmpSystemLog::getUserAgent, keyword)
        );
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
        
        List<UmpSystemLog> logs = list(queryWrapper);
        
        return logs.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> exportLogs(SystemLogQueryDTO queryDTO) {
        // 使用分页查询获取所有数据
        int batchSize = 1000;
        int current = 1;
        List<SystemLogDetailVO> allLogs = new ArrayList<>();
        
        while (true) {
            queryDTO.setCurrent((long) current);
            queryDTO.setSize((long) batchSize);
            
            Page<SystemLogPageVO> page = queryLogPage(queryDTO);
            if (page.getRecords().isEmpty()) {
                break;
            }
            
            // 转换为详情VO
            List<SystemLogDetailVO> batchLogs = page.getRecords().stream()
                    .map(pageVO -> {
                        SystemLogDetailVO detailVO = new SystemLogDetailVO();
                        BeanUtils.copyProperties(pageVO, detailVO);
                        return detailVO;
                    })
                    .collect(Collectors.toList());
            
            allLogs.addAll(batchLogs);
            
            if (page.getRecords().size() < batchSize) {
                break;
            }
            
            current++;
        }
        
        log.info("导出日志成功，共导出{}条日志", allLogs.size());
        return allLogs;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpSystemLog> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemLog::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
                }
                break;
            case "costTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemLog::getCostTime);
                } else {
                    queryWrapper.orderByDesc(UmpSystemLog::getCostTime);
                }
                break;
            case "logLevel":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemLog::getLogLevel);
                } else {
                    queryWrapper.orderByDesc(UmpSystemLog::getLogLevel);
                }
                break;
            case "operation":
                if (asc) {
                    queryWrapper.orderByAsc(UmpSystemLog::getOperation);
                } else {
                    queryWrapper.orderByDesc(UmpSystemLog::getOperation);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
                break;
        }
    }

    private SystemLogDetailVO convertToDetailVO(UmpSystemLog log) {
        SystemLogDetailVO vo = new SystemLogDetailVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private SystemLogPageVO convertToPageVO(UmpSystemLog log) {
        SystemLogPageVO vo = new SystemLogPageVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private ErrorLogStatisticsVO convertToErrorLogStatisticsVO(Map<String, Object> statMap) {
        ErrorLogStatisticsVO vo = new ErrorLogStatisticsVO();
        vo.setErrorType((String) statMap.get("error_type"));
        vo.setErrorMessage((String) statMap.get("error_message"));
        vo.setErrorCount(((Number) statMap.getOrDefault("error_count", 0)).longValue());
        vo.setFirstOccurrence((String) statMap.get("first_occurrence"));
        vo.setLastOccurrence((String) statMap.get("last_occurrence"));
        vo.setAffectedApi((String) statMap.get("affected_api"));
        vo.setAffectedApp((String) statMap.get("affected_app"));
        return vo;
    }

    private ApiCallStatisticsVO convertToApiCallStatisticsVO(Map<String, Object> statMap) {
        ApiCallStatisticsVO vo = new ApiCallStatisticsVO();
        vo.setApiPath((String) statMap.get("api_path"));
        vo.setHttpMethod((String) statMap.get("http_method"));
        vo.setCallCount(((Number) statMap.getOrDefault("call_count", 0)).longValue());
        vo.setSuccessCount(((Number) statMap.getOrDefault("success_count", 0)).longValue());
        vo.setErrorCount(((Number) statMap.getOrDefault("error_count", 0)).longValue());
        vo.setAvgCostTime(((Number) statMap.getOrDefault("avg_cost_time", 0)).doubleValue());
        vo.setMaxCostTime(((Number) statMap.getOrDefault("max_cost_time", 0)).intValue());
        
        if (vo.getCallCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getCallCount() * 100);
            vo.setErrorRate((double) vo.getErrorCount() / vo.getCallCount() * 100);
        }
        
        return vo;
    }

    private OperatorStatisticsVO convertToOperatorStatisticsVO(Map<String, Object> statMap) {
        OperatorStatisticsVO vo = new OperatorStatisticsVO();
        vo.setOperator((String) statMap.get("operator"));
        vo.setOperationCount(((Number) statMap.getOrDefault("operation_count", 0)).longValue());
        vo.setSuccessCount(((Number) statMap.getOrDefault("success_count", 0)).longValue());
        vo.setErrorCount(((Number) statMap.getOrDefault("error_count", 0)).longValue());
        vo.setLastOperationTime((String) statMap.get("last_operation_time"));
        vo.setFrequentOperation((String) statMap.get("frequent_operation"));
        
        if (vo.getOperationCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getOperationCount() * 100);
        }
        
        return vo;
    }

    private AppStatisticsVO convertToAppStatisticsVO(Map<String, Object> statMap) {
        AppStatisticsVO vo = new AppStatisticsVO();
        vo.setAppKey((String) statMap.get("app_key"));
        vo.setRequestCount(((Number) statMap.getOrDefault("request_count", 0)).longValue());
        vo.setSuccessCount(((Number) statMap.getOrDefault("success_count", 0)).longValue());
        vo.setErrorCount(((Number) statMap.getOrDefault("error_count", 0)).longValue());
        vo.setAvgCostTime(((Number) statMap.getOrDefault("avg_cost_time", 0)).doubleValue());
        vo.setLastRequestTime((String) statMap.get("last_request_time"));
        vo.setFrequentApi((String) statMap.get("frequent_api"));
        
        if (vo.getRequestCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getRequestCount() * 100);
        }
        
        return vo;
    }

    private LogTrendVO convertToLogTrendVO(Map<String, Object> statMap) {
        LogTrendVO vo = new LogTrendVO();
        vo.setTimePeriod((String) statMap.get("time_period"));
        vo.setTotalCount(((Number) statMap.getOrDefault("total_count", 0)).longValue());
        vo.setAuthCount(((Number) statMap.getOrDefault("auth_count", 0)).longValue());
        vo.setOperationCount(((Number) statMap.getOrDefault("operation_count", 0)).longValue());
        vo.setSystemCount(((Number) statMap.getOrDefault("system_count", 0)).longValue());
        vo.setInfoCount(((Number) statMap.getOrDefault("info_count", 0)).longValue());
        vo.setWarnCount(((Number) statMap.getOrDefault("warn_count", 0)).longValue());
        vo.setErrorCount(((Number) statMap.getOrDefault("error_count", 0)).longValue());
        
        if (vo.getTotalCount() > 0) {
            vo.setErrorRate((double) vo.getErrorCount() / vo.getTotalCount() * 100);
        }
        
        return vo;
    }
}