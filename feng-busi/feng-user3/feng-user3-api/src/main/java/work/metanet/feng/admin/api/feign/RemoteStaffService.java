package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.vo.StaffRoleDeptVO;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteStaffService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteStaffService {

    /**
     * 通过人员工号和机构编码获取人员详情
     *
     * @param username:用户名
     * @param organCode:机构编码
     * @param from:
     * @return R
     */
    @GetMapping("/sysStaff/getStaffByUsername")
    R<SysStaff> getStaffByUsername(@RequestParam("username") String username, @RequestParam("organCode") String organCode, @RequestHeader(SecurityConstants.FROM) String from);


    /**
     * 通过工号获取角色和科室信息，【外部接口，免token获取】
     *
     * @param staffNo
     * @return 单条数据
     */
    @GetMapping("/sysStaff/getRoleAndDeptByUsername")
    R<StaffRoleDeptVO> getRoleAndDeptByUsername(@RequestParam("organCode") String organCode, @RequestParam("staffNo") String staffNo, @RequestHeader(SecurityConstants.FROM) String from);

}
