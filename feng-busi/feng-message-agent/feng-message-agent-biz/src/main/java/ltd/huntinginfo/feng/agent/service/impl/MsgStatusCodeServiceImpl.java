package ltd.huntinginfo.feng.agent.service.impl;

import ltd.huntinginfo.feng.agent.api.entity.MsgStatusCode;
import ltd.huntinginfo.feng.agent.mapper.MsgStatusCodeMapper;
import ltd.huntinginfo.feng.agent.service.MsgStatusCodeService;
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
public class MsgStatusCodeServiceImpl extends ServiceImpl<MsgStatusCodeMapper, MsgStatusCode> implements MsgStatusCodeService {

    private final MsgStatusCodeMapper msgStatusCodeMapper;

    @Override
    public MsgStatusCode getById(String id) {
        try {
            MsgStatusCode result = super.getById(id);
            if (result == null) {
                log.warn("未找到对应的状态码记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询状态码详情失败: id={}", id, e);
            throw new RuntimeException("查询状态码详情失败", e);
        }
    }

    @Override
    public IPage<MsgStatusCode> page(IPage<MsgStatusCode> page, MsgStatusCode msgStatusCode) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = buildQueryWrapper(msgStatusCode);
            wrapper.orderByAsc(MsgStatusCode::getSortOrder); // 按排序号正序
            wrapper.orderByAsc(MsgStatusCode::getStatusCode); // 再按状态码正序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询状态码列表失败", e);
            throw new RuntimeException("分页查询状态码列表失败", e);
        }
    }

    @Override
    public List<MsgStatusCode> list(MsgStatusCode msgStatusCode) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = buildQueryWrapper(msgStatusCode);
            wrapper.orderByAsc(MsgStatusCode::getSortOrder); // 按排序号正序
            wrapper.orderByAsc(MsgStatusCode::getStatusCode); // 再按状态码正序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询状态码列表失败", e);
            throw new RuntimeException("查询状态码列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgStatusCode msgStatusCode) {
        try {
            // 设置ID
            if (StrUtil.isBlank(msgStatusCode.getId())) {
            	msgStatusCode.setId(IdUtil.fastSimpleUUID());
            }
            
            // 设置创建时间
            if (msgStatusCode.getCreateTime() == null) {
                msgStatusCode.setCreateTime(new Date());
            }
            
            // 验证状态码是否已存在
            MsgStatusCode existingCode = getByStatusCode(msgStatusCode.getStatusCode());
            if (existingCode != null) {
                log.error("保存状态码失败，状态码已存在: statusCode={}", msgStatusCode.getStatusCode());
                throw new RuntimeException("状态码已存在");
            }
            
            // 设置默认值
            if (msgStatusCode.getCategory() == null) {
                msgStatusCode.setCategory("PROXY");
            }
            
            if (msgStatusCode.getSortOrder() == null) {
                msgStatusCode.setSortOrder(0);
            }
            
            if (msgStatusCode.getIsFinal() == null) {
                msgStatusCode.setIsFinal(0);
            }
            
            if (msgStatusCode.getStatus() == null) {
                msgStatusCode.setStatus(1);
            }
            
            boolean result = super.save(msgStatusCode);
            if (result) {
                log.debug("保存状态码成功: id={}, statusCode={}, statusName={}", 
                        msgStatusCode.getId(), msgStatusCode.getStatusCode(), msgStatusCode.getStatusName());
            } else {
                log.error("保存状态码失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存状态码失败", e);
            throw new RuntimeException("保存状态码失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgStatusCode msgStatusCode) {
        try {
            // 验证状态码是否存在
            MsgStatusCode existingCode = super.getById(msgStatusCode.getId());
            if (existingCode == null) {
                log.warn("更新状态码失败，记录不存在: id={}", msgStatusCode.getId());
                return false;
            }
            
            // 如果修改了状态码，需要检查是否与其他记录冲突
            if (msgStatusCode.getStatusCode() != null && 
                !msgStatusCode.getStatusCode().equals(existingCode.getStatusCode())) {
                MsgStatusCode duplicateCode = getByStatusCode(msgStatusCode.getStatusCode());
                if (duplicateCode != null && !duplicateCode.getId().equals(msgStatusCode.getId())) {
                    log.error("更新状态码失败，状态码已存在: statusCode={}", msgStatusCode.getStatusCode());
                    throw new RuntimeException("状态码已存在");
                }
            }
            
            boolean result = super.updateById(msgStatusCode);
            if (result) {
                log.debug("更新状态码成功: id={}, statusCode={}", 
                        msgStatusCode.getId(), msgStatusCode.getStatusCode());
            } else {
                log.warn("更新状态码失败: id={}", msgStatusCode.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新状态码失败: id={}", msgStatusCode.getId(), e);
            throw new RuntimeException("更新状态码失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(String id) {
        try {
            // 先查询是否存在
            MsgStatusCode existingCode = super.getById(id);
            if (existingCode == null) {
                log.warn("删除状态码失败，记录不存在: id={}", id);
                return false;
            }
            
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除状态码成功: id={}, statusCode={}, statusName={}", 
                        id, existingCode.getStatusCode(), existingCode.getStatusName());
            } else {
                log.error("删除状态码失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除状态码失败: id={}", id, e);
            throw new RuntimeException("删除状态码失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MsgStatusCode> buildQueryWrapper(MsgStatusCode msgStatusCode) {
        LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
        
        if (msgStatusCode != null) {
            // 按ID查询
            if (msgStatusCode.getId() != null && !msgStatusCode.getId().isEmpty()) {
                wrapper.eq(MsgStatusCode::getId, msgStatusCode.getId());
            }
            
            // 按状态码查询
            if (msgStatusCode.getStatusCode() != null && !msgStatusCode.getStatusCode().isEmpty()) {
                wrapper.eq(MsgStatusCode::getStatusCode, msgStatusCode.getStatusCode());
            }
            
            // 按状态名称模糊查询
            if (msgStatusCode.getStatusName() != null && !msgStatusCode.getStatusName().isEmpty()) {
                wrapper.like(MsgStatusCode::getStatusName, msgStatusCode.getStatusName());
            }
            
            // 按分类查询
            if (msgStatusCode.getCategory() != null && !msgStatusCode.getCategory().isEmpty()) {
                wrapper.eq(MsgStatusCode::getCategory, msgStatusCode.getCategory());
            }
            
            // 按父状态码查询
            if (msgStatusCode.getParentCode() != null && !msgStatusCode.getParentCode().isEmpty()) {
                wrapper.eq(MsgStatusCode::getParentCode, msgStatusCode.getParentCode());
            }
            
            // 按是否为最终状态查询
            if (msgStatusCode.getIsFinal() != null) {
                wrapper.eq(MsgStatusCode::getIsFinal, msgStatusCode.getIsFinal());
            }
            
            // 按状态查询
            if (msgStatusCode.getStatus() != null) {
                wrapper.eq(MsgStatusCode::getStatus, msgStatusCode.getStatus());
            }
            
            // 按排序号范围查询
            // if (msgStatusCode.getSortOrder() != null) {
            //     wrapper.ge(MsgStatusCode::getSortOrder, minOrder);
            //     wrapper.le(MsgStatusCode::getSortOrder, maxOrder);
            // }
            
            // 按描述模糊查询
            if (msgStatusCode.getStatusDesc() != null && !msgStatusCode.getStatusDesc().isEmpty()) {
                wrapper.like(MsgStatusCode::getStatusDesc, msgStatusCode.getStatusDesc());
            }
        }
        
        return wrapper;
    }

    /**
     * 根据状态码获取状态码对象
     */
    public MsgStatusCode getByStatusCode(String statusCode) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getStatusCode, statusCode);
            wrapper.last("LIMIT 1");
            return super.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据状态码查询失败: statusCode={}", statusCode, e);
            throw new RuntimeException("查询状态码失败", e);
        }
    }

    /**
     * 检查状态码是否存在
     */
    public boolean existsStatusCode(String statusCode) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getStatusCode, statusCode);
            return super.count(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查状态码是否存在失败: statusCode={}", statusCode, e);
            throw new RuntimeException("检查状态码失败", e);
        }
    }

    /**
     * 根据分类获取状态码列表
     */
    public List<MsgStatusCode> listByCategory(String category) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getCategory, category);
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据分类查询状态码列表失败: category={}", category, e);
            throw new RuntimeException("查询状态码列表失败", e);
        }
    }

    /**
     * 根据父状态码获取子状态码列表
     */
    public List<MsgStatusCode> listByParentCode(String parentCode) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getParentCode, parentCode);
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据父状态码查询子状态码列表失败: parentCode={}", parentCode, e);
            throw new RuntimeException("查询状态码列表失败", e);
        }
    }

    /**
     * 获取所有启用的状态码列表
     */
    public List<MsgStatusCode> getEnabledStatusCodes() {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getCategory);
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取启用的状态码列表失败", e);
            throw new RuntimeException("获取状态码列表失败", e);
        }
    }

    /**
     * 获取代理平台的状态码列表
     */
    public List<MsgStatusCode> getProxyStatusCodes() {
        return listByCategory("PROXY");
    }

    /**
     * 获取部级消息中心的状态码列表
     */
    public List<MsgStatusCode> getCenterStatusCodes() {
        return listByCategory("CENTER");
    }

    /**
     * 获取所有最终状态的状态码
     */
    public List<MsgStatusCode> getFinalStatusCodes() {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getIsFinal, 1); // 最终状态
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取最终状态的状态码列表失败", e);
            throw new RuntimeException("获取状态码列表失败", e);
        }
    }

    /**
     * 获取所有非最终状态的状态码
     */
    public List<MsgStatusCode> getNonFinalStatusCodes() {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MsgStatusCode::getIsFinal, 0); // 非最终状态
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取非最终状态的状态码列表失败", e);
            throw new RuntimeException("获取状态码列表失败", e);
        }
    }

    /**
     * 获取状态码的完整描述（状态码 + 状态名称 + 描述）
     */
    public String getFullDescription(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            if (code != null && code.getStatus() == 1) {
                return String.format("%s - %s: %s", 
                    code.getStatusCode(), code.getStatusName(), code.getStatusDesc());
            }
            return null;
        } catch (Exception e) {
            log.error("获取状态码完整描述失败: statusCode={}", statusCode, e);
            return null;
        }
    }

    /**
     * 根据状态码获取状态名称
     */
    public String getStatusName(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null ? code.getStatusName() : null;
        } catch (Exception e) {
            log.error("获取状态名称失败: statusCode={}", statusCode, e);
            return null;
        }
    }

    /**
     * 根据状态码获取状态描述
     */
    public String getStatusDesc(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null ? code.getStatusDesc() : null;
        } catch (Exception e) {
            log.error("获取状态描述失败: statusCode={}", statusCode, e);
            return null;
        }
    }

    /**
     * 检查状态码是否为最终状态
     */
    public boolean isFinalStatus(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null && code.getStatus() == 1 && code.getIsFinal() == 1;
        } catch (Exception e) {
            log.error("检查状态码是否为最终状态失败: statusCode={}", statusCode, e);
            return false;
        }
    }

    /**
     * 检查状态码是否有效（存在且启用）
     */
    public boolean isValidStatusCode(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null && code.getStatus() == 1;
        } catch (Exception e) {
            log.error("检查状态码是否有效失败: statusCode={}", statusCode, e);
            return false;
        }
    }

    /**
     * 获取状态码的分类
     */
    public String getCategory(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null ? code.getCategory() : null;
        } catch (Exception e) {
            log.error("获取状态码分类失败: statusCode={}", statusCode, e);
            return null;
        }
    }

    /**
     * 获取状态码的排序号
     */
    public Integer getSortOrder(String statusCode) {
        try {
            MsgStatusCode code = getByStatusCode(statusCode);
            return code != null ? code.getSortOrder() : null;
        } catch (Exception e) {
            log.error("获取状态码排序号失败: statusCode={}", statusCode, e);
            return null;
        }
    }

    /**
     * 创建代理平台状态码
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgStatusCode createProxyStatusCode(String statusCode, String statusName, String statusDesc, 
                                              String parentCode, Integer sortOrder, Integer isFinal) {
        try {
            MsgStatusCode code = new MsgStatusCode();
            code.setStatusCode(statusCode);
            code.setStatusName(statusName);
            code.setStatusDesc(statusDesc);
            code.setCategory("PROXY");
            code.setParentCode(parentCode);
            code.setSortOrder(sortOrder != null ? sortOrder : 0);
            code.setIsFinal(isFinal != null ? isFinal : 0);
            code.setStatus(1);
            
            boolean result = this.save(code);
            if (result) {
                log.debug("创建代理平台状态码成功: statusCode={}, statusName={}", statusCode, statusName);
                return code;
            } else {
                log.error("创建代理平台状态码失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建代理平台状态码失败", e);
            throw new RuntimeException("创建状态码失败", e);
        }
    }

    /**
     * 创建部级消息中心状态码
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgStatusCode createCenterStatusCode(String statusCode, String statusName, String statusDesc, 
                                               Integer sortOrder) {
        try {
            MsgStatusCode code = new MsgStatusCode();
            code.setStatusCode(statusCode);
            code.setStatusName(statusName);
            code.setStatusDesc(statusDesc);
            code.setCategory("CENTER");
            code.setSortOrder(sortOrder != null ? sortOrder : 0);
            code.setIsFinal(1); // 部级状态码默认都是最终状态
            code.setStatus(1);
            
            boolean result = this.save(code);
            if (result) {
                log.debug("创建部级消息中心状态码成功: statusCode={}, statusName={}", statusCode, statusName);
                return code;
            } else {
                log.error("创建部级消息中心状态码失败");
                return null;
            }
        } catch (Exception e) {
            log.error("创建部级消息中心状态码失败", e);
            throw new RuntimeException("创建状态码失败", e);
        }
    }

    /**
     * 批量更新状态码状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<String> statusCodes, Integer status) {
        try {
            if (statusCodes == null || statusCodes.isEmpty()) {
                log.warn("批量更新状态码状态失败，状态码列表为空");
                return false;
            }
            
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MsgStatusCode::getStatusCode, statusCodes);
            
            MsgStatusCode updateEntity = new MsgStatusCode();
            updateEntity.setStatus(status);
            updateEntity.setUpdateTime(new Date());
            
            boolean result = super.update(updateEntity, wrapper);
            if (result) {
                log.debug("批量更新状态码状态成功: count={}, status={}", statusCodes.size(), status);
            } else {
                log.warn("批量更新状态码状态失败");
            }
            return result;
        } catch (Exception e) {
            log.error("批量更新状态码状态失败", e);
            throw new RuntimeException("批量更新状态码状态失败", e);
        }
    }

    /**
     * 根据状态码前缀查询
     */
    public List<MsgStatusCode> listByStatusCodePrefix(String prefix) {
        try {
            LambdaQueryWrapper<MsgStatusCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.likeRight(MsgStatusCode::getStatusCode, prefix);
            wrapper.eq(MsgStatusCode::getStatus, 1); // 启用的
            wrapper.orderByAsc(MsgStatusCode::getSortOrder);
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("根据状态码前缀查询失败: prefix={}", prefix, e);
            throw new RuntimeException("查询状态码失败", e);
        }
    }
}