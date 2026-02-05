package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysConfig;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 系统配置表(SysConfig)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysConfigMapper extends FengBaseMapper<SysConfig> {

}