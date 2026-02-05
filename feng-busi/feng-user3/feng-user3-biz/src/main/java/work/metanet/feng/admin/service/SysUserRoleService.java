package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色表(SysUserRole)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户Id删除该用户的角色关系
     *
     * @param userId 用户ID
     * @return boolean
     * @author edison
     * @date 2017年12月7日 16:31:38
     */
    Boolean deleteByUserId(Integer userId);

    /**
     * 批量用户分配多角色
     *
     * @param userId:
     * @param roleIds:
     * @return R
     */
    void batchSave(Integer userId, List<Integer> roleIds);
}