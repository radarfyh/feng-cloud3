package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.SysRoleApplication;
import work.metanet.feng.common.data.datascope.FengBaseMapper;
import work.metanet.feng.admin.api.entity.SysApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色外部应用关联表(RoleApplication)表数据库访问层
 *
 * @author edison
 * @since 2022-08-09 10:36:06
 */
@Mapper
public interface SysRoleApplicationMapper extends FengBaseMapper<SysRoleApplication> {

    /**
     * 根据角色id配置外部应用限
     *
     * @param roleId:                 角色id
     * @param applicationIds:外部应用id集合
     * @return R
     */
    void saveRoleApplications(@Param("roleId") Integer roleId, @Param("applicationIds") List<Integer> applicationIds);

    /**
     * 获取外部应用
     *
     * @param userId:
     * @return R
     */
    List<SysApplication> getApplicationList(Integer userId);
}
