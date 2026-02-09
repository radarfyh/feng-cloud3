package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import ltd.huntinginfo.feng.center.api.dto.MsgAppPermissionDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgAppPermissionVO;

import java.util.List;

public interface MsgAppPermissionService {

    /**
     * 根据ID查询权限
     */
	MsgAppPermissionVO getById(String id);

    /**
     * 分页查询权限列表
     */
    IPage<MsgAppPermissionVO> page(IPage page, MsgAppPermissionDTO dto);

    /**
     * 查询应用的有效权限列表
     */
    List<MsgAppPermissionVO> listActivePermissions(String appKey);

    /**
     * 新增权限
     */
    boolean save(MsgAppPermissionDTO dto);

    /**
     * 批量新增权限
     */
    boolean saveBatch(List<MsgAppPermissionDTO> dtos);

    /**
     * 更新权限
     */
    boolean updateById(MsgAppPermissionDTO dto);

    /**
     * 删除权限
     */
    boolean removeById(String id);

    /**
     * 校验权限是否存在
     */
    boolean checkPermissionExists(String appKey, String resourceCode);

	List<MsgAppPermissionVO> list(MsgAppPermissionDTO dto);
}