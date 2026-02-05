package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysUserDepartment;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 用户科室表(SysUserDepartment)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Mapper
public interface SysUserDepartmentMapper extends FengBaseMapper<SysUserDepartment> {

}