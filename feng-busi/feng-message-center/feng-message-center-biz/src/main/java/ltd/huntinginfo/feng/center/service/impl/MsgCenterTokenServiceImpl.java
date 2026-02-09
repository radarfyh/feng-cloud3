package ltd.huntinginfo.feng.center.service.impl;

import ltd.huntinginfo.feng.center.api.entity.MsgCenterToken;
import ltd.huntinginfo.feng.center.mapper.MsgCenterTokenMapper;
import ltd.huntinginfo.feng.center.service.MsgCenterTokenService;
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
public class MsgCenterTokenServiceImpl extends ServiceImpl<MsgCenterTokenMapper, MsgCenterToken> implements MsgCenterTokenService {

    private final MsgCenterTokenMapper msgCenterTokenMapper;

    @Override
    public MsgCenterToken getById(String id) {
        try {
            MsgCenterToken result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的Token记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询Token详情失败: id={}", id, e);
            throw new RuntimeException("查询Token详情失败", e);
        }
    }

    @Override
    public IPage<MsgCenterToken> page(IPage<MsgCenterToken> page, MsgCenterToken msgCenterToken) {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = buildQueryWrapper(msgCenterToken);
            wrapper.orderByDesc(MsgCenterToken::getCreateTime); // 默认按创建时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询Token列表失败", e);
            throw new RuntimeException("分页查询Token列表失败", e);
        }
    }

    @Override
    public List<MsgCenterToken> list(MsgCenterToken msgCenterToken) {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = buildQueryWrapper(msgCenterToken);
            wrapper.orderByDesc(MsgCenterToken::getCreateTime); // 默认按创建时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询Token列表失败", e);
            throw new RuntimeException("查询Token列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgCenterToken msgCenterToken) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgCenterToken.getId())) {
            	msgCenterToken.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgCenterToken.getCreateTime() == null) {
                msgCenterToken.setCreateTime(new Date());
            }
            
            // 验证应用标识是否已存在
            MsgCenterToken existingToken = getByAppKey(msgCenterToken.getAppKey());
            if (existingToken != null) {
                log.error("保存Token失败，应用标识已存在: appKey={}", msgCenterToken.getAppKey());
                throw new RuntimeException("应用标识已存在");
            }
            
            // 设置默认值
            if (msgCenterToken.getTokenType() == null) {
                msgCenterToken.setTokenType("BEARER");
            }
            
            if (msgCenterToken.getRefreshCount() == null) {
                msgCenterToken.setRefreshCount(0L);
            }
            
            if (msgCenterToken.getTotalRequests() == null) {
                msgCenterToken.setTotalRequests(0);
            }
            
            if (msgCenterToken.getSuccessRequests() == null) {
                msgCenterToken.setSuccessRequests(0);
            }
            
            if (msgCenterToken.getStatus() == null) {
                msgCenterToken.setStatus(1);
            }
            
            boolean result = super.save(msgCenterToken);
            if (result) {
                log.debug("保存Token成功: id={}, appKey={}", 
                        msgCenterToken.getId(), msgCenterToken.getAppKey());
            } else {
                log.error("保存Token失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存Token失败", e);
            throw new RuntimeException("保存Token失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgCenterToken msgCenterToken) {
        try {
            // 验证Token是否存在
            MsgCenterToken existingToken = super.getById(msgCenterToken.getId());
            if (existingToken == null) {
                log.warn("更新Token失败，记录不存在: id={}", msgCenterToken.getId());
                return false;
            }
            
            // 如果修改了appKey，需要检查是否与其他记录冲突
            if (msgCenterToken.getAppKey() != null && 
                !msgCenterToken.getAppKey().equals(existingToken.getAppKey())) {
                MsgCenterToken duplicateToken = getByAppKey(msgCenterToken.getAppKey());
                if (duplicateToken != null && !duplicateToken.getId().equals(msgCenterToken.getId())) {
                    log.error("更新Token失败，应用标识已存在: appKey={}", msgCenterToken.getAppKey());
                    throw new RuntimeException("应用标识已存在");
                }
            }
            
            boolean result = super.updateById(msgCenterToken);
            if (result) {
                log.debug("更新Token成功: id={}, appKey={}", 
                        msgCenterToken.getId(), msgCenterToken.getAppKey());
            } else {
                log.warn("更新Token失败: id={}", msgCenterToken.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新Token失败: id={}", msgCenterToken.getId(), e);
            throw new RuntimeException("更新Token失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgCenterToken existingToken = super.getById(id);
            if (existingToken == null) {
                log.warn("删除Token失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除Token成功: id={}, appKey={}", id, existingToken.getAppKey());
            } else {
                log.error("删除Token失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除Token失败: id={}", id, e);
            throw new RuntimeException("删除Token失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgCenterToken> buildQueryWrapper(MsgCenterToken msgCenterToken) {
        LambdaQueryWrapper<MsgCenterToken> wrapper = new LambdaQueryWrapper<>();
        
        if (msgCenterToken != null) {
            // 按ID查询
            if (msgCenterToken.getId() != null && !msgCenterToken.getId().isEmpty()) {
                wrapper.eq(MsgCenterToken::getId, msgCenterToken.getId());
            }
            
            // 按应用标识查询
            if (msgCenterToken.getAppKey() != null && !msgCenterToken.getAppKey().isEmpty()) {
                wrapper.eq(MsgCenterToken::getAppKey, msgCenterToken.getAppKey());
            }
            
            // 按Token类型查询
            if (msgCenterToken.getTokenType() != null && !msgCenterToken.getTokenType().isEmpty()) {
                wrapper.eq(MsgCenterToken::getTokenType, msgCenterToken.getTokenType());
            }
            
            // 按状态查询
            if (msgCenterToken.getStatus() != null) {
                wrapper.eq(MsgCenterToken::getStatus, msgCenterToken.getStatus());
            }
            
            // 按过期时间范围查询
            if (msgCenterToken.getExpireTime() != null) {
                // 查询即将过期的Token
                if (msgCenterToken.getExpireTime().before(new Date())) {
                    wrapper.lt(MsgCenterToken::getExpireTime, new Date()); // 已过期
                } else {
                    wrapper.gt(MsgCenterToken::getExpireTime, new Date()); // 未过期
                }
            }
            
            // 按上次请求时间范围查询
            // if (msgCenterToken.getLastRequestTime() != null) {
            //     wrapper.ge(MsgCenterToken::getLastRequestTime, startTime);
            //     wrapper.le(MsgCenterToken::getLastRequestTime, endTime);
            // }
        }
        
        return wrapper;
    }

    /**
     * 根据应用标识获取Token
     */
    @Override
    public MsgCenterToken getByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据应用标识查询Token失败: appKey={}", appKey, e);
            throw new RuntimeException("查询Token失败", e);
        }
    }

    /**
     * 检查应用标识是否存在
     */
    public boolean existsAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查应用标识是否存在失败: appKey={}", appKey, e);
            throw new RuntimeException("检查应用标识失败", e);
        }
    }

    /**
     * 更新Token信息
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateToken(String appKey, String centerToken, Date expireTime) {
        try {
            MsgCenterToken token = getByAppKey(appKey);
            if (token == null) {
                log.warn("更新Token失败，记录不存在: appKey={}", appKey);
                return false;
            }
            
            LambdaUpdateWrapper<MsgCenterToken> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            wrapper.set(MsgCenterToken::getCenterToken, centerToken);
            wrapper.set(MsgCenterToken::getExpireTime, expireTime);
            wrapper.set(MsgCenterToken::getStatus, 1); // 设置为有效
            wrapper.set(MsgCenterToken::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新Token成功: appKey={}", appKey);
            } else {
                log.warn("更新Token失败: appKey={}", appKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新Token失败: appKey={}", appKey, e);
            throw new RuntimeException("更新Token失败", e);
        }
    }

    /**
     * 更新刷新次数
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRefreshCount(String appKey) {
        try {
            MsgCenterToken token = getByAppKey(appKey);
            if (token == null) {
                log.warn("更新刷新次数失败，记录不存在: appKey={}", appKey);
                return false;
            }
            
            LambdaUpdateWrapper<MsgCenterToken> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            wrapper.set(MsgCenterToken::getRefreshCount, token.getRefreshCount() + 1);
            wrapper.set(MsgCenterToken::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新刷新次数成功: appKey={}, refreshCount={}", 
                        appKey, token.getRefreshCount() + 1);
            } else {
                log.warn("更新刷新次数失败: appKey={}", appKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新刷新次数失败: appKey={}", appKey, e);
            throw new RuntimeException("更新刷新次数失败", e);
        }
    }

    /**
     * 记录请求统计
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordRequest(String appKey, String apiName, boolean success) {
        try {
            MsgCenterToken token = getByAppKey(appKey);
            if (token == null) {
                log.warn("记录请求统计失败，记录不存在: appKey={}", appKey);
                return false;
            }
            
            LambdaUpdateWrapper<MsgCenterToken> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            wrapper.set(MsgCenterToken::getTotalRequests, token.getTotalRequests() + 1);
            
            if (success) {
                wrapper.set(MsgCenterToken::getSuccessRequests, token.getSuccessRequests() + 1);
            }
            
            wrapper.set(MsgCenterToken::getLastRequestTime, new Date());
            wrapper.set(MsgCenterToken::getLastRequestApi, apiName);
            wrapper.set(MsgCenterToken::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result && success) {
                log.debug("记录请求成功: appKey={}, api={}", appKey, apiName);
            }
            return result;
        } catch (Exception e) {
            log.error("记录请求统计失败: appKey={}, api={}", appKey, apiName, e);
            throw new RuntimeException("记录请求统计失败", e);
        }
    }

    /**
     * 更新Token状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String appKey, Integer status) {
        try {
            LambdaUpdateWrapper<MsgCenterToken> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgCenterToken::getAppKey, appKey);
            wrapper.set(MsgCenterToken::getStatus, status);
            wrapper.set(MsgCenterToken::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新Token状态成功: appKey={}, status={}", appKey, status);
            } else {
                log.warn("更新Token状态失败，记录不存在: appKey={}", appKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新Token状态失败: appKey={}", appKey, e);
            throw new RuntimeException("更新Token状态失败", e);
        }
    }

    /**
     * 检查Token是否有效（未过期且状态为有效）
     */
    @Override
    public boolean isTokenValid(String appKey) {
        try {
            MsgCenterToken token = getByAppKey(appKey);
            if (token == null) {
                return false;
            }
            
            // 检查状态
            if (token.getStatus() != 1) {
                return false;
            }
            
            // 检查过期时间
            if (token.getExpireTime() == null || token.getExpireTime().before(new Date())) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("检查Token有效性失败: appKey={}", appKey, e);
            return false;
        }
    }

    /**
     * 检查Token是否即将过期（在指定分钟内过期）
     */
    public boolean isTokenExpiringSoon(String appKey, int minutes) {
        try {
            MsgCenterToken token = getByAppKey(appKey);
            if (token == null) {
                return false;
            }
            
            if (token.getExpireTime() == null) {
                return false;
            }
            
            // 计算过期时间
            long expireTime = token.getExpireTime().getTime();
            long currentTime = System.currentTimeMillis();
            long bufferTime = minutes * 60 * 1000L;
            
            // 如果过期时间在当前时间之后，但在指定分钟之内，则认为即将过期
            return expireTime > currentTime && expireTime - currentTime <= bufferTime;
        } catch (Exception e) {
            log.error("检查Token是否即将过期失败: appKey={}", appKey, e);
            return false;
        }
    }

    /**
     * 获取过期的Token列表
     */
    public List<MsgCenterToken> getExpiredTokens() {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterToken::getStatus, 1); // 只查询有效的Token
            wrapper.lt(MsgCenterToken::getExpireTime, new Date()); // 过期时间小于当前时间
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取过期Token列表失败", e);
            throw new RuntimeException("获取过期Token列表失败", e);
        }
    }

    /**
     * 获取所有有效的Token列表
     */
    public List<MsgCenterToken> getValidTokens() {
        try {
            LambdaQueryWrapper<MsgCenterToken> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgCenterToken::getStatus, 1); // 有效的
            wrapper.gt(MsgCenterToken::getExpireTime, new Date()); // 未过期的
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取有效Token列表失败", e);
            throw new RuntimeException("获取有效Token列表失败", e);
        }
    }

    /**
     * 创建或更新Token记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgCenterToken createOrUpdateToken(String appKey, String centerToken, Date expireTime) {
        try {
            MsgCenterToken existingToken = getByAppKey(appKey);
            
            if (existingToken != null) {
                // 更新现有记录
                existingToken.setCenterToken(centerToken);
                existingToken.setExpireTime(expireTime);
                existingToken.setRefreshCount(existingToken.getRefreshCount() + 1);
                existingToken.setStatus(1);
                
                boolean result = this.updateById(existingToken);
                if (result) {
                    log.debug("更新Token记录成功: appKey={}", appKey);
                    return existingToken;
                }
            } else {
                // 创建新记录
                MsgCenterToken newToken = new MsgCenterToken();
                newToken.setAppKey(appKey);
                newToken.setCenterToken(centerToken);
                newToken.setTokenType("BEARER");
                newToken.setExpireTime(expireTime);
                newToken.setRefreshCount(0L);
                newToken.setTotalRequests(0);
                newToken.setSuccessRequests(0);
                newToken.setStatus(1);
                
                boolean result = this.save(newToken);
                if (result) {
                    log.debug("创建Token记录成功: appKey={}", appKey);
                    return newToken;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("创建或更新Token记录失败: appKey={}", appKey, e);
            throw new RuntimeException("创建或更新Token记录失败", e);
        }
    }
}