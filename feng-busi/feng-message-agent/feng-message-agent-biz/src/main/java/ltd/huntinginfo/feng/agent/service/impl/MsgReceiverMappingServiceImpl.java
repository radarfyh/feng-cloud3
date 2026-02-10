package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.agent.api.entity.MsgReceiverMapping;
import ltd.huntinginfo.feng.agent.mapper.MsgReceiverMappingMapper;
import ltd.huntinginfo.feng.agent.service.MsgReceiverMappingService;
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
public class MsgReceiverMappingServiceImpl extends ServiceImpl<MsgReceiverMappingMapper, MsgReceiverMapping> implements MsgReceiverMappingService {

    private final MsgReceiverMappingMapper msgReceiverMappingMapper;

    @Override
    public MsgReceiverMapping getById(String id) {
        try {
            MsgReceiverMapping result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的接收者映射记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询接收者映射详情失败: id={}", id, e);
            throw new RuntimeException("查询接收者映射详情失败", e);
        }
    }

    @Override
    public IPage<MsgReceiverMapping> page(IPage<MsgReceiverMapping> page, MsgReceiverMapping msgReceiverMapping) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = buildQueryWrapper(msgReceiverMapping);
            wrapper.orderByDesc(MsgReceiverMapping::getCreateTime); // 默认按创建时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询接收者映射列表失败", e);
            throw new RuntimeException("分页查询接收者映射列表失败", e);
        }
    }

    @Override
    public List<MsgReceiverMapping> list(MsgReceiverMapping msgReceiverMapping) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = buildQueryWrapper(msgReceiverMapping);
            wrapper.orderByDesc(MsgReceiverMapping::getCreateTime); // 默认按创建时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询接收者映射列表失败", e);
            throw new RuntimeException("查询接收者映射列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgReceiverMapping msgReceiverMapping) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgReceiverMapping.getId())) {
            	msgReceiverMapping.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgReceiverMapping.getCreateTime() == null) {
                msgReceiverMapping.setCreateTime(new Date());
            }
            
            // 验证应用标识和业务接收者ID是否已存在
            if (msgReceiverMapping.getAppKey() != null && msgReceiverMapping.getBizReceiverId() != null) {
                MsgReceiverMapping existingMapping = getByAppKeyAndBizReceiverId(
                    msgReceiverMapping.getAppKey(), msgReceiverMapping.getBizReceiverId());
                if (existingMapping != null) {
                    log.error("保存接收者映射失败，应用标识和业务接收者ID组合已存在: appKey={}, bizReceiverId={}", 
                            msgReceiverMapping.getAppKey(), msgReceiverMapping.getBizReceiverId());
                    throw new RuntimeException("应用标识和业务接收者ID组合已存在");
                }
            }
            
            // 验证部级接收者标识的唯一性（根据接收者类型）
            if (msgReceiverMapping.getCenterReceiverType() != null) {
                if ("1".equals(msgReceiverMapping.getCenterReceiverType())) { // 个人
                    if (msgReceiverMapping.getJsrzjhm() != null) {
                        MsgReceiverMapping existingPerson = getByCenterPerson(
                            msgReceiverMapping.getJsrzjhm());
                        if (existingPerson != null) {
                            log.error("保存接收者映射失败，部级个人接收者已存在: jsrzjhm={}", 
                                    msgReceiverMapping.getJsrzjhm());
                            throw new RuntimeException("部级个人接收者已存在");
                        }
                    }
                } else if ("2".equals(msgReceiverMapping.getCenterReceiverType())) { // 单位
                    if (msgReceiverMapping.getJsdwdm() != null) {
                        MsgReceiverMapping existingUnit = getByCenterUnit(
                            msgReceiverMapping.getJsdwdm());
                        if (existingUnit != null) {
                            log.error("保存接收者映射失败，部级单位接收者已存在: jsdwdm={}", 
                                    msgReceiverMapping.getJsdwdm());
                            throw new RuntimeException("部级单位接收者已存在");
                        }
                    }
                }
            }
            
            // 设置默认值
            if (msgReceiverMapping.getBizReceiverType() == null) {
                msgReceiverMapping.setBizReceiverType("USER");
            }
            
            if (msgReceiverMapping.getMappingType() == null) {
                msgReceiverMapping.setMappingType("STATIC");
            }
            
            if (msgReceiverMapping.getStatus() == null) {
                msgReceiverMapping.setStatus(1);
            }
            
            boolean result = super.save(msgReceiverMapping);
            if (result) {
                log.debug("保存接收者映射成功: id={}, appKey={}, bizReceiverId={}", 
                        msgReceiverMapping.getId(), msgReceiverMapping.getAppKey(), 
                        msgReceiverMapping.getBizReceiverId());
            } else {
                log.error("保存接收者映射失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存接收者映射失败", e);
            throw new RuntimeException("保存接收者映射失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgReceiverMapping msgReceiverMapping) {
        try {
            // 验证接收者映射是否存在
            MsgReceiverMapping existingMapping = super.getById(msgReceiverMapping.getId());
            if (existingMapping == null) {
                log.warn("更新接收者映射失败，记录不存在: id={}", msgReceiverMapping.getId());
                return false;
            }
            
            // 如果修改了应用标识或业务接收者ID，需要检查是否与其他记录冲突
            if ((msgReceiverMapping.getAppKey() != null && !msgReceiverMapping.getAppKey().equals(existingMapping.getAppKey())) ||
                (msgReceiverMapping.getBizReceiverId() != null && !msgReceiverMapping.getBizReceiverId().equals(existingMapping.getBizReceiverId()))) {
                
                String appKey = msgReceiverMapping.getAppKey() != null ? msgReceiverMapping.getAppKey() : existingMapping.getAppKey();
                String bizReceiverId = msgReceiverMapping.getBizReceiverId() != null ? msgReceiverMapping.getBizReceiverId() : existingMapping.getBizReceiverId();
                
                MsgReceiverMapping duplicateMapping = getByAppKeyAndBizReceiverId(appKey, bizReceiverId);
                if (duplicateMapping != null && !duplicateMapping.getId().equals(msgReceiverMapping.getId())) {
                    log.error("更新接收者映射失败，应用标识和业务接收者ID组合已存在: appKey={}, bizReceiverId={}", 
                            appKey, bizReceiverId);
                    throw new RuntimeException("应用标识和业务接收者ID组合已存在");
                }
            }
            
            // 验证部级接收者标识的唯一性（如果修改了）
            if (msgReceiverMapping.getCenterReceiverType() != null || 
                msgReceiverMapping.getJsrzjhm() != null || 
                msgReceiverMapping.getJsdwdm() != null) {
                
                String centerReceiverType = msgReceiverMapping.getCenterReceiverType() != null ? 
                    msgReceiverMapping.getCenterReceiverType() : existingMapping.getCenterReceiverType();
                String jsrzjhm = msgReceiverMapping.getJsrzjhm() != null ? 
                    msgReceiverMapping.getJsrzjhm() : existingMapping.getJsrzjhm();
                String jsdwdm = msgReceiverMapping.getJsdwdm() != null ? 
                    msgReceiverMapping.getJsdwdm() : existingMapping.getJsdwdm();
                
                if ("1".equals(centerReceiverType) && jsrzjhm != null) { // 个人
                    MsgReceiverMapping existingPerson = getByCenterPerson(jsrzjhm);
                    if (existingPerson != null && !existingPerson.getId().equals(msgReceiverMapping.getId())) {
                        log.error("更新接收者映射失败，部级个人接收者已存在: jsrzjhm={}", jsrzjhm);
                        throw new RuntimeException("部级个人接收者已存在");
                    }
                } else if ("2".equals(centerReceiverType) && jsdwdm != null) { // 单位
                    MsgReceiverMapping existingUnit = getByCenterUnit(jsdwdm);
                    if (existingUnit != null && !existingUnit.getId().equals(msgReceiverMapping.getId())) {
                        log.error("更新接收者映射失败，部级单位接收者已存在: jsdwdm={}", jsdwdm);
                        throw new RuntimeException("部级单位接收者已存在");
                    }
                }
            }
            
            boolean result = super.updateById(msgReceiverMapping);
            if (result) {
                log.debug("更新接收者映射成功: id={}, appKey={}, bizReceiverId={}", 
                        msgReceiverMapping.getId(), msgReceiverMapping.getAppKey(), 
                        msgReceiverMapping.getBizReceiverId());
            } else {
                log.warn("更新接收者映射失败: id={}", msgReceiverMapping.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新接收者映射失败: id={}", msgReceiverMapping.getId(), e);
            throw new RuntimeException("更新接收者映射失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgReceiverMapping existingMapping = super.getById(id);
            if (existingMapping == null) {
                log.warn("删除接收者映射失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除接收者映射成功: id={}, appKey={}, bizReceiverId={}", 
                        id, existingMapping.getAppKey(), existingMapping.getBizReceiverId());
            } else {
                log.error("删除接收者映射失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除接收者映射失败: id={}", id, e);
            throw new RuntimeException("删除接收者映射失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgReceiverMapping> buildQueryWrapper(MsgReceiverMapping msgReceiverMapping) {
        LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
        
        if (msgReceiverMapping != null) {
            // 按ID查询
            if (msgReceiverMapping.getId() != null && !msgReceiverMapping.getId().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getId, msgReceiverMapping.getId());
            }
            
            // 按应用标识查询
            if (msgReceiverMapping.getAppKey() != null && !msgReceiverMapping.getAppKey().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getAppKey, msgReceiverMapping.getAppKey());
            }
            
            // 按业务接收者ID查询
            if (msgReceiverMapping.getBizReceiverId() != null && !msgReceiverMapping.getBizReceiverId().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getBizReceiverId, msgReceiverMapping.getBizReceiverId());
            }
            
            // 按业务接收者名称模糊查询
            if (msgReceiverMapping.getBizReceiverName() != null && !msgReceiverMapping.getBizReceiverName().isEmpty()) {
                wrapper.like(MsgReceiverMapping::getBizReceiverName, msgReceiverMapping.getBizReceiverName());
            }
            
            // 按业务接收者类型查询
            if (msgReceiverMapping.getBizReceiverType() != null && !msgReceiverMapping.getBizReceiverType().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getBizReceiverType, msgReceiverMapping.getBizReceiverType());
            }
            
            // 按部级接收者类型查询
            if (msgReceiverMapping.getCenterReceiverType() != null && !msgReceiverMapping.getCenterReceiverType().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getCenterReceiverType, msgReceiverMapping.getCenterReceiverType());
            }
            
            // 按接收人证件号码查询
            if (msgReceiverMapping.getJsrzjhm() != null && !msgReceiverMapping.getJsrzjhm().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getJsrzjhm, msgReceiverMapping.getJsrzjhm());
            }
            
            // 按接收人姓名模糊查询
            if (msgReceiverMapping.getJsrName() != null && !msgReceiverMapping.getJsrName().isEmpty()) {
                wrapper.like(MsgReceiverMapping::getJsrName, msgReceiverMapping.getJsrName());
            }
            
            // 按接收单位代码查询
            if (msgReceiverMapping.getJsdwdm() != null && !msgReceiverMapping.getJsdwdm().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getJsdwdm, msgReceiverMapping.getJsdwdm());
            }
            
            // 按接收单位名称模糊查询
            if (msgReceiverMapping.getJsdwmc() != null && !msgReceiverMapping.getJsdwmc().isEmpty()) {
                wrapper.like(MsgReceiverMapping::getJsdwmc, msgReceiverMapping.getJsdwmc());
            }
            
            // 按状态查询
            if (msgReceiverMapping.getStatus() != null) {
                wrapper.eq(MsgReceiverMapping::getStatus, msgReceiverMapping.getStatus());
            }
            
            // 按映射类型查询
            if (msgReceiverMapping.getMappingType() != null && !msgReceiverMapping.getMappingType().isEmpty()) {
                wrapper.eq(MsgReceiverMapping::getMappingType, msgReceiverMapping.getMappingType());
            }
            
            // 按创建时间范围查询
            // if (msgReceiverMapping.getCreateTime() != null) {
            //     wrapper.ge(MsgReceiverMapping::getCreateTime, startTime);
            //     wrapper.le(MsgReceiverMapping::getCreateTime, endTime);
            // }
        }
        
        return wrapper;
    }

    /**
     * 根据应用标识和业务接收者ID获取映射
     */
    public MsgReceiverMapping getByAppKeyAndBizReceiverId(String appKey, String bizReceiverId) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getAppKey, appKey);
            wrapper.eq(MsgReceiverMapping::getBizReceiverId, bizReceiverId);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据应用标识和业务接收者ID查询失败: appKey={}, bizReceiverId={}", 
                    appKey, bizReceiverId, e);
            throw new RuntimeException("查询接收者映射失败", e);
        }
    }

    /**
     * 根据部级个人接收者证件号码获取映射
     */
    @Override
    public MsgReceiverMapping getByCenterPerson(String jsrzjhm) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getCenterReceiverType, "1"); // 个人
            wrapper.eq(MsgReceiverMapping::getJsrzjhm, jsrzjhm);
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据部级个人接收者证件号码查询失败: jsrzjhm={}", jsrzjhm, e);
            throw new RuntimeException("查询接收者映射失败", e);
        }
    }

    /**
     * 根据部级单位接收者代码获取映射
     */
    @Override
    public MsgReceiverMapping getByCenterUnit(String jsdwdm) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getCenterReceiverType, "2"); // 单位
            wrapper.eq(MsgReceiverMapping::getJsdwdm, jsdwdm);
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据部级单位接收者代码查询失败: jsdwdm={}", jsdwdm, e);
            throw new RuntimeException("查询接收者映射失败", e);
        }
    }

    /**
     * 根据部级接收者信息获取映射
     */
    public MsgReceiverMapping getByCenterReceiver(String jsdwdm, String jsrzjhm) {
        try {
            if (jsrzjhm != null && !jsrzjhm.isEmpty()) {
                // 先按个人查询
                MsgReceiverMapping personMapping = getByCenterPerson(jsrzjhm);
                if (personMapping != null) {
                    return personMapping;
                }
            }
            
            if (jsdwdm != null && !jsdwdm.isEmpty()) {
                // 再按单位查询
                return getByCenterUnit(jsdwdm);
            }
            
            return null;
        } catch (Exception e) {
            log.error("根据部级接收者信息查询失败: jsdwdm={}, jsrzjhm={}", jsdwdm, jsrzjhm, e);
            throw new RuntimeException("查询接收者映射失败", e);
        }
    }

    /**
     * 检查应用标识和业务接收者ID组合是否存在
     */
    public boolean existsAppKeyAndBizReceiverId(String appKey, String bizReceiverId) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getAppKey, appKey);
            wrapper.eq(MsgReceiverMapping::getBizReceiverId, bizReceiverId);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查应用标识和业务接收者ID组合是否存在失败: appKey={}, bizReceiverId={}", 
                    appKey, bizReceiverId, e);
            throw new RuntimeException("检查接收者映射失败", e);
        }
    }

    /**
     * 更新映射状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String id, Integer status) {
        try {
            LambdaUpdateWrapper<MsgReceiverMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MsgReceiverMapping::getId, id);
            wrapper.set(MsgReceiverMapping::getStatus, status);
            wrapper.set(MsgReceiverMapping::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新接收者映射状态成功: id={}, status={}", id, status);
            } else {
                log.warn("更新接收者映射状态失败，记录不存在: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("更新接收者映射状态失败: id={}", id, e);
            throw new RuntimeException("更新接收者映射状态失败", e);
        }
    }

    /**
     * 批量更新映射状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<String> ids, Integer status) {
        try {
            if (ids == null || ids.isEmpty()) {
                log.warn("批量更新映射状态失败，ID列表为空");
                return false;
            }
            
            LambdaUpdateWrapper<MsgReceiverMapping> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(MsgReceiverMapping::getId, ids);
            wrapper.set(MsgReceiverMapping::getStatus, status);
            wrapper.set(MsgReceiverMapping::getUpdateTime, new Date());
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("批量更新接收者映射状态成功: count={}, status={}", ids.size(), status);
            } else {
                log.warn("批量更新接收者映射状态失败");
            }
            return result;
        } catch (Exception e) {
            log.error("批量更新接收者映射状态失败", e);
            throw new RuntimeException("批量更新接收者映射状态失败", e);
        }
    }

    /**
     * 获取应用的所有接收者映射
     */
    public List<MsgReceiverMapping> getMappingsByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getAppKey, appKey);
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.eq(MsgReceiverMapping::getDelFlag, "0"); // 未删除
            wrapper.orderByDesc(MsgReceiverMapping::getCreateTime);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取应用接收者映射列表失败: appKey={}", appKey, e);
            throw new RuntimeException("获取接收者映射列表失败", e);
        }
    }

    /**
     * 获取启用的接收者映射列表
     */
    public List<MsgReceiverMapping> getEnabledMappings() {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.eq(MsgReceiverMapping::getDelFlag, "0"); // 未删除
            wrapper.orderByAsc(MsgReceiverMapping::getAppKey);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取启用的接收者映射列表失败", e);
            throw new RuntimeException("获取接收者映射列表失败", e);
        }
    }

    /**
     * 统计应用的接收者映射数量
     */
    public long countByAppKey(String appKey) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getAppKey, appKey);
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.eq(MsgReceiverMapping::getDelFlag, "0"); // 未删除
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("统计接收者映射数量失败: appKey={}", appKey, e);
            throw new RuntimeException("统计接收者映射数量失败", e);
        }
    }

    /**
     * 根据接收者类型统计数量
     */
    public long countByReceiverType(String centerReceiverType) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getCenterReceiverType, centerReceiverType);
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.eq(MsgReceiverMapping::getDelFlag, "0"); // 未删除
            return super.count(wrapper);
        } catch (Exception e) {
            log.error("按接收者类型统计数量失败: centerReceiverType={}", centerReceiverType, e);
            throw new RuntimeException("统计接收者映射数量失败", e);
        }
    }

    /**
     * 创建个人接收者映射
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgReceiverMapping createPersonMapping(String appKey, String bizReceiverId, String bizReceiverName,
                                                 String jsrzjhm, String jsrName) {
        try {
            MsgReceiverMapping mapping = new MsgReceiverMapping();
            mapping.setAppKey(appKey);
            mapping.setBizReceiverId(bizReceiverId);
            mapping.setBizReceiverName(bizReceiverName);
            mapping.setBizReceiverType("USER");
            mapping.setCenterReceiverType("1"); // 个人
            mapping.setJsrzjhm(jsrzjhm);
            mapping.setJsrName(jsrName);
            mapping.setMappingType("STATIC");
            mapping.setStatus(1);
            
            boolean result = this.save(mapping);
            if (result) {
                log.debug("创建个人接收者映射成功: id={}, appKey={}, jsrzjhm={}", 
                        mapping.getId(), appKey, jsrzjhm);
                return mapping;
            } else {
                log.error("创建个人接收者映射失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建个人接收者映射失败", e);
            throw new RuntimeException("创建个人接收者映射失败", e);
        }
    }

    /**
     * 创建单位接收者映射
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgReceiverMapping createUnitMapping(String appKey, String bizReceiverId, String bizReceiverName,
                                               String jsdwdm, String jsdwmc) {
        try {
            MsgReceiverMapping mapping = new MsgReceiverMapping();
            mapping.setAppKey(appKey);
            mapping.setBizReceiverId(bizReceiverId);
            mapping.setBizReceiverName(bizReceiverName);
            mapping.setBizReceiverType("ORG");
            mapping.setCenterReceiverType("2"); // 单位
            mapping.setJsdwdm(jsdwdm);
            mapping.setJsdwmc(jsdwmc);
            mapping.setMappingType("STATIC");
            mapping.setStatus(1);
            
            boolean result = this.save(mapping);
            if (result) {
                log.debug("创建单位接收者映射成功: id={}, appKey={}, jsdwdm={}", 
                        mapping.getId(), appKey, jsdwdm);
                return mapping;
            } else {
                log.error("创建单位接收者映射失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建单位接收者映射失败", e);
            throw new RuntimeException("创建单位接收者映射失败", e);
        }
    }

    /**
     * 根据业务系统接收者信息查找对应的部级接收者
     */
    public MsgReceiverMapping findCenterReceiverByBizInfo(String appKey, String bizReceiverId, String bizReceiverType) {
        try {
            LambdaQueryWrapper<MsgReceiverMapping> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgReceiverMapping::getAppKey, appKey);
            
            if (bizReceiverId != null) {
                wrapper.eq(MsgReceiverMapping::getBizReceiverId, bizReceiverId);
            }
            
            if (bizReceiverType != null) {
                wrapper.eq(MsgReceiverMapping::getBizReceiverType, bizReceiverType);
            }
            
            wrapper.eq(MsgReceiverMapping::getStatus, 1); // 启用的
            wrapper.last("LIMIT 1");
            
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据业务系统接收者信息查找部级接收者失败: appKey={}, bizReceiverId={}, bizReceiverType={}", 
                    appKey, bizReceiverId, bizReceiverType, e);
            throw new RuntimeException("查找部级接收者失败", e);
        }
    }
}