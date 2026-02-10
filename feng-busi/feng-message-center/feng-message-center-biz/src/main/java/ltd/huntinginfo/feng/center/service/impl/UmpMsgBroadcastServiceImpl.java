package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;
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
 * 广播信息筒表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgBroadcastServiceImpl extends ServiceImpl<UmpMsgBroadcastMapper, UmpMsgBroadcast> implements UmpMsgBroadcastService {

    private final UmpMsgBroadcastMapper umpMsgBroadcastMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBroadcast(String msgId, String broadcastType,
                                 Map<String, Object> targetScope, String targetDescription) {
        // 检查是否已存在
        UmpMsgBroadcast existing = umpMsgBroadcastMapper.selectByMsgId(msgId);
        if (existing != null) {
            log.warn("广播记录已存在，消息ID: {}", msgId);
            return existing.getId();
        }

        // 创建新记录
        UmpMsgBroadcast broadcast = new UmpMsgBroadcast();
        broadcast.setMsgId(msgId);
        broadcast.setBroadcastType(broadcastType);
        broadcast.setTargetScope(targetScope);
        broadcast.setTargetDescription(targetDescription);
        broadcast.setStatus("DISTRIBUTING");
        broadcast.setCreateTime(LocalDateTime.now());
        broadcast.setStartTime(LocalDateTime.now());
        
        // 设置默认统计值
        broadcast.setTotalReceivers(0);
        broadcast.setDistributedCount(0);
        broadcast.setReceivedCount(0);
        broadcast.setReadCount(0);

        if (save(broadcast)) {
            log.info("广播记录创建成功，消息ID: {}, 广播ID: {}", msgId, broadcast.getId());
            
            // 异步触发广播分发事件
            triggerBroadcastDistributeEvent(broadcast);
            
            return broadcast.getId();
        } else {
            log.error("广播记录创建失败，消息ID: {}", msgId);
            throw new RuntimeException("广播记录创建失败");
        }
    }

    @Override
    public BroadcastDetailVO getBroadcastByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        UmpMsgBroadcast broadcast = umpMsgBroadcastMapper.selectByMsgId(msgId);
        if (broadcast == null) {
            log.warn("广播记录不存在，消息ID: {}", msgId);
            return null;
        }

        return convertToDetailVO(broadcast);
    }

    @Override
    public Page<BroadcastPageVO> queryBroadcastPage(BroadcastQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgBroadcast> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getBroadcastType())) {
            queryWrapper.eq(UmpMsgBroadcast::getBroadcastType, queryDTO.getBroadcastType());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgBroadcast::getStatus, queryDTO.getStatus());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgBroadcast::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgBroadcast::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgBroadcast> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgBroadcast> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<BroadcastPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<BroadcastPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBroadcastStatistics(String broadcastId, Integer distributedCount,
                                           Integer receivedCount, Integer readCount) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }

        int updated = umpMsgBroadcastMapper.updateBroadcastStatistics(
                broadcastId, distributedCount, receivedCount, readCount, 
                calculateStatus(broadcast), LocalDateTime.now());
        
        boolean success = updated > 0;
        if (success) {
            log.info("广播统计信息更新成功，广播ID: {}, 分发: {}, 接收: {}, 已读: {}", 
                    broadcastId, distributedCount, receivedCount, readCount);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBroadcastStatus(String broadcastId, String status) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("广播ID和状态不能为空");
        }

        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }

        broadcast.setStatus(status);
        broadcast.setUpdateTime(LocalDateTime.now());
        
        // 根据状态设置相应的时间字段
        if ("COMPLETED".equals(status) && broadcast.getCompleteTime() == null) {
            broadcast.setCompleteTime(LocalDateTime.now());
        }

        boolean success = updateById(broadcast);
        if (success) {
            log.info("广播状态更新成功，广播ID: {}, 旧状态: {}, 新状态: {}", 
                    broadcastId, broadcast.getStatus(), status);
            
            // 异步触发状态变更事件
            triggerStatusChangeEvent(broadcastId, status);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateBroadcastStatus(List<String> broadcastIds, String status) {
        if (CollectionUtils.isEmpty(broadcastIds) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("广播ID列表和状态不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpMsgBroadcastMapper.batchUpdateStatus(broadcastIds, status, now);
        
        if (updatedCount > 0) {
            log.info("批量更新广播状态成功，数量: {}, 目标状态: {}", updatedCount, status);
            
            // 异步触发批量状态变更事件
            triggerBatchStatusChangeEvent(broadcastIds, status);
        }

        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsDistributing(String broadcastId) {
        return updateBroadcastStatus(broadcastId, "DISTRIBUTING");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsCompleted(String broadcastId) {
        return updateBroadcastStatus(broadcastId, "COMPLETED");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processPendingDistribute(int limit) {
        List<UmpMsgBroadcast> pendingList = umpMsgBroadcastMapper.selectPendingDistribute(limit);
        if (CollectionUtils.isEmpty(pendingList)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgBroadcast broadcast : pendingList) {
            try {
                // 标记为分发中
                markAsDistributing(broadcast.getId());
                
                // TODO: 实际分发逻辑
                // 根据广播类型和目标范围，分发消息给接收者
                boolean distributeSuccess = distributeBroadcastMessage(broadcast);
                
                if (distributeSuccess) {
                    // 更新分发统计
                    updateBroadcastStatistics(broadcast.getId(), 
                            broadcast.getTotalReceivers(), 0, 0);
                    
                    // 如果目标接收者数量已知，可以计算完成进度
                    if (broadcast.getTotalReceivers() != null && broadcast.getTotalReceivers() > 0) {
                        if (broadcast.getTotalReceivers().equals(broadcast.getDistributedCount())) {
                            markAsCompleted(broadcast.getId());
                        }
                    }
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("广播分发处理失败，广播ID: {}", broadcast.getId(), e);
                // 可以记录错误状态
            }
        }
        
        return processedCount;
    }

    @Override
    public BroadcastStatisticsVO getBroadcastStatistics(LocalDateTime startTime,
                                                       LocalDateTime endTime,
                                                       String broadcastType) {
        Map<String, Object> statsMap = umpMsgBroadcastMapper.selectBroadcastStatistics(
                startTime, endTime, broadcastType);
        
        BroadcastStatisticsVO statisticsVO = new BroadcastStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setBroadcastType(broadcastType);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setDistributingCount(((Number) statsMap.getOrDefault("distributing_count", 0)).longValue());
            statisticsVO.setCompletedCount(((Number) statsMap.getOrDefault("completed_count", 0)).longValue());
            statisticsVO.setTotalReceivers(((Number) statsMap.getOrDefault("total_receivers", 0)).longValue());
            statisticsVO.setDistributedReceivers(((Number) statsMap.getOrDefault("distributed_receivers", 0)).longValue());
            statisticsVO.setReceivedReceivers(((Number) statsMap.getOrDefault("received_receivers", 0)).longValue());
            statisticsVO.setReadReceivers(((Number) statsMap.getOrDefault("read_receivers", 0)).longValue());
            
            // 计算比率
            if (statisticsVO.getTotalReceivers() > 0) {
                statisticsVO.setDistributeRate((double) statisticsVO.getDistributedReceivers() / statisticsVO.getTotalReceivers() * 100);
                statisticsVO.setReceiveRate((double) statisticsVO.getReceivedReceivers() / statisticsVO.getTotalReceivers() * 100);
                statisticsVO.setReadRate((double) statisticsVO.getReadReceivers() / statisticsVO.getTotalReceivers() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<BroadcastDetailVO> getBroadcastsByReceiver(String receiverId,
                                                          String receiverType,
                                                          int limit) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        // 查询符合条件的广播记录
        // 注意：这里需要根据接收者信息和广播的目标范围来匹配
        // 简化实现，实际需要根据业务逻辑调整
        LambdaQueryWrapper<UmpMsgBroadcast> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgBroadcast::getStatus, "DISTRIBUTING")
                   .or().eq(UmpMsgBroadcast::getStatus, "COMPLETED")
                   .orderByDesc(UmpMsgBroadcast::getCreateTime)
                   .last("LIMIT " + limit);
        
        List<UmpMsgBroadcast> broadcasts = list(queryWrapper);
        
        // TODO: 需要根据接收者信息和广播的目标范围进行过滤
        
        return broadcasts.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgBroadcast> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
                }
                break;
            case "startTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getStartTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getStartTime);
                }
                break;
            case "completeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getCompleteTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getCompleteTime);
                }
                break;
            case "totalReceivers":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getTotalReceivers);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getTotalReceivers);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
                break;
        }
    }

    private String calculateStatus(UmpMsgBroadcast broadcast) {
        if (broadcast.getTotalReceivers() == null || broadcast.getTotalReceivers() == 0) {
            return "DISTRIBUTING";
        }
        
        if (broadcast.getDistributedCount() == null) {
            broadcast.setDistributedCount(0);
        }
        
        if (broadcast.getDistributedCount() >= broadcast.getTotalReceivers()) {
            return "COMPLETED";
        } else if (broadcast.getDistributedCount() > 0) {
            return "PARTIAL";
        } else {
            return "DISTRIBUTING";
        }
    }

    private boolean distributeBroadcastMessage(UmpMsgBroadcast broadcast) {
        // TODO: 实现广播消息分发逻辑
        // 1. 根据广播类型和目标范围，获取所有接收者
        // 2. 创建接收记录（如果需要）
        // 3. 触发消息推送
        // 4. 更新统计信息
        
        log.debug("分发广播消息，广播ID: {}, 消息ID: {}", broadcast.getId(), broadcast.getMsgId());
        // 模拟分发成功
        return true;
    }

    private void triggerBroadcastDistributeEvent(UmpMsgBroadcast broadcast) {
        // 异步触发广播分发事件
        log.debug("触发广播分发事件，广播ID: {}, 消息ID: {}", broadcast.getId(), broadcast.getMsgId());
        // TODO: 集成消息队列发布广播分发事件
    }

    private void triggerStatusChangeEvent(String broadcastId, String status) {
        // 异步触发状态变更事件
        log.debug("触发广播状态变更事件，广播ID: {}, 新状态: {}", broadcastId, status);
        // TODO: 集成消息队列发布状态变更事件
    }

    private void triggerBatchStatusChangeEvent(List<String> broadcastIds, String status) {
        // 异步触发批量状态变更事件
        log.debug("触发批量广播状态变更事件，数量: {}, 新状态: {}", broadcastIds.size(), status);
        // TODO: 集成消息队列发布批量状态变更事件
    }

    private BroadcastDetailVO convertToDetailVO(UmpMsgBroadcast broadcast) {
        BroadcastDetailVO vo = new BroadcastDetailVO();
        BeanUtils.copyProperties(broadcast, vo);
        
        // 计算分发进度
        if (broadcast.getTotalReceivers() != null && broadcast.getTotalReceivers() > 0) {
            vo.setDistributeProgress(broadcast.getDistributedCount() != null ? 
                    (double) broadcast.getDistributedCount() / broadcast.getTotalReceivers() * 100 : 0);
            vo.setReceiveProgress(broadcast.getReceivedCount() != null ? 
                    (double) broadcast.getReceivedCount() / broadcast.getTotalReceivers() * 100 : 0);
            vo.setReadProgress(broadcast.getReadCount() != null ? 
                    (double) broadcast.getReadCount() / broadcast.getTotalReceivers() * 100 : 0);
        }
        
        return vo;
    }

    private BroadcastPageVO convertToPageVO(UmpMsgBroadcast broadcast) {
        BroadcastPageVO vo = new BroadcastPageVO();
        BeanUtils.copyProperties(broadcast, vo);
        
        // 计算分发进度
        if (broadcast.getTotalReceivers() != null && broadcast.getTotalReceivers() > 0) {
            vo.setDistributeProgress(broadcast.getDistributedCount() != null ? 
                    (double) broadcast.getDistributedCount() / broadcast.getTotalReceivers() * 100 : 0);
        }
        
        return vo;
    }
}