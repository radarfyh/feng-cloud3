package work.metanet.feng.admin.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import work.metanet.feng.admin.service.PrmCustomerRelationService;
import work.metanet.feng.admin.service.PrmCustomerService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.admin.api.entity.PrmCustomerRelation;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.dto.CustomerRelationDTO;
import work.metanet.feng.admin.api.vo.CustomerRelationVO;
import work.metanet.feng.admin.mapper.PrmCustomerRelationMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrmCustomerRelationServiceImpl extends ServiceImpl<PrmCustomerRelationMapper, PrmCustomerRelation> implements PrmCustomerRelationService {
	
	@Autowired
	private SysDictService dictService;
	
	@Autowired
	private PrmCustomerService customerService;
	
    @Override
    public IPage<CustomerRelationVO> pageRelationship(Page<PrmCustomerRelation> page, CustomerRelationDTO dto) {
    	return page(page, getWrapper(dto)).convert(this::convertToVO);
    }
    
    LambdaQueryWrapper<PrmCustomerRelation> getWrapper(CustomerRelationDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<PrmCustomerRelation> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(dto.getId() != null, PrmCustomerRelation::getId, dto.getId())
               .eq(dto.getCustomerId() != null, PrmCustomerRelation::getCustomerId, dto.getCustomerId())
               .eq(dto.getRelatedCustomerId() != null, PrmCustomerRelation::getRelatedCustomerId, dto.getRelatedCustomerId())
               .eq(dto.getRelationType() != null, PrmCustomerRelation::getRelationType, dto.getRelationType())
               .ge(dto.getRelationStrength() != null, PrmCustomerRelation::getRelationStrength, dto.getRelationStrength())
               .orderByDesc(PrmCustomerRelation::getId)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<CustomerRelationVO> listRelationship(CustomerRelationDTO dto) {
        return list(getWrapper(dto)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private CustomerRelationVO convertToVO(PrmCustomerRelation customerRelation) {
    	CustomerRelationVO vo = new CustomerRelationVO();
        BeanUtil.copyProperties(customerRelation, vo);
        
        // 处理关系类型名称
        if (customerRelation.getRelationType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("customer_relationship_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), customerRelation.getRelationType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setRelationTypeName(label);
        }
        
        // 处理客户信息        
        vo.setCustomerInfo(customerService.getCustomerWithRelations(customerRelation.getCustomerId()));
        vo.setRelatedCustomerInfo(customerService.getCustomerWithRelations(customerRelation.getRelatedCustomerId()));
        
        return vo;
    }
    
    @Override
    @Transactional
    public CustomerRelationVO saveRelation(CustomerRelationDTO dto) {
        PrmCustomerRelation relation = new PrmCustomerRelation();
        BeanUtils.copyProperties(dto, relation);
        
        int ret = 0;
        if (relation.getId() == null) {
        	ret = baseMapper.insert(relation);
        } else {
        	ret = baseMapper.updateById(relation);
        }
        if (ret > 0) {
        	return convertToVO(relation);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void removeRelation(Integer id) {
    	baseMapper.deleteById(id);
    }

    @Override
    public List<CustomerRelationVO> listRelations(Integer customerId) {
        return baseMapper.selectByCustomerId(customerId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerRelationVO> getRelationBetweenCustomers(Integer customerId, Integer relatedCustomerId) {
        return baseMapper.selectRelationBetweenCustomers(customerId, relatedCustomerId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recalculateStrength(Integer customerId) {
        List<PrmCustomerRelation> relations = baseMapper.selectByCustomerId(customerId);
        // 实现关系强度计算逻辑
        relations.forEach(relation -> {
            // 示例：基于共享联系人和互动记录计算
            double newStrength = calculateDynamicStrength(relation);
            relation.setRelationStrength(newStrength);
        });
        baseMapper.batchUpdateStrength(relations);
    }

    private double calculateDynamicStrength(PrmCustomerRelation relation) {
        // 实际业务中实现更复杂的计算逻辑
        return Math.min(1.0, relation.getRelationStrength().doubleValue() * 1.1);
    }
}