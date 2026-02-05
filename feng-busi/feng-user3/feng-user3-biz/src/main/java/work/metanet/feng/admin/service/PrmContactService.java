package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.ContactDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.vo.ContactDetailVO;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;

import java.util.List;

public interface PrmContactService extends IService<PrmContact> {

    /**
     * 联系人分页查询
     */
    IPage<ContactDetailVO> pageContact(Page<PrmContact> page, PrmContact contact);

    /**
     * 联系人列表查询（简略信息）
     */
    List<ContactSimpleVO> listContact(PrmContact contact);

    /**
     * 获取联系人详情
     */
    ContactDetailVO getContactDetail(Integer contactId);

    /**
     * 根据客户ID查询联系人
     */
    List<ContactSimpleVO> listByCustomerId(Integer customerId);

    /**
     * 新增联系人
     */
    Boolean saveContact(ContactDTO dto);

    /**
     * 修改联系人
     */
    Boolean updateContact(ContactDTO dto);

    /**
     * 删除联系人
     */
    Boolean removeContact(Integer contactId);
}