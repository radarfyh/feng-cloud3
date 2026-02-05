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
import work.metanet.feng.admin.api.dto.SocialRelationshipDTO;
import work.metanet.feng.admin.api.entity.PrmSocialRelationship;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.api.vo.ContactCustomerRelationshipVO;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;
import work.metanet.feng.admin.api.vo.SocialRelationshipVO;
import work.metanet.feng.admin.mapper.PrmContactMapper;
import work.metanet.feng.admin.mapper.PrmCustomerMapper;
import work.metanet.feng.admin.mapper.PrmSocialRelationshipMapper;
import work.metanet.feng.admin.service.PrmSocialRelationshipService;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrmSocialRelationshipServiceImpl extends ServiceImpl<PrmSocialRelationshipMapper, PrmSocialRelationship> 
    implements PrmSocialRelationshipService {

    private final PrmContactMapper contactMapper;
    private final PrmSocialRelationshipMapper relationshipMapper;
    private final PrmCustomerMapper customerMapper;
    private final SysDictService dictService;

    @Override
    public IPage<SocialRelationshipVO> pageRelationship(Page<PrmSocialRelationship> page, SocialRelationshipDTO dto) {
        //return relationshipMapper.pageRelationship(page, dto);
    	
    	return page(page, getWrapper(dto)).convert(this::convertToVO);
    }
    
    LambdaQueryWrapper<PrmSocialRelationship> getWrapper(SocialRelationshipDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<PrmSocialRelationship> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(dto.getId() != null, PrmSocialRelationship::getId, dto.getId())
               .eq(dto.getContactA() != null, PrmSocialRelationship::getContactA, dto.getContactA())
               .eq(dto.getContactB() != null, PrmSocialRelationship::getContactB, dto.getContactB())
               .eq(dto.getRelationshipType() != null, PrmSocialRelationship::getRelationshipType, dto.getRelationshipType())
               .eq(dto.getBiDirectional() != null, PrmSocialRelationship::getBiDirectional, dto.getBiDirectional())
               .eq(dto.getIntimacyScore() != null, PrmSocialRelationship::getIntimacyScore, dto.getIntimacyScore())
               .le(dto.getContactTime() != null, PrmSocialRelationship::getContactTime, dto.getContactTime())
               .like(StrUtil.isNotBlank(dto.getRemark()), PrmSocialRelationship::getRemark, dto.getRemark())
               .orderByDesc(PrmSocialRelationship::getContactTime)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<SocialRelationshipVO> listRelationship(SocialRelationshipDTO dto) {
        return list(getWrapper(dto)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private SocialRelationshipVO convertToVO(PrmSocialRelationship socialRelation) {
    	SocialRelationshipVO vo = new SocialRelationshipVO();
        BeanUtil.copyProperties(socialRelation, vo);
        
        // 处理关系类型名称
        if (socialRelation.getRelationshipType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("contact_relationship_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), socialRelation.getRelationshipType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setRelationshipTypeName(label);
        }
        
        // 联系时间格式化
        if (socialRelation.getContactTime() != null) {
            vo.setContactTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(socialRelation.getContactTime()));
        }
        
        // 处理联系人信息
        vo.setContactAInfo(contactMapper.selectContactDetail(socialRelation.getContactA()));
        vo.setContactBInfo(contactMapper.selectContactDetail(socialRelation.getContactB()));
        
        return vo;
    }

    @Override
    public SocialRelationshipVO getRelationshipDetail(Integer id) {
        SocialRelationshipVO vo = relationshipMapper.getRelationshipDetail(id);
        
        // 处理关系类型名称
        if (vo.getRelationshipType() != null) {
        	R<List<SysDictItem>> items = dictService.getDictByType("contact_relationship_type");
        	String label = items.getData().stream()
        	    .filter(e -> StrUtil.equalsIgnoreCase(e.getValue(), vo.getRelationshipType()))
        	    .findFirst()
        	    .map(SysDictItem::getLabel)  // 如果存在则提取label
        	    .orElse(null);               // 不存在则返回null
        	vo.setRelationshipTypeName(label);
        }
        
        if (vo != null) {
            vo.setContactAInfo(contactMapper.selectContactDetail(vo.getContactAInfo().getId()));
            vo.setContactBInfo(contactMapper.selectContactDetail(vo.getContactBInfo().getId()));
        }
        return vo;
    }

    @Override
    public void saveRelationship(SocialRelationshipDTO dto) {
        PrmSocialRelationship relationship = new PrmSocialRelationship();
        BeanUtil.copyProperties(dto, relationship);
        save(relationship);
    }

    @Override
    public void updateRelationship(SocialRelationshipDTO dto) {
        PrmSocialRelationship relationship = new PrmSocialRelationship();
        BeanUtil.copyProperties(dto, relationship);
        updateById(relationship);
    }

    @Override
    public void removeRelationship(Integer id) {
        removeById(id);
    }

    @Override
    public List<SocialRelationshipVO> listByContactA(Integer contactAId) {
        return list(new LambdaQueryWrapper<PrmSocialRelationship>()
                .eq(PrmSocialRelationship::getContactA, contactAId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<SocialRelationshipVO> listByContactB(Integer contactBId) {
        return list(new LambdaQueryWrapper<PrmSocialRelationship>()
                .eq(PrmSocialRelationship::getContactB, contactBId))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ContactCustomerRelationshipVO> getVisibleRelationships(Integer userId, boolean includeDiscrete, Page page) {
        // 1. 获取用户有权限访问的客户ID列表
        List<Integer> accessibleCustomerIds = customerMapper.getAccessibleCustomerIds(userId);
        
        // 2. 查询关系数据
        Page<PrmSocialRelationship> relationships = relationshipMapper.selectVisibleRelationships(
            accessibleCustomerIds, 
            includeDiscrete, 
            page
        );
        
        // 3. 转换为视图对象
        IPage<ContactCustomerRelationshipVO> ret = relationships.convert(this::convertToContactCustomerRelationshipVO);
        return (Page<ContactCustomerRelationshipVO>) ret;
    }
    
    private ContactCustomerRelationshipVO convertToContactCustomerRelationshipVO(PrmSocialRelationship relationship) {
        ContactCustomerRelationshipVO vo = new ContactCustomerRelationshipVO();
        // 填充联系人A/B信息
        vo.setContactA(contactMapper.getSimpleContactInfo(relationship.getContactA()));
        vo.setContactB(contactMapper.getSimpleContactInfo(relationship.getContactB()));
        
        // 填充关联客户信息（如果存在）
        if(vo.getContactA().getCustomerId() != null) {
            vo.setCustomerA(customerMapper.getSimpleCustomerInfo(vo.getContactA().getCustomerId()));
        }
        if(vo.getContactB().getCustomerId() != null) {
            vo.setCustomerB(customerMapper.getSimpleCustomerInfo(vo.getContactB().getCustomerId()));
        }
        
        // 填充其他字段
        vo.setRelationshipId(relationship.getId());
        vo.setRelationshipType(relationship.getRelationshipType());
        vo.setIntimacyScore(relationship.getIntimacyScore());
        vo.setLastContactTime(relationship.getContactTime());
        
        return vo;
    }

    @Override
    public Page<ContactCustomerRelationshipVO> getRelationshipsByCustomer(Integer customerId, Page page) {
        // 1. 获取客户所有联系人
        List<Integer> contactIds = contactMapper.getContactIdsByCustomer(customerId);
        if (contactIds.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }
        
        // 2. 获取这些联系人的直接关系
        return relationshipMapper.getRelationsByContacts(contactIds, page);
    }

    @Override
    public List<ContactCustomerRelationshipVO> findRelationshipPath(Integer contactAId, Integer contactBId, int maxDepth) {
        // 使用广度优先搜索算法
        Map<Integer, Integer> predecessor = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> depthMap = new HashMap<>();
        
        queue.offer(contactAId);
        visited.add(contactAId);
        depthMap.put(contactAId, 0);
        
        while(!queue.isEmpty()) {
        	Integer current = queue.poll();
            int currentDepth = depthMap.get(current);
            
            if (currentDepth >= maxDepth) {
                continue;
            }
            
            // 获取当前联系人的所有关系
            List<PrmSocialRelationship> relations = relationshipMapper.getContactRelations(current);
                
            for(PrmSocialRelationship rel : relations) {
            	Integer neighbor = rel.getContactA().equals(current) ? 
                    rel.getContactB() : rel.getContactA();
                    
                if(!visited.contains(neighbor)) {
                    predecessor.put(neighbor, current);
                    visited.add(neighbor);
                    depthMap.put(neighbor, currentDepth + 1);
                    queue.offer(neighbor);
                    
                    if(neighbor.equals(contactBId)) {
                        // 找到目标，构建路径
                        return buildPath(contactAId, contactBId, predecessor);
                    }
                }
            }
        }
        
        return Collections.emptyList();
    }

    private List<ContactCustomerRelationshipVO> buildPath(Integer sourceContactId, Integer targetContactId, 
                                                        Map<Integer, Integer> predecessor) {
        LinkedList<Integer> path = new LinkedList<>();
        Integer current = targetContactId;
        
        // 反向构建路径
        while (current != null && !current.equals(sourceContactId)) {
            path.addFirst(current);
            current = predecessor.get(current);
        }
        if (current != null) {
            path.addFirst(current);
        }
        
        // 转换为关系视图对象
        List<ContactCustomerRelationshipVO> result = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
        	Integer contactA = path.get(i);
        	Integer contactB = path.get(i + 1);
            ContactCustomerRelationshipVO relationship = relationshipMapper
                .getRelationshipBetweenContacts(contactA, contactB);
            if (relationship != null) {
                result.add(relationship);
            }
        }
        
        return result;
    }

    @Override
    public boolean checkRelationshipAccess(Integer userId, Integer relationshipId) {
        // 1. 获取关系数据
        PrmSocialRelationship relationship = relationshipMapper.selectById(relationshipId);
        if (relationship == null) {
            return false;
        }
        
        // 2. 获取关联客户ID
        Set<Integer> customerIds = new HashSet<>();
        ContactSimpleVO contactA = contactMapper.getSimpleContactInfo(relationship.getContactA());
        ContactSimpleVO contactB = contactMapper.getSimpleContactInfo(relationship.getContactB());
        
        if(contactA != null && contactA.getCustomerId() != null) {
            customerIds.add(contactA.getCustomerId());
        }
        if(contactB != null && contactB.getCustomerId() != null) {
            customerIds.add(contactB.getCustomerId());
        }
        
        // 3. 如果没有关联客户，检查用户是否有权查看离散关系
        if(customerIds.isEmpty()) {
            // 假设有UserService提供此功能
            // return userService.canViewDiscreteRelations(userId);
            return false; // 默认不允许查看离散关系
        }
        
        // 4. 检查用户是否有权访问这些客户
        // 假设有CustomerAccessService提供此功能
        return customerIds.stream()
            .allMatch(cid -> {
                // return customerAccessService.hasAccess(userId, cid);
                return true; // 默认允许访问
            });
    }

    private Set<Integer> extractRelatedContactIds(List<ContactCustomerRelationshipVO> directRelations) {
        Set<Integer> relatedContactIds = new HashSet<>();
        for (ContactCustomerRelationshipVO relation : directRelations) {
            if (relation.getContactA() != null) {
                relatedContactIds.add(relation.getContactA().getId());
            }
            if (relation.getContactB() != null) {
                relatedContactIds.add(relation.getContactB().getId());
            }
        }
        return relatedContactIds;
    }
}