package work.metanet.feng.admin.mapper;

import work.metanet.feng.admin.api.entity.SysRoleMenu;
import work.metanet.feng.common.data.datascope.FengBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单表(SysRoleMenu)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysRoleMenuMapper extends FengBaseMapper<SysRoleMenu> {

    /**
     * 删除该角色对应该的菜单集合
     *
     * @param menuIds:
     * @param roleId:
     * @return: void
     **/
    void removeMenuIdsByRoleId(@Param("roleId") Integer roleId,@Param("menuIds") List<Integer> menuIds);
}