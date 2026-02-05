package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysStaff;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysTeamStaffMapper;
import work.metanet.feng.admin.api.entity.SysTeamStaff;
import work.metanet.feng.admin.service.SysTeamStaffService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小组人员关联表(TeamStaff)表服务实现类
 *
 * @author edison
 * @since 2023-08-02 09:54:37
 */
@Service
@AllArgsConstructor
public class SysTeamStaffServiceImpl extends ServiceImpl<SysTeamStaffMapper, SysTeamStaff> implements SysTeamStaffService {

    @Override
    public IPage<SysStaff> selectPage(Page page, SysTeamStaff teamStaff) {
        return baseMapper.getStaffList(page,teamStaff.getTeamId());
    }

    @Override
    public List<SysStaff> getStaffListByTeamId(Integer teamId) {
        return baseMapper.getStaffList(teamId);
    }
}
