package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.StaffDTO;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.vo.StaffRoleDeptVO;
import work.metanet.feng.admin.api.vo.SysStaffVO;
import work.metanet.feng.admin.xml.BatchStaffXml;

import java.util.List;

/**
 * 人员信息表(SysStaff)表服务接口
 * <p>
 * 该接口定义了人员信息的增删改查、批量操作以及查询角色和科室信息等功能。
 * </p>
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysStaffService extends IService<SysStaff> {

    /**
     * 新增人员信息
     * <p>
     * 该方法用于新增一条人员信息记录。
     * </p>
     *
     * @param sysStaff 人员信息实体对象
     * @return 是否新增成功
     * @throws IllegalArgumentException 如果 sysStaff 为空
     */
    Boolean saveSysStaff(SysStaff sysStaff);

    /**
     * 修改人员信息
     * <p>
     * 该方法用于根据 ID 修改人员信息记录。
     * </p>
     *
     * @param sysStaff 人员信息实体对象
     * @return 是否修改成功
     * @throws IllegalArgumentException 如果 sysStaff 为空或 ID 为空
     */
    Boolean updateSysStaffById(SysStaff sysStaff);

    /**
     * 分页查询人员信息
     * <p>
     * 该方法用于分页查询人员信息，并返回包含分页信息的 VO 对象列表。
     * </p>
     *
     * @param page     分页参数
     * @param sysStaff 查询条件
     * @return 包含分页信息的人员 VO 对象列表
     * @throws IllegalArgumentException 如果 page 或 sysStaff 为空
     */
    IPage<SysStaffVO> getStaffVoPage(Page<SysStaffVO> page, SysStaff sysStaff);

    /**
     * 批量操作人员信息
     * <p>
     * 该方法用于批量新增、修改或删除人员信息，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param staffDTO   人员数据传输对象
     * @param failMsgList 错误信息列表
     * @return 操作失败的错误信息列表
     * @throws IllegalArgumentException 如果 staffDTO 或 failMsgList 为空
     */
    List<String> batchSaveStaffs(StaffDTO staffDTO, List<String> failMsgList);

    /**
     * 批量操作人员信息
     * <p>
     * 该方法用于通过接口批量新增人员信息，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param batchStaffXml 批量人员信息 XML 对象
     * @return 操作失败的错误信息列表
     * @throws IllegalArgumentException 如果 batchStaffXml 为空
     */
    List<String> batchAddStaffs(BatchStaffXml batchStaffXml);

    /**
     * 根据工号获取角色和科室信息
     * <p>
     * 该方法用于根据机构编码和工号查询人员的角色和科室信息。
     * </p>
     *
     * @param organCode 机构编码
     * @param staffNo   工号
     * @return 包含角色和科室信息的 VO 对象
     * @throws IllegalArgumentException 如果 organCode 或 staffNo 为空
     */
    StaffRoleDeptVO getRoleAndDeptByUsername(String organCode, String staffNo);

    /**
     * 根据手机号查询人员基本信息
     * <p>
     * 该方法用于根据手机号和 OpenID 查询人员的基本信息。
     * </p>
     *
     * @param phone  手机号
     * @param openId OpenID
     * @return 人员基本信息 VO 对象
     * @throws IllegalArgumentException 如果 phone 或 openId 为空
     */
    SysStaffVO getStaffByPhone(String phone, String openId);
}