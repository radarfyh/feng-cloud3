package ltd.huntinginfo.feng.center.service.impl;

import ltd.huntinginfo.feng.center.api.entity.MsgAgentMapping;
import ltd.huntinginfo.feng.center.mapper.MsgAgentMappingMapper;
import ltd.huntinginfo.feng.center.service.MsgAgentMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MsgAgentMappingServiceImpl extends ServiceImpl<MsgAgentMappingMapper, MsgAgentMapping> implements MsgAgentMappingService {

    private final MsgAgentMappingMapper msgAgentMappingMapper;

    @Override
    public MsgAgentMapping getById(String id) {
        try {
            MsgAgentMapping result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的消息映射记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询消息映射详情失败: id={}", id, e);
            throw new RuntimeException("查询消息映射详情失败", e);
        }
    }
    
    /**
     * 根据应用标识和业务ID查询消息映射
     */
    @Override
    public MsgAgentMapping getByAppKeyAndBizId(String appKey, String bizId) {
        try {
            if (StrUtil.isBlank(appKey) || StrUtil.isBlank(bizId)) {
                log.warn("查询参数不能为空: appKey={}, bizId={}", appKey, bizId);
                return null;
            }
            
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getAppKey, appKey);
            wrapper.eq(MsgAgentMapping::getBizId, bizId);
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            wrapper.last("LIMIT 1");
            
            MsgAgentMapping result = super.getOne(wrapper);
            if (result == null) {
                log.debug("未找到对应的消息映射记录: appKey={}, bizId={}", appKey, bizId);
            }
            return result;
            
        } catch (Exception e) {
            log.error("根据应用标识和业务ID查询失败: appKey={}, bizId={}", appKey, bizId, e);
            throw new RuntimeException("查询消息映射失败", e);
        }
    }
    
    @Override
    public IPage<MsgAgentMapping> page(IPage<MsgAgentMapping> page, MsgAgentMapping msgAgentMapping) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = buildQueryWrapper(msgAgentMapping);
            wrapper.orderByDesc(MsgAgentMapping::getSendTime); // 默认按发送时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询消息映射列表失败", e);
            throw new RuntimeException("分页查询消息映射列表失败", e);
        }
    }

    @Override
    public List<MsgAgentMapping> list(MsgAgentMapping msgAgentMapping) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = buildQueryWrapper(msgAgentMapping);
            wrapper.orderByDesc(MsgAgentMapping::getSendTime); // 默认按发送时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询消息映射列表失败", e);
            throw new RuntimeException("查询消息映射列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgAgentMapping msgAgentMapping) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgAgentMapping.getId())) {
            	msgAgentMapping.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgAgentMapping.getCreateTime() == null) {
                msgAgentMapping.setCreateTime(new Date());
            }
            
            // 设置发送时间（如果为空）
            if (msgAgentMapping.getSendTime() == null) {
                msgAgentMapping.setSendTime(new Date());
            }
            
            // 设置默认状态
            if (msgAgentMapping.getStatus() == null) {
                msgAgentMapping.setStatus("ACCEPTED");
            }
            
            if (msgAgentMapping.getStatusCode() == null) {
                msgAgentMapping.setStatusCode("1000");
            }
            
            boolean result = super.save(msgAgentMapping);
            if (result) {
                log.debug("保存消息映射成功: id={}, appKey={}, xxbm={}", 
                        msgAgentMapping.getId(), msgAgentMapping.getAppKey(), msgAgentMapping.getXxbm());
            } else {
                log.error("保存消息映射失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存消息映射失败", e);
            throw new RuntimeException("保存消息映射失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgAgentMapping msgAgentMapping) {
        try {
            boolean result = super.updateById(msgAgentMapping);
            if (result) {
                log.debug("更新消息映射成功: id={}", msgAgentMapping.getId());
            } else {
                log.warn("更新消息映射失败，记录不存在: id={}", msgAgentMapping.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新消息映射失败: id={}", msgAgentMapping.getId(), e);
            throw new RuntimeException("更新消息映射失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgAgentMapping existMapping = super.getById(id);
            if (existMapping == null) {
                log.warn("删除消息映射失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除消息映射成功: id={}, appKey={}, xxbm={}", 
                        id, existMapping.getAppKey(), existMapping.getXxbm());
            } else {
                log.error("删除消息映射失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除消息映射失败: id={}", id, e);
            throw new RuntimeException("删除消息映射失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgAgentMapping> buildQueryWrapper(MsgAgentMapping msgAgentMapping) {
        LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
        
        if (msgAgentMapping != null) {
            // 按ID查询
            if (msgAgentMapping.getId() != null && !msgAgentMapping.getId().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getId, msgAgentMapping.getId());
            }
            
            // 按应用标识查询
            if (msgAgentMapping.getAppKey() != null && !msgAgentMapping.getAppKey().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getAppKey, msgAgentMapping.getAppKey());
            }
            
            // 按部级消息编码查询
            if (msgAgentMapping.getXxbm() != null && !msgAgentMapping.getXxbm().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getXxbm, msgAgentMapping.getXxbm());
            }
            
            // 按部级消息ID查询
            if (msgAgentMapping.getCenterMsgId() != null && !msgAgentMapping.getCenterMsgId().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getCenterMsgId, msgAgentMapping.getCenterMsgId());
            }
            
            // 按消息类型查询
            if (msgAgentMapping.getMsgType() != null && !msgAgentMapping.getMsgType().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getMsgType, msgAgentMapping.getMsgType());
            }
            
            // 按消息状态查询
            if (msgAgentMapping.getStatus() != null && !msgAgentMapping.getStatus().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getStatus, msgAgentMapping.getStatus());
            }
            
            // 按状态码查询
            if (msgAgentMapping.getStatusCode() != null && !msgAgentMapping.getStatusCode().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getStatusCode, msgAgentMapping.getStatusCode());
            }
            
            // 按部级处理状态查询
            if (msgAgentMapping.getCenterClzt() != null && !msgAgentMapping.getCenterClzt().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getCenterClzt, msgAgentMapping.getCenterClzt());
            }
            
            // 按发送时间范围查询（需要扩展功能时使用）
            // if (startTime != null && endTime != null) {
            //     wrapper.between(MsgAgentMapping::getSendTime, startTime, endTime);
            // }
            
            // 按发送方信息查询
            if (msgAgentMapping.getSenderName() != null && !msgAgentMapping.getSenderName().isEmpty()) {
                wrapper.like(MsgAgentMapping::getSenderName, msgAgentMapping.getSenderName());
            }
            
            if (msgAgentMapping.getSenderIdcard() != null && !msgAgentMapping.getSenderIdcard().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getSenderIdcard, msgAgentMapping.getSenderIdcard());
            }
            
            // 按接收方信息查询
            if (msgAgentMapping.getReceiverName() != null && !msgAgentMapping.getReceiverName().isEmpty()) {
                wrapper.like(MsgAgentMapping::getReceiverName, msgAgentMapping.getReceiverName());
            }
            
            if (msgAgentMapping.getReceiverIdcard() != null && !msgAgentMapping.getReceiverIdcard().isEmpty()) {
                wrapper.eq(MsgAgentMapping::getReceiverIdcard, msgAgentMapping.getReceiverIdcard());
            }
            
            // 按消息标题模糊查询
            if (msgAgentMapping.getMsgTitle() != null && !msgAgentMapping.getMsgTitle().isEmpty()) {
                wrapper.like(MsgAgentMapping::getMsgTitle, msgAgentMapping.getMsgTitle());
            }
            
            // 按部级消息标题模糊查询
            if (msgAgentMapping.getCenterXxbt() != null && !msgAgentMapping.getCenterXxbt().isEmpty()) {
                wrapper.like(MsgAgentMapping::getCenterXxbt, msgAgentMapping.getCenterXxbt());
            }
        }
        
        return wrapper;
    }

    /**
     * 根据部级消息编码查询消息映射
     */
    @Override
    public MsgAgentMapping getByXxbm(String xxbm) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getXxbm, xxbm);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据部级消息编码查询失败: xxbm={}", xxbm, e);
            throw new RuntimeException("查询消息映射失败", e);
        }
    }

    /**
     * 根据应用标识查询最近的消息（按接收时间倒序）
     */
    @Override
    public List<MsgAgentMapping> getRecentMessages(String appKey, int limit) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getAppKey, appKey);
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            wrapper.orderByDesc(MsgAgentMapping::getCenterReceiveTime);
            wrapper.last("LIMIT " + limit);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询最近消息失败: appKey={}, limit={}", appKey, limit, e);
            throw new RuntimeException("查询最近消息失败", e);
        }
    }

    /**
     * 查询应用未读消息
     */
    @Override
    public List<MsgAgentMapping> getUnreadMessages(String appKey) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getAppKey, appKey);
            wrapper.eq(MsgAgentMapping::getCenterClzt, "0"); // 未读
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            wrapper.orderByDesc(MsgAgentMapping::getCenterReceiveTime);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询未读消息失败: appKey={}", appKey, e);
            throw new RuntimeException("查询未读消息失败", e);
        }
    }

    /**
     * 更新消息状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String id, String status, String statusCode, String detail) {
        try {
            LambdaUpdateWrapper<MsgAgentMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgAgentMapping::getId, id);
            wrapper.set(MsgAgentMapping::getStatus, status);
            wrapper.set(MsgAgentMapping::getStatusCode, statusCode);
            wrapper.set(MsgAgentMapping::getStatusDetail, detail);
            wrapper.set(MsgAgentMapping::getUpdateTime, new Date());
            
            // 根据状态更新时间字段
            if ("CALLBACK_SENT".equals(status)) {
                wrapper.set(MsgAgentMapping::getCallbackTime, new Date());
            } else if ("CALLBACK_ACKED".equals(status)) {
                wrapper.set(MsgAgentMapping::getBizAckTime, new Date());
            } else if ("COMPLETED".equals(status)) {
                wrapper.set(MsgAgentMapping::getCompleteTime, new Date());
            }
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新消息状态成功: id={}, status={}, statusCode={}", id, status, statusCode);
            } else {
                log.warn("更新消息状态失败，记录不存在: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("更新消息状态失败: id={}", id, e);
            throw new RuntimeException("更新消息状态失败", e);
        }
    }

    /**
     * 更新部级处理状态（已读/未读）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCenterClzt(String id, String clzt) {
        try {
            LambdaUpdateWrapper<MsgAgentMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgAgentMapping::getId, id);
            wrapper.set(MsgAgentMapping::getCenterClzt, clzt);
            wrapper.set(MsgAgentMapping::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新部级处理状态成功: id={}, clzt={}", id, clzt);
            } else {
                log.warn("更新部级处理状态失败，记录不存在: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("更新部级处理状态失败: id={}", id, e);
            throw new RuntimeException("更新部级处理状态失败", e);
        }
    }

    /**
     * 批量更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<String> ids, String status, String statusCode) {
        try {
            if (ids == null || ids.isEmpty()) {
                log.warn("批量更新状态失败，ID列表为空");
                return false;
            }
            
            LambdaUpdateWrapper<MsgAgentMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(MsgAgentMapping::getId, ids);
            wrapper.set(MsgAgentMapping::getStatus, status);
            wrapper.set(MsgAgentMapping::getStatusCode, statusCode);
            wrapper.set(MsgAgentMapping::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("批量更新状态成功: count={}, status={}, statusCode={}", ids.size(), status, statusCode);
            } else {
                log.warn("批量更新状态失败");
            }
            return result;
        } catch (Exception e) {
            log.error("批量更新状态失败", e);
            throw new RuntimeException("批量更新状态失败", e);
        }
    }

    /**
     * 统计应用消息数量
     */
    @Override
    public long countByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getAppKey, appKey);
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计消息数量失败: appKey={}", appKey, e);
            throw new RuntimeException("统计消息数量失败", e);
        }
    }

    /**
     * 统计应用未读消息数量
     */
    @Override
    public long countUnreadByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentMapping::getAppKey, appKey);
            wrapper.eq(MsgAgentMapping::getCenterClzt, "0"); // 未读
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计未读消息数量失败: appKey={}", appKey, e);
            throw new RuntimeException("统计未读消息数量失败", e);
        }
    }

    /**
     * 清理已完成的消息（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long cleanCompletedMessages(Date beforeDate) {
        try {
            LambdaUpdateWrapper<MsgAgentMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(MsgAgentMapping::getStatusCode, "1007", "1008", "1009"); // 完成状态
            wrapper.eq(MsgAgentMapping::getDelFlag, "0"); // 未删除
            wrapper.lt(MsgAgentMapping::getCompleteTime, beforeDate); // 完成时间早于指定时间
            
            wrapper.set(MsgAgentMapping::getDelFlag, "1");
            wrapper.set(MsgAgentMapping::getUpdateTime, new Date());
            
            Long count = this.count(wrapper);
            if (count > 0) {
                boolean result = super.update(wrapper);
                if (result) {
                    log.info("清理已完成消息成功: 清理条数={}, 完成时间<={}", count, beforeDate);
                    return count;
                }
            }
            return 0L;
        } catch (Exception e) {
            log.error("清理已完成消息失败", e);
            throw new RuntimeException("清理消息失败", e);
        }
    }

    /**
     * 创建消息映射（封装常用字段）
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgAgentMapping createMapping(String appKey, String xxbm, String centerMsgId, 
                                        String msgTitle, String content, String status, String statusCode) {
        try {
            MsgAgentMapping mapping = new MsgAgentMapping();
            mapping.setAppKey(appKey);
            mapping.setXxbm(xxbm);
            mapping.setCenterMsgId(centerMsgId);
            mapping.setMsgTitle(msgTitle);
            mapping.setContent(content);
            mapping.setStatus(status);
            mapping.setStatusCode(statusCode);
            mapping.setSendTime(new Date());
            mapping.setPriority(3); // 默认中等优先级
            
            boolean result = this.save(mapping);
            if (result) {
                log.debug("创建消息映射成功: id={}, appKey={}, xxbm={}", 
                        mapping.getId(), appKey, xxbm);
                return mapping;
            } else {
                log.error("创建消息映射失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建消息映射失败", e);
            throw new RuntimeException("创建消息映射失败", e);
        }
    }
}