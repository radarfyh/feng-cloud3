package work.metanet.feng.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.SocialRelationshipDTO;
import work.metanet.feng.admin.api.entity.PrmSocialRelationship;
import work.metanet.feng.admin.api.vo.ContactCustomerRelationshipVO;
import work.metanet.feng.admin.api.vo.SocialRelationshipVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

@Mapper
public interface PrmSocialRelationshipMapper extends FengBaseMapper<PrmSocialRelationship> {

    Page<SocialRelationshipVO> pageRelationship(Page<SocialRelationshipDTO> page, SocialRelationshipDTO dto);

    SocialRelationshipVO getRelationshipDetail(Integer id);

	Page<PrmSocialRelationship> selectVisibleRelationships(List<Integer> accessibleCustomerIds, boolean includeDiscrete,
			Page page);

	Page<ContactCustomerRelationshipVO> getRelationsByContacts(List<Integer> contactIds, Page page);

	List<PrmSocialRelationship> getContactRelations(Integer current);

	ContactCustomerRelationshipVO getRelationshipBetweenContacts(Integer contactA, Integer contactB);
}