package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxPageVO;
import ltd.huntinginfo.feng.center.api.vo.ReceiverStatisticsVO;
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
 * 收件箱表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgInboxServiceImpl extends ServiceImpl<UmpMsgInboxMapper, UmpMsgInbox> implements UmpMsgInboxService {

    private final UmpMsgInboxMapper umpMsgInboxMapper;

    @Override
    public Page<InboxPageVO> queryInboxPage(InboxQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverType())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverType, queryDTO.getReceiverType());
        }
        
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(UmpMsgInbox::getReadStatus, queryDTO.getReadStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiveStatus())) {
            queryWrapper.eq(UmpMsgInbox::getReceiveStatus, queryDTO.getReceiveStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgInbox::getMsgId, queryDTO.getMsgId());
        }
        
        if (StringUtils.hasText(queryDTO.getDistributeMode())) {
            queryWrapper.eq(UmpMsgInbox::getDistributeMode, queryDTO.getDistributeMode());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgInbox::getDistributeTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgInbox::getDistributeTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
        }

        // 执行分页查询
        Page<UmpMsgInbox> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgInbox> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<InboxPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<InboxPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    private void applySort(LambdaQueryWrapper<UmpMsgInbox> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "distributeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getDistributeTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
                }
                break;
            case "receiveTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getReceiveTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getReceiveTime);
                }
                break;
            case "readTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getReadTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getReadTime);
                }
                break;
            case "lastPushTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getLastPushTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getLastPushTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
                break;
        }
    }
    
    @Override
    public InboxDetailVO getInboxDetail(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return null;
        }
        return convertToDetailVO(inbox);
    }

    @Override
    public InboxDetailVO getByMsgAndReceiver(String msgId, String receiverId, String receiverType) {
        UmpMsgInbox inbox = umpMsgInboxMapper.selectByMsgAndReceiver(msgId, receiverId, receiverType);
        if (inbox == null) {
            return null;
        }
        return convertToDetailVO(inbox);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createInboxRecord(String msgId, String receiverId, String receiverType, 
                                   String receiverName, String distributeMode) {
        // 检查是否已存在
        UmpMsgInbox existing = umpMsgInboxMapper.selectByMsgAndReceiver(msgId, receiverId, receiverType);
        if (existing != null) {
            log.warn("收件箱记录已存在，消息ID: {}, 接收者ID: {}", msgId, receiverId);
            return existing.getId();
        }

        // 创建新记录
        UmpMsgInbox inbox = new UmpMsgInbox();
        inbox.setMsgId(msgId);
        inbox.setReceiverId(receiverId);
        inbox.setReceiverType(receiverType);
        inbox.setReceiverName(receiverName);
        inbox.setDistributeMode(StringUtils.hasText(distributeMode) ? distributeMode : "INBOX");
        inbox.setDistributeTime(LocalDateTime.now());
        inbox.setReceiveStatus("PENDING");
        inbox.setReadStatus(0);
        inbox.setPushStatus("PENDING");
        inbox.setPushCount(0);

        if (save(inbox)) {
            log.info("收件箱记录创建成功，消息ID: {}, 接收者ID: {}", msgId, receiverId);
            return inbox.getId();
        } else {
            log.error("收件箱记录创建失败，消息ID: {}, 接收者ID: {}", msgId, receiverId);
            throw new RuntimeException("收件箱记录创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateInboxRecords(String msgId, List<Map<String, Object>> receivers, String distributeMode) {
        if (CollectionUtils.isEmpty(receivers)) {
            return 0;
        }

        int createdCount = 0;
        List<UmpMsgInbox> inboxList = new ArrayList<>();

        for (Map<String, Object> receiver : receivers) {
            String receiverId = (String) receiver.get("receiverId");
            String receiverType = (String) receiver.get("receiverType");
            String receiverName = (String) receiver.get("receiverName");

            // 检查是否已存在
            UmpMsgInbox existing = umpMsgInboxMapper.selectByMsgAndReceiver(msgId, receiverId, receiverType);
            if (existing != null) {
                continue;
            }

            UmpMsgInbox inbox = new UmpMsgInbox();
            inbox.setMsgId(msgId);
            inbox.setReceiverId(receiverId);
            inbox.setReceiverType(receiverType);
            inbox.setReceiverName(receiverName);
            inbox.setDistributeMode(StringUtils.hasText(distributeMode) ? distributeMode : "INBOX");
            inbox.setDistributeTime(LocalDateTime.now());
            inbox.setReceiveStatus("PENDING");
            inbox.setReadStatus(0);
            inbox.setPushStatus("PENDING");
            inbox.setPushCount(0);

            inboxList.add(inbox);
            createdCount++;
        }

        if (!CollectionUtils.isEmpty(inboxList)) {
            saveBatch(inboxList);
            log.info("批量创建收件箱记录成功，消息ID: {}, 数量: {}", msgId, createdCount);
        }

        return createdCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsReceived(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        if ("RECEIVED".equals(inbox.getReceiveStatus())) {
            log.debug("收件箱记录已是已接收状态，ID: {}", inboxId);
            return true;
        }

        inbox.setReceiveStatus("RECEIVED");
        inbox.setReceiveTime(LocalDateTime.now());
        
        boolean success = updateById(inbox);
        if (success) {
            log.info("收件箱记录标记为已接收，ID: {}", inboxId);
            // 可以触发接收事件
            triggerReceiveEvent(inbox);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        if (inbox.getReadStatus() == 1) {
            log.debug("收件箱记录已是已读状态，ID: {}", inboxId);
            return true;
        }

        inbox.setReadStatus(1);
        inbox.setReadTime(LocalDateTime.now());
        
        boolean success = updateById(inbox);
        if (success) {
            log.info("收件箱记录标记为已读，ID: {}", inboxId);
            // 可以触发阅读事件
            triggerReadEvent(inbox);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMarkAsRead(List<String> inboxIds) {
        if (CollectionUtils.isEmpty(inboxIds)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpMsgInboxMapper.batchUpdateReadStatus(inboxIds, 1, now);
        
        if (updatedCount > 0) {
            log.info("批量标记为已读成功，数量: {}", updatedCount);
            // 触发批量阅读事件
            triggerBatchReadEvent(inboxIds);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAsReadByReceiver(String receiverId, String receiverType, List<String> msgIds) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgInbox::getReceiverId, receiverId)
                   .eq(UmpMsgInbox::getReceiverType, receiverType)
                   .eq(UmpMsgInbox::getReadStatus, 0);

        if (!CollectionUtils.isEmpty(msgIds)) {
            queryWrapper.in(UmpMsgInbox::getMsgId, msgIds);
        }

        List<UmpMsgInbox> unreadInboxes = list(queryWrapper);
        if (CollectionUtils.isEmpty(unreadInboxes)) {
            return 0;
        }

        List<String> inboxIds = unreadInboxes.stream()
                .map(UmpMsgInbox::getId)
                .collect(Collectors.toList());

        return batchMarkAsRead(inboxIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePushStatus(String inboxId, String pushStatus, String errorMessage) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        int pushCount = "PROCESSING".equals(pushStatus) ? inbox.getPushCount() + 1 : inbox.getPushCount();
        LocalDateTime lastPushTime = "PROCESSING".equals(pushStatus) ? LocalDateTime.now() : inbox.getLastPushTime();

        int updated = umpMsgInboxMapper.updatePushStatus(inboxId, pushStatus, pushCount, lastPushTime);
        if (updated > 0 && StringUtils.hasText(errorMessage)) {
            inbox.setErrorMessage(errorMessage);
            updateById(inbox);
        }

        boolean success = updated > 0;
        if (success) {
            log.info("收件箱推送状态更新，ID: {}, 状态: {}", inboxId, pushStatus);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processPendingPush(int limit) {
        List<UmpMsgInbox> pendingList = umpMsgInboxMapper.selectPendingPush(limit);
        if (CollectionUtils.isEmpty(pendingList)) {
            return 0;
        }

        int processedCount = 0;
        for (UmpMsgInbox inbox : pendingList) {
            try {
                // 更新为处理中状态
                updatePushStatus(inbox.getId(), "PROCESSING", null);
                
                // TODO: 实际推送逻辑，调用MQ或HTTP接口
                // 这里可以调用消息推送服务
                boolean pushSuccess = pushMessageToReceiver(inbox);
                
                if (pushSuccess) {
                    updatePushStatus(inbox.getId(), "SUCCESS", null);
                    // 标记为已接收
                    markAsReceived(inbox.getId());
                } else {
                    updatePushStatus(inbox.getId(), "FAILED", "推送失败");
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("推送处理失败，收件箱ID: {}", inbox.getId(), e);
                updatePushStatus(inbox.getId(), "FAILED", e.getMessage());
            }
        }
        
        return processedCount;
    }

    @Override
    public Integer countUnreadMessages(String receiverId, String receiverType) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }
        
        return umpMsgInboxMapper.countUnreadByReceiver(receiverId, receiverType);
    }

    @Override
    public ReceiverStatisticsVO getReceiverStatistics(String receiverId, String receiverType, 
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statsMap = umpMsgInboxMapper.selectReceiverStatistics(receiverId, receiverType, startTime, endTime);
        
        ReceiverStatisticsVO statisticsVO = new ReceiverStatisticsVO();
        statisticsVO.setReceiverId(receiverId);
        statisticsVO.setReceiverType(receiverType);
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).intValue());
            statisticsVO.setUnreadCount(((Number) statsMap.getOrDefault("unread_count", 0)).intValue());
            statisticsVO.setReadCount(((Number) statsMap.getOrDefault("read_count", 0)).intValue());
            statisticsVO.setReceivedCount(((Number) statsMap.getOrDefault("received_count", 0)).intValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).intValue());
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInboxRecord(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        // 逻辑删除，实际业务可能需要物理删除
        boolean success = removeById(inboxId);
        if (success) {
            log.info("收件箱记录删除成功，ID: {}", inboxId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMsgId(String msgId) {
        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgInbox::getMsgId, msgId);
        
        long deletedCount = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除收件箱记录成功，消息ID: {}, 数量: {}", msgId, deletedCount);
            return (int) deletedCount;
        }
        
        return 0;
    }

    // ============ 私有方法 ============

    private InboxPageVO convertToPageVO(UmpMsgInbox inbox) {
        InboxPageVO vo = new InboxPageVO();
        BeanUtils.copyProperties(inbox, vo);
        return vo;
    }

    private InboxDetailVO convertToDetailVO(UmpMsgInbox inbox) {
        InboxDetailVO vo = new InboxDetailVO();
        BeanUtils.copyProperties(inbox, vo);
        return vo;
    }

    private String getSortField(String sortField) {
        // 映射前端排序字段到数据库字段
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("distributeTime", "distribute_time");
        fieldMap.put("receiveTime", "receive_time");
        fieldMap.put("readTime", "read_time");
        fieldMap.put("lastPushTime", "last_push_time");
        
        return fieldMap.getOrDefault(sortField, "distribute_time");
    }

    private boolean pushMessageToReceiver(UmpMsgInbox inbox) {
        // TODO: 实现消息推送逻辑
        // 1. 根据推送方式（PUSH/POLL）选择不同的推送策略
        // 2. 调用相应的消息推送服务
        // 3. 返回推送结果
        
        log.debug("推送消息给接收者，收件箱ID: {}, 接收者: {}", inbox.getId(), inbox.getReceiverId());
        // 模拟推送成功
        return true;
    }

    private void triggerReceiveEvent(UmpMsgInbox inbox) {
        // 异步触发接收事件
        log.debug("触发消息接收事件，收件箱ID: {}, 消息ID: {}", inbox.getId(), inbox.getMsgId());
        // TODO: 集成消息队列发布接收事件
    }

    private void triggerReadEvent(UmpMsgInbox inbox) {
        // 异步触发阅读事件
        log.debug("触发消息阅读事件，收件箱ID: {}, 消息ID: {}", inbox.getId(), inbox.getMsgId());
        // TODO: 集成消息队列发布阅读事件
    }

    private void triggerBatchReadEvent(List<String> inboxIds) {
        // 异步触发批量阅读事件
        log.debug("触发批量消息阅读事件，数量: {}", inboxIds.size());
        // TODO: 集成消息队列发布批量阅读事件
    }
}