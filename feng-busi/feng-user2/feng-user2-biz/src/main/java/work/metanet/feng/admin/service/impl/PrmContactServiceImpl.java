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
import work.metanet.feng.admin.api.dto.ContactDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.vo.ContactDetailVO;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;
import work.metanet.feng.admin.api.vo.CustomerSimpleVO;
import work.metanet.feng.admin.mapper.PrmContactMapper;
import work.metanet.feng.admin.mapper.PrmCustomerMapper;
import work.metanet.feng.admin.service.PrmContactService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrmContactServiceImpl extends ServiceImpl<PrmContactMapper, PrmContact> implements PrmContactService {

    private final PrmContactMapper contactMapper;
    private final SysDictService dictService;
    private final PrmCustomerMapper customerMapper;

    @Override
    public IPage<ContactDetailVO> pageContact(Page<PrmContact> page, PrmContact contact) {
        Page<PrmContact> contactPage = page(page, getWrapper(contact));
        IPage<ContactDetailVO> ret = contactPage.convert(this::convertToDetailVO);
        return ret;
    }

    LambdaQueryWrapper<PrmContact> getWrapper(PrmContact contact) {
        // 构建查询条件
        LambdaQueryWrapper<PrmContact> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(contact.getId() != null, PrmContact::getId, contact.getId())
               .eq(contact.getStaffId() != null, PrmContact::getStaffId, contact.getStaffId())
               .eq(contact.getCustomerId() != null, PrmContact::getCustomerId, contact.getCustomerId())
               .eq(contact.getRelationshipType() != null, PrmContact::getRelationshipType, contact.getRelationshipType())
               .like(StrUtil.isNotBlank(contact.getName()), PrmContact::getName, contact.getName())
               .eq(contact.getGender() != null, PrmContact::getGender, contact.getGender())
               .eq(contact.getBirthday() != null, PrmContact::getBirthday, contact.getBirthday())
               .like(StrUtil.isNotBlank(contact.getMobile()), PrmContact::getMobile, contact.getMobile())
               .like(StrUtil.isNotBlank(contact.getPhone()), PrmContact::getPhone, contact.getPhone())
               .like(StrUtil.isNotBlank(contact.getEmail()), PrmContact::getEmail, contact.getEmail())
               .eq(contact.getPosition() != null, PrmContact::getPosition, contact.getPosition())
               .eq(contact.getIsDecisionMaker() != null, PrmContact::getIsDecisionMaker, contact.getIsDecisionMaker())
               .eq(contact.getIsPrimary() != null, PrmContact::getIsPrimary, contact.getIsPrimary())
               .eq(contact.getSuperiorId() != null, PrmContact::getSuperiorId, contact.getSuperiorId())
               .like(StrUtil.isNotBlank(contact.getProvince()), PrmContact::getProvince, contact.getProvince())
               .like(StrUtil.isNotBlank(contact.getCity()), PrmContact::getCity, contact.getCity())
               .like(StrUtil.isNotBlank(contact.getDistrict()), PrmContact::getDistrict, contact.getDistrict())
               .like(StrUtil.isNotBlank(contact.getAddress()), PrmContact::getAddress, contact.getAddress())
               .eq(contact.getContactTime() != null, PrmContact::getContactTime, contact.getContactTime())
               .eq(contact.getVisitTime() != null, PrmContact::getVisitTime, contact.getVisitTime())
               .like(StrUtil.isNotBlank(contact.getLatestStatus()), PrmContact::getLatestStatus, contact.getLatestStatus())
               .eq(contact.getIntimacyScore() != null, PrmContact::getIntimacyScore, contact.getIntimacyScore())
               .eq(contact.getContactFrequency() != null, PrmContact::getContactFrequency, contact.getContactFrequency())
               .eq(StrUtil.isNotBlank(contact.getOrganCode()), PrmContact::getOrganCode, contact.getOrganCode())
               .orderByDesc(PrmContact::getIsPrimary)
               .orderByDesc(PrmContact::getContactTime)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<ContactSimpleVO> listContact(PrmContact contact) {
        // 转换结果
        return list(getWrapper(contact)).stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
    }

    private ContactSimpleVO convertToSimpleVO(PrmContact contact) {
        ContactSimpleVO vo = new ContactSimpleVO();
        BeanUtil.copyProperties(contact, vo);
        
        // 手机号脱敏处理
        //if (StrUtil.isNotBlank(contact.getMobile())) {
        //    vo.setMobile(StrUtil.hide(contact.getMobile(), 3, 7));
        //}
        
        // 处理职位名称
        if (contact.getPosition() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("position");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), contact.getPosition()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setPositionName(label);
        }
        
        // 联系时间格式化
        if (contact.getContactTime() != null) {
            vo.setContactTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(contact.getContactTime()));
        }
        
        return vo;
    }
    
    private ContactSimpleVO convertToSimpleVO2(ContactSimpleVO contact) {
        ContactSimpleVO vo = new ContactSimpleVO();
        BeanUtil.copyProperties(contact, vo);
        
        // 处理职位名称
        if (contact.getPosition() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("position");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), contact.getPosition()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setPositionName(label);
        }
        
        return vo;
    }
    
    @Override
    public ContactDetailVO getContactDetail(Integer contactId) {
        PrmContact contact = getById(contactId);
        return contact != null ? convertToDetailVO(contact) : null;
    }

    @Override
    public List<ContactSimpleVO> listByCustomerId(Integer customerId) {
    	List<ContactSimpleVO> list = contactMapper.selectByCustomerId(customerId);
    	
        return list.stream()
                .map(this::convertToSimpleVO2)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean saveContact(ContactDTO dto) {
        PrmContact contact = BeanUtil.copyProperties(dto, PrmContact.class);
        return save(contact);
    }

    @Override
    public Boolean updateContact(ContactDTO dto) {
        PrmContact contact = BeanUtil.copyProperties(dto, PrmContact.class);
        return updateById(contact);
    }

    @Override
    public Boolean removeContact(Integer contactId) {
    	return (contactMapper.deleteById(contactId) > 0);
    }

    private ContactDetailVO convertToDetailVO(PrmContact contact) {
        ContactDetailVO vo = new ContactDetailVO();
        BeanUtil.copyProperties(contact, vo);
        // 电话号码脱敏
        //if (contact.getMobile() != null) vo.setMobile(DesensitizedUtils.mobilePhone(contact.getMobile()));
        // 处理性别
        if (contact.getGender() != null) vo.setGenderName(contact.getGender().getName());
        // 处理职位名称
        if (contact.getPosition() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("position");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), contact.getPosition()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setPositionName(label);
        }
        // 处理关系名称
        if (contact.getRelationshipType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("contact_relationship_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), contact.getRelationshipType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setRelationshipTypeName(label);
        }
 
        // 加载关联数据
        if (contact.getSuperiorId() != null) {
            vo.setSuperior(convertToSimpleVO(getById(contact.getSuperiorId())));
        }
        if (contact.getCustomerId() != null) {
        	PrmCustomer customer = customerMapper.selectById(contact.getCustomerId());
        	CustomerSimpleVO customerSimple = new CustomerSimpleVO();
        	BeanUtil.copyProperties(customer, customerSimple);
        	vo.setCustomer(customerSimple);
        }
        
        // 生日格式化
        if (contact.getBirthday() != null) {
            vo.setBirthday(contact.getBirthday().toString());
        }
        
        // 联系时间格式化
        if (contact.getVisitTime() != null) {
            vo.setVisitTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(contact.getVisitTime()));
        }
        
        // 创建时间格式化
        if (contact.getCreateTime() != null) {
            vo.setCreateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(contact.getCreateTime()));
        }
        
        // 修改时间格式化
        if (contact.getUpdateTime() != null) {
            vo.setUpdateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(contact.getUpdateTime()));
        }
        
        return vo;
    }
}