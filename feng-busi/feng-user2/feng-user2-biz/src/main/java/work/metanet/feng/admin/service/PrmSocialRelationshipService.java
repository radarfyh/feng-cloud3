package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.SocialRelationshipDTO;
import work.metanet.feng.admin.api.entity.PrmSocialRelationship;
import work.metanet.feng.admin.api.vo.ContactCustomerRelationshipVO;
import work.metanet.feng.admin.api.vo.SocialRelationshipVO;

import java.util.List;

public interface PrmSocialRelationshipService {

    /**
     * 分页查询关系网络
     */
    IPage<SocialRelationshipVO> pageRelationship(Page<PrmSocialRelationship> page, SocialRelationshipDTO dto);
    /**
     * 查询关系网络列表
     */
    List<SocialRelationshipVO> listRelationship(SocialRelationshipDTO dto);

    /**
     * 获取关系详情
     */
    SocialRelationshipVO getRelationshipDetail(Integer id);

    /**
     * 新增联系人关系
     */
    void saveRelationship(SocialRelationshipDTO dto);

    /**
     * 修改联系人关系
     */
    void updateRelationship(SocialRelationshipDTO dto);

    /**
     * 删除联系人关系
     */
    void removeRelationship(Integer id);

    /**
     * 查询联系人A的所有关系
     */
    List<SocialRelationshipVO> listByContactA(Integer contactAId);

    /**
     * 查询联系人B的所有关系
     */
    List<SocialRelationshipVO> listByContactB(Integer contactBId);
    
    /**
     * 查询用户可见的联系人关系网络
     * @param userId 系统用户ID
     * @param includeDiscrete 是否包含离散个人关系
     */
    Page<ContactCustomerRelationshipVO> getVisibleRelationships(Integer userId, boolean includeDiscrete, Page page);
    
    /**
     * 查询特定客户相关的所有关系
     */
    Page<ContactCustomerRelationshipVO> getRelationshipsByCustomer(Integer customerId, Page page);
    
    /**
     * 查询两个联系人之间的路径关系
     */
    List<ContactCustomerRelationshipVO> findRelationshipPath(Integer contactAId, Integer contactBId, int maxDepth);

	/**
	 * 检查用户是否有权查看特定关系
	 */
	boolean checkRelationshipAccess(Integer userId, Integer relationshipId);
}