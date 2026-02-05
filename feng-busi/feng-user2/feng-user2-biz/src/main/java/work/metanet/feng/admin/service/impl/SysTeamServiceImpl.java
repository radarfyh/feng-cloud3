package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysTeamMapper;
import work.metanet.feng.admin.api.entity.SysTeam;
import work.metanet.feng.admin.service.SysTeamService;
import org.springframework.stereotype.Service;

/**
 * 小组(SysTeam)表服务实现类
 *
 * @author edison
 * @since 2023-08-02 09:53:53
 */
@Service
@AllArgsConstructor
public class SysTeamServiceImpl extends ServiceImpl<SysTeamMapper, SysTeam> implements SysTeamService {

}
