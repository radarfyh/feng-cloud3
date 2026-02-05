package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.lang.Dict;
import work.metanet.feng.admin.api.dto.UserDTO;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.vo.UserVO;
import work.metanet.feng.common.data.datascope.DataScope;
import work.metanet.feng.common.data.datascope.FengBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表(SysUser)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Mapper
public interface SysUserMapper extends FengBaseMapper<SysUser> {

    /**
     * 分页查询用户信息（含角色）
     *
     * @param page      分页
     * @param userDTO   查询参数
     * @param dataScope
     * @return list
     */
    IPage<UserVO> getUserVosPage(Page page, @Param("query") UserDTO userDTO, DataScope dataScope);

    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return userVo
     */
    UserVO getUserVoById(@Param("id") Integer id);

    /**
     * 查询用户列表
     *
     * @param userDTO   查询条件
     * @param dataScope 数据权限声明
     * @return
     */
    List<UserVO> selectVoListByScope(@Param("query") UserDTO userDTO, DataScope dataScope);

    /*
     *
     * @Description: 通过角色id获取所有的username集合
     * @author edison
     * @date 2021/1/22
     * @param: roleId
     * @return
     */
    List<SysUser> findRoleByUsers(@Param("roleId") Integer roleId);

    /**
     * 通过科室编码获取用户集合
     *
     * @param deptCode:
     * @return R
     */
    List<SysUser> getListByDeptCode(@Param("organCode") String organCode, @Param("deptCode") String deptCode);

    /**
     * 通过岗位类别获取用户集合
     *
     * @param organCode:
     * @param jobCategory:
     * @return R
     */
    List<SysUser> getListByJobCategory(@Param("organCode") String organCode, @Param("jobCategory") String jobCategory);
    
    @Select(
            "SELECT \r\n"
            + "    COALESCE(COUNT(*), 0) AS totalUser, \r\n"
            + "    COALESCE(SUM(\r\n"
            + "        CASE WHEN \r\n"
            + "            EXTRACT(YEAR FROM create_time) = EXTRACT(YEAR FROM CURRENT_DATE) \r\n"
            + "            AND EXTRACT(MONTH FROM create_time) = EXTRACT(MONTH FROM CURRENT_DATE) \r\n"
            + "        THEN 1 ELSE 0 END\r\n"
            + "    ), 0) AS curUser \r\n"
            + "FROM \r\n"
            + "    sys_user "
    )
    Dict getCount();

}