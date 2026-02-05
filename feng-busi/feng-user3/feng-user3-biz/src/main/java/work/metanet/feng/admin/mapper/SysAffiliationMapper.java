package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysAffiliation;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 联盟信息表(SysAffiliation)表数据库访问层
 *
 * @author edison
 * @since 2023-08-02 09:50:08
 */
@Mapper
public interface SysAffiliationMapper extends FengBaseMapper<SysAffiliation> {

}
