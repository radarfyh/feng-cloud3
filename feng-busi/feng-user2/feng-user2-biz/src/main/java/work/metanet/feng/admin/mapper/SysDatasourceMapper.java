package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 数据源表(SysDatasource)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysDatasourceMapper extends FengBaseMapper<SysDatasource> {

}