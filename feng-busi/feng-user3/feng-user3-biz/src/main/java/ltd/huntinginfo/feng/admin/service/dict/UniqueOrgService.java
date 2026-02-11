package ltd.huntinginfo.feng.admin.service.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;

import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueOrgInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueOrg;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueOrgInfoVO;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueOrgTreeVO;

import java.util.List;

/**
 * 统一机构信息表 服务接口
 */
public interface UniqueOrgService extends BaseDictService<UniqueOrg> {

    /**
     * 根据ID查询机构详情
     */
    UniqueOrgInfoVO getById(String id);

    /**
     * 分页查询机构列表
     */
    IPage<UniqueOrgInfoVO> page(IPage page, UniqueOrgInfoDTO uniqueOrgInfo);

    /**
     * 查询机构列表
     */
    List<UniqueOrgInfoVO> list(UniqueOrgInfoDTO uniqueOrgInfo);
    
    /**
     * 查询机构树
     */
    List<UniqueOrgTreeVO> getOrgTree();

    /**
     * 新增机构信息
     */
    boolean save(UniqueOrgInfoDTO uniqueOrgInfo);

    /**
     * 批量新增机构信息
     */
    boolean saveBatch(List<UniqueOrgInfoDTO> uniqueOrgInfos);

    /**
     * 更新机构信息
     */
    boolean updateById(UniqueOrgInfoDTO uniqueOrgInfo);

    /**
     * 删除机构信息
     */
    boolean removeById(String id);
}
