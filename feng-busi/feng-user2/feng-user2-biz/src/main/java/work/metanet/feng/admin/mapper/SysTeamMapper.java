package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysTeam;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 小组(SysTeam)表数据库访问层
 *
 * @author edison
 * @since 2023-08-02 09:53:53
 */
@Mapper
public interface SysTeamMapper extends FengBaseMapper<SysTeam> {

}
