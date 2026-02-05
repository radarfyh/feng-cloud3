package work.metanet.feng.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import work.metanet.feng.common.data.datascope.FengBaseMapper;
import work.metanet.feng.admin.api.entity.PrmContact;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.vo.ContactDetailVO;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;

/**
 * 联系人表(PrmContact)Mapper接口
 */
@Mapper
public interface PrmContactMapper extends FengBaseMapper<PrmContact> {

    /**
     * 分页查询联系人（带关联数据）
     */
    IPage<ContactDetailVO> selectContactPage(Page<PrmContact> page, @Param("query") PrmContact query);

    /**
     * 根据客户ID查询联系人列表
     */
    List<ContactSimpleVO> selectByCustomerId(@Param("customerId") Integer customerId);

    /**
     * 获取联系人详情（带上级信息）
     */
    ContactDetailVO selectContactDetail(@Param("contactId") Integer contactId);

	ContactSimpleVO getSimpleContactInfo(Integer contactA);

	List<Integer> getContactIdsByCustomer(Integer customerId);
	
	List<ContactSimpleVO> listByCustomerId(Integer customerId);
}