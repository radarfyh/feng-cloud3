package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.admin.api.entity.SysRole;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author edison
 * @date 2019-09-07
 * <p>
 * 远程数据权限调用接口
 */
@FeignClient(contextId = "remoteDataScopeService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteDataScopeService {

	/**
	 * 通过角色ID 查询角色列表
	 * @param roleIdList 角色ID
	 * @return
	 */
	@PostMapping("/sysRole/getRoleList")
	R<List<SysRole>> getRoleList(@RequestBody List<String> roleIdList);
	
	/**
	 * 通过角色ID 查询角色列表
	 * @param roleIdList 角色ID
	 * @return
	 */
	@PostMapping("/sysRole/getRoleListByCode")
	R<List<SysRole>> getRoleListByCode(@RequestBody List<String> roleCodeList);

	/**
	 * 获取子级科室
	 * @param deptId 科室ID
	 * @return
	 */
	@GetMapping("/sysDepartment/getDescendantList/{deptId}")
	R<List<SysDeptRelation>> getDescendantList(@PathVariable("deptId") Integer deptId);

}
