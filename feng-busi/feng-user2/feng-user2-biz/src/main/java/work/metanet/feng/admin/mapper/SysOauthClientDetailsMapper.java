package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 终端信息表(SysOauthClientDetails)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysOauthClientDetailsMapper extends FengBaseMapper<SysOauthClientDetails> {

}