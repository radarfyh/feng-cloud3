package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysProjectStaff;
import org.apache.ibatis.annotations.Param;

/**
 * 项目成员表(ProjectStaff)表数据库访问层
 *
 * @author edison
 * @since 2023-01-31 09:46:50
 */
@Mapper
public interface SysProjectStaffMapper extends FengBaseMapper<SysProjectStaff> {

    IPage<SysStaff> getStaffListByProjectId(Page page, @Param("organCode") String organCode, @Param("projectId") Integer projectId, @Param("roleId") Integer roleId);
}
