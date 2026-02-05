package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysPublicParam;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 公共参数配置表(SysPublicParam)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysPublicParamMapper extends FengBaseMapper<SysPublicParam> {

}