package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.metanet.feng.admin.api.dto.SocialDetailsDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.entity.SysSocialDetails;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.vo.SocialDetailsVO;
import work.metanet.feng.admin.mapper.PrmContactMapper;
import work.metanet.feng.admin.mapper.PrmCustomerMapper;
import work.metanet.feng.admin.mapper.SysSocialDetailsMapper;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.admin.service.SysSocialDetailsService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysSocialDetailsServiceImpl extends ServiceImpl<SysSocialDetailsMapper, SysSocialDetails> 
    implements SysSocialDetailsService {
	
	private final SysDictService dictService;
    private final SysStaffMapper staffMapper;
    private final PrmContactMapper contactMapper;
    private final PrmCustomerMapper customerMapper;
    @Override
    public IPage<SocialDetailsVO> pageSocialDetails(Page<SysSocialDetails> page, SocialDetailsDTO dto) {
    	return page(page, getWrapper(dto)).convert(this::convertToVO);
    }
    
    LambdaQueryWrapper<SysSocialDetails> getWrapper(SocialDetailsDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<SysSocialDetails> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(dto.getId() != null, SysSocialDetails::getId, dto.getId())
               .eq(dto.getStaffId() != null, SysSocialDetails::getStaffId, dto.getStaffId())
               .eq(dto.getCustomerId() != null, SysSocialDetails::getCustomerId, dto.getCustomerId())
               .eq(dto.getContactId() != null, SysSocialDetails::getContactId, dto.getContactId())
               .eq(dto.getType() != null, SysSocialDetails::getType, dto.getType())
               .eq(dto.getSocialAccount() != null, SysSocialDetails::getSocialAccount, dto.getSocialAccount())
               .eq(dto.getNickname() != null, SysSocialDetails::getNickname, dto.getNickname())
               .eq(dto.getAvatar() != null, SysSocialDetails::getAvatar, dto.getAvatar())
               .like(StrUtil.isNotBlank(dto.getRemark()), SysSocialDetails::getRemark, dto.getRemark())
               .eq(dto.getIsPrimary() != null, SysSocialDetails::getIsPrimary, dto.getIsPrimary())
               .orderByDesc(SysSocialDetails::getSocialAccount)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<SocialDetailsVO> listSocialDetails(SocialDetailsDTO dto) {
        return list(getWrapper(dto)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private SocialDetailsVO convertToVO(SysSocialDetails followRecord) {
    	SocialDetailsVO vo = new SocialDetailsVO();
        BeanUtil.copyProperties(followRecord, vo);
        
        // 处理社交平台类型名称
        if (followRecord.getType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("social_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), followRecord.getType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setTypeName(label);
        }
        
        // 处理联系人姓名
        PrmContact contact = contactMapper.selectById(vo.getContactId());
        if (contact != null) vo.setContactName(contact.getName());
        
        // 处理客户名称
        PrmCustomer customer = customerMapper.selectById(vo.getCustomerId());
        if (customer != null) vo.setCustomerName(customer.getName());
        
        // 处理员工姓名
        SysStaff staff = staffMapper.selectById(vo.getStaffId());
        if (staff != null) vo.setStaffName(staff.getStaffName());
        
        return vo;
    }

    @Override
    public List<SocialDetailsVO> listByCustomer(Integer customerId) {
        return list(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getCustomerId, customerId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<SocialDetailsVO> listByContact(Integer contactId) {
        return list(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getContactId, contactId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<SocialDetailsVO> listByStaff(Integer staffId) {
        return list(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getStaffId, staffId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public SocialDetailsVO getSocialDetails(Integer id) {
        SysSocialDetails details = getById(id);
        return details != null ? convertToVO(details) : null;
    }

    @Override
    @Transactional
    public void saveSocialDetails(SocialDetailsDTO dto) {
        SysSocialDetails details = new SysSocialDetails();
        BeanUtil.copyProperties(dto, details);
        
        // 设置关联类型
        if (dto.getCustomerId() != null) {
            details.setOwnerType("customer");
        } else if (dto.getContactId() != null) {
            details.setOwnerType("contact");
        } else if (dto.getStaffId() != null) {
            details.setOwnerType("staff");
        }
        
        save(details);
    }

    @Override
    @Transactional
    public void updateSocialDetails(SocialDetailsDTO dto) {
        SysSocialDetails details = new SysSocialDetails();
        BeanUtil.copyProperties(dto, details);
        updateById(details);
    }

    @Override
    @Transactional
    public void removeSocialDetails(Integer id) {
        // 逻辑删除
        SysSocialDetails details = new SysSocialDetails();
        details.setId(id);
        updateById(details);
    }
}