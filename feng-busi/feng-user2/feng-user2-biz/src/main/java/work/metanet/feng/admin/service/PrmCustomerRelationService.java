package work.metanet.feng.admin.service;

import work.metanet.feng.admin.api.dto.CustomerRelationDTO;
import work.metanet.feng.admin.api.entity.PrmCustomerRelation;
import work.metanet.feng.admin.api.vo.CustomerRelationVO;
import work.metanet.feng.common.core.util.R;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PrmCustomerRelationService extends IService<PrmCustomerRelation> {

    /**
     * 创建或更新客户关系
     */
    CustomerRelationVO saveRelation(CustomerRelationDTO dto);

    /**
     * 删除客户关系
     */
    void removeRelation(Integer id);

    /**
     * 查询客户的所有关系
     */
    List<CustomerRelationVO> listRelations(Integer customerId);

    /**
     * 查询两个客户间的关系
     */
    List<CustomerRelationVO> getRelationBetweenCustomers(Integer customerId, Integer relatedCustomerId);

    /**
     * 重新计算关系强度
     */
    void recalculateStrength(Integer customerId);

	IPage<CustomerRelationVO> pageRelationship(Page<PrmCustomerRelation> page, CustomerRelationDTO dto);
	
	List<CustomerRelationVO> listRelationship(CustomerRelationDTO dto);
}