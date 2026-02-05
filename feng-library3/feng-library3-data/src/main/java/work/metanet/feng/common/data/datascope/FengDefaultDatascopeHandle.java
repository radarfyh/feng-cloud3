package work.metanet.feng.common.data.datascope;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.admin.api.feign.RemoteDataScopeService;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.enums.DataScopeTypeEnum;
import work.metanet.feng.common.core.util.RetOps;
import work.metanet.feng.common.data.tenant.TenantContextHolder;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认数据权限判断处理器，负责计算用户的数据权限范围。
 * <p>
 * 该类通过用户角色、数据权限范围和部门信息，决定是否需要对数据进行权限过滤。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class FengDefaultDatascopeHandle implements DataScopeHandle {

    private final RemoteDataScopeService dataScopeService;

    /**
     * 计算用户数据权限范围。
     * <p>
     * 根据用户角色的权限类型（如查询全部、自定义权限、本级及下级、本级），
     * 计算出当前用户的部门数据范围，若没有权限，返回 `false`，否则返回 `true`。
     * </p>
     * 
     * @param deptList 当前用户的部门ID列表
     * @return true 表示无需过滤数据，false 表示需要进行数据权限过滤
     */
    @Override
    public boolean shouldFilterData(List<Integer> deptList) {
        FengUser user = SecurityUtils.getUser();

        // 获取用户的角色ID列表
        List<String> roleIdList = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(SecurityConstants.ROLE))
                .map(authority -> authority.split(StrUtil.UNDERLINE)[1])
                .collect(Collectors.toList());

        // 当前用户没有角色，返回不需要过滤
        if (CollectionUtil.isEmpty(roleIdList)) {
            return false;
        }
        // 获取角色列表并找出数据权限最小类型的角色
        SysRole role = new SysRole();
        // 是管理员的话，按照编码admin来查找，因为1在多租户情况下不能重复
        if (TenantContextHolder.getTenant() != null &&
        		StrUtil.isNotBlank(TenantContextHolder.getTenant())) {
	        if (roleIdList.contains("1")) {
	        	List<String> roleCodeList = new ArrayList<>();
	        	roleCodeList.add("admin");
	        	SysRole ret = RetOps.of(dataScopeService.getRoleListByCode(roleCodeList))
	                    .getData()
	                    .orElseGet(Collections::emptyList)
	                    .stream()
	                    .min(Comparator.comparingInt(SysRole::getDsType))
	                    .orElse(null);
	        	BeanUtil.copyProperties(ret, role);
	        } else {
	        	SysRole ret = RetOps.of(dataScopeService.getRoleList(roleIdList))
	                .getData()
	                .orElseGet(Collections::emptyList)
	                .stream()
	                .min(Comparator.comparingInt(SysRole::getDsType))
	                .orElse(null);
	        	BeanUtil.copyProperties(ret, role);
	        }
        }

        // 角色为空，说明角色不存在或已删除，返回不需要过滤
        if (role == null) {
            log.warn("User {} has no valid role or the role has been deleted.", user.getUsername());
            return false;
        }

        Integer dsType = role.getDsType();
        // 查询全部权限
        if (DataScopeTypeEnum.ALL.getType() == dsType) {
            return true;
        }

        // 自定义数据权限
        if (DataScopeTypeEnum.CUSTOM.getType() == dsType && StrUtil.isNotBlank(role.getDsScope())) {
            String dsScope = role.getDsScope();
            deptList.addAll(Arrays.stream(dsScope.split(StrUtil.COMMA))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList()));
        }

        // 本级及子级权限
        if (DataScopeTypeEnum.OWN_CHILD_LEVEL.getType() == dsType) {
            List<Integer> deptIdList = dataScopeService.getDescendantList(user.getDeptId()).getData().stream()
                    .map(SysDeptRelation::getDescendant)
                    .collect(Collectors.toList());
            deptList.addAll(deptIdList);
        }

        // 只查询本级权限
        if (DataScopeTypeEnum.OWN_LEVEL.getType() == dsType) {
            deptList.add(user.getDeptId());
        }

        return false;
    }
}
