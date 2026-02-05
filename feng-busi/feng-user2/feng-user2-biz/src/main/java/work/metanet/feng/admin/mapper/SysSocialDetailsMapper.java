package work.metanet.feng.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.SocialDetailsDTO;
import work.metanet.feng.admin.api.entity.SysSocialDetails;
import work.metanet.feng.admin.api.vo.SocialDetailsVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

@Mapper
public interface SysSocialDetailsMapper extends FengBaseMapper<SysSocialDetails> {

    IPage<SocialDetailsVO> pageSocialDetails(Page<SysSocialDetails> page, SocialDetailsDTO dto);
    
    /**
     * 根据所有者ID和类型查询社交账号
     * @param ownerId 所有者ID
     * @param ownerType 所有者类型(staff/customer/contact)
     * @return 社交账号列表
     */
    List<SocialDetailsVO> selectByOwner(@Param("ownerId") Integer ownerId, @Param("ownerType") String ownerType);
}