package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysUserRoleMapper;
import work.metanet.feng.admin.api.entity.SysUserRole;
import work.metanet.feng.admin.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色表(SysUserRole)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Service
@AllArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {


    /**
     * 根据用户Id删除该用户的角色关系
     * @param userId 用户ID
     * @return boolean
     * @author edison
     * @date 2017年12月7日 16:31:38
     */
    @Override
    public Boolean deleteByUserId(Integer userId) {
        return baseMapper.deleteByUserId(userId);
    }

    @Override
    public void batchSave(Integer userId, List<Integer> roleIds) {
        baseMapper.batchSave(userId,roleIds);
    }

}