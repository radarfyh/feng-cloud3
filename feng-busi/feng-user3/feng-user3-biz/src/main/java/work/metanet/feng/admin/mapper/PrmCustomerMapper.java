package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.CustomerDTO;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.vo.CustomerSimpleVO;
import work.metanet.feng.admin.api.vo.PrmCustomerVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户表(prm_customer)数据库访问层
 *
 * @author EdisonFeng
 * @since 2025-06-01
 */
@Mapper
public interface PrmCustomerMapper extends FengBaseMapper<PrmCustomer> {

    /**
     * 分页查询客户信息（带关联数据）
     *
     * @param page      分页参数
     * @param customer  查询条件
     * @return 客户分页列表（包含扩展信息）
     */
    Page<PrmCustomerVO> selectCustomerPage(Page page, @Param("customer") CustomerDTO customer);

    /**
     * 根据客户名称模糊查询
     *
     * @param name 客户名称（模糊匹配）
     * @return 客户列表
     */
    List<PrmCustomer> selectByNameLike(@Param("name") String name);

    /**
     * 获取客户关系网络
     *
     * @param customerId 客户ID
     * @return 客户及其关联客户信息
     */
    PrmCustomerVO selectWithRelations(@Param("customerId") Integer customerId);

	List<Integer> getAccessibleCustomerIds(Integer userId);

	CustomerSimpleVO getSimpleCustomerInfo(Integer customerId);
}
