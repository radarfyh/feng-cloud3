package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.CustomerDTO;
import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.vo.CustomerDetailVO;
import work.metanet.feng.admin.api.vo.PrmCustomerVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

/**
 * 客户服务接口
 */
public interface PrmCustomerService {

    /**
     * 分页查询客户信息
     */
    IPage<PrmCustomerVO> customerPage(Page<PrmCustomer> page, CustomerDTO customer);

    /**
     * 查询客户信息列表
     */
	List<PrmCustomerVO> customerList(@Valid CustomerDTO dto);
	
    /**
     * 获取客户详情（带关联数据）
     */
    PrmCustomerVO getCustomerWithRelations(Integer customerId);

    /**
     * 根据名称模糊查询客户
     */
    List<PrmCustomer> searchByName(String name);

    /**
     * 新增/更新客户信息
     */
    Boolean saveCustomer(PrmCustomer customer);

    /**
     * 删除客户（逻辑删除）
     */
    Boolean removeCustomer(Integer customerId);

    /**
     * 获取客户关系网络
     */
    CustomerDetailVO getCustomerNetwork(Integer customerId);

    /**
     * 获取客户详情
     * @param customerId
     * @return
     */
    CustomerDetailVO getCustomerDetail(Integer customerId);

	void updateLastFollowTime(Integer customerId, LocalDateTime followTime);

	void addFollowRecord(Integer customerId, FollowRecordDTO dto);


}
