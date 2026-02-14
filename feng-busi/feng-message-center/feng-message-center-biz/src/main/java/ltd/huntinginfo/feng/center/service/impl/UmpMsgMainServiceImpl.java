package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.mapper.UmpMsgMainMapper;
import ltd.huntinginfo.feng.center.service.UmpBroadcastReceiveRecordService;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息主表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgMainServiceImpl extends ServiceImpl<UmpMsgMainMapper, UmpMsgMain> implements UmpMsgMainService {

    private final UmpMsgMainMapper umpMsgMainMapper;
    private final MqMessageProducer mqMessageProducer;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMessage(MessageSendDTO sendDTO) {
        // 验证参数
        validateMessageSendDTO(sendDTO);

        // 构建消息实体
        UmpMsgMain message = buildMessageFromDTO(sendDTO);
        message.setMsgType(MqMessageEventConstants.BusinessTypes.BIZ);
        message.setStatus(MqMessageEventConstants.EventTypes.RECEIVED);
        message.setCreateTime(LocalDateTime.now());
        
        String oldStatus = "";

        // 保存到数据库
        if (save(message)) {
            log.info("消息创建成功，消息ID: {}, 消息编码: {}", message.getId(), message.getMsgCode());
            
            // 异步发布消息事件到RabbitMQ
            
            publishEventByStatus(message, oldStatus);
            
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
        message.setMsgType(MqMessageEventConstants.BusinessTypes.AGENT);
        message.setStatus(MqMessageEventConstants.EventTypes.RECEIVED);
        message.setCreateTime(LocalDateTime.now());
        
        String oldStatus = "";

        // 保存到数据库
        if (save(message)) {
            log.info("代理消息创建成功，消息ID: {}, 代理消息ID: {}", message.getId(), agentMsgId);
            
            // 异步发布消息事件到RabbitMQ
            publishEventByStatus(message, oldStatus);
            
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
            case MqMessageEventConstants.EventTypes.RECEIVED:
                message.setSendTime(LocalDateTime.now());
                break;
            case MqMessageEventConstants.EventTypes.DISTRIBUTED:
                message.setDistributeTime(LocalDateTime.now());
                break;
            case MqMessageEventConstants.EventTypes.READ:
                message.setCompleteTime(LocalDateTime.now());
                break;
            case MqMessageEventConstants.EventTypes.PULL_FAILED:
            case MqMessageEventConstants.EventTypes.PUSH_FAILED:
            case MqMessageEventConstants.EventTypes.DIST_FAILED:
                message.setCompleteTime(LocalDateTime.now());
                break;
        }
        if (!updateById(message)) {
        	return false;
        }
        
        publishEventByStatus(message, oldStatus);
        
        log.info("消息状态更新成功，消息ID: {}, 旧状态: {}, 新状态: {}", 
                msgId, oldStatus, status);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateMessageStatus(List<String> msgIds, String status) {
        if (CollectionUtils.isEmpty(msgIds) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("消息ID列表和状态不能为空");
        }
        
        int updatedCount = 0;
		for(String msgId : msgIds) {
			if (updateMessageStatus(msgId, status))
			{
				updatedCount++;
			}
        }
   
        if (updatedCount > 0) {
            log.info("批量更新消息状态成功，数量: {}, 目标状态: {}", updatedCount, status);
        }

        return updatedCount;
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

        // 过期没有单独的状态，使用 POLL_FAILED 状态代替
        int updatedCount = batchUpdateMessageStatus(expiredMsgIds, MqMessageEventConstants.EventTypes.PULL_FAILED);
        log.info("已处理过期消息数量: {}", updatedCount);
        
        // 发布过期事件
        publishBatchMessageExpired(expiredMessages);
        
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
    public List<MessageDetailVO> getAllUnreadMessages(int limit) {
    	
    	// 直接读状态，前提是收件箱和广播信息筒状态改变时同步修改主表的状态
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getDelFlag, 0)
                   .ne(UmpMsgMain::getStatus, MqMessageEventConstants.EventTypes.READ)
                   .in(UmpMsgMain::getStatus, MqMessageEventConstants.EventTypes.PUSHED, MqMessageEventConstants.EventTypes.BIZ_PULLED)
                   .orderByDesc(UmpMsgMain::getCreateTime)
                   .last("LIMIT " + limit);
        
        List<UmpMsgMain> messages = list(queryWrapper);
        return messages.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageDetailVO> getUnreadMessages(String receiverId, String receiverType, int limit) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        List<MessageDetailVO> result = new ArrayList<>();

        // 1. 查询收件箱中未读的点对点消息
        List<UmpMsgInbox> inboxList = umpMsgInboxService.lambdaQuery()
                .eq(UmpMsgInbox::getReceiverId, receiverId)
                .eq(UmpMsgInbox::getReceiverType, receiverType)
                .eq(UmpMsgInbox::getReadStatus, 0) // 0-未读
                .orderByDesc(UmpMsgInbox::getDistributeTime)
                .last("LIMIT " + limit)
                .list();

        if (!CollectionUtils.isEmpty(inboxList)) {
            List<String> msgIds = inboxList.stream()
                    .map(UmpMsgInbox::getMsgId)
                    .collect(Collectors.toList());

            // 批量查询消息主表
            List<UmpMsgMain> msgList = listByIds(msgIds);
            Map<String, UmpMsgMain> msgMap = msgList.stream()
                    .collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

            for (UmpMsgInbox inbox : inboxList) {
                UmpMsgMain msg = msgMap.get(inbox.getMsgId());
                if (msg != null) {
                    MessageDetailVO vo = convertToDetailVO(msg);
//                    vo.setInboxId(inbox.getId()); // 可附带收件箱ID
//                    vo.setReadStatus(inbox.getReadStatus());
                    result.add(vo);
                }
            }
        }

        // 2. 如果数量不足，再查询广播中未读的消息（简化示例）
        if (result.size() < limit) {
            int remaining = limit - result.size();
            List<UmpBroadcastReceiveRecord> broadcastRecords = umpBroadcastReceiveRecordService.lambdaQuery()
                    .eq(UmpBroadcastReceiveRecord::getReceiverId, receiverId)
                    .eq(UmpBroadcastReceiveRecord::getReceiverType, receiverType)
                    .eq(UmpBroadcastReceiveRecord::getReadStatus, 0)
                    .orderByDesc(UmpBroadcastReceiveRecord::getCreateTime)
                    .last("LIMIT " + remaining)
                    .list();

            if (!CollectionUtils.isEmpty(broadcastRecords)) {
                List<String> broadcastIds = broadcastRecords.stream()
                        .map(UmpBroadcastReceiveRecord::getBroadcastId)
                        .collect(Collectors.toList());

                List<UmpMsgBroadcast> broadcasts = umpMsgBroadcastService.listByIds(broadcastIds);
                List<String> msgIds = broadcasts.stream()
                        .map(UmpMsgBroadcast::getMsgId)
                        .collect(Collectors.toList());

                Map<String, UmpMsgMain> msgMap = listByIds(msgIds).stream()
                        .collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

                for (UmpBroadcastReceiveRecord record : broadcastRecords) {
                    UmpMsgBroadcast broadcast = broadcasts.stream()
                            .filter(b -> b.getId().equals(record.getBroadcastId()))
                            .findFirst().orElse(null);
                    if (broadcast != null) {
                        UmpMsgMain msg = msgMap.get(broadcast.getMsgId());
                        if (msg != null) {
                            MessageDetailVO vo = convertToDetailVO(msg);
//                            vo.setBroadcastId(record.getBroadcastId());
//                            vo.setReadStatus(record.getReadStatus());
                            result.add(vo);
                        }
                    }
                }
            }
        }

        return result;
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

	@Override
	public boolean updateReceiverCount(String messageId, Integer totalReceivers, 
			Integer receivedCount, Integer readCount) {
		
		UmpMsgMain message = this.getById(messageId);
		message.setReceivedCount(receivedCount);
		message.setTotalReceivers(totalReceivers);
		message.setReadCount(readCount);
		
		int ret = baseMapper.updateById(message);
		
		return (ret > 0) ? true : false;
	}

    // ==================== 事件发布统一入口 ====================

    /**
     * 根据消息新状态发布对应的事件
     */
    private void publishEventByStatus(UmpMsgMain message, String oldStatus) {
        executePublish(() -> {
            Map<String, Object> eventData = buildBaseEventData(message, oldStatus);
            switch (message.getStatus()) {
		        case MqMessageEventConstants.EventTypes.RECEIVED:
		        	// 将会进入ump_msg_queue表，队列类型：分发
		        	mqMessageProducer.sendMessageDistributeStart(eventData, message.getMsgType());
		            break;	
		        case MqMessageEventConstants.EventTypes.DISTRIBUTING:
		        	// 不发布事件到MQ，因为已经进入ump_msg_queue表，队列类型：分发
		        	//mqMessageProducer.sendMessageDistributing(eventData, message.getMsgType());
		            break;	
		        case MqMessageEventConstants.EventTypes.DISTRIBUTED:
		        	// 将会进入ump_msg_queue表，队列类型：推送
		        	mqMessageProducer.sendMessageDistributed(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.DIST_FAILED:
	            	// 将会进入ump_msg_queue表，队列类型：重试
	            	mqMessageProducer.sendMessageDistributeFailed(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.PUSHED:
	            	// 不会发布事件，不会进入ump_msg_queue表
	            	//mqMessageProducer.sendMessagePushed(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.PUSH_FAILED:
	            	// 将会进入ump_msg_queue表，队列类型：重试
	            	mqMessageProducer.sendMessagePushFailed(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.BIZ_RECEIVED:
	            	//业务系统上报接收成功（暂定调用回调地址成功就表示业务已接收）（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageBusinessReceived(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.PULL:
	            	//mqMessageProducer.sendMessagePullReady(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.BIZ_PULLED:
	            	//业务系统上报拉取成功（暂定查询成功就表示业务已拉取）（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageBusinessPulled(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.PULL_FAILED:
	            	//mqMessageProducer.sendMessagePullFailed(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.READ:
	            	//业务系统上报已读（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageRead(eventData, message.getMsgType());
	                break;
	            case MqMessageEventConstants.EventTypes.EXPIRED:
	            	// 发布到MQ，进入ump_msg_queue表进行专门处理
	            	mqMessageProducer.sendMessageExpired(eventData, message.getMsgType());
	                break;
	            default:
	                log.debug("状态 {} 无需发布事件", message.getStatus());
	        }
            
            log.debug("消息已接收事件发布成功，消息ID: {}", message.getId());
        }, "消息已接收事件发布失败，消息ID: {}", message.getId());

    }

    // ==================== 辅助方法 ====================

    /**
     * 构建消息基础事件数据（所有事件共有的字段）
     */
    private Map<String, Object> buildBaseEventData(UmpMsgMain message, String oldStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put(MqMessageEventConstants.TaskDataKeys.MESSAGE_ID, message.getId());
        data.put(MqMessageEventConstants.TaskDataKeys.MSG_CODE, message.getMsgCode());
        data.put(MqMessageEventConstants.TaskDataKeys.TITLE, message.getTitle());
        data.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_TYPE, message.getReceiverType());
        data.put(MqMessageEventConstants.TaskDataKeys.RECEIVER_SCOPE, message.getReceiverScope());
        data.put(MqMessageEventConstants.TaskDataKeys.PUSH_MODE, message.getPushMode());
        data.put(MqMessageEventConstants.TaskDataKeys.CALLBACK_URL, message.getCallbackUrl());
        data.put(MqMessageEventConstants.TaskDataKeys.CALLBACK_CONFIG, message.getCallbackConfig());
        data.put(MqMessageEventConstants.TaskDataKeys.PRIORITY, message.getPriority());
        data.put(MqMessageEventConstants.TaskDataKeys.EXPIRE_TIME, message.getExpireTime());
        data.put(MqMessageEventConstants.TaskDataKeys.SEND_TIME, message.getSendTime());
        data.put(MqMessageEventConstants.TaskDataKeys.CREATE_TIME, message.getCreateTime());
        data.put(MqMessageEventConstants.TaskDataKeys.STATUS, message.getStatus());
        data.put(MqMessageEventConstants.TaskDataKeys.OLD_STATUS, oldStatus);

        return data;
    }

    /**
     * 统一执行事件发布，捕获异常并记录日志
     */
    private void executePublish(Runnable publishAction, String errorMsg, Object... args) {
        try {
            publishAction.run();
        } catch (Exception e) {
            log.error(errorMsg, args, e);
            // 不抛出异常，不影响主流程
        }
    }
    
    // ==================== 消息过期事件 ====================

    /**
     * 发布【消息已过期】事件（EXPIRED）
     */
    private void publishMessageExpired(UmpMsgMain message) {
    	// 注意没有过期状态，但是有过期事件
    	publishEventByStatus(message, MqMessageEventConstants.EventTypes.EXPIRED);
    }

    /**
     * 批量发布消息过期事件（用于定时清理）
     */
    private void publishBatchMessageExpired(List<UmpMsgMain> expiredMessages) {
        if (CollectionUtils.isEmpty(expiredMessages)) {
            return;
        }
        // 批量发送单个事件，或压缩为一个批量事件（需消费者支持）
        expiredMessages.forEach(this::publishMessageExpired);
        log.debug("批量消息过期事件发布完成，数量: {}", expiredMessages.size());
    }
}