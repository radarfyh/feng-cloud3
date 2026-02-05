package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysStaffDept;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人员科室关联表(SysStaffDept)表数据库访问层
 *
 * @author edison
 * @since 2022-12-26 09:26:07
 */
@Mapper
public interface SysStaffDeptMapper extends FengBaseMapper<SysStaffDept> {

    List<SysDepartment> getDeptsByStaffNo(@Param("staffId") Integer staffId);

}
