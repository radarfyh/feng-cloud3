package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import ltd.huntinginfo.feng.center.mapper.UmpMsgCallbackMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgCallbackService;
import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;
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
 * 回调记录表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgCallbackServiceImpl extends ServiceImpl<UmpMsgCallbackMapper, UmpMsgCallback> implements UmpMsgCallbackService {

    private static final Integer MAX_RETRY_COUNT = 5;
	private final UmpMsgCallbackMapper umpMsgCallbackMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createCallback(String msgId, String receiverId, String callbackUrl,
                                String callbackMethod, Map<String, Object> callbackData,
                                String signature, String callbackId) {
        if (!StringUtils.hasText(msgId) || !StringUtils.hasText(receiverId) || 
            !StringUtils.hasText(callbackUrl) || callbackData == null) {
            throw new IllegalArgumentException("消息ID、接收者ID、回调地址和回调数据不能为空");
        }

        // 创建回调记录
        UmpMsgCallback callback = new UmpMsgCallback();
        callback.setMsgId(msgId);
        callback.setReceiverId(receiverId);
        callback.setCallbackUrl(callbackUrl);
        callback.setCallbackMethod(StringUtils.hasText(callbackMethod) ? callbackMethod : "POST");
        callback.setCallbackData(callbackData);
        callback.setSignature(signature);
        callback.setCallbackId(callbackId);
        callback.setStatus("PENDING");
        callback.setRetryCount(0);
        callback.setCreateTime(LocalDateTime.now());

        if (save(callback)) {
            log.info("回调记录创建成功，消息ID: {}, 接收者ID: {}, 回调地址: {}", 
                    msgId, receiverId, callbackUrl);
            
            // 异步触发回调创建事件
            triggerCallbackCreateEvent(callback);
            
            return callback.getId();
        } else {
            log.error("回调记录创建失败，消息ID: {}, 接收者ID: {}", msgId, receiverId);
            throw new RuntimeException("回调记录创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateCallbacks(List<Map<String, Object>> callbacks) {
        if (CollectionUtils.isEmpty(callbacks)) {
            return 0;
        }

        List<UmpMsgCallback> callbackList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Map<String, Object> callbackData : callbacks) {
            String msgId = (String) callbackData.get("msgId");
            String receiverId = (String) callbackData.get("receiverId");
            String callbackUrl = (String) callbackData.get("callbackUrl");
            Map<String, Object> data = (Map<String, Object>) callbackData.get("callbackData");
            
            if (!StringUtils.hasText(msgId) || !StringUtils.hasText(receiverId) || 
                !StringUtils.hasText(callbackUrl) || data == null) {
                continue;
            }

            String callbackMethod = (String) callbackData.get("callbackMethod");
            String signature = (String) callbackData.get("signature");
            String callbackId = (String) callbackData.get("callbackId");

            UmpMsgCallback callback = new UmpMsgCallback();
            callback.setMsgId(msgId);
            callback.setReceiverId(receiverId);
            callback.setCallbackUrl(callbackUrl);
            callback.setCallbackMethod(StringUtils.hasText(callbackMethod) ? callbackMethod : "POST");
            callback.setCallbackData(data);
            callback.setSignature(signature);
            callback.setCallbackId(callbackId);
            callback.setStatus("PENDING");
            callback.setRetryCount(0);
            callback.setCreateTime(now);
            
            callbackList.add(callback);
        }

        if (!CollectionUtils.isEmpty(callbackList)) {
            boolean success = saveBatch(callbackList);
            if (success) {
                log.info("批量创建回调记录成功，数量: {}", callbackList.size());
                return callbackList.size();
            }
        }
        
        return 0;
    }

    @Override
    public List<CallbackDetailVO> getCallbacksByMsgAndReceiver(String msgId, String receiverId) {
        if (!StringUtils.hasText(msgId) || !StringUtils.hasText(receiverId)) {
            throw new IllegalArgumentException("消息ID和接收者ID不能为空");
        }

        List<UmpMsgCallback> callbacks = umpMsgCallbackMapper.selectByMsgAndReceiver(msgId, receiverId);
        return callbacks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CallbackPageVO> queryCallbackPage(CallbackQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgCallback> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgCallback::getMsgId, queryDTO.getMsgId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpMsgCallback::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgCallback::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getCallbackUrl())) {
            queryWrapper.like(UmpMsgCallback::getCallbackUrl, queryDTO.getCallbackUrl());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgCallback::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgCallback::getCreateTime, queryDTO.getEndTime());
        }
        
        if (queryDTO.getMinRetryCount() != null) {
            queryWrapper.ge(UmpMsgCallback::getRetryCount, queryDTO.getMinRetryCount());
        }
        
        if (queryDTO.getMaxRetryCount() != null) {
            queryWrapper.le(UmpMsgCallback::getRetryCount, queryDTO.getMaxRetryCount());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgCallback> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgCallback> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<CallbackPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<CallbackPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public CallbackDetailVO getCallbackDetail(String callbackId) {
        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return null;
        }
        return convertToDetailVO(callback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCallbackStatus(String callbackId, String status, Integer httpStatus,
                                      String responseBody, String errorMessage) {
        if (!StringUtils.hasText(callbackId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("回调记录ID和状态不能为空");
        }

        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return false;
        }

        LocalDateTime sendTime = null;
        LocalDateTime responseTime = null;
        Integer costTime = null;
        
        if ("PROCESSING".equals(status)) {
            sendTime = LocalDateTime.now();
        } else if ("SUCCESS".equals(status) || "FAILED".equals(status)) {
            responseTime = LocalDateTime.now();
            if (callback.getSendTime() != null) {
                costTime = (int) java.time.Duration.between(callback.getSendTime(), responseTime).toMillis();
            }
        }

        int updated = umpMsgCallbackMapper.updateCallbackStatus(
                callbackId, status, httpStatus, responseBody, errorMessage,
                sendTime, responseTime, costTime);
        
        boolean success = updated > 0;
        if (success) {
            log.info("回调状态更新成功，回调ID: {}, 状态: {}, HTTP状态码: {}", 
                    callbackId, status, httpStatus);
            // 异步触发状态更新事件
            triggerCallbackStatusUpdateEvent(callbackId, status);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessing(String callbackId) {
        return updateCallbackStatus(callbackId, "PROCESSING", null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSuccess(String callbackId, Integer httpStatus, String responseBody) {
        return updateCallbackStatus(callbackId, "SUCCESS", httpStatus, responseBody, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsFailed(String callbackId, Integer httpStatus, String errorMessage) {
        return updateCallbackStatus(callbackId, "FAILED", httpStatus, null, errorMessage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryFailedCallback(String callbackId, int retryDelayMinutes) {
        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return false;
        }

        if (!"FAILED".equals(callback.getStatus())) {
            log.warn("回调记录不是失败状态，无法重试，ID: {}, 状态: {}", callbackId, callback.getStatus());
            return false;
        }

        // 更新重试信息
        int newRetryCount = callback.getRetryCount() + 1;
        LocalDateTime nextRetryTime = LocalDateTime.now().plusMinutes(retryDelayMinutes);
        
        int updated = umpMsgCallbackMapper.updateRetryInfo(
                callbackId, newRetryCount, nextRetryTime, "PENDING");
        
        boolean success = updated > 0;
        if (success) {
            log.info("回调重试成功，回调ID: {}, 重试次数: {}, 下次重试时间: {}", 
                    callbackId, newRetryCount, nextRetryTime);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRetryFailedCallbacks(List<String> callbackIds, int retryDelayMinutes) {
        if (CollectionUtils.isEmpty(callbackIds)) {
            return 0;
        }

        int retriedCount = 0;
        for (String callbackId : callbackIds) {
            try {
                boolean success = retryFailedCallback(callbackId, retryDelayMinutes);
                if (success) {
                    retriedCount++;
                }
            } catch (Exception e) {
                log.error("重试回调失败，回调ID: {}", callbackId, e);
            }
        }
        
        if (retriedCount > 0) {
            log.info("批量重试失败回调成功，数量: {}", retriedCount);
        }
        
        return retriedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processPendingCallbacks(int limit) {
        List<UmpMsgCallback> pendingCallbacks = umpMsgCallbackMapper.selectPendingSend(limit);
        if (CollectionUtils.isEmpty(pendingCallbacks)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgCallback callback : pendingCallbacks) {
            try {
                // 标记为处理中
                markAsProcessing(callback.getId());
                
                // 执行回调请求
                boolean callbackSuccess = executeCallbackRequest(callback);
                
                if (callbackSuccess) {
                    // 假设HTTP 200为成功
                    markAsSuccess(callback.getId(), 200, "回调成功");
                } else {
                    markAsFailed(callback.getId(), null, "回调执行失败");
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("处理回调失败，回调ID: {}", callback.getId(), e);
                markAsFailed(callback.getId(), null, e.getMessage());
            }
        }
        
        return processedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processRetryCallbacks(Integer maxRetryCount, int limit) {
        List<UmpMsgCallback> retryCallbacks = umpMsgCallbackMapper.selectPendingRetry(maxRetryCount, limit);
        if (CollectionUtils.isEmpty(retryCallbacks)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgCallback callback : retryCallbacks) {
            try {
                // 标记为处理中
                markAsProcessing(callback.getId());
                
                // 执行回调请求
                boolean callbackSuccess = executeCallbackRequest(callback);
                
                if (callbackSuccess) {
                    markAsSuccess(callback.getId(), 200, "回调成功");
                } else {
                    // 如果重试次数超过限制，标记为最终失败
                    if (callback.getRetryCount() >= MAX_RETRY_COUNT) {
                        markAsFailed(callback.getId(), null, "超过最大重试次数");
                    } else {
                        // 安排下次重试
                        int retryDelayMinutes = calculateRetryDelay(callback.getRetryCount());
                        retryFailedCallback(callback.getId(), retryDelayMinutes);
                    }
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("处理重试回调失败，回调ID: {}", callback.getId(), e);
                markAsFailed(callback.getId(), null, e.getMessage());
            }
        }
        
        return processedCount;
    }

    @Override
    public CallbackStatisticsVO getCallbackStatistics(LocalDateTime startTime,
                                                    LocalDateTime endTime,
                                                    String msgId) {
        Map<String, Object> statsMap = umpMsgCallbackMapper.selectCallbackStatistics(
                startTime, endTime, msgId);
        
        CallbackStatisticsVO statisticsVO = new CallbackStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setMsgId(msgId);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setPendingCount(((Number) statsMap.getOrDefault("pending_count", 0)).longValue());
            statisticsVO.setProcessingCount(((Number) statsMap.getOrDefault("processing_count", 0)).longValue());
            statisticsVO.setSuccessCount(((Number) statsMap.getOrDefault("success_count", 0)).longValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).longValue());
            statisticsVO.setAvgCostTime(((Number) statsMap.getOrDefault("avg_cost_time", 0)).doubleValue());
            statisticsVO.setAvgRetryCount(((Number) statsMap.getOrDefault("avg_retry_count", 0)).doubleValue());
            
            // 计算成功率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setSuccessRate((double) statisticsVO.getSuccessCount() / statisticsVO.getTotalCount() * 100);
            }
            
            // 计算平均响应时间
            if (statisticsVO.getAvgCostTime() > 0) {
                statisticsVO.setAvgResponseTime(statisticsVO.getAvgCostTime());
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCallback(String callbackId) {
        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return false;
        }

        boolean success = removeById(callbackId);
        if (success) {
            log.info("回调记录删除成功，ID: {}", callbackId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        LambdaQueryWrapper<UmpMsgCallback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgCallback::getMsgId, msgId);
        
        long count = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除回调记录成功，消息ID: {}, 数量: {}", msgId, count);
            return count;
        }
        
        return 0L;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgCallback> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
                }
                break;
            case "sendTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getSendTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getSendTime);
                }
                break;
            case "responseTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getResponseTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getResponseTime);
                }
                break;
            case "retryCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getRetryCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getRetryCount);
                }
                break;
            case "nextRetryTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getNextRetryTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getNextRetryTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
                break;
        }
    }

    private boolean executeCallbackRequest(UmpMsgCallback callback) {
        // TODO: 实现具体的回调请求逻辑
        // 使用HTTP客户端发送回调请求
        log.debug("执行回调请求，回调ID: {}, 回调地址: {}, 消息ID: {}", 
                callback.getId(), callback.getCallbackUrl(), callback.getMsgId());
        
        try {
            // 模拟回调请求
            // 实际应该使用HTTP客户端如RestTemplate、OkHttp等发送请求
            // 并根据响应判断是否成功
            return true;
        } catch (Exception e) {
            log.error("执行回调请求异常，回调ID: {}", callback.getId(), e);
            return false;
        }
    }

    private int calculateRetryDelay(int retryCount) {
        // 指数退避算法计算重试延迟
        // 第一次重试延迟1分钟，第二次2分钟，第三次4分钟，以此类推
        return (int) Math.pow(2, retryCount);
    }

    private CallbackDetailVO convertToDetailVO(UmpMsgCallback callback) {
        CallbackDetailVO vo = new CallbackDetailVO();
        BeanUtils.copyProperties(callback, vo);
        return vo;
    }

    private CallbackPageVO convertToPageVO(UmpMsgCallback callback) {
        CallbackPageVO vo = new CallbackPageVO();
        BeanUtils.copyProperties(callback, vo);
        return vo;
    }

    private void triggerCallbackCreateEvent(UmpMsgCallback callback) {
        // 异步触发回调创建事件
        log.debug("触发回调创建事件，回调ID: {}, 消息ID: {}", callback.getId(), callback.getMsgId());
        // TODO: 集成消息队列发布回调创建事件
    }

    private void triggerCallbackStatusUpdateEvent(String callbackId, String status) {
        // 异步触发回调状态更新事件
        log.debug("触发回调状态更新事件，回调ID: {}, 状态: {}", callbackId, status);
        // TODO: 集成消息队列发布状态更新事件
    }
}