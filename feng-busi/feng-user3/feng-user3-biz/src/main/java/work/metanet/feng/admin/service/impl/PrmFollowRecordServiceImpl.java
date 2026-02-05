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
import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.PrmFollowRecord;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.vo.FollowRecordVO;
import work.metanet.feng.admin.api.vo.UserVO;
import work.metanet.feng.admin.mapper.PrmContactMapper;
import work.metanet.feng.admin.mapper.PrmCustomerMapper;
import work.metanet.feng.admin.mapper.PrmFollowRecordMapper;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.mapper.SysUserMapper;
import work.metanet.feng.admin.service.PrmContactService;
import work.metanet.feng.admin.service.PrmFollowRecordService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.SecurityUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrmFollowRecordServiceImpl extends ServiceImpl<PrmFollowRecordMapper, PrmFollowRecord> 
    implements PrmFollowRecordService {

    private final SysUserMapper userMapper;
    private final SysStaffMapper staffMapper;
    private final SysDictService dictService;
    private final PrmContactMapper contactMapper;
    private final PrmCustomerMapper customerMapper;

    @Override
    public IPage<FollowRecordVO> pageFollowRecord(Page<PrmFollowRecord> page, FollowRecordDTO dto) {
        return page(page, getWrapper(dto)).convert(this::convertToVO);
    }
    
    LambdaQueryWrapper<PrmFollowRecord> getWrapper(FollowRecordDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<PrmFollowRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(dto.getId() != null, PrmFollowRecord::getId, dto.getId())
               .eq(dto.getRelationshipId() != null, PrmFollowRecord::getRelationshipId, dto.getRelationshipId())
               .eq(dto.getCustomerId() != null, PrmFollowRecord::getCustomerId, dto.getCustomerId())
               .eq(dto.getContactId() != null, PrmFollowRecord::getContactId, dto.getContactId())
               .eq(dto.getFollowType() != null, PrmFollowRecord::getFollowType, dto.getFollowType())
               .like(StrUtil.isNotBlank(dto.getContent()), PrmFollowRecord::getContent, dto.getContent())
               .like(StrUtil.isNotBlank(dto.getResult()), PrmFollowRecord::getResult, dto.getResult())
               .ge(dto.getFollowTime() != null, PrmFollowRecord::getFollowTime, dto.getFollowTime())
               .ge(dto.getNextFollowTime() != null, PrmFollowRecord::getNextFollowTime, dto.getNextFollowTime())
               .eq(dto.getStaffId() != null, PrmFollowRecord::getStaffId, dto.getStaffId())
               .eq(dto.getStaffName() != null, PrmFollowRecord::getStaffName, dto.getStaffName())
               .orderByDesc(PrmFollowRecord::getFollowTime)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<FollowRecordVO> listFollowRecord(FollowRecordDTO dto) {
        return list(getWrapper(dto)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private FollowRecordVO convertToVO(PrmFollowRecord followRecord) {
    	FollowRecordVO vo = new FollowRecordVO();
        BeanUtil.copyProperties(followRecord, vo);
        
        // 处理跟踪方式名称
        if (followRecord.getFollowType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("follow_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), followRecord.getFollowType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setFollowTypeName(label);
        }
        
        // 跟踪时间格式化
        if (followRecord.getFollowTime() != null) {
            vo.setFollowTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(followRecord.getFollowTime()));
        }
        
        // 下次跟踪时间格式化
        if (followRecord.getNextFollowTime() != null) {
            vo.setNextFollowTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(followRecord.getNextFollowTime()));
        }
        
        // 创建时间格式化
        if (followRecord.getCreateTime() != null) {
            vo.setCreateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(followRecord.getCreateTime()));
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
    public FollowRecordVO getFollowRecordDetail(Integer id) {
        PrmFollowRecord record = getById(id);
        return record != null ? convertToVO(record) : null;
    }

    @Override
    @Transactional
    public void saveFollowRecord(FollowRecordDTO dto) {
        PrmFollowRecord record = new PrmFollowRecord();
        BeanUtil.copyProperties(dto, record);
        
        Integer userId = SecurityUtils.getUser().getId();
        UserVO user = userMapper.getUserVoById(userId);
        // 自动填充当前用户信息
        Integer staffId = user.getStaffId();
        if (staffId != null) {
        	SysStaff staff = staffMapper.selectById(staffId);
        	record.setStaffId(staffId);
        	record.setStaffName(staff.getStaffName());
        }
        save(record);
        
        // 更新关联的关系表的最后联系时间
        if (dto.getRelationshipId() != null) {
            updateRelationshipContactTime(dto.getRelationshipId());
        }
    }

    @Override
    @Transactional
    public void updateFollowRecord(FollowRecordDTO dto) {
        PrmFollowRecord record = new PrmFollowRecord();
        BeanUtil.copyProperties(dto, record);
        updateById(record);
    }

    @Override
    @Transactional
    public void removeFollowRecord(Integer id) {
        PrmFollowRecord record = new PrmFollowRecord();
        record.setId(id);
        updateById(record);
    }

    @Override
    public List<FollowRecordVO> listByCustomerId(Integer customerId) {
        return list(new LambdaQueryWrapper<PrmFollowRecord>()
                .eq(PrmFollowRecord::getCustomerId, customerId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<FollowRecordVO> listByContactId(Integer contactId) {
        return list(new LambdaQueryWrapper<PrmFollowRecord>()
                .eq(PrmFollowRecord::getContactId, contactId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private void updateRelationshipContactTime(Integer relationshipId) {
        PrmFollowRecord record = new PrmFollowRecord();
        record.setId(relationshipId);
        record.setFollowTime(LocalDateTime.now());
        updateById(record);
    }
}