package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.ProjectDTO;
import work.metanet.feng.admin.api.entity.SysProject;
import work.metanet.feng.admin.api.vo.SysProjectVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目表(SysProject)表服务接口
 * <p>
 * 该接口定义了项目的增删改查、批量操作以及查询项目信息等功能。
 * </p>
 *
 * @author edison
 * @since 2023-10-01
 */
public interface SysProjectService extends IService<SysProject> {

    /**
     * 新增项目
     * <p>
     * 该方法用于新增一个项目记录。
     * </p>
     *
     * @param sysProject 项目实体对象
     * @return 是否新增成功
     * @throws IllegalArgumentException 如果 sysProject 为空
     */
    Boolean saveSysProject(SysProject sysProject);

    /**
     * 修改项目
     * <p>
     * 该方法用于根据项目ID修改项目信息。
     * </p>
     *
     * @param sysProject 项目实体对象
     * @return 是否修改成功
     * @throws IllegalArgumentException 如果 sysProject 为空或 ID 为空
     */
    Boolean updateSysProjectById(SysProject sysProject);

    /**
     * 分页查询项目
     * <p>
     * 该方法用于分页查询项目，并返回包含分页信息的 VO 对象列表。
     * </p>
     *
     * @param page        分页参数
     * @param sysProject  查询条件
     * @return 包含分页信息的项目 VO 对象列表
     * @throws IllegalArgumentException 如果 page 或 sysProject 为空
     */
    IPage<SysProjectVO> getProjectVoPage(Page<SysProjectVO> page, SysProject sysProject);

    /**
     * 批量操作项目
     * <p>
     * 该方法用于批量新增、修改或删除项目，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param projectDTO 项目数据传输对象
     * @param failMsgList 错误信息列表
     * @return 操作失败的错误信息列表
     * @throws IllegalArgumentException 如果 projectDTO 或 failMsgList 为空
     */
    List<String> batchSaveProjects(ProjectDTO projectDTO, List<String> failMsgList);

    /**
     * 根据项目编码获取项目详情
     * <p>
     * 该方法用于根据项目编码查询项目的详细信息。
     * </p>
     *
     * @param projectCode 项目编码
     * @return 项目详情 VO 对象
     * @throws IllegalArgumentException 如果 projectCode 为空
     */
    SysProjectVO getProjectByCode(String projectCode);

    /**
     * 获取项目进度
     * <p>
     * 该方法用于获取指定项目的当前进度。
     * </p>
     *
     * @param projectId 项目ID
     * @return 项目进度（百分比）
     * @throws IllegalArgumentException 如果 projectId 为空
     */
    BigDecimal getProjectProgress(Integer projectId);
}

