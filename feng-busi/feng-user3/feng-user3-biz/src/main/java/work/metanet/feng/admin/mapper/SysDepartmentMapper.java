package work.metanet.feng.admin.mapper;

import work.metanet.feng.common.data.datascope.FengBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysDepartment;

/**
 * 科室表(SysDepartment)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysDepartmentMapper extends FengBaseMapper<SysDepartment> {

}