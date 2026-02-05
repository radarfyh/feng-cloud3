package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.vo.SysRoleVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色表(SysRole)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysRoleMapper extends FengBaseMapper<SysRole> {

    /**
     * 通过用户ID，查询角色信息
     *
     * @param userId
     * @return
     */
    List<SysRoleVO> listRolesByUserId(@Param("userId") Integer userId);

    /**
     * 通过用户ID，查询角色id集合
     *
     * @param userId
     * @return
     */
    List<Integer> getRoleIdsByUserId(@Param("userId") Integer userId);

    /**
     * 查出当前角色对应该应用下的所有权限
     *
     * @param applicationCode:
     * @param roleId:
     * @return: java.util.List<java.lang.Integer>
     **/
    List<Integer> selectMenuIdListByRoleId(@Param("applicationCode") String applicationCode, @Param("roleId") Integer roleId);

    /**
     * 获取所有角色列表
     *
     * @return R
     */
    List<SysRoleVO> listAll();

}