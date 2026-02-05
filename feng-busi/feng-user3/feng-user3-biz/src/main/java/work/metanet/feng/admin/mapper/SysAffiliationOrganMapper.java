package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysAffiliationOrgan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联盟机构关联表(SysAffiliationOrgan)表数据库访问层
 *
 * @author edison
 * @since 2023-08-02 09:53:02
 */
@Mapper
public interface SysAffiliationOrganMapper extends FengBaseMapper<SysAffiliationOrgan> {

    List<SysOrgan> getOrganListByAffiliationId(@Param("affiliationId") Integer affiliationId);
}
