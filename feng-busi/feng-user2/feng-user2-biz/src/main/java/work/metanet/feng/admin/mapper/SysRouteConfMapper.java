package work.metanet.feng.admin.mapper;

import work.metanet.feng.common.data.datascope.FengBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysRouteConf;

/**
 * 路由配置表(SysRouteConf)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Mapper
public interface SysRouteConfMapper extends FengBaseMapper<SysRouteConf> {

}