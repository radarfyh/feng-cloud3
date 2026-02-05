package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import work.metanet.feng.admin.api.entity.SysProjectStaff;
import work.metanet.feng.admin.api.entity.SysStaff;

import java.util.List;

/**
 * 项目成员表(ProjectStaff)服务接口
 * <p>
 * 提供项目员工相关操作服务，包括员工分页查询、角色权限获取以及根据项目角色等条件查询用户ID等方法。
 * </p>
 * <p>
 * 本接口定义了与项目员工管理相关的业务操作，具体实现需要在对应的Service类中完成。
 * </p>
 * 
 * @author edison
 * @since 2023-01-31 09:46:50
 */
public interface SysProjectStaffService extends IService<SysProjectStaff> {

    /**
     * 分页条件查询根据项目id和角色id查询人员信息
     * <p>
     * 该方法用于分页查询指定项目ID和角色ID下的所有员工信息。
     * </p>
     *
     * @param page 分页对象
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @param roleId 角色ID，指定查询的角色
     * @return 返回分页结果
     */
    IPage<SysStaff> getStaffListByProjectId(Page<SysProjectStaff> page, String organCode, Integer projectId, Integer roleId);

    /**
     * 根据项目id和人员工号获取菜单权限
     * <p>
     * 该方法用于根据项目ID和员工工号获取对应的角色ID和菜单权限。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param applicationCode 应用编码，用于区分不同应用的权限
     * @param projectId 项目ID，指定查询的项目
     * @param staffNo 员工工号，用于唯一标识员工
     * @return 返回角色ID和菜单权限
     */
    List<Tree<Integer>> getRoleIdByMenu(String organCode, String applicationCode, Integer projectId, String staffNo);

    /**
     * 根据项目id和角色id查询所有人员对应的userId【内部feign接口】
     * <p>
     * 该方法用于根据项目ID和角色ID查询所有人员的userId，以便于进行其他的操作。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @param roleId 角色ID，指定查询的角色
     * @return 返回所有员工的userId列表
     */
    List<Integer> getUserIdByProjectAndRole(String organCode, Integer projectId, Integer roleId);

    /**
     * 根据项目id查询所有人员对应的userId
     * <p>
     * 该方法用于根据项目ID查询所有人员的userId，以便于进行其他的操作。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @return 返回所有员工的userId列表
     */
    List<Integer> getUserIdByProjectId(String organCode, Integer projectId);

    /**
     * 查询角色id集合
     * <p>
     * 该方法用于查询所有项目成员的角色ID集合。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @return 返回所有角色ID的集合
     */
    List<Integer> getRoleIdsByProjectRoleCode(String organCode);

    /**
     * 查询团队组长角色id集合
     * <p>
     * 该方法用于查询所有团队组长的角色ID集合。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @return 返回所有团队组长角色ID的集合
     */
    List<Integer> getRoleIdsByLeaderRoleCode(String organCode);

    /**
     * 判断当前用户是否为项目领导角色，是则加入当前项目成员列表中
     * <p>
     * 该方法用于判断当前用户是否为指定项目的领导角色，并将其加入到项目成员列表中。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @return 返回判断结果，true表示当前用户是项目领导角色，false表示不是
     */
    Boolean isProjectLeader(String organCode, Integer projectId);
}
