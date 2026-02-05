package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.SysStaffDeptDTO;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysStaffDept;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.xml.BatchStaffDeptXml;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 人员科室关联表(SysStaffDept)表服务接口
 *
 * @author edison
 * @since 2022-12-26 09:26:07
 */
public interface SysStaffDeptService extends IService<SysStaffDept> {

    /**
     * 通过人员id查询科室列表
     *
     * @param staffId:
     * @return R
     */
    List<SysDepartment> getDeptsByStaffId(Integer staffId);

    /**
     * 通过人员工号查询科室列表
     *
     * @param staffNo:
     * @return R
     */
    List<SysDepartment> getDeptsByStaffNo(String organCode, String staffNo);

    /**
     * 通过人员id配置科室列表
     *
     * @param sysStaffDeptDTO:
     * @return R
     */
    R configDept(SysStaffDeptDTO sysStaffDeptDTO);

    /**
     * 批量导入人员科室关联
     *
     * @param batchStaffDeptXml:
     * @return R
     */
    R addStaffDepts(BatchStaffDeptXml batchStaffDeptXml);
}
