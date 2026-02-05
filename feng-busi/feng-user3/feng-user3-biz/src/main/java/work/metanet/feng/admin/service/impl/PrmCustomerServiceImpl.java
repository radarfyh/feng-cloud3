package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import work.metanet.feng.admin.api.dto.CustomerDTO;
import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.entity.PrmFollowRecord;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;
import work.metanet.feng.admin.api.vo.CustomerDetailVO;
import work.metanet.feng.admin.api.vo.CustomerSimpleVO;
import work.metanet.feng.admin.api.vo.FollowRecordVO;
import work.metanet.feng.admin.api.vo.PrmCustomerVO;
import work.metanet.feng.admin.api.vo.SocialDetailsVO;
import work.metanet.feng.admin.api.vo.UserVO;
import work.metanet.feng.admin.mapper.PrmContactMapper;
import work.metanet.feng.admin.mapper.PrmCustomerMapper;
import work.metanet.feng.admin.mapper.PrmFollowRecordMapper;
import work.metanet.feng.admin.mapper.SysSocialDetailsMapper;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.mapper.SysUserMapper;
import work.metanet.feng.admin.service.PrmCustomerService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.DesensitizedUtils;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.SecurityUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrmCustomerServiceImpl extends ServiceImpl<PrmCustomerMapper, PrmCustomer> implements PrmCustomerService {

    private final PrmCustomerMapper customerMapper;
    private final PrmContactMapper contactMapper;
    private final SysSocialDetailsMapper socialDetailsMapper;
    private final PrmFollowRecordMapper followRecordMapper;
    private final SysUserMapper userMapper;
    private final SysStaffMapper staffMapper;
    private final SysDictService dictService;
    
    @Override
    public IPage<PrmCustomerVO> customerPage(Page<PrmCustomer> page, CustomerDTO customer) {
    	Page<PrmCustomer> ret = customerMapper.selectPage(page, getWrapper(customer));
        return ret.convert(this::convertToVO);
    }
    
    private LambdaQueryWrapper<PrmCustomer> getWrapper(CustomerDTO customer) {
        // 构建查询条件
        LambdaQueryWrapper<PrmCustomer> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(customer.getLevelCode() != null, PrmCustomer::getLevelCode, customer.getLevelCode())
               .eq(customer.getRelationshipType() != null, PrmCustomer::getRelationshipType, customer.getRelationshipType())
               .eq(customer.getSourceCode() != null, PrmCustomer::getSourceCode, customer.getSourceCode())
               .eq(customer.getId() != null && customer.getId() > 0, PrmCustomer::getId, customer.getId())
               .eq(customer.getOrganId() != null && customer.getOrganId() > 0, PrmCustomer::getOrganId, customer.getOrganId())
               .eq(customer.getParentId() != null && customer.getParentId() >= 0, PrmCustomer::getParentId, customer.getParentId())
               .like(StrUtil.isNotBlank(customer.getMobile()), PrmCustomer::getMobile, customer.getMobile())
               .like(StrUtil.isNotBlank(customer.getName()), PrmCustomer::getName, customer.getName())
               .orderByDesc(PrmCustomer::getCreateTime)
               .last(CommonConstants.LIMIT_LIST_QUERY);
        return wrapper;
    }
    
    @Override
    public List<PrmCustomerVO> customerList(CustomerDTO customer) {
        return customerMapper.selectList(getWrapper(customer)).stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private PrmCustomerVO convertToVO(PrmCustomer customerEntity) {
        PrmCustomerVO vo = new PrmCustomerVO();
        BeanUtil.copyProperties(customerEntity, vo);
        
        // 脱敏
        //vo.setMobile(DesensitizedUtils.mobilePhone(customerEntity.getMobile()));
        
        R<List<SysDictItem>> itemsCustomerLevel = dictService.getDictByType("customer_level");
        Optional.ofNullable(customerEntity.getLevelCode()).ifPresent(code -> {
        	String label = itemsCustomerLevel.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerEntity.getLevelCode()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setLevelName(label);
        });
        
        R<List<SysDictItem>> itemsRelationshipType = dictService.getDictByType("customer_relationship_type");
        Optional.ofNullable(customerEntity.getRelationshipType()).ifPresent(type -> {
        	String label = itemsRelationshipType.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerEntity.getRelationshipType()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setRelationshipTypeName(label);
        });
        
        R<List<SysDictItem>> itemsSourceCode = dictService.getDictByType("customer_source");
        
        Optional.ofNullable(customerEntity.getSourceCode()).ifPresent(source -> {
        	String label = itemsSourceCode.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerEntity.getSourceCode()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setSourceName(label);
        });
        return vo;
    }

    private PrmCustomerVO convertToVO3(PrmCustomerVO customerVo) {
        PrmCustomerVO vo = new PrmCustomerVO();
        BeanUtil.copyProperties(customerVo, vo);
        
        // 脱敏
        //vo.setMobile(DesensitizedUtils.mobilePhone(customerEntity.getMobile()));
        
        R<List<SysDictItem>> itemsCustomerLevel = dictService.getDictByType("customer_level");
        Optional.ofNullable(customerVo.getLevelCode()).ifPresent(code -> {
        	String label = itemsCustomerLevel.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerVo.getLevelCode()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setLevelName(label);
        });
        
        R<List<SysDictItem>> itemsRelationshipType = dictService.getDictByType("customer_relationship_type");
        Optional.ofNullable(customerVo.getRelationshipType()).ifPresent(type -> {
        	String label = itemsRelationshipType.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerVo.getRelationshipType()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setRelationshipTypeName(label);
        });
        
        R<List<SysDictItem>> itemsSourceCode = dictService.getDictByType("customer_source");
        
        Optional.ofNullable(customerVo.getSourceCode()).ifPresent(source -> {
        	String label = itemsSourceCode.getData().stream()
            	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerVo.getSourceCode()))
            	    .findFirst()
            	    .map(SysDictItem::getLabel)  // 如果存在则提取label
            	    .orElse(null);               // 不存在则返回null
        	vo.setSourceName(label);
        });
        return vo;
    }
    @Override
    @Cacheable(value = "customer", key = "#customerId")
    public PrmCustomerVO getCustomerWithRelations(Integer customerId) {
        return convertToVO3(customerMapper.selectWithRelations(customerId));
    }

    @Override
    public List<PrmCustomer> searchByName(String name) {
        LambdaQueryWrapper<PrmCustomer> wrapper = new LambdaQueryWrapper<PrmCustomer>()
                .like(StringUtils.isNotBlank(name), PrmCustomer::getName, name)
                .eq(PrmCustomer::getDelFlag, "0")
                .orderByDesc(PrmCustomer::getUpdateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    @CacheEvict(value = "customer", key = "#customer.id")
    public Boolean saveCustomer(PrmCustomer customer) {
        if (customer.getId() == null) {
            // 新增客户
            return this.save(customer);
        } else {
            // 更新客户
            return this.updateById(customer);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "customer", key = "#customerId")
    public Boolean removeCustomer(Integer customerId) {
    	return (customerMapper.deleteById(customerId) > 0);
    }

    private CustomerDetailVO convertToVO2(PrmCustomer customerEntity) {
    	PrmCustomerVO vo = convertToVO(customerEntity);
    	CustomerDetailVO detail = new CustomerDetailVO();
    	BeanUtil.copyProperties(vo, detail);
    	return detail;
    }
    @Override
    @Transactional(readOnly = true)
    public CustomerDetailVO getCustomerNetwork(Integer customerId) {
        // 1. 获取客户基本信息
        PrmCustomer customer = getById(customerId);
        if (customer == null) {
            return null;
        }

        CustomerDetailVO detailVO = convertToVO2(customer);

        // 2. 获取上级客户简略信息
        if (customer.getParentId() != null) {
            PrmCustomer parentCustomer = getById(customer.getParentId());
            if (parentCustomer != null) {
                CustomerSimpleVO parentVO = new CustomerSimpleVO();
                BeanUtil.copyProperties(parentCustomer, parentVO);
                detailVO.setParentCustomer(parentVO);
            }
        }

        // 3. 获取关联联系人列表
        List<ContactSimpleVO> contacts = contactMapper.selectByCustomerId(customerId);
        detailVO.setContacts(contacts);

        // 4. 获取社交账号列表（使用新的selectByOwner方法）
        List<SocialDetailsVO> socialAccounts = socialDetailsMapper.selectByOwner(customerId, "customer");
        detailVO.setSocialAccounts(socialAccounts);

        // 5. 获取跟踪记录（按跟进时间倒序）
        List<PrmFollowRecord> records = followRecordMapper.selectList(
            new LambdaQueryWrapper<PrmFollowRecord>()
                .eq(PrmFollowRecord::getCustomerId, customerId)
                .orderByDesc(PrmFollowRecord::getFollowTime)
                .orderByDesc(PrmFollowRecord::getCreateTime)
        );
        
        List<FollowRecordVO> followRecords = records.stream()
            .map(this::convertToFollowRecordVO)
            .collect(Collectors.toList());
        detailVO.setFollowRecords(followRecords);

        // 6. 计算客户关系强度指数（使用跟进时间计算）
        Double relationshipScore = calculateRelationshipScore(customerId);
        detailVO.setRelationshipScore(relationshipScore);

        return detailVO;
    }

    /**
     * 计算客户关系强度指数（基于跟进时间优化）
     */
    private Double calculateRelationshipScore(Integer customerId) {
        // 1. 获取关系强度基础值（修改为获取主要联系人）
        PrmContact contact = contactMapper.selectOne(
            new LambdaQueryWrapper<PrmContact>()
                .eq(PrmContact::getCustomerId, customerId)
                .eq(PrmContact::getIsPrimary, "1") // 只查询主要联系人
                .last("LIMIT 1") // 确保只返回一条记录
        );
        
        double baseScore = contact != null ? 
            (contact.getIntimacyScore() != null ? contact.getIntimacyScore() : 0.5) : 0.5;

        // 2. 根据互动频次调整分数
        Integer contactFrequency = followRecordMapper.countByCustomerId(customerId);
        double frequencyFactor = Math.min(1.0, contactFrequency * 0.05);

        // 3. 根据最近跟进时间调整分数（使用followTime字段）
        Date lastFollowTime = followRecordMapper.getLastFollowTimeByCustomer(customerId);
        long daysSinceLastFollow = lastFollowTime != null ? 
            (System.currentTimeMillis() - lastFollowTime.getTime()) / (1000 * 60 * 60 * 24) : 30;
        double timeFactor = Math.max(0.5, 1.0 - (daysSinceLastFollow * 0.01));

        // 4. 综合计算最终分数
        return Math.min(1.0, baseScore * 0.6 + frequencyFactor * 0.2 + timeFactor * 0.2);
    }

    @Override
    @Transactional
    public void addFollowRecord(Integer customerId, FollowRecordDTO dto) {
        // 保存跟踪记录
        PrmFollowRecord followRecord = new PrmFollowRecord();
        BeanUtil.copyProperties(dto, followRecord);
        followRecord.setCustomerId(customerId);
        
        Integer userId = SecurityUtils.getUser().getId();
        UserVO user = userMapper.getUserVoById(userId);
        // 自动填充当前用户信息
        Integer staffId = user.getStaffId();
        SysStaff staff = staffMapper.selectById(staffId);
        followRecord.setStaffId(staffId);
        followRecord.setStaffName(staff.getStaffName());
        
        // 设置跟进时间为当前时间（如果未指定）
        if (followRecord.getFollowTime() == null) {
            followRecord.setFollowTime(LocalDateTime.now());
        }
        
        followRecordMapper.insert(followRecord);
        
        // 更新客户最后跟踪时间（使用跟进时间）
        updateLastFollowTime(customerId, followRecord.getFollowTime());
    }

    @Override
    @Transactional
    public void updateLastFollowTime(Integer customerId, LocalDateTime followTime) {
        PrmCustomer customer = new PrmCustomer();
        customer.setId(customerId);
        customer.setFollowTime(followTime);  // 更新客户表的最后跟踪时间
        updateById(customer);
    }

    private FollowRecordVO convertToFollowRecordVO(PrmFollowRecord record) {
        FollowRecordVO vo = new FollowRecordVO();
        BeanUtil.copyProperties(record, vo);
        return vo;
    }

	@Override
	public CustomerDetailVO getCustomerDetail(Integer customerId) {		
		PrmCustomer customer = customerMapper.selectById(customerId);
		CustomerDetailVO vo = new CustomerDetailVO();
		BeanUtil.copyProperties(customer, vo);
		return vo;
	}
}