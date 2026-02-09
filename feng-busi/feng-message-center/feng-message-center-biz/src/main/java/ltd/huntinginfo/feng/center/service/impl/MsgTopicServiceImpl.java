package ltd.huntinginfo.feng.center.service.impl;

import ltd.huntinginfo.feng.center.api.entity.MsgTopic;
import ltd.huntinginfo.feng.center.mapper.MsgTopicMapper;
import ltd.huntinginfo.feng.center.service.MsgTopicService;
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
public class MsgTopicServiceImpl extends ServiceImpl<MsgTopicMapper, MsgTopic> implements MsgTopicService {

    private final MsgTopicMapper msgTopicMapper;

    @Override
    public MsgTopic getById(String id) {
        try {
            MsgTopic result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的主题记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询主题详情失败: id={}", id, e);
            throw new RuntimeException("查询主题详情失败", e);
        }
    }

    @Override
    public IPage<MsgTopic> page(IPage<MsgTopic> page, MsgTopic msgTopic) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = buildQueryWrapper(msgTopic);
            wrapper.orderByAsc(MsgTopic::getCode); // 按主题代码排序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询主题列表失败", e);
            throw new RuntimeException("分页查询主题列表失败", e);
        }
    }

    @Override
    public List<MsgTopic> list(MsgTopic msgTopic) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = buildQueryWrapper(msgTopic);
            wrapper.orderByAsc(MsgTopic::getCode); // 按主题代码排序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询主题列表失败", e);
            throw new RuntimeException("查询主题列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgTopic msgTopic) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgTopic.getId())) {
            	msgTopic.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgTopic.getCreateTime() == null) {
                msgTopic.setCreateTime(new Date());
            }
            
            // 验证主题代码是否已存在
            MsgTopic existingTopic = getByCode(msgTopic.getCode());
            if (existingTopic != null) {
                log.error("保存主题失败，主题代码已存在: code={}", msgTopic.getCode());
                throw new RuntimeException("主题代码已存在");
            }
            
            // 设置默认值
            if (msgTopic.getStatus() == null) {
                msgTopic.setStatus(1);
            }
            
            boolean result = super.save(msgTopic);
            if (result) {
                log.debug("保存主题成功: id={}, code={}, name={}", 
                        msgTopic.getId(), msgTopic.getCode(), msgTopic.getName());
            } else {
                log.error("保存主题失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存主题失败", e);
            throw new RuntimeException("保存主题失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgTopic msgTopic) {
        try {
            // 验证主题是否存在
            MsgTopic existingTopic = super.getById(msgTopic.getId());
            if (existingTopic == null) {
                log.warn("更新主题失败，记录不存在: id={}", msgTopic.getId());
                return false;
            }
            
            // 如果修改了主题代码，需要检查是否与其他记录冲突
            if (msgTopic.getCode() != null && 
                !msgTopic.getCode().equals(existingTopic.getCode())) {
                MsgTopic duplicateTopic = getByCode(msgTopic.getCode());
                if (duplicateTopic != null && !duplicateTopic.getId().equals(msgTopic.getId())) {
                    log.error("更新主题失败，主题代码已存在: code={}", msgTopic.getCode());
                    throw new RuntimeException("主题代码已存在");
                }
            }
            
            boolean result = super.updateById(msgTopic);
            if (result) {
                log.debug("更新主题成功: id={}, code={}", 
                        msgTopic.getId(), msgTopic.getCode());
            } else {
                log.warn("更新主题失败: id={}", msgTopic.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新主题失败: id={}", msgTopic.getId(), e);
            throw new RuntimeException("更新主题失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgTopic existingTopic = super.getById(id);
            if (existingTopic == null) {
                log.warn("删除主题失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除主题成功: id={}, code={}, name={}", 
                        id, existingTopic.getCode(), existingTopic.getName());
            } else {
                log.error("删除主题失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除主题失败: id={}", id, e);
            throw new RuntimeException("删除主题失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgTopic> buildQueryWrapper(MsgTopic msgTopic) {
        LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
        
        if (msgTopic != null) {
            // 按ID查询
            if (msgTopic.getId() != null && !msgTopic.getId().isEmpty()) {
                wrapper.eq(MsgTopic::getId, msgTopic.getId());
            }
            
            // 按主题代码查询
            if (msgTopic.getCode() != null && !msgTopic.getCode().isEmpty()) {
                wrapper.eq(MsgTopic::getCode, msgTopic.getCode());
            }
            
            // 按主题名称模糊查询
            if (msgTopic.getName() != null && !msgTopic.getName().isEmpty()) {
                wrapper.like(MsgTopic::getName, msgTopic.getName());
            }
            
            // 按描述模糊查询
            if (msgTopic.getDescription() != null && !msgTopic.getDescription().isEmpty()) {
                wrapper.like(MsgTopic::getDescription, msgTopic.getDescription());
            }
            
            // 按状态查询
            if (msgTopic.getStatus() != null) {
                wrapper.eq(MsgTopic::getStatus, msgTopic.getStatus());
            }
            
            // 按创建者查询
            if (msgTopic.getCreateBy() != null && !msgTopic.getCreateBy().isEmpty()) {
                wrapper.eq(MsgTopic::getCreateBy, msgTopic.getCreateBy());
            }
            
            // 按创建时间范围查询
            // if (msgTopic.getCreateTime() != null) {
            //     wrapper.ge(MsgTopic::getCreateTime, startTime);
            //     wrapper.le(MsgTopic::getCreateTime, endTime);
            // }
            
            // 按更新者查询
            if (msgTopic.getUpdateBy() != null && !msgTopic.getUpdateBy().isEmpty()) {
                wrapper.eq(MsgTopic::getUpdateBy, msgTopic.getUpdateBy());
            }
        }
        
        return wrapper;
    }

    /**
     * 根据主题代码获取主题
     */
    public MsgTopic getByCode(String code) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getCode, code);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据主题代码查询失败: code={}", code, e);
            throw new RuntimeException("查询主题失败", e);
        }
    }

    /**
     * 根据主题名称模糊查询
     */
    public List<MsgTopic> getByName(String name) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(MsgTopic::getName, name);
            wrapper.eq(MsgTopic::getStatus, 1); // 只查询启用的
            wrapper.orderByAsc(MsgTopic::getCode);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据主题名称查询失败: name={}", name, e);
            throw new RuntimeException("查询主题失败", e);
        }
    }

    /**
     * 检查主题代码是否存在
     */
    public boolean existsCode(String code) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getCode, code);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查主题代码是否存在失败: code={}", code, e);
            throw new RuntimeException("检查主题失败", e);
        }
    }

    /**
     * 获取所有启用的主题列表
     */
    public List<MsgTopic> getEnabledTopics() {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getStatus, 1); // 启用的
            wrapper.eq(MsgTopic::getDelFlag, "0"); // 未删除
            wrapper.orderByAsc(MsgTopic::getCode);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取启用的主题列表失败", e);
            throw new RuntimeException("获取主题列表失败", e);
        }
    }

    /**
     * 更新主题状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String id, Integer status) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getId, id);
            
            MsgTopic updateEntity = new MsgTopic();
            updateEntity.setStatus(status);
            updateEntity.setUpdateTime(new Date());
            
            boolean result = super.update(updateEntity, wrapper);
            if (result) {
                log.debug("更新主题状态成功: id={}, status={}", id, status);
            } else {
                log.warn("更新主题状态失败，记录不存在: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("更新主题状态失败: id={}", id, e);
            throw new RuntimeException("更新主题状态失败", e);
        }
    }

    /**
     * 批量更新主题状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<String> ids, Integer status) {
        try {
            if (ids == null || ids.isEmpty()) {
                log.warn("批量更新主题状态失败，ID列表为空");
                return false;
            }
            
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgTopic::getId, ids);
            
            MsgTopic updateEntity = new MsgTopic();
            updateEntity.setStatus(status);
            updateEntity.setUpdateTime(new Date());
            
            boolean result = super.update(updateEntity, wrapper);
            if (result) {
                log.debug("批量更新主题状态成功: count={}, status={}", ids.size(), status);
            } else {
                log.warn("批量更新主题状态失败");
            }
            return result;
        } catch (Exception e) {
            log.error("批量更新主题状态失败", e);
            throw new RuntimeException("批量更新主题状态失败", e);
        }
    }

    /**
     * 根据主题代码更新主题状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusByCode(String code, Integer status) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getCode, code);
            
            MsgTopic updateEntity = new MsgTopic();
            updateEntity.setStatus(status);
            updateEntity.setUpdateTime(new Date());
            
            boolean result = super.update(updateEntity, wrapper);
            if (result) {
                log.debug("根据代码更新主题状态成功: code={}, status={}", code, status);
            } else {
                log.warn("根据代码更新主题状态失败，记录不存在: code={}", code);
            }
            return result;
        } catch (Exception e) {
            log.error("根据代码更新主题状态失败: code={}", code, e);
            throw new RuntimeException("更新主题状态失败", e);
        }
    }

    /**
     * 检查主题是否启用
     */
    public boolean isTopicEnabled(String code) {
        try {
            MsgTopic topic = getByCode(code);
            return topic != null && topic.getStatus() == 1 && "0".equals(topic.getDelFlag());
        } catch (Exception e) {
            log.error("检查主题是否启用失败: code={}", code, e);
            return false;
        }
    }

    /**
     * 获取主题名称
     */
    public String getTopicName(String code) {
        try {
            MsgTopic topic = getByCode(code);
            return topic != null ? topic.getName() : null;
        } catch (Exception e) {
            log.error("获取主题名称失败: code={}", code, e);
            return null;
        }
    }

    /**
     * 获取主题描述
     */
    public String getTopicDescription(String code) {
        try {
            MsgTopic topic = getByCode(code);
            return topic != null ? topic.getDescription() : null;
        } catch (Exception e) {
            log.error("获取主题描述失败: code={}", code, e);
            return null;
        }
    }

    /**
     * 创建主题
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgTopic createTopic(String code, String name, String description, String createBy) {
        try {
            MsgTopic topic = new MsgTopic();
            topic.setCode(code);
            topic.setName(name);
            topic.setDescription(description);
            topic.setStatus(1);
            topic.setCreateBy(createBy);
            
            boolean result = this.save(topic);
            if (result) {
                log.debug("创建主题成功: id={}, code={}, name={}", 
                        topic.getId(), code, name);
                return topic;
            } else {
                log.error("创建主题失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建主题失败", e);
            throw new RuntimeException("创建主题失败", e);
        }
    }

    /**
     * 根据主题代码删除主题（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCode(String code) {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getCode, code);
            
            // 先查询是否存在
            MsgTopic existingTopic = super.getOne(wrapper);
            if (existingTopic == null) {
                log.warn("根据代码删除主题失败，记录不存在: code={}", code);
                return false;
            }
            
            // 逻辑删除
            MsgTopic deleteEntity = new MsgTopic();
            deleteEntity.setId(existingTopic.getId());
            deleteEntity.setDelFlag("1");
            deleteEntity.setUpdateTime(new Date());
            
            boolean result = super.updateById(deleteEntity);
            if (result) {
                log.debug("根据代码删除主题成功: code={}, name={}", code, existingTopic.getName());
            } else {
                log.error("根据代码删除主题失败: code={}", code);
            }
            return result;
        } catch (Exception e) {
            log.error("根据代码删除主题失败: code={}", code, e);
            throw new RuntimeException("删除主题失败", e);
        }
    }

    /**
     * 批量逻辑删除主题
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                log.warn("批量删除主题失败，ID列表为空");
                return false;
            }
            
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgTopic::getId, ids);
            
            MsgTopic updateEntity = new MsgTopic();
            updateEntity.setDelFlag("1");
            updateEntity.setUpdateTime(new Date());
            
            boolean result = super.update(updateEntity, wrapper);
            if (result) {
                log.debug("批量删除主题成功: count={}", ids.size());
            } else {
                log.warn("批量删除主题失败");
            }
            return result;
        } catch (Exception e) {
            log.error("批量删除主题失败", e);
            throw new RuntimeException("批量删除主题失败", e);
        }
    }

    /**
     * 根据主题代码列表查询主题
     */
    public List<MsgTopic> listByCodes(List<String> codes) {
        try {
            if (codes == null || codes.isEmpty()) {
                return List.of();
            }
            
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgTopic::getCode, codes);
            wrapper.eq(MsgTopic::getStatus, 1); // 启用的
            wrapper.eq(MsgTopic::getDelFlag, "0"); // 未删除
            wrapper.orderByAsc(MsgTopic::getCode);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据代码列表查询主题失败", e);
            throw new RuntimeException("查询主题失败", e);
        }
    }

    /**
     * 统计启用的主题数量
     */
    public long countEnabledTopics() {
        try {
            LambdaQueryWrapper<MsgTopic> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgTopic::getStatus, 1); // 启用的
            wrapper.eq(MsgTopic::getDelFlag, "0"); // 未删除
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计启用的主题数量失败", e);
            throw new RuntimeException("统计主题数量失败", e);
        }
    }

    /**
     * 验证主题代码格式（根据业务需求自定义）
     */
    public boolean validateTopicCode(String code) {
        try {
            if (code == null || code.isEmpty()) {
                return false;
            }
            
            // 示例验证规则：字母、数字、下划线、短横线组成，长度3-50
            String pattern = "^[a-zA-Z0-9_-]{3,50}$";
            return code.matches(pattern);
        } catch (Exception e) {
            log.error("验证主题代码格式失败: code={}", code, e);
            return false;
        }
    }

    /**
     * 获取主题的完整信息（代码 + 名称 + 描述）
     */
    public String getTopicFullInfo(String code) {
        try {
            MsgTopic topic = getByCode(code);
            if (topic != null && topic.getStatus() == 1 && "0".equals(topic.getDelFlag())) {
                if (topic.getDescription() != null && !topic.getDescription().isEmpty()) {
                    return String.format("%s (%s) - %s", topic.getCode(), topic.getName(), topic.getDescription());
                } else {
                    return String.format("%s (%s)", topic.getCode(), topic.getName());
                }
            }
            return null;
        } catch (Exception e) {
            log.error("获取主题完整信息失败: code={}", code, e);
            return null;
        }
    }
}