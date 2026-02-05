package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.vo.SysStaffVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysStaff;
import org.apache.ibatis.annotations.Param;

/**
 * 人员信息表(SysStaff)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysStaffMapper extends FengBaseMapper<SysStaff> {

    /**
     * 分页查询人员信息
     *
     * @param page            分页
     * @param sysStaff 查询参数
     * @return list
     */
    IPage<SysStaffVO> getStaffVosPage(Page page, @Param("sysStaff") SysStaff sysStaff);
}