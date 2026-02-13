package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.mapper.UmpMsgQueueMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;
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
 * 消息队列表服务实现类
 * 主要业务逻辑参见数据库表脚本和UmpMsgQueueService的注释
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgQueueServiceImpl extends ServiceImpl<UmpMsgQueueMapper, UmpMsgQueue> implements UmpMsgQueueService {

    private final UmpMsgQueueMapper umpMsgQueueMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createQueueTask(String queueType, String queueName, String msgId,
                                 Map<String, Object> taskData, Integer priority,
                                 LocalDateTime executeTime, Integer maxRetry) {
        if (!StringUtils.hasText(queueType) || !StringUtils.hasText(queueName) || 
            !StringUtils.hasText(msgId) || taskData == null) {
            throw new IllegalArgumentException("队列类型、队列名称、消息ID和任务数据不能为空");
        }

        // 创建任务
        UmpMsgQueue queueTask = new UmpMsgQueue();
        queueTask.setQueueType(queueType);
        queueTask.setQueueName(queueName);
        queueTask.setMsgId(msgId);
        queueTask.setTaskData(taskData);
        queueTask.setPriority(priority != null ? priority : 5);
        queueTask.setExecuteTime(executeTime != null ? executeTime : LocalDateTime.now());
        queueTask.setMaxRetry(maxRetry != null ? maxRetry : 3);
        queueTask.setCurrentRetry(0);
        queueTask.setStatus("PENDING");
        queueTask.setCreateTime(LocalDateTime.now());

        if (save(queueTask)) {
            log.info("队列任务创建成功，任务ID: {}, 队列类型: {}, 消息ID: {}", 
                    queueTask.getId(), queueType, msgId);
            
            // 异步触发任务创建事件
            triggerQueueTaskCreateEvent(queueTask);
            
            return queueTask.getId();
        } else {
            log.error("队列任务创建失败，队列类型: {}, 消息ID: {}", queueType, msgId);
            throw new RuntimeException("队列任务创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateQueueTasks(List<Map<String, Object>> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return 0;
        }

        List<UmpMsgQueue> queueTasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Map<String, Object> task : tasks) {
            String queueType = (String) task.get("queueType");
            String queueName = (String) task.get("queueName");
            String msgId = (String) task.get("msgId");
            Map<String, Object> taskData = (Map<String, Object>) task.get("taskData");
            
            if (!StringUtils.hasText(queueType) || !StringUtils.hasText(queueName) || 
                !StringUtils.hasText(msgId) || taskData == null) {
                continue;
            }

            Integer priority = (Integer) task.get("priority");
            LocalDateTime executeTime = (LocalDateTime) task.get("executeTime");
            Integer maxRetry = (Integer) task.get("maxRetry");

            UmpMsgQueue queueTask = new UmpMsgQueue();
            queueTask.setQueueType(queueType);
            queueTask.setQueueName(queueName);
            queueTask.setMsgId(msgId);
            queueTask.setTaskData(taskData);
            queueTask.setPriority(priority != null ? priority : 5);
            queueTask.setExecuteTime(executeTime != null ? executeTime : now);
            queueTask.setMaxRetry(maxRetry != null ? maxRetry : 3);
            queueTask.setCurrentRetry(0);
            queueTask.setStatus("PENDING");
            queueTask.setCreateTime(now);
            
            queueTasks.add(queueTask);
        }

        if (!CollectionUtils.isEmpty(queueTasks)) {
            boolean success = saveBatch(queueTasks);
            if (success) {
                log.info("批量创建队列任务成功，数量: {}", queueTasks.size());
                return queueTasks.size();
            }
        }
        
        return 0;
    }

    @Override
    public List<MsgQueueDetailVO> getQueueTasksByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        List<UmpMsgQueue> tasks = umpMsgQueueMapper.selectByMsgId(msgId);
        return tasks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MsgQueuePageVO> queryQueuePage(MsgQueueQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgQueue> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getQueueType())) {
            queryWrapper.eq(UmpMsgQueue::getQueueType, queryDTO.getQueueType());
        }
        
        if (StringUtils.hasText(queryDTO.getQueueName())) {
            queryWrapper.eq(UmpMsgQueue::getQueueName, queryDTO.getQueueName());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgQueue::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgQueue::getMsgId, queryDTO.getMsgId());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgQueue::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgQueue::getCreateTime, queryDTO.getEndTime());
        }
        
        if (queryDTO.getPriorityMin() != null) {
            queryWrapper.ge(UmpMsgQueue::getPriority, queryDTO.getPriorityMin());
        }
        
        if (queryDTO.getPriorityMax() != null) {
            queryWrapper.le(UmpMsgQueue::getPriority, queryDTO.getPriorityMax());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByAsc(UmpMsgQueue::getPriority)
                       .orderByAsc(UmpMsgQueue::getExecuteTime);
        }

        // 执行分页查询
        Page<UmpMsgQueue> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgQueue> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<MsgQueuePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<MsgQueuePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<MsgQueueDetailVO> getPendingTasks(String queueType, String queueName, int limit) {
        List<UmpMsgQueue> tasks = umpMsgQueueMapper.selectPendingTasks(queueType, queueName, limit);
        return tasks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public MsgQueueDetailVO getQueueTaskDetail(String taskId) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return null;
        }
        return convertToDetailVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStatus(String taskId, String status, String workerId,
                                   String resultCode, String resultMessage, String errorStack) {
        if (!StringUtils.hasText(taskId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("任务ID和状态不能为空");
        }

        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if ("PROCESSING".equals(status)) {
            startTime = LocalDateTime.now();
        } else if ("SUCCESS".equals(status) || "FAILED".equals(status)) {
            endTime = LocalDateTime.now();
        }

        int updated = umpMsgQueueMapper.updateTaskStatus(
                taskId, status, workerId, startTime, endTime, 
                resultCode, resultMessage, errorStack);
        
        boolean success = updated > 0;
        if (success) {
            log.info("队列任务状态更新成功，任务ID: {}, 状态: {}", taskId, status);
            // 异步触发状态更新事件
            triggerTaskStatusUpdateEvent(taskId, status);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessing(String taskId, String workerId) {
        return updateTaskStatus(taskId, "PROCESSING", workerId, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSuccess(String taskId, String workerId, String resultMessage) {
        return updateTaskStatus(taskId, "SUCCESS", workerId, "SUCCESS", resultMessage, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsFailed(String taskId, String workerId, String errorMessage, String errorStack) {
        return updateTaskStatus(taskId, "FAILED", workerId, "FAILED", errorMessage, errorStack);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryFailedTask(String taskId, int retryDelayMinutes) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        if (!"FAILED".equals(task.getStatus())) {
            log.warn("任务不是失败状态，无法重试，任务ID: {}, 状态: {}", taskId, task.getStatus());
            return false;
        }

        if (task.getCurrentRetry() >= task.getMaxRetry()) {
            log.warn("任务已达到最大重试次数，任务ID: {}, 当前重试: {}, 最大重试: {}", 
                    taskId, task.getCurrentRetry(), task.getMaxRetry());
            return false;
        }

        int newRetryCount = task.getCurrentRetry() + 1;
        LocalDateTime newExecuteTime = LocalDateTime.now().plusMinutes(retryDelayMinutes);
        
        int updated = umpMsgQueueMapper.updateRetryCount(taskId, newRetryCount, newExecuteTime);
        
        if (updated > 0) {
            // 更新状态为PENDING，以便重新执行
            updateTaskStatus(taskId, "PENDING", null, null, null, null);
            log.info("任务重试成功，任务ID: {}, 重试次数: {}, 下次执行时间: {}", 
                    taskId, newRetryCount, newExecuteTime);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRetryFailedTasks(List<String> taskIds, int retryDelayMinutes) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return 0;
        }

        int retriedCount = 0;
        for (String taskId : taskIds) {
            try {
                boolean success = retryFailedTask(taskId, retryDelayMinutes);
                if (success) {
                    retriedCount++;
                }
            } catch (Exception e) {
                log.error("重试任务失败，任务ID: {}", taskId, e);
            }
        }
        
        if (retriedCount > 0) {
            log.info("批量重试失败任务成功，数量: {}", retriedCount);
        }
        
        return retriedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processPendingTasks(String queueType, String queueName, String workerId, int limit) {
        List<UmpMsgQueue> pendingTasks = umpMsgQueueMapper.selectPendingTasks(queueType, queueName, limit);
        if (CollectionUtils.isEmpty(pendingTasks)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgQueue task : pendingTasks) {
            try {
                // 标记为处理中
                markAsProcessing(task.getId(), workerId);
                
                // TODO: 实际执行任务逻辑
                // 根据任务类型执行不同的业务逻辑
                boolean executeSuccess = executeQueueTask(task);
                
                if (executeSuccess) {
                    markAsSuccess(task.getId(), workerId, "任务执行成功");
                } else {
                    markAsFailed(task.getId(), workerId, "任务执行失败", null);
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("处理队列任务失败，任务ID: {}", task.getId(), e);
                markAsFailed(task.getId(), workerId, e.getMessage(), e.toString());
            }
        }
        
        return processedCount;
    }

    @Override
    public MsgQueueStatisticsVO getQueueStatistics(LocalDateTime startTime,
                                                  LocalDateTime endTime,
                                                  String queueType) {
        Map<String, Object> statsMap = umpMsgQueueMapper.selectQueueStatistics(
                startTime, endTime, queueType);
        
        MsgQueueStatisticsVO statisticsVO = new MsgQueueStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setQueueType(queueType);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setPendingCount(((Number) statsMap.getOrDefault("pending_count", 0)).longValue());
            statisticsVO.setProcessingCount(((Number) statsMap.getOrDefault("processing_count", 0)).longValue());
            statisticsVO.setSuccessCount(((Number) statsMap.getOrDefault("success_count", 0)).longValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).longValue());
            statisticsVO.setAvgPriority(((Number) statsMap.getOrDefault("avg_priority", 0)).doubleValue());
            statisticsVO.setAvgRetryCount(((Number) statsMap.getOrDefault("avg_retry_count", 0)).doubleValue());
            
            // 计算成功率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setSuccessRate((double) statisticsVO.getSuccessCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processTimeoutTasks(int timeoutMinutes, int limit) {
        List<UmpMsgQueue> timeoutTasks = umpMsgQueueMapper.selectTimeoutTasks(timeoutMinutes, limit);
        if (CollectionUtils.isEmpty(timeoutTasks)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgQueue task : timeoutTasks) {
            try {
                // 将超时任务标记为失败
                markAsFailed(task.getId(), "SYSTEM", "任务执行超时", 
                        "任务执行超过" + timeoutMinutes + "分钟未完成");
                
                processedCount++;
            } catch (Exception e) {
                log.error("处理超时任务失败，任务ID: {}", task.getId(), e);
            }
        }
        
        log.info("处理超时任务完成，数量: {}", processedCount);
        return processedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQueueTask(String taskId) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        boolean success = removeById(taskId);
        if (success) {
            log.info("队列任务删除成功，任务ID: {}", taskId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        LambdaQueryWrapper<UmpMsgQueue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgQueue::getMsgId, msgId);
        
        long count = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除队列任务成功，消息ID: {}, 数量: {}", msgId, count);
            return count;
        }
        
        return 0L;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgQueue> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getCreateTime);
                }
                break;
            case "executeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getExecuteTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getExecuteTime);
                }
                break;
            case "priority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getPriority);
                }
                break;
            case "startTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getStartTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getStartTime);
                }
                break;
            case "endTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getEndTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getEndTime);
                }
                break;
            default:
                queryWrapper.orderByAsc(UmpMsgQueue::getPriority)
                           .orderByAsc(UmpMsgQueue::getExecuteTime);
                break;
        }
    }

    private boolean executeQueueTask(UmpMsgQueue task) {
        // TODO: 实现具体的任务执行逻辑
        // 根据queueType执行不同的业务逻辑
        log.debug("执行队列任务，任务ID: {}, 队列类型: {}, 消息ID: {}", 
                task.getId(), task.getQueueType(), task.getMsgId());
        
        // 模拟任务执行
        try {
            // 根据不同的队列类型执行不同的逻辑
            switch (task.getQueueType()) {
                case "SEND":
                    // 执行消息发送逻辑
                    return executeSendTask(task);
                case "DISTRIBUTE":
                    // 执行消息分发逻辑
                    return executeDistributeTask(task);
                case "CALLBACK":
                    // 执行回调逻辑
                    return executeCallbackTask(task);
                case "RETRY":
                    // 执行重试逻辑
                    return executeRetryTask(task);
                default:
                    log.warn("未知的队列类型: {}", task.getQueueType());
                    return false;
            }
        } catch (Exception e) {
            log.error("执行队列任务异常，任务ID: {}", task.getId(), e);
            return false;
        }
    }

    private boolean executeSendTask(UmpMsgQueue task) {
        // TODO: 实现消息发送逻辑
        log.debug("执行消息发送任务，任务ID: {}, 消息ID: {}", task.getId(), task.getMsgId());
        // 模拟执行成功
        return true;
    }

    private boolean executeDistributeTask(UmpMsgQueue task) {
        // TODO: 实现消息分发逻辑
        log.debug("执行消息分发任务，任务ID: {}, 消息ID: {}", task.getId(), task.getMsgId());
        // 模拟执行成功
        return true;
    }

    private boolean executeCallbackTask(UmpMsgQueue task) {
        // TODO: 实现回调逻辑
        log.debug("执行回调任务，任务ID: {}, 消息ID: {}", task.getId(), task.getMsgId());
        // 模拟执行成功
        return true;
    }

    private boolean executeRetryTask(UmpMsgQueue task) {
        // TODO: 实现重试逻辑
        log.debug("执行重试任务，任务ID: {}, 消息ID: {}", task.getId(), task.getMsgId());
        // 模拟执行成功
        return true;
    }

    private MsgQueueDetailVO convertToDetailVO(UmpMsgQueue task) {
        MsgQueueDetailVO vo = new MsgQueueDetailVO();
        BeanUtils.copyProperties(task, vo);
        
        // 计算耗时
        if (task.getStartTime() != null && task.getEndTime() != null) {
            long costSeconds = java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds();
            vo.setCostSeconds(costSeconds);
        }
        
        return vo;
    }

    private MsgQueuePageVO convertToPageVO(UmpMsgQueue task) {
        MsgQueuePageVO vo = new MsgQueuePageVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }

    private void triggerQueueTaskCreateEvent(UmpMsgQueue task) {
        // 异步触发队列任务创建事件
        log.debug("触发队列任务创建事件，任务ID: {}, 队列类型: {}", task.getId(), task.getQueueType());
        // TODO: 集成消息队列发布任务创建事件
    }

    private void triggerTaskStatusUpdateEvent(String taskId, String status) {
        // 异步触发任务状态更新事件
        log.debug("触发任务状态更新事件，任务ID: {}, 状态: {}", taskId, status);
        // TODO: 集成消息队列发布状态更新事件
    }
}