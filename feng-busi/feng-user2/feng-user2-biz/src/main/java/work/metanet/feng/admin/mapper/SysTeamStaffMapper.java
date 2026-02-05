package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysTeamStaff;
import work.metanet.feng.common.data.datascope.FengBaseMapper;
import work.metanet.feng.admin.api.entity.SysStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 小组人员关联表(TeamStaff)表数据库访问层
 *
 * @author edison
 * @since 2023-08-02 09:54:38
 */
@Mapper
public interface SysTeamStaffMapper extends FengBaseMapper<SysTeamStaff> {

    IPage<SysStaff> getStaffList(@Param("page") Page page, @Param("teamId") Integer teamId);

    List<SysStaff> getStaffList(@Param("teamId") Integer teamId);
}
