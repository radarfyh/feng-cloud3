package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysProjectStaff;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteProjectStaffService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteProjectStaffService {

    /**
     * 获取当前登录用户的项目id集合
     *
     * @param staffNo
     * @param from
     * @return
     */
    @GetMapping("/projectStaff/getProIdListByStaffId")
    R<List<SysProjectStaff>> getProIdListByStaffId(@RequestParam("staffNo") String staffNo, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 根据角色字典项查询角色id集合
     *
     * @param organCode:
     * @return R
     */
    @GetMapping("/projectStaff/getRoleIdsByCode")
    R<List<Integer>> getRoleIdsByCode(@RequestParam("organCode") String organCode);

    /**
     * 判断当前用户是否为组长角色，是则加入当前项目成员列表中
     *
     * @param organCode
     * @param projectId
     * @return
     */
    @GetMapping("/projectStaff/isProjectLeader")
    R isProjectLeader(@RequestParam("organCode") String organCode, @RequestParam("projectId") Integer projectId);

    /**
     * 根据项目id/角色id查询所有人员对应的userId
     *
     * @param organCode:
     * @param projectId:
     * @return R
     */
    @GetMapping("/projectStaff/getUserIdByProAndRole")
    R<List<Integer>> getUserIdByProAndRole(@RequestParam("organCode") String organCode, @RequestParam("projectId") Integer projectId, @RequestParam("roleId") Integer roleId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 根据项目id查询所有人员对应的userId
     *
     * @param organCode:
     * @param projectId:
     * @return R
     */
    @GetMapping("/projectStaff/getUserIdByProId")
    R<List<Integer>> getUserIdByProId(@RequestParam("organCode") String organCode, @RequestParam("projectId") Integer projectId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 删除项目所有角色成员
     *
     * @param projectId 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/projectStaff/deleteProject")
    public R deleteProject(@RequestParam("projectId") Integer projectId, @RequestHeader(SecurityConstants.FROM) String from);
}
