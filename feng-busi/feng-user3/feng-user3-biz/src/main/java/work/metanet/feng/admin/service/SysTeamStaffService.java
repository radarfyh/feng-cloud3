package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysTeamStaff;
import work.metanet.feng.admin.api.entity.SysStaff;

import java.util.List;

/**
 * 小组人员关联表(TeamStaff)表服务接口
 *
 * @author edison
 * @since 2023-08-02 09:54:37
 */
public interface SysTeamStaffService extends IService<SysTeamStaff> {

    /**
     * 分页查询小组人员列表
     *
     * @param page
     * @param teamStaff
     * @return
     */
    IPage<SysStaff> selectPage(Page page, SysTeamStaff teamStaff);

    /**
     * 根据小组id查询小组人员列表
     * @param teamId
     * @return
     */
    List<SysStaff> getStaffListByTeamId(Integer teamId);
}
