package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import work.metanet.feng.admin.api.entity.SysMenu;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import java.util.List;

/**
 * 菜单权限表(SysMenu)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysMenuMapper extends FengBaseMapper<SysMenu> {

    /**
     * 通过角色编号查询菜单
     * @param roleId 角色ID
     * @return
     */
    List<SysMenu> listMenusByRoleId(@Param("roleId") Integer roleId);
    
    List<SysMenu> listMenusByRoleIdAndAppCode(
    	    @Param("roleId") Integer roleId, 
    	    @Param("appCode") String appCode, 
    	    @Param("parentId") Integer parentId
    	);

}