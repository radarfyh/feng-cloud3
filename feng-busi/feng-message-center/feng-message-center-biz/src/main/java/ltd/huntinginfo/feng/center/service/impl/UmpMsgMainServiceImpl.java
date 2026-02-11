package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.mapper.UmpMsgMainMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.common.rabbitmq.dto.RabbitMessage;
import ltd.huntinginfo.feng.common.rabbitmq.service.RabbitMqService;
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
 * 消息主表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgMainServiceImpl extends ServiceImpl<UmpMsgMainMapper, UmpMsgMain> implements UmpMsgMainService {

    private final UmpMsgMainMapper umpMsgMainMapper;
    private final RabbitMqService rabbitMqService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMessage(MessageSendDTO sendDTO) {
        // 验证参数
        validateMessageSendDTO(sendDTO);

        // 构建消息实体
        UmpMsgMain message = buildMessageFromDTO(sendDTO);
        message.setStatus("RECEIVED");
        message.setCreateTime(LocalDateTime.now());

        // 保存到数据库
        if (save(message)) {
            log.info("消息创建成功，消息ID: {}, 消息编码: {}", message.getId(), message.getMsgCode());
            
            // 异步发布消息事件到RabbitMQ
            publishMessageCreatedEvent(message);
            
            return message.getId();
        } else {
            log.error("消息创建失败，发送方: {}", sendDTO.getSenderAppKey());
            throw new RuntimeException("消息创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createAgentMessage(MessageSendDTO sendDTO, String agentAppKey, String agentMsgId) {
        // 验证参数
        validateMessageSendDTO(sendDTO);
        
        if (!StringUtils.hasText(agentAppKey) || !StringUtils.hasText(agentMsgId)) {
            throw new IllegalArgumentException("代理平台标识和代理消息ID不能为空");
        }

        // 构建消息实体
        UmpMsgMain message = buildMessageFromDTO(sendDTO);
        message.setAgentAppKey(agentAppKey);
        message.setAgentMsgId(agentMsgId);
        message.setMsgType("AGENT");
        message.setStatus("RECEIVED");
        message.setCreateTime(LocalDateTime.now());

        // 保存到数据库
        if (save(message)) {
            log.info("代理消息创建成功，消息ID: {}, 代理消息ID: {}", message.getId(), agentMsgId);
            
            // 异步发布消息事件到RabbitMQ
            publishMessageCreatedEvent(message);
            
            return message.getId();
        } else {
            log.error("代理消息创建失败，代理平台: {}, 代理消息ID: {}", agentAppKey, agentMsgId);
            throw new RuntimeException("代理消息创建失败");
        }
    }

    @Override
    public MessageDetailVO getMessageByCode(String msgCode) {
        if (!StringUtils.hasText(msgCode)) {
            throw new IllegalArgumentException("消息编码不能为空");
        }

        UmpMsgMain message = umpMsgMainMapper.selectByMsgCode(msgCode);
        if (message == null) {
            log.warn("消息不存在，消息编码: {}", msgCode);
            return null;
        }

        return convertToDetailVO(message);
    }

    @Override
    public Page<MessagePageVO> queryMessagePage(MessageQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getDelFlag, 0);

        if (StringUtils.hasText(queryDTO.getSenderAppKey())) {
            queryWrapper.eq(UmpMsgMain::getSenderAppKey, queryDTO.getSenderAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgType())) {
            queryWrapper.eq(UmpMsgMain::getMsgType, queryDTO.getMsgType());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgMain::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getTitle())) {
            queryWrapper.like(UmpMsgMain::getTitle, queryDTO.getTitle());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgMain::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgMain::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgMain> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgMain> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<MessagePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<MessagePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    private void applySort(LambdaQueryWrapper<UmpMsgMain> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
                }
                break;
            case "sendTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getSendTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getSendTime);
                }
                break;
            case "priority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getPriority);
                }
                break;
            case "title":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getTitle);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getTitle);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
                break;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMessageStatus(String msgId, String status) {
        if (!StringUtils.hasText(msgId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("消息ID和状态不能为空");
        }

        UmpMsgMain message = getById(msgId);
        if (message == null || message.getDelFlag() == 1) {
            log.warn("消息不存在或已被删除，消息ID: {}", msgId);
            return false;
        }

        String oldStatus = message.getStatus();
        
        // 更新状态
        message.setStatus(status);
        message.setUpdateTime(LocalDateTime.now());
        
        // 根据状态设置相应的时间字段
        switch (status) {
            case "SENT":
                message.setSendTime(LocalDateTime.now());
                break;
            case "DISTRIBUTED":
                message.setDistributeTime(LocalDateTime.now());
                break;
            case "READ":
                message.setCompleteTime(LocalDateTime.now());
                break;
            case "FAILED":
                message.setCompleteTime(LocalDateTime.now());
                break;
        }

        boolean success = updateById(message);
        if (success) {
            log.info("消息状态更新成功，消息ID: {}, 旧状态: {}, 新状态: {}", 
                    msgId, oldStatus, status);
            
            // 异步发布状态变更事件到RabbitMQ
            publishMessageStatusUpdatedEvent(msgId, oldStatus, status, message);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateMessageStatus(List<String> msgIds, String status) {
        if (CollectionUtils.isEmpty(msgIds) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("消息ID列表和状态不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpMsgMainMapper.batchUpdateStatus(msgIds, status, now);
        
        if (updatedCount > 0) {
            log.info("批量更新消息状态成功，数量: {}, 目标状态: {}", updatedCount, status);
            
            // 异步发布批量状态变更事件到RabbitMQ
            publishBatchStatusChangeEvent(msgIds, status);
        }

        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSent(String msgId) {
        return updateMessageStatus(msgId, "SENT");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsDistributed(String msgId) {
        boolean success = updateMessageStatus(msgId, "DISTRIBUTED");
        if (success) {
            // 触发消息分发事件
            publishMessageDistributedEvent(msgId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReadStatistics(String msgId, int readCount) {
        if (!StringUtils.hasText(msgId) || readCount < 0) {
            throw new IllegalArgumentException("消息ID和已读人数不能为空");
        }

        UmpMsgMain message = getById(msgId);
        if (message == null || message.getDelFlag() == 1) {
            log.warn("消息不存在或已被删除，消息ID: {}", msgId);
            return false;
        }

        int updated = umpMsgMainMapper.updateReadCount(msgId, readCount, LocalDateTime.now());
        if (updated > 0) {
            log.info("消息已读统计更新成功，消息ID: {}, 已读人数: {}", msgId, readCount);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processExpiredMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<UmpMsgMain> expiredMessages = umpMsgMainMapper.selectExpiredMessages(now);
        
        if (CollectionUtils.isEmpty(expiredMessages)) {
            return 0;
        }

        List<String> expiredMsgIds = expiredMessages.stream()
                .map(UmpMsgMain::getId)
                .collect(Collectors.toList());

        int updatedCount = batchUpdateMessageStatus(expiredMsgIds, "EXPIRED");
        log.info("已处理过期消息数量: {}", updatedCount);
        
        // 发布过期事件
        publishMessageExpiredEvent(expiredMessages);
        
        return updatedCount;
    }

    @Override
    public MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime, String appKey) {
        List<Map<String, Object>> statsList = umpMsgMainMapper.selectMessageStatistics(startTime, endTime, appKey);
        
        MessageStatisticsVO statisticsVO = new MessageStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setAppKey(appKey);
        
        // 计算汇总统计
        int totalCount = 0;
        int successCount = 0;
        int failedCount = 0;
        int totalReceivers = 0;
        int totalRead = 0;
        
        for (Map<String, Object> stat : statsList) {
            Integer count = (Integer) stat.get("count");
            String status = (String) stat.get("status");
            Integer receivers = (Integer) stat.getOrDefault("receivers", 0);
            Integer readCount = (Integer) stat.getOrDefault("read_count", 0);
            
            totalCount += count;
            totalReceivers += receivers;
            totalRead += readCount;
            
            if ("SENT".equals(status) || "READ".equals(status)) {
                successCount += count;
            } else if ("FAILED".equals(status)) {
                failedCount += count;
            }
        }
        
        statisticsVO.setTotalCount(totalCount);
        statisticsVO.setSuccessCount(successCount);
        statisticsVO.setFailedCount(failedCount);
        statisticsVO.setTotalReceivers(totalReceivers);
        statisticsVO.setTotalRead(totalRead);
        
        if (totalCount > 0) {
            statisticsVO.setSuccessRate((double) successCount / totalCount * 100);
            statisticsVO.setReadRate(totalReceivers > 0 ? (double) totalRead / totalReceivers * 100 : 0);
        }
        
        return statisticsVO;
    }

    @Override
    public List<MessageDetailVO> getUnreadMessages(String receiverId, String receiverType, int limit) {
        // 这里需要关联查询ump_msg_inbox表获取未读消息
        // 简化实现，实际需要根据业务逻辑调整
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getDelFlag, 0)
                   .eq(UmpMsgMain::getStatus, "SENT")
                   .orderByDesc(UmpMsgMain::getCreateTime)
                   .last("LIMIT " + limit);
        
        List<UmpMsgMain> messages = list(queryWrapper);
        return messages.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsAndValid(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            return false;
        }
        
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getId, msgId)
                   .eq(UmpMsgMain::getDelFlag, 0);
        
        return count(queryWrapper) > 0;
    }

    // ============ 私有方法 ============

    private void validateMessageSendDTO(MessageSendDTO sendDTO) {
        if (sendDTO == null) {
            throw new IllegalArgumentException("消息发送DTO不能为空");
        }
        
        if (!StringUtils.hasText(sendDTO.getSenderAppKey())) {
            throw new IllegalArgumentException("发送应用标识不能为空");
        }
        
        if (!StringUtils.hasText(sendDTO.getTitle())) {
            throw new IllegalArgumentException("消息标题不能为空");
        }
        
        if (sendDTO.getContent() == null || sendDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }

    private UmpMsgMain buildMessageFromDTO(MessageSendDTO sendDTO) {
        UmpMsgMain message = new UmpMsgMain();
        BeanUtils.copyProperties(sendDTO, message);
        
        // 生成消息编码（如果未提供）
        if (!StringUtils.hasText(message.getMsgCode())) {
            message.setMsgCode(generateMsgCode());
        }
        
        // 设置默认值
        if (message.getPriority() == null) {
            message.setPriority(3); // 默认优先级
        }
        
        if (!StringUtils.hasText(message.getMsgType())) {
            message.setMsgType("NOTICE"); // 默认消息类型
        }
        
        if (!StringUtils.hasText(message.getPushMode())) {
            message.setPushMode("PUSH"); // 默认推送方式
        }
        
        if (message.getReceiverCount() == null) {
            message.setReceiverCount(1); // 默认接收者数量
        }
        
        if (!StringUtils.hasText(message.getReceiverType())) {
            message.setReceiverType("USER"); // 默认接收者类型
        }
        
        // 设置过期时间（默认7天）
        if (message.getExpireTime() == null) {
            message.setExpireTime(LocalDateTime.now().plusDays(7));
        }
        
        return message;
    }

    private String generateMsgCode() {
        // 生成唯一消息编码，格式：MSG-时间戳-随机数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 10000));
        return "MSG-" + timestamp + "-" + random;
    }

    // ============ RabbitMQ事件发布方法 ============

    private void publishMessageCreatedEvent(UmpMsgMain message) {
        try {
            // 构建消息创建事件数据
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("messageId", message.getId());
            eventData.put("msgCode", message.getMsgCode());
            eventData.put("title", message.getTitle());
            eventData.put("msgType", message.getMsgType());
            eventData.put("priority", message.getPriority());
            eventData.put("senderAppKey", message.getSenderAppKey());
            eventData.put("senderId", message.getSenderId());
            eventData.put("receiverCount", message.getReceiverCount());
            eventData.put("receiverType", message.getReceiverType());
            eventData.put("receiverScope", message.getReceiverScope());
            eventData.put("pushMode", message.getPushMode());
            eventData.put("expireTime", message.getExpireTime());
            eventData.put("agentAppKey", message.getAgentAppKey());
            eventData.put("agentMsgId", message.getAgentMsgId());
            eventData.put("createTime", message.getCreateTime());
            
            // 发送消息创建事件
            rabbitMqService.sendMessageCreatedEvent(eventData);
            
            log.debug("消息创建事件发布成功，消息ID: {}, 消息编码: {}", 
                    message.getId(), message.getMsgCode());
        } catch (Exception e) {
            log.error("消息创建事件发布失败，消息ID: {}", message.getId(), e);
            // 记录错误但不影响主业务流程
        }
    }

    private void publishMessageStatusUpdatedEvent(String msgId, String oldStatus, 
                                                 String newStatus, UmpMsgMain message) {
        try {
            // 构建状态变更事件数据
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("messageId", msgId);
            eventData.put("oldStatus", oldStatus);
            eventData.put("newStatus", newStatus);
            eventData.put("updateTime", LocalDateTime.now());
            
            if (message != null) {
                eventData.put("msgCode", message.getMsgCode());
                eventData.put("senderAppKey", message.getSenderAppKey());
                eventData.put("sendTime", message.getSendTime());
                eventData.put("distributeTime", message.getDistributeTime());
                eventData.put("completeTime", message.getCompleteTime());
            }
            
            // 发送状态变更事件
            rabbitMqService.sendMessageStatusUpdatedEvent(eventData);
            
            log.debug("消息状态变更事件发布成功，消息ID: {}, 状态: {} -> {}", 
                    msgId, oldStatus, newStatus);
        } catch (Exception e) {
            log.error("消息状态变更事件发布失败，消息ID: {}", msgId, e);
            // 记录错误但不影响主业务流程
        }
    }

    private void publishMessageDistributedEvent(String msgId) {
        try {
            UmpMsgMain message = getById(msgId);
            if (message == null) {
                log.warn("消息不存在，无法发布分发事件，消息ID: {}", msgId);
                return;
            }
            
            // 构建分发事件数据
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("messageId", msgId);
            eventData.put("msgCode", message.getMsgCode());
            eventData.put("title", message.getTitle());
            eventData.put("receiverCount", message.getReceiverCount());
            eventData.put("receiverType", message.getReceiverType());
            eventData.put("receiverScope", message.getReceiverScope());
            eventData.put("distributeTime", message.getDistributeTime());
            eventData.put("senderAppKey", message.getSenderAppKey());
            
            // 发送分发事件
            rabbitMqService.sendMessageDistributedEvent(eventData);
            
            log.debug("消息分发事件发布成功，消息ID: {}, 接收者数量: {}", 
                    msgId, message.getReceiverCount());
        } catch (Exception e) {
            log.error("消息分发事件发布失败，消息ID: {}", msgId, e);
            // 记录错误但不影响主业务流程
        }
    }

    private void publishMessageExpiredEvent(List<UmpMsgMain> expiredMessages) {
        if (CollectionUtils.isEmpty(expiredMessages)) {
            return;
        }
        
        try {
            // 构建过期事件数据
            List<Map<String, Object>> expiredList = expiredMessages.stream()
                    .map(message -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("messageId", message.getId());
                        data.put("msgCode", message.getMsgCode());
                        data.put("title", message.getTitle());
                        data.put("senderAppKey", message.getSenderAppKey());
                        data.put("createTime", message.getCreateTime());
                        data.put("expireTime", message.getExpireTime());
                        data.put("status", message.getStatus());
                        return data;
                    })
                    .collect(Collectors.toList());
            
            // 发送过期事件
            rabbitMqService.sendMessageExpiredEvent(expiredList);
            
            log.debug("消息过期事件发布成功，数量: {}", expiredMessages.size());
        } catch (Exception e) {
            log.error("消息过期事件发布失败", e);
            // 记录错误但不影响主业务流程
        }
    }

    private void publishBatchStatusChangeEvent(List<String> msgIds, String status) {
        if (CollectionUtils.isEmpty(msgIds)) {
            return;
        }
        
        try {
            // 构建批量状态变更事件数据
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("messageIds", msgIds);
            eventData.put("newStatus", status);
            eventData.put("updateTime", LocalDateTime.now());
            eventData.put("count", msgIds.size());
            
            // 发送批量状态变更事件
            rabbitMqService.sendMessageStatusUpdatedEvent(eventData);
            
            log.debug("批量消息状态变更事件发布成功，数量: {}, 状态: {}", 
                    msgIds.size(), status);
        } catch (Exception e) {
            log.error("批量消息状态变更事件发布失败", e);
            // 记录错误但不影响主业务流程
        }
    }

    private MessageDetailVO convertToDetailVO(UmpMsgMain message) {
        MessageDetailVO vo = new MessageDetailVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    private MessagePageVO convertToPageVO(UmpMsgMain message) {
        MessagePageVO vo = new MessagePageVO();
        BeanUtils.copyProperties(message, vo);
        
        // 计算已读率
        if (message.getTotalReceivers() != null && message.getTotalReceivers() > 0 
                && message.getReadCount() != null) {
            vo.setReadRate((double) message.getReadCount() / message.getTotalReceivers() * 100);
        }
        
        return vo;
    }
}