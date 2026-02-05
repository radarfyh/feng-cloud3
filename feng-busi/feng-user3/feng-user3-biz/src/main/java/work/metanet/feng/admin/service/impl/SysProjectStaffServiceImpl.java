package work.metanet.feng.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysProjectStaff;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysMenu;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.mapper.SysProjectStaffMapper;
import work.metanet.feng.admin.service.*;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目成员表(ProjectStaff)服务实现类
 * <p>
 * 提供对项目成员表的业务操作，支持按项目ID和角色ID查询员工，查询角色权限、用户ID等功能。
 * </p>
 * <p>
 * 本类通过 MyBatis-Plus 实现对 SysProjectStaff 表的 CRUD 操作，并根据业务需求封装查询方法。
 * </p>
 *
 * @author edison
 * @since 2023-01-31 09:46:50
 */
@Service
@AllArgsConstructor
public class SysProjectStaffServiceImpl extends ServiceImpl<SysProjectStaffMapper, SysProjectStaff> implements SysProjectStaffService {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;

    /**
     * 根据项目ID和角色ID分页查询项目员工信息
     * <p>
     * 该方法通过项目ID和角色ID分页查询指定项目下的员工信息。
     * </p>
     *
     * @param page 分页对象
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @param roleId 角色ID，指定查询的角色
     * @return 返回分页结果，包含员工信息
     */
    @Override
    public IPage<SysStaff> getStaffListByProjectId(Page<SysProjectStaff> page, String organCode, Integer projectId, Integer roleId) {
        return baseMapper.getStaffListByProjectId(page, organCode, projectId, roleId);
    }

    /**
     * 根据项目id、人员工号获取菜单权限
     * <p>
     * 该方法根据项目ID和员工工号，查询其角色ID，并返回相应的菜单权限。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param applicationCode 应用编码，用于区分不同应用的权限
     * @param projectId 项目ID，指定查询的项目
     * @param staffNo 员工工号，用于唯一标识员工
     * @return 返回角色ID和菜单权限的树形结构
     */
    @Override
    public List<Tree<Integer>> getRoleIdByMenu(String organCode, String applicationCode, Integer projectId, String staffNo) {
        // 当前用户的角色集合
        List<Integer> roleIdList = SecurityUtils.getRoles();
        Set<SysMenu> all = new HashSet<>();
        // 查询所有项目角色id集合
        List<Integer> roleIds = getRoleIdsByProjectRoleCode(organCode);

        if (CollectionUtil.isNotEmpty(roleIds)) {
            // 获取所有相关菜单
            all.addAll(sysMenuService.list(Wrappers.<SysMenu>lambdaQuery().eq(StrUtil.isNotBlank(applicationCode), SysMenu::getApplicationCode, applicationCode)));
        } else {
            // 查询项目成员
            List<SysProjectStaff> projectStaffs = list(Wrappers.<SysProjectStaff>lambdaQuery().eq(SysProjectStaff::getProjectId, projectId).eq(SysProjectStaff::getStaffNo, staffNo));
            if (CollectionUtil.isNotEmpty(projectStaffs)) {
                // 当前用户角色
                List<Integer> proRoleIdList = projectStaffs.stream().map(SysProjectStaff::getRoleId).collect(Collectors.toList());
                // 合并角色
                roleIdList.addAll(proRoleIdList);
            }
            Set<Integer> roleIdSet = roleIdList.stream().collect(Collectors.toSet());
            // 根据角色id进行菜单筛选
            roleIdSet.forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));
        }
        return sysMenuService.filterMenu(all, "menu", 0);
    }

    /**
     * 根据项目id和角色id查询所有人员对应的userId
     * <p>
     * 该方法根据项目ID和角色ID查询所有人员的userId，以便于进行其他操作。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @param roleId 角色ID，指定查询的角色
     * @return 返回所有员工的userId列表
     */
    @Override
    public List<Integer> getUserIdByProjectAndRole(String organCode, Integer projectId, Integer roleId) {
        List<Integer> userIds = new ArrayList<>();
        List<SysProjectStaff> list = this.list(Wrappers.<SysProjectStaff>lambdaQuery()
                .eq(SysProjectStaff::getProjectId, projectId)
                .eq(SysProjectStaff::getRoleId, roleId));

        list.forEach(staff -> {
            SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getOrganCode, organCode)
                    .eq(SysUser::getUsername, staff.getStaffNo()));
            if (sysUser != null) {
                userIds.add(sysUser.getId());
            }
        });
        return userIds;
    }

    /**
     * 根据项目id查询所有人员对应的userId
     * <p>
     * 该方法根据项目ID查询所有人员的userId，以便于进行其他操作。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @return 返回所有员工的userId列表
     */
    @Override
    public List<Integer> getUserIdByProjectId(String organCode, Integer projectId) {
        List<Integer> userIds = new ArrayList<>();
        List<SysProjectStaff> list = this.list(Wrappers.<SysProjectStaff>lambdaQuery()
                .eq(SysProjectStaff::getProjectId, projectId));

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> staffNoList = list.stream().map(SysProjectStaff::getStaffNo).distinct().collect(Collectors.toList());
        for (String staffNo : staffNoList) {
            SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getOrganCode, organCode)
                    .eq(SysUser::getUsername, staffNo));
            if (sysUser != null) {
                userIds.add(sysUser.getId());
            }
        }
        return userIds;
    }

    /**
     * 查询角色id集合
     * <p>
     * 该方法查询所有项目成员的角色ID集合。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @return 返回角色ID的集合
     */
    @Override
    public List<Integer> getRoleIdsByProjectRoleCode(String organCode) {
        if (StrUtil.isBlank(organCode)) {
            return Collections.emptyList();
        }
        List<String> listRole = Arrays.asList(BuiltInRoleEnum.MEMBER.getCode(), BuiltInRoleEnum.LEADER.getCode());
        return sysRoleService.getRoleIdsByCode(organCode, listRole);
    }

    /**
     * 查询团队组长角色id集合
     * <p>
     * 该方法查询所有团队组长的角色ID集合。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @return 返回角色ID的集合
     */
    @Override
    public List<Integer> getRoleIdsByLeaderRoleCode(String organCode) {
        if (StrUtil.isBlank(organCode)) {
            return Collections.emptyList();
        }
        List<String> listRole = Arrays.asList(BuiltInRoleEnum.LEADER.getCode());
        return sysRoleService.getRoleIdsByCode(organCode, listRole);
    }

    /**
     * 判断当前用户是否为项目领导角色
     * <p>
     * 该方法判断当前用户是否为项目的领导角色，并将其加入到项目成员列表中。
     * </p>
     *
     * @param organCode 组织编码，用于区分不同的组织
     * @param projectId 项目ID，指定查询的项目
     * @return 如果是项目领导角色，返回true；否则返回false
     */
    @Override
    public Boolean isProjectLeader(String organCode, Integer projectId) {
        String username = SecurityUtils.getUser().getUsername();
        //List<Integer> roleIdList = SecurityUtils.getRoles();

        List<Integer> roleIds = getRoleIdsByLeaderRoleCode(organCode);
        if (CollectionUtil.isEmpty(roleIds)) {
            log.error("项目组长角色未维护");
            return false;
        }

        // 将当前用户加入项目角色列表
        SysProjectStaff projectStaff = new SysProjectStaff();
        projectStaff.setProjectId(projectId);
        projectStaff.setRoleId(roleIds.get(0)); // 若配置了多个组长角色，则取第一个
        projectStaff.setStaffNo(username);
        save(projectStaff);
        return true;
    }
}
