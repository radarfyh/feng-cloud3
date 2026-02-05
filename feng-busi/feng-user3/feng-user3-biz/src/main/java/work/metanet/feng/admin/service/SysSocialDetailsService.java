package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.SocialDetailsDTO;
import work.metanet.feng.admin.api.entity.SysSocialDetails;
import work.metanet.feng.admin.api.vo.SocialDetailsVO;

import java.util.List;

public interface SysSocialDetailsService {

    IPage<SocialDetailsVO> pageSocialDetails(Page<SysSocialDetails> page, SocialDetailsDTO dto);
    
    List<SocialDetailsVO> listSocialDetails(SocialDetailsDTO dto);

    List<SocialDetailsVO> listByCustomer(Integer customerId);

    List<SocialDetailsVO> listByContact(Integer contactId);

    List<SocialDetailsVO> listByStaff(Integer staffId);

    SocialDetailsVO getSocialDetails(Integer id);

    void saveSocialDetails(SocialDetailsDTO dto);

    void updateSocialDetails(SocialDetailsDTO dto);

    void removeSocialDetails(Integer id);
}