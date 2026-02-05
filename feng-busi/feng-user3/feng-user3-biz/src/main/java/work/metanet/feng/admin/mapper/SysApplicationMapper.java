package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用系统表(SysApplication)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysApplicationMapper extends FengBaseMapper<SysApplication> {

    List<SysApplicationVO> getAppListByUserId(@Param("userId") Integer userId);

    List<SysApplicationVO> getAppListByRoleId(@Param("roleId") Integer roleId);

    List<SysApplication> getApplicationListByUserId(@Param("userId") Integer userId);

    IPage<SysApplication> getApplicationByUserPage(@Param("page") Page page, @Param("userId") Integer userId, @Param("sysApplication") SysApplication sysApplication);
}