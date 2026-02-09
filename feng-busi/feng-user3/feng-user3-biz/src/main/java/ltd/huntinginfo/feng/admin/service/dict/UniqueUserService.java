package ltd.huntinginfo.feng.admin.service.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;

import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;

import java.util.List;

/**
 * 统一用户信息表 服务接口
 */
public interface UniqueUserService extends BaseDictService<UniqueUser> {

    /**
     * 根据ID查询用户详情
     */
    UniqueUserInfoVO getById(Integer id);

    /**
     * 分页查询用户列表
     */
    IPage<UniqueUserInfoVO> page(IPage page, UniqueUserInfoDTO dictUserInfo);

    /**
     * 查询用户列表
     */
    List<UniqueUserInfoVO> list(UniqueUserInfoDTO dictUserInfo);

    /**
     * 新增用户信息
     */
    boolean save(UniqueUserInfoDTO dictUserInfo);

    /**
     * 批量新增用户信息
     */
    boolean saveBatch(List<UniqueUserInfoDTO> dictUserInfos);

    /**
     * 更新用户信息
     */
    boolean updateById(UniqueUserInfoDTO dictUserInfo);

    /**
     * 删除用户信息
     */
    boolean removeById(Integer id);
}