package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.agent.api.entity.MsgCenterConfig;
import ltd.huntinginfo.feng.agent.mapper.MsgCenterConfigMapper;
import ltd.huntinginfo.feng.agent.service.MsgCenterConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class MsgCenterConfigServiceImpl extends ServiceImpl<MsgCenterConfigMapper, MsgCenterConfig> implements MsgCenterConfigService {

    private final MsgCenterConfigMapper msgCenterConfigMapper;

    @Override
    public MsgCenterConfig getById(String id) {
        try {
            MsgCenterConfig result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的配置记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询配置详情失败: id={}", id, e);
            throw new RuntimeException("查询配置详情失败", e);
        }
    }

    @Override
    public IPage<MsgCenterConfig> page(IPage<MsgCenterConfig> page, MsgCenterConfig msgCenterConfig) {
        try {
            LambdaQueryWrapper<MsgCenterConfig> wrapper = buildQueryWrapper(msgCenterConfig);
            wrapper.orderByAsc(MsgCenterConfig::getConfigKey); // 按配置键排序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询配置列表失败", e);
            throw new RuntimeException("分页查询配置列表失败", e);
        }
    }

    @Override
    public List<MsgCenterConfig> list(MsgCenterConfig msgCenterConfig) {
        try {
            LambdaQueryWrapper<MsgCenterConfig> wrapper = buildQueryWrapper(msgCenterConfig);
            wrapper.orderByAsc(MsgCenterConfig::getConfigKey); // 按配置键排序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询配置列表失败", e);
            throw new RuntimeException("查询配置列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgCenterConfig msgCenterConfig) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgCenterConfig.getId())) {
            	msgCenterConfig.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgCenterConfig.getCreateTime() == null) {
                msgCenterConfig.setCreateTime(new Date());
            }
            
            // 验证配置键是否已存在
            MsgCenterConfig existingConfig = getByConfigKey(msgCenterConfig.getConfigKey());
            if (existingConfig != null) {
                log.error("保存配置失败，配置键已存在: configKey={}", msgCenterConfig.getConfigKey());
                throw new RuntimeException("配置键已存在");
            }
            
            // 设置默认值
            if (msgCenterConfig.getConfigType() == null) {
                msgCenterConfig.setConfigType("STRING");
            }
            
            if (msgCenterConfig.getCategory() == null) {
                msgCenterConfig.setCategory("COMMON");
            }
            
            if (msgCenterConfig.getStatus() == null) {
                msgCenterConfig.setStatus(1);
            }
            
            boolean result = super.save(msgCenterConfig);
            if (result) {
                log.debug("保存配置成功: id={}, configKey={}", 
                        msgCenterConfig.getId(), msgCenterConfig.getConfigKey());
            } else {
                log.error("保存配置失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存配置失败", e);
            throw new RuntimeException("保存配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgCenterConfig msgCenterConfig) {
        try {
            // 验证配置是否存在
            MsgCenterConfig existingConfig = super.getById(msgCenterConfig.getId());
            if (existingConfig == null) {
                log.warn("更新配置失败，记录不存在: id={}", msgCenterConfig.getId());
                return false;
            }
            
            // 如果修改了configKey，需要检查是否与其他记录冲突
            if (msgCenterConfig.getConfigKey() != null && 
                !msgCenterConfig.getConfigKey().equals(existingConfig.getConfigKey())) {
                MsgCenterConfig duplicateConfig = getByConfigKey(msgCenterConfig.getConfigKey());
                if (duplicateConfig != null && !duplicateConfig.getId().equals(msgCenterConfig.getId())) {
                    log.error("更新配置失败，配置键已存在: configKey={}", msgCenterConfig.getConfigKey());
                    throw new RuntimeException("配置键已存在");
                }
            }
            
            boolean result = super.updateById(msgCenterConfig);
            if (result) {
                log.debug("更新配置成功: id={}, configKey={}", 
                        msgCenterConfig.getId(), msgCenterConfig.getConfigKey());
            } else {
                log.warn("更新配置失败: id={}", msgCenterConfig.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新配置失败: id={}", msgCenterConfig.getId(), e);
            throw new RuntimeException("更新配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgCenterConfig existingConfig = super.getById(id);
            if (existingConfig == null) {
                log.warn("删除配置失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除配置成功: id={}, configKey={}", id, existingConfig.getConfigKey());
            } else {
                log.error("删除配置失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除配置失败: id={}", id, e);
            throw new RuntimeException("删除配置失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgCenterConfig> buildQueryWrapper(MsgCenterConfig msgCenterConfig) {
        LambdaQueryWrapper<MsgCenterConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (msgCenterConfig != null) {
            // 按ID查询
            if (msgCenterConfig.getId() != null && !msgCenterConfig.getId().isEmpty()) {
                wrapper.eq(MsgCenterConfig::getId, msgCenterConfig.getId());
            }
            
            // 按配置键查询（支持模糊查询）
            if (msgCenterConfig.getConfigKey() != null && !msgCenterConfig.getConfigKey().isEmpty()) {
                wrapper.like(MsgCenterConfig::getConfigKey, msgCenterConfig.getConfigKey());
            }
            
            // 按配置值模糊查询
            if (msgCenterConfig.getConfigValue() != null && !msgCenterConfig.getConfigValue().isEmpty()) {
                wrapper.like(MsgCenterConfig::getConfigValue, msgCenterConfig.getConfigValue());
            }
            
            // 按配置类型查询
            if (msgCenterConfig.getConfigType() != null && !msgCenterConfig.getConfigType().isEmpty()) {
                wrapper.eq(MsgCenterConfig::getConfigType, msgCenterConfig.getConfigType());
            }
            
            // 按配置类别查询
            if (msgCenterConfig.getCategory() != null && !msgCenterConfig.getCategory().isEmpty()) {
                wrapper.eq(MsgCenterConfig::getCategory, msgCenterConfig.getCategory());
            }
            
            // 按状态查询
            if (msgCenterConfig.getStatus() != null) {
                wrapper.eq(MsgCenterConfig::getStatus, msgCenterConfig.getStatus());
            }
            
            // 按描述模糊查询
            if (msgCenterConfig.getConfigDesc() != null && !msgCenterConfig.getConfigDesc().isEmpty()) {
                wrapper.like(MsgCenterConfig::getConfigDesc, msgCenterConfig.getConfigDesc());
            }
        }
        
        return wrapper;
    }

    /**
     * 根据配置键获取配置值
     */
    @Override
    public String getConfigValue(String configKey) {
        try {
            MsgCenterConfig config = getByConfigKey(configKey);
            if (config != null && config.getStatus() == 1) {
                return config.getConfigValue();
            }
            return null;
        } catch (Exception e) {
            log.error("获取配置值失败: configKey={}", configKey, e);
            throw new RuntimeException("获取配置值失败", e);
        }
    }

    /**
     * 根据配置键获取配置对象
     */
    public MsgCenterConfig getByConfigKey(String configKey) {
        try {
            LambdaQueryWrapper<MsgCenterConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterConfig::getConfigKey, configKey);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据配置键查询失败: configKey={}", configKey, e);
            throw new RuntimeException("查询配置失败", e);
        }
    }

    /**
     * 根据类别获取配置列表
     */
    public List<MsgCenterConfig> listByCategory(String category) {
        try {
            LambdaQueryWrapper<MsgCenterConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterConfig::getCategory, category);
            wrapper.eq(MsgCenterConfig::getStatus, 1); // 只查询启用的配置
            wrapper.orderByAsc(MsgCenterConfig::getConfigKey);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据类别查询配置失败: category={}", category, e);
            throw new RuntimeException("查询配置失败", e);
        }
    }

    /**
     * 检查配置键是否存在
     */
    public boolean existsConfigKey(String configKey) {
        try {
            LambdaQueryWrapper<MsgCenterConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterConfig::getConfigKey, configKey);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查配置键是否存在失败: configKey={}", configKey, e);
            throw new RuntimeException("检查配置键失败", e);
        }
    }

    /**
     * 获取部级消息中心基础地址
     */
    public String getCenterBaseUrl() {
        return getConfigValue("center.base.url");
    }

    /**
     * 获取部级发号中心基础地址
     */
    public String getFhBaseUrl() {
        return getConfigValue("center.fh.base.url");
    }

    /**
     * 获取消息发送服务接口路径
     */
    public String getMessageSendApi() {
        return getConfigValue("api.message.send");
    }

    /**
     * 获取消息接收服务接口路径
     */
    public String getMessageReceiveApi() {
        return getConfigValue("api.message.receive");
    }

    /**
     * 获取消息编码申请接口路径
     */
    public String getCodeApplyApi() {
        return getConfigValue("api.code.apply");
    }

    /**
     * 获取消息状态更新接口路径
     */
    public String getMessageStatusUpdateApi() {
        return getConfigValue("api.message.status.update");
    }

    /**
     * 获取查询未读消息接口路径
     */
    public String getUnreadMessageApi() {
        return getConfigValue("api.message.unread");
    }

    /**
     * 是否启用桩代码
     */
    @Override
    public boolean isMockEnabled() {
        String mockEnabled = getConfigValue("enable.mock");
        return "true".equalsIgnoreCase(mockEnabled);
    }

    /**
     * 获取Token过期缓冲时间
     */
    public int getTokenExpireBuffer() {
        try {
            String buffer = getConfigValue("token.expire.buffer");
            return buffer != null ? Integer.parseInt(buffer) : 300;
        } catch (Exception e) {
            log.warn("获取Token过期缓冲时间失败，使用默认值300秒");
            return 300;
        }
    }

    /**
     * 获取最小轮询间隔
     */
    public int getPollIntervalMin() {
        try {
            String interval = getConfigValue("poll.interval.min");
            return interval != null ? Integer.parseInt(interval) : 10;
        } catch (Exception e) {
            log.warn("获取最小轮询间隔失败，使用默认值10秒");
            return 10;
        }
    }

    /**
     * 获取最大重试次数
     */
    public int getMaxRetryCount() {
        try {
            String retry = getConfigValue("retry.max.count");
            return retry != null ? Integer.parseInt(retry) : 3;
        } catch (Exception e) {
            log.warn("获取最大重试次数失败，使用默认值3");
            return 3;
        }
    }

    /**
     * 获取回调超时时间
     */
    public int getCallbackTimeout() {
        try {
            String timeout = getConfigValue("callback.timeout");
            return timeout != null ? Integer.parseInt(timeout) : 5000;
        } catch (Exception e) {
            log.warn("获取回调超时时间失败，使用默认值5000毫秒");
            return 5000;
        }
    }
}