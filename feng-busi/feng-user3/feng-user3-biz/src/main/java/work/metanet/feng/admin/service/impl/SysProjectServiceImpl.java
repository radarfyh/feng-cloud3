package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.ProjectDTO;
import work.metanet.feng.admin.api.entity.SysProject;
import work.metanet.feng.admin.api.vo.SysProjectVO;
import work.metanet.feng.admin.mapper.SysProjectMapper;
import work.metanet.feng.admin.service.SysProjectService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 项目表(SysProject)表服务实现类
 * <p>
 * 该类实现了项目管理的增、删、改、查功能，支持批量操作项目的保存、更新及分页查询。
 * </p>
 *
 * @author edison
 * @since 2023-10-01
 */
@Service
@AllArgsConstructor
public class SysProjectServiceImpl extends ServiceImpl<SysProjectMapper, SysProject> implements SysProjectService {

    private final SysUserService sysUserService;

    /**
     * 新增项目
     * <p>
     * 该方法用于新增一个项目记录。
     * </p>
     *
     * @param sysProject 项目实体对象
     * @return 是否新增成功
     */
    @Override
    public Boolean saveSysProject(SysProject sysProject) {
        // 校验项目是否符合业务规则
        if (!sysProjectCheck(sysProject, "1")) {
            return false;
        }
        return this.save(sysProject);
    }

    /**
     * 根据 ID 更新项目
     * <p>
     * 该方法用于根据项目 ID 修改项目信息。
     * </p>
     *
     * @param sysProject 项目实体对象
     * @return 是否修改成功
     */
    @Override
    public Boolean updateSysProjectById(SysProject sysProject) {
        // 校验项目是否符合业务规则
        if (!sysProjectCheck(sysProject, "2")) {
            return false;
        }
        return this.updateById(sysProject);
    }

    /**
     * 分页查询项目信息
     * <p>
     * 该方法用于分页查询项目信息，并返回包含分页信息的 VO 对象列表。
     * </p>
     *
     * @param page        分页参数
     * @param sysProject 查询条件
     * @return 包含分页信息的项目 VO 对象列表
     */
    @Override
    public IPage<SysProjectVO> getProjectVoPage(Page page, SysProject sysProject) {
        IPage<SysProjectVO> projectVosPage = baseMapper.getProjectVosPage(page, sysProject);
        for (SysProjectVO sysProjectVO : projectVosPage.getRecords()) {
            // 在此可以为项目VO添加额外信息，如负责人、部门等
            // 例如，查询项目经理信息、项目部门等
        }
        return projectVosPage;
    }

    /**
     * 批量保存或更新项目
     * <p>
     * 该方法用于批量新增、修改项目，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param projectDTO  项目数据传输对象
     * @param failMsgList 错误信息列表
     * @return 操作失败的错误信息列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> batchSaveProjects(ProjectDTO projectDTO, List<String> failMsgList) {
        String organCode = projectDTO.getProjectList().get(0).getOrganCode();
        for (SysProject project : projectDTO.getProjectList()) {
            long count = baseMapper.selectCount(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getOrganCode, organCode).eq(SysProject::getProjectCode, project.getProjectCode()));
            if (count > 0) {
                // 修改操作
                baseMapper.update(project, Wrappers.<SysProject>lambdaUpdate().eq(SysProject::getOrganCode, organCode).eq(SysProject::getProjectCode, project.getProjectCode()));
            } else {
                // 新增项目
                baseMapper.insert(project);
            }
        }
        return failMsgList;
    }

    /**
     * 校验项目是否符合业务规则
     * <p>
     * 该方法用于校验项目的唯一性，确保项目编码等字段的唯一性。
     * </p>
     *
     * @param sysProject 项目实体对象
     * @param type       操作类型（0-新增，1-修改）
     * @return 校验结果
     */
    private Boolean sysProjectCheck(SysProject sysProject, String type) {
        Long count = 0L;
        if ("0".equals(type)) {
            // 新增操作，校验项目编码是否重复
            count = baseMapper.selectCount(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getProjectCode, sysProject.getProjectCode()));
        } else if ("1".equals(type)) {
            // 修改操作，排除当前ID的项目
            count = baseMapper.selectCount(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getProjectCode, sysProject.getProjectCode()).ne(SysProject::getId, sysProject.getId()));
        }
        return count == 0;
    }

    @Override
    public SysProjectVO getProjectByCode(String projectCode) {
        // 根据项目编码查询项目实体
        SysProject sysProject = baseMapper.selectOne(Wrappers.<SysProject>lambdaQuery().eq(SysProject::getProjectCode, projectCode));
        if (sysProject == null) {
            return null; // 如果项目不存在，返回 null
        }
        // 将 SysProject 实体对象转换为 SysProjectVO
        SysProjectVO sysProjectVO = BeanUtil.copyProperties(sysProject, SysProjectVO.class);
        
        // 你可以在这里添加更多的业务逻辑，例如获取项目经理信息等

        return sysProjectVO; // 返回项目详情的 VO 对象
    }

    @Override
    public BigDecimal getProjectProgress(Integer projectId) {
        // 根据项目ID查询项目实体
        SysProject sysProject = baseMapper.selectById(projectId);
        if (sysProject == null) {
            return null; // 如果项目不存在，返回 null
        }
        // 假设项目进度存在于 SysProject 实体中，返回该进度
        return sysProject.getProgress(); // 返回项目进度百分比
    }

}
