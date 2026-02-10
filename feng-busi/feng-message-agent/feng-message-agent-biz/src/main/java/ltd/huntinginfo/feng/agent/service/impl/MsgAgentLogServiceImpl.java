package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.agent.api.entity.MsgAgentLog;
import ltd.huntinginfo.feng.agent.mapper.MsgAgentLogMapper;
import ltd.huntinginfo.feng.agent.service.MsgAgentLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MsgAgentLogServiceImpl extends ServiceImpl<MsgAgentLogMapper, MsgAgentLog> implements MsgAgentLogService {

    private final MsgAgentLogMapper msgAgentLogMapper;

    @Override
    public MsgAgentLog getById(String id) {
        try {
            MsgAgentLog result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的日志记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询日志详情失败: id={}", id, e);
            throw new RuntimeException("查询日志详情失败", e);
        }
    }

    @Override
    public IPage<MsgAgentLog> page(IPage<MsgAgentLog> page, MsgAgentLog msgAgentLog) {
        try {
            LambdaQueryWrapper<MsgAgentLog> wrapper = buildQueryWrapper(msgAgentLog);
            wrapper.orderByDesc(MsgAgentLog::getCreateTime); // 默认按创建时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询日志列表失败", e);
            throw new RuntimeException("分页查询日志列表失败", e);
        }
    }

    @Override
    public List<MsgAgentLog> list(MsgAgentLog msgAgentLog) {
        try {
            LambdaQueryWrapper<MsgAgentLog> wrapper = buildQueryWrapper(msgAgentLog);
            wrapper.orderByDesc(MsgAgentLog::getCreateTime); // 默认按创建时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询日志列表失败", e);
            throw new RuntimeException("查询日志列表失败", e);
        }
    }

    @Override
    public boolean save(MsgAgentLog msgAgentLog) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgAgentLog.getId())) {
                msgAgentLog.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgAgentLog.getCreateTime() == null) {
                msgAgentLog.setCreateTime(new Date());
            }
            
            // 设置日志级别（默认为INFO）
            if (msgAgentLog.getLogLevel() == null) {
                msgAgentLog.setLogLevel("INFO");
            }
            
            boolean result = super.save(msgAgentLog);
            if (result) {
                log.debug("保存日志成功: id={}, logType={}", msgAgentLog.getId(), msgAgentLog.getLogType());
            } else {
                log.error("保存日志失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存日志失败", e);
            throw new RuntimeException("保存日志失败", e);
        }
    }

    @Override
    public boolean updateById(MsgAgentLog msgAgentLog) {
        try {
            boolean result = super.updateById(msgAgentLog);
            if (result) {
                log.debug("更新日志成功: id={}", msgAgentLog.getId());
            } else {
                log.warn("更新日志失败，记录不存在: id={}", msgAgentLog.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新日志失败: id={}", msgAgentLog.getId(), e);
            throw new RuntimeException("更新日志失败", e);
        }
    }

    @Override
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgAgentLog existLog = super.getById(id);
            if (existLog == null) {
                log.warn("删除日志失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除日志成功: id={}, msgId={}", id, existLog.getMsgId());
            } else {
                log.error("删除日志失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除日志失败: id={}", id, e);
            throw new RuntimeException("删除日志失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgAgentLog> buildQueryWrapper(MsgAgentLog msgAgentLog) {
        LambdaQueryWrapper<MsgAgentLog> wrapper = new LambdaQueryWrapper<>();
        
        if (msgAgentLog != null) {
            // 按代理平台消息ID查询
            if (msgAgentLog.getMsgId() != null && !msgAgentLog.getMsgId().isEmpty()) {
                wrapper.eq(MsgAgentLog::getMsgId, msgAgentLog.getMsgId());
            }
            
            // 按应用标识查询
            if (msgAgentLog.getAppKey() != null && !msgAgentLog.getAppKey().isEmpty()) {
                wrapper.eq(MsgAgentLog::getAppKey, msgAgentLog.getAppKey());
            }
            
            // 按消息ID查询
            if (msgAgentLog.getMsgId() != null && !msgAgentLog.getMsgId().isEmpty()) {
                wrapper.eq(MsgAgentLog::getMsgId, msgAgentLog.getMsgId());
            }
            
            // 按日志类型查询
            if (msgAgentLog.getLogType() != null && !msgAgentLog.getLogType().isEmpty()) {
                wrapper.eq(MsgAgentLog::getLogType, msgAgentLog.getLogType());
            }
            
            // 按日志级别查询
            if (msgAgentLog.getLogLevel() != null && !msgAgentLog.getLogLevel().isEmpty()) {
                wrapper.eq(MsgAgentLog::getLogLevel, msgAgentLog.getLogLevel());
            }
            
            // 按操作名称模糊查询
            if (msgAgentLog.getOperation() != null && !msgAgentLog.getOperation().isEmpty()) {
                wrapper.like(MsgAgentLog::getOperation, msgAgentLog.getOperation());
            }
            
            // 按API地址查询
            if (msgAgentLog.getApiUrl() != null && !msgAgentLog.getApiUrl().isEmpty()) {
                wrapper.like(MsgAgentLog::getApiUrl, msgAgentLog.getApiUrl());
            }
            
            // 按HTTP状态码查询
            if (msgAgentLog.getHttpStatus() != null) {
                wrapper.eq(MsgAgentLog::getHttpStatus, msgAgentLog.getHttpStatus());
            }
            
            // 按日志内容模糊查询
            if (msgAgentLog.getLogContent() != null && !msgAgentLog.getLogContent().isEmpty()) {
                wrapper.like(MsgAgentLog::getLogContent, msgAgentLog.getLogContent());
            }
            
            // 按创建时间范围查询（需要扩展功能时使用）
            // if (msgAgentLog.getCreateTime() != null) {
            //     wrapper.ge(MsgAgentLog::getCreateTime, startTime);
            //     wrapper.le(MsgAgentLog::getCreateTime, endTime);
            // }
        }
        
        return wrapper;
    }

    /**
     * 记录发送日志
     */
    @Override
    public void logSend(String msgId, String appKey, String operation, String content, Object detail) {
        try {
            MsgAgentLog logRecord = new MsgAgentLog();
            logRecord.setAppKey(appKey);
            logRecord.setMsgId(msgId);
            logRecord.setLogType("SEND");
            logRecord.setLogLevel("INFO");
            logRecord.setOperation(operation);
            logRecord.setLogContent(content);
            logRecord.setLogDetail(detail);
            
            this.save(logRecord);
        } catch (Exception e) {
            log.error("记录发送日志失败: msgId={}, appKey={}", msgId, appKey, e);
        }
    }

    /**
     * 记录回调日志
     */
    @Override
    public void logCallback(String msgId, String appKey, String operation, String content, 
                           String apiUrl, String httpMethod, Integer httpStatus, Integer responseTime, Object detail) {
        try {
            MsgAgentLog logRecord = new MsgAgentLog();
            logRecord.setAppKey(appKey);
            logRecord.setMsgId(msgId);
            logRecord.setLogType("CALLBACK");
            logRecord.setLogLevel("INFO");
            logRecord.setOperation(operation);
            logRecord.setLogContent(content);
            logRecord.setApiUrl(apiUrl);
            logRecord.setHttpMethod(httpMethod);
            logRecord.setHttpStatus(httpStatus);
            logRecord.setResponseTime(responseTime);
            logRecord.setLogDetail(detail);
            
            this.save(logRecord);
        } catch (Exception e) {
            log.error("记录回调日志失败: msgId={}, appKey={}", msgId, appKey, e);
        }
    }

    /**
     * 记录错误日志
     */
    @Override
    public void logError(String msgId, String appKey, String operation, String content, Object detail) {
        try {
            MsgAgentLog logRecord = new MsgAgentLog();
            logRecord.setAppKey(appKey);
            logRecord.setMsgId(msgId);
            logRecord.setLogType("ERROR");
            logRecord.setLogLevel("ERROR");
            logRecord.setOperation(operation);
            logRecord.setLogContent(content);
            logRecord.setLogDetail(detail);
            
            this.save(logRecord);
        } catch (Exception e) {
            log.error("记录错误日志失败: msgId={}, appKey={}", msgId, appKey, e);
        }
    }

    /**
     * 记录轮询日志
     */
    @Override
    public void logPoll(String msgId, String appKey, String operation, String content, Object detail) {
        try {
            MsgAgentLog logRecord = new MsgAgentLog();
            logRecord.setAppKey(appKey);
            logRecord.setMsgId(msgId);
            logRecord.setLogType("POLL");
            logRecord.setLogLevel("INFO");
            logRecord.setOperation(operation);
            logRecord.setLogContent(content);
            logRecord.setLogDetail(detail);
            
            this.save(logRecord);
        } catch (Exception e) {
            log.error("记录轮询日志失败: msgId={}, appKey={}", msgId, appKey, e);
        }
    }

    /**
     * 记录状态更新日志
     */
    @Override
    public void logStatus(String msgId, String appKey, String operation, 
                         String oldStatus, String newStatus, String content) {
        try {
            MsgAgentLog logRecord = new MsgAgentLog();
            logRecord.setAppKey(appKey);
            logRecord.setMsgId(msgId);
            logRecord.setLogType("STATUS");
            logRecord.setLogLevel("INFO");
            logRecord.setOperation(operation);
            logRecord.setLogContent(String.format("%s [%s -> %s]", content, oldStatus, newStatus));
            
            this.save(logRecord);
        } catch (Exception e) {
            log.error("记录状态更新日志失败: msgId={}, appKey={}", msgId, appKey, e);
        }
    }

    /**
     * 根据代理平台消息ID查询日志列表
     */
    public List<MsgAgentLog> listByMsgId(String msgId) {
        try {
            LambdaQueryWrapper<MsgAgentLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentLog::getMsgId, msgId);
            wrapper.orderByAsc(MsgAgentLog::getCreateTime); // 按时间正序，方便查看处理流程
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据代理消息ID查询日志失败: msgId={}", msgId, e);
            throw new RuntimeException("查询日志失败", e);
        }
    }

    /**
     * 根据应用标识查询日志列表
     */
    public List<MsgAgentLog> listByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgAgentLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgAgentLog::getAppKey, appKey);
            wrapper.orderByDesc(MsgAgentLog::getCreateTime); // 按时间倒序，查看最新的日志
            wrapper.last("LIMIT 100"); // 限制返回数量，防止数据过多
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据应用标识查询日志失败: appKey={}", appKey, e);
            throw new RuntimeException("查询日志失败", e);
        }
    }

    /**
     * 清理指定时间之前的日志（用于定时任务）
     */
    public Long cleanExpiredLogs(Date expireDate) {
        try {
            LambdaQueryWrapper<MsgAgentLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(MsgAgentLog::getCreateTime, expireDate);
            
            Long count = this.count(wrapper);
            if (count > 0) {
                boolean result = this.remove(wrapper);
                if (result) {
                    log.info("清理过期日志成功: 清理条数={}, 过期时间={}", count, expireDate);
                    return count;
                }
            }
            return 0L;
        } catch (Exception e) {
            log.error("清理过期日志失败", e);
            throw new RuntimeException("清理日志失败", e);
        }
    }
}