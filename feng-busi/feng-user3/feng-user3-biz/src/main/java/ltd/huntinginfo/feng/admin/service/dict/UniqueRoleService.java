package ltd.huntinginfo.feng.admin.service.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueRoleInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueRole;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueRoleInfoVO;

import java.util.List;

/**
 * 统一角色信息表 服务接口
 */
public interface UniqueRoleService extends BaseDictService<UniqueRole> {

    /**
     * 根据ID查询角色详情
     */
    UniqueRoleInfoVO getById(Integer id);

    /**
     * 分页查询角色列表
     */
    IPage<UniqueRoleInfoVO> page(IPage page, UniqueRoleInfoDTO uniqueRoleInfo);

    /**
     * 查询角色列表
     */
    List<UniqueRoleInfoVO> list(UniqueRoleInfoDTO uniqueRoleInfo);

    /**
     * 新增角色信息
     */
    boolean save(UniqueRoleInfoDTO uniqueRoleInfo);

    /**
     * 批量新增角色信息
     */
    boolean saveBatch(List<UniqueRoleInfoDTO> uniqueRoleInfos);

    /**
     * 更新角色信息
     */
    boolean updateById(UniqueRoleInfoDTO uniqueRoleInfo);

    /**
     * 删除角色信息
     */
    boolean removeById(Integer id);
}
