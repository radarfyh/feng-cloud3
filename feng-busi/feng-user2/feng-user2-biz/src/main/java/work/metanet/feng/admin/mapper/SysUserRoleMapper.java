package work.metanet.feng.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysUserRole;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色表(SysUserRole)表数据库访问层
 */
@Mapper
public interface SysUserRoleMapper extends FengBaseMapper<SysUserRole> {

    /**
     * 根据用户Id删除该用户的角色关系
     */
    Boolean deleteByUserId(@Param("userId") Integer userId);

    void batchSave(@Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds);
}