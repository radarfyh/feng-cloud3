package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.vo.SysProjectVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysProject;
import org.apache.ibatis.annotations.Param;

/**
 * 项目表(SysProject)表数据库访问层
 * <p>
 * 该接口定义了与数据库交互的操作方法，主要用于项目的增、删、改、查等操作。
 * </p>
 *
 * @author edison
 * @since 2023-10-01
 */
@Mapper
public interface SysProjectMapper extends FengBaseMapper<SysProject> {

    /**
     * 分页查询项目信息
     * <p>
     * 该方法用于分页查询项目信息，并返回包含分页信息的 VO 对象列表。
     * </p>
     *
     * @param page        分页参数
     * @param sysProject 查询条件
     * @return 包含分页信息的项目信息 VO 对象列表
     * @throws IllegalArgumentException 如果 page 或 sysProject 为空
     */
    IPage<SysProjectVO> getProjectVosPage(Page page, @Param("sysProject") SysProject sysProject);
}

