package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.agent.api.entity.MsgPollCursor;
import ltd.huntinginfo.feng.agent.mapper.MsgPollCursorMapper;
import ltd.huntinginfo.feng.agent.service.MsgPollCursorService;
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
public class MsgPollCursorServiceImpl extends ServiceImpl<MsgPollCursorMapper, MsgPollCursor> implements MsgPollCursorService {

    private final MsgPollCursorMapper msgPollCursorMapper;

    @Override
    public MsgPollCursor getById(String id) {
        try {
            MsgPollCursor result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的游标记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询游标详情失败: id={}", id, e);
            throw new RuntimeException("查询游标详情失败", e);
        }
    }

    @Override
    public IPage<MsgPollCursor> page(IPage<MsgPollCursor> page, MsgPollCursor msgPollCursor) {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = buildQueryWrapper(msgPollCursor);
            wrapper.orderByDesc(MsgPollCursor::getLastPollTime); // 默认按最后轮询时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询游标列表失败", e);
            throw new RuntimeException("分页查询游标列表失败", e);
        }
    }

    @Override
    public List<MsgPollCursor> list(MsgPollCursor msgPollCursor) {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = buildQueryWrapper(msgPollCursor);
            wrapper.orderByDesc(MsgPollCursor::getLastPollTime); // 默认按最后轮询时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询游标列表失败", e);
            throw new RuntimeException("查询游标列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgPollCursor msgPollCursor) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgPollCursor.getId())) {
                msgPollCursor.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgPollCursor.getCreateTime() == null) {
                msgPollCursor.setCreateTime(new Date());
            }
            
            // 验证应用标识和游标键是否已存在
            if (msgPollCursor.getAppKey() != null && msgPollCursor.getCursorKey() != null) {
                MsgPollCursor existingCursor = getByAppKeyAndCursorKey(
                    msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
                if (existingCursor != null) {
                    log.error("保存游标失败，应用标识和游标键组合已存在: appKey={}, cursorKey={}", 
                            msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
                    throw new RuntimeException("应用标识和游标键组合已存在");
                }
            }
            
            // 设置默认值
            if (msgPollCursor.getCursorKey() == null) {
                msgPollCursor.setCursorKey("DEFAULT");
            }
            
            if (msgPollCursor.getPollInterval() == null) {
                msgPollCursor.setPollInterval(10); // 默认10秒
            }
            
            if (msgPollCursor.getPollCount() == null) {
                msgPollCursor.setPollCount(0);
            }
            
            if (msgPollCursor.getMessageCount() == null) {
                msgPollCursor.setMessageCount(0);
            }
            
            if (msgPollCursor.getErrorCount() == null) {
                msgPollCursor.setErrorCount(0);
            }
            
            if (msgPollCursor.getStatus() == null) {
                msgPollCursor.setStatus(1);
            }
            
            boolean result = super.save(msgPollCursor);
            if (result) {
                log.debug("保存游标成功: id={}, appKey={}, cursorKey={}", 
                        msgPollCursor.getId(), msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
            } else {
                log.error("保存游标失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存游标失败", e);
            throw new RuntimeException("保存游标失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgPollCursor msgPollCursor) {
        try {
            // 验证游标是否存在
            MsgPollCursor existingCursor = super.getById(msgPollCursor.getId());
            if (existingCursor == null) {
                log.warn("更新游标失败，记录不存在: id={}", msgPollCursor.getId());
                return false;
            }
            
            // 如果修改了应用标识或游标键，需要检查是否与其他记录冲突
            if ((msgPollCursor.getAppKey() != null && !msgPollCursor.getAppKey().equals(existingCursor.getAppKey())) ||
                (msgPollCursor.getCursorKey() != null && !msgPollCursor.getCursorKey().equals(existingCursor.getCursorKey()))) {
                
                String appKey = msgPollCursor.getAppKey() != null ? msgPollCursor.getAppKey() : existingCursor.getAppKey();
                String cursorKey = msgPollCursor.getCursorKey() != null ? msgPollCursor.getCursorKey() : existingCursor.getCursorKey();
                
                MsgPollCursor duplicateCursor = getByAppKeyAndCursorKey(appKey, cursorKey);
                if (duplicateCursor != null && !duplicateCursor.getId().equals(msgPollCursor.getId())) {
                    log.error("更新游标失败，应用标识和游标键组合已存在: appKey={}, cursorKey={}", appKey, cursorKey);
                    throw new RuntimeException("应用标识和游标键组合已存在");
                }
            }
            
            boolean result = super.updateById(msgPollCursor);
            if (result) {
                log.debug("更新游标成功: id={}, appKey={}, cursorKey={}", 
                        msgPollCursor.getId(), msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
            } else {
                log.warn("更新游标失败: id={}", msgPollCursor.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新游标失败: id={}", msgPollCursor.getId(), e);
            throw new RuntimeException("更新游标失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgPollCursor existingCursor = super.getById(id);
            if (existingCursor == null) {
                log.warn("删除游标失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除游标成功: id={}, appKey={}, cursorKey={}", 
                        id, existingCursor.getAppKey(), existingCursor.getCursorKey());
            } else {
                log.error("删除游标失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除游标失败: id={}", id, e);
            throw new RuntimeException("删除游标失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgPollCursor> buildQueryWrapper(MsgPollCursor msgPollCursor) {
        LambdaQueryWrapper<MsgPollCursor> wrapper = new LambdaQueryWrapper<>();
        
        if (msgPollCursor != null) {
            // 按ID查询
            if (msgPollCursor.getId() != null && !msgPollCursor.getId().isEmpty()) {
                wrapper.eq(MsgPollCursor::getId, msgPollCursor.getId());
            }
            
            // 按应用标识查询
            if (msgPollCursor.getAppKey() != null && !msgPollCursor.getAppKey().isEmpty()) {
                wrapper.eq(MsgPollCursor::getAppKey, msgPollCursor.getAppKey());
            }
            
            // 按游标键查询
            if (msgPollCursor.getCursorKey() != null && !msgPollCursor.getCursorKey().isEmpty()) {
                wrapper.eq(MsgPollCursor::getCursorKey, msgPollCursor.getCursorKey());
            }
            
            // 按状态查询
            if (msgPollCursor.getStatus() != null) {
                wrapper.eq(MsgPollCursor::getStatus, msgPollCursor.getStatus());
            }
            
            // 按错误次数查询
            if (msgPollCursor.getErrorCount() != null) {
                wrapper.ge(MsgPollCursor::getErrorCount, msgPollCursor.getErrorCount());
            }
            
            // 按最后轮询时间范围查询
            // if (msgPollCursor.getLastPollTime() != null) {
            //     wrapper.ge(MsgPollCursor::getLastPollTime, startTime);
            //     wrapper.le(MsgPollCursor::getLastPollTime, endTime);
            // }
            
            // 按最后成功时间范围查询
            // if (msgPollCursor.getLastSuccessTime() != null) {
            //     wrapper.ge(MsgPollCursor::getLastSuccessTime, startTime);
            //     wrapper.le(MsgPollCursor::getLastSuccessTime, endTime);
            // }
        }
        
        return wrapper;
    }

    /**
     * 根据应用标识和游标键获取游标
     */
    public MsgPollCursor getByAppKeyAndCursorKey(String appKey, String cursorKey) {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据应用标识和游标键查询失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("查询游标失败", e);
        }
    }

    /**
     * 根据应用标识获取游标（使用默认游标键）
     */
    @Override
    public MsgPollCursor getByAppKey(String appKey) {
        return getByAppKeyAndCursorKey(appKey, "DEFAULT");
    }

    /**
     * 检查应用标识和游标键组合是否存在
     */
    public boolean existsAppKeyAndCursorKey(String appKey, String cursorKey) {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查应用标识和游标键组合是否存在失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("检查游标失败", e);
        }
    }

    /**
     * 获取或创建游标（如果不存在则创建）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgPollCursor getOrCreateCursor(String appKey, String cursorKey, Integer pollInterval) {
        try {
            MsgPollCursor cursor = getByAppKeyAndCursorKey(appKey, cursorKey);
            
            if (cursor == null) {
                // 创建新游标
                cursor = new MsgPollCursor();
                cursor.setId(IdUtil.fastSimpleUUID());
                cursor.setAppKey(appKey);
                cursor.setCursorKey(cursorKey);
                cursor.setPollInterval(pollInterval != null ? pollInterval : 10);
                cursor.setYbid(""); // 初始为空
                cursor.setStatus(1);
                
                boolean result = this.save(cursor);
                if (result) {
                    log.debug("创建游标成功: appKey={}, cursorKey={}", appKey, cursorKey);
                } else {
                    log.error("创建游标失败: appKey={}, cursorKey={}", appKey, cursorKey);
                    return null;
                }
            }
            
            return cursor;
        } catch (Exception e) {
            log.error("获取或创建游标失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("获取或创建游标失败", e);
        }
    }

    /**
     * 更新游标值
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateYbid(String appKey, String cursorKey, String ybid) {
        try {
            LambdaUpdateWrapper<MsgPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            wrapper.set(MsgPollCursor::getYbid, ybid);
            wrapper.set(MsgPollCursor::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新游标值成功: appKey={}, cursorKey={}, ybid={}...", 
                        appKey, cursorKey, ybid != null ? ybid.substring(0, Math.min(10, ybid.length())) : "");
            } else {
                log.warn("更新游标值失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新游标值失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("更新游标值失败", e);
        }
    }

    /**
     * 记录轮询成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordPollSuccess(String appKey, String cursorKey, String newYbid, int messageCount) {
        try {
            LambdaUpdateWrapper<MsgPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            
            // 设置游标值
            if (newYbid != null) {
                wrapper.set(MsgPollCursor::getYbid, newYbid);
            }
            
            // 更新统计信息
            wrapper.setSql("poll_count = poll_count + 1");
            wrapper.setSql("message_count = message_count + " + messageCount);
            wrapper.set(MsgPollCursor::getLastPollTime, new Date());
            wrapper.set(MsgPollCursor::getLastSuccessTime, new Date());
            wrapper.set(MsgPollCursor::getErrorCount, 0); // 重置错误计数
            wrapper.set(MsgPollCursor::getLastError, null); // 清除错误信息
            
            if (messageCount > 0) {
                wrapper.set(MsgPollCursor::getLastMessageTime, new Date());
            }
            
            wrapper.set(MsgPollCursor::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("记录轮询成功: appKey={}, cursorKey={}, messageCount={}", 
                        appKey, cursorKey, messageCount);
            } else {
                log.warn("记录轮询成功失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("记录轮询成功失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("记录轮询成功失败", e);
        }
    }

    /**
     * 记录轮询错误
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordPollError(String appKey, String cursorKey, String errorMessage) {
        try {
            LambdaUpdateWrapper<MsgPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            
            wrapper.setSql("poll_count = poll_count + 1");
            wrapper.setSql("error_count = error_count + 1");
            wrapper.set(MsgPollCursor::getLastPollTime, new Date());
            wrapper.set(MsgPollCursor::getLastError, errorMessage);
            wrapper.set(MsgPollCursor::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("记录轮询错误: appKey={}, cursorKey={}, error={}", 
                        appKey, cursorKey, errorMessage);
            } else {
                log.warn("记录轮询错误失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("记录轮询错误失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("记录轮询错误失败", e);
        }
    }

    /**
     * 更新游标状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCursorStatus(String appKey, String cursorKey, Integer status) {
        try {
            LambdaUpdateWrapper<MsgPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            wrapper.set(MsgPollCursor::getStatus, status);
            wrapper.set(MsgPollCursor::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新游标状态成功: appKey={}, cursorKey={}, status={}", 
                        appKey, cursorKey, status);
            } else {
                log.warn("更新游标状态失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新游标状态失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("更新游标状态失败", e);
        }
    }

    /**
     * 重置游标（清空游标值和错误信息）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetCursor(String appKey, String cursorKey) {
        try {
            LambdaUpdateWrapper<MsgPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getCursorKey, cursorKey);
            
            wrapper.set(MsgPollCursor::getYbid, "");
            wrapper.set(MsgPollCursor::getErrorCount, 0);
            wrapper.set(MsgPollCursor::getLastError, null);
            wrapper.set(MsgPollCursor::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("重置游标成功: appKey={}, cursorKey={}", appKey, cursorKey);
            } else {
                log.warn("重置游标失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("重置游标失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new RuntimeException("重置游标失败", e);
        }
    }

    /**
     * 获取所有运行中的游标列表
     */
    @Override
    public List<MsgPollCursor> getRunningCursors() {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgPollCursor::getStatus, 1); // 运行中
            wrapper.eq(MsgPollCursor::getDelFlag, "0"); // 未删除
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取运行中的游标列表失败", e);
            throw new RuntimeException("获取游标列表失败", e);
        }
    }

    /**
     * 获取应用的所有游标
     */
    public List<MsgPollCursor> getCursorsByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgPollCursor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgPollCursor::getAppKey, appKey);
            wrapper.eq(MsgPollCursor::getDelFlag, "0"); // 未删除
            wrapper.orderByAsc(MsgPollCursor::getCursorKey);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取应用游标列表失败: appKey={}", appKey, e);
            throw new RuntimeException("获取游标列表失败", e);
        }
    }

    /**
     * 检查游标是否需要轮询（根据最后轮询时间和轮询间隔）
     */
    public boolean needsPolling(String appKey, String cursorKey) {
        try {
            MsgPollCursor cursor = getByAppKeyAndCursorKey(appKey, cursorKey);
            if (cursor == null || cursor.getStatus() != 1) {
                return false;
            }
            
            // 如果从未轮询过，需要轮询
            if (cursor.getLastPollTime() == null) {
                return true;
            }
            
            // 计算距离上次轮询的时间（秒）
            long lastPollTime = cursor.getLastPollTime().getTime();
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - lastPollTime) / 1000;
            
            // 如果超过轮询间隔，需要轮询
            return elapsedSeconds >= cursor.getPollInterval();
        } catch (Exception e) {
            log.error("检查游标是否需要轮询失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            return false;
        }
    }

    /**
     * 检查游标是否因错误过多而需要暂停
     */
    public boolean shouldPauseDueToErrors(String appKey, String cursorKey, int maxErrorCount) {
        try {
            MsgPollCursor cursor = getByAppKeyAndCursorKey(appKey, cursorKey);
            if (cursor == null) {
                return false;
            }
            
            return cursor.getErrorCount() >= maxErrorCount;
        } catch (Exception e) {
            log.error("检查游标是否需要暂停失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            return true; // 发生异常时保守暂停
        }
    }
}