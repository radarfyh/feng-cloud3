package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.StaffDTO;
import work.metanet.feng.admin.api.dto.UserDTO;
import work.metanet.feng.admin.api.entity.*;
import work.metanet.feng.admin.api.vo.StaffRoleDeptVO;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.api.vo.SysStaffVO;
import work.metanet.feng.admin.api.vo.SysRoleVO;
import work.metanet.feng.admin.mapper.SysDepartmentMapper;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.service.SysDeptAttributeService;
import work.metanet.feng.admin.service.SysStaffService;
import work.metanet.feng.admin.service.SysRoleService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.admin.xml.BatchStaffXml;
import work.metanet.feng.admin.xml.StaffXml;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.constant.enums.Gender;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 人员信息表(SysStaff)表服务实现类
 * <p>
 * 该类实现了人员信息的增、删、改、查功能，支持批量操作人员信息，以及与其他模块（如用户、角色、部门等）的交互。
 * </p>
 *
 * @author edison
 * @date 2022-05-11
 */
@Service
@AllArgsConstructor
public class SysStaffServiceImpl extends ServiceImpl<SysStaffMapper, SysStaff> implements SysStaffService {

    private final SysUserService sysUserService;

    private final SysRoleService sysRoleService;

    private final SysDepartmentMapper sysDepartmentMapper;

    private final SysDeptAttributeService sysDeptAttributeService;

    /**
     * 新增人员信息
     * <p>
     * 该方法用于新增一条人员信息记录。
     * </p>
     *
     * @param sysStaff 人员信息实体对象
     * @return 是否新增成功
     */
    @Override
    public Boolean saveSysStaff(SysStaff sysStaff) {
        if (!sysStaffCheck(sysStaff, "1")) {
        	return false;
        }
      
        return this.save(sysStaff);
    }

    /**
     * 根据 ID 更新人员信息
     * <p>
     * 该方法用于根据人员 ID 修改人员信息记录，并更新相关的用户信息（如头像、邮箱等）。
     * </p>
     *
     * @param sysStaff 人员信息实体对象
     * @return 是否修改成功
     */
    @Override
    public Boolean updateSysStaffById(SysStaff sysStaff) {
        if (!sysStaffCheck(sysStaff, "2")) {
        	return false;
        }
        
        Boolean result = this.updateById(sysStaff);
        
        if (result) { 
        	// 更新人员信息后，更新用户信息的：头像、电子邮件地址、性别
            SysUser sysUser = sysUserService.getOne(
            		Wrappers.<SysUser>lambdaQuery()
            		.eq(SysUser::getOrganCode, sysStaff.getOrganCode())
            		.eq(SysUser::getUsername, sysStaff.getStaffNo()));
            if (!Objects.isNull(sysUser)) {
            	String avatar = sysUser.getAvatar();
            	String photograph = sysStaff.getPhotograph();
            	// 使用Objects.equals，是因为该函数考虑了null
            	if (!Objects.equals(avatar, photograph)) {
            		sysUserService.updateUserByStaffId(sysStaff.getId(), sysStaff);
            	}
            }
        }
        return result;
    }

    /**
     * 分页查询人员信息
     * <p>
     * 该方法用于分页查询人员信息，并返回包含分页信息的 VO 对象列表。
     * </p>
     *
     * @param page     分页参数
     * @param sysStaff 查询条件
     * @return 包含分页信息的人员信息 VO 对象列表
     */
    @Override
    public IPage<SysStaffVO> getStaffVoPage(Page page, SysStaff sysStaff) {
        IPage<SysStaffVO> staffVosPage = baseMapper.getStaffVosPage(page, sysStaff);
        for (SysStaffVO sysStaffVO : staffVosPage.getRecords()) {
        	//  传递账号是否开启标志给前端
            long count = sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getStaffId, sysStaffVO.getId()));
            String accountOpened = count > 0 ? "1" : "0";
            sysStaffVO.setAccountOpened(accountOpened);
            
            // 获取部门名称
            SysDepartment sysDepartment = sysDepartmentMapper.selectById(sysStaffVO.getDeptId());
            if (Objects.nonNull(sysDepartment)) sysStaffVO.setDeptName(sysDepartment.getDeptName());
            
            // 获取性别名称
            if (sysStaffVO.getGenderCode() != null) {
            	sysStaffVO.setGenderName(sysStaffVO.getGenderCode().getName());
            } else {
            	sysStaffVO.setGenderCode(Gender.UNKNOWN);
            	sysStaffVO.setGenderName(sysStaffVO.getGenderCode().getName());
            }
        }
        return staffVosPage;
    }

    /**
     * 批量保存人员信息
     * <p>
     * 该方法用于批量新增、修改或删除人员信息，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param staffDTO   人员数据传输对象
     * @param failMsgList 错误信息列表
     * @return 操作失败的错误信息列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> batchSaveStaffs(StaffDTO staffDTO, List<String> failMsgList) {
        String organCode = staffDTO.getSysStaffList().get(0).getOrganCode();
        for (SysStaff staff : staffDTO.getSysStaffList()) {
            long count2 = sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrganCode, organCode).eq(SysUser::getUsername, staff.getStaffNo()));
            if ("0".equals(staffDTO.getType())) {
                //禁用账号操作【人员工号】
                if (count2 > 0) {
                    //人员对应的账号禁用
                    sysUserService.update(Wrappers.<SysUser>lambdaUpdate().eq(SysUser::getOrganCode, organCode).eq(SysUser::getUsername, staff.getStaffNo()).set(SysUser::getStatus, "0"));
                } else {
                	String msg = "batchSaveStaffs-->" + BusinessEnum.U2_DELETE_STAFF.getMsg() + "。工号：" + staff.getStaffNo();
                	log.error(msg);
                    failMsgList.add(msg);
                }
            } else {
                //新增或修改操作
                long count = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, staff.getStaffNo()));
                if (count > 0) {
                    //修改操作
                    baseMapper.update(staff, Wrappers.<SysStaff>lambdaUpdate().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, staff.getStaffNo()));
                    //更新科室id和手机号
                    if (count2 > 0) {
                        SysUser sysUser = new SysUser();
                        sysUser.setUsername(staff.getStaffNo());
                        sysUser.setOrganCode(staff.getOrganCode());
                        sysUser.setDeptId(staff.getDeptId());
                        sysUser.setPhone(staff.getTelephone());
                        sysUserService.update(sysUser, Wrappers.<SysUser>lambdaUpdate().eq(SysUser::getOrganCode, organCode).eq(SysUser::getUsername, staff.getStaffNo()));
                    }
                } else {
                    //新增人员操作
                    baseMapper.insert(staff);
                    //对应的账号不存在的话，则新增账号
                    if (!(count2 > 0)) {
                        //查询角色id
                        SysRole sysRole = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, "default_role"));
                        if (Objects.isNull(sysRole) || null == sysRole.getId()) {
                            //查询字典不存在返回报错
                        	String msg = "batchSaveStaffs-->" + BusinessEnum.U2_QUERY_ROLE.getMsg() + "。角色编码：" + BuiltInRoleEnum.DEFAULT.getCode();
                        	log.error(msg);
                        	failMsgList.add(msg);
                            continue;
                        }
                        //新增账号，并赋予默认角色
                        List<Integer> list = new ArrayList<>();
                        list.add(sysRole.getId());
                        UserDTO userDTO = new UserDTO();
                        userDTO.setUsername(staff.getStaffNo());
                        userDTO.setOrganCode(staff.getOrganCode());
                        userDTO.setDeptId(staff.getDeptId());
                        userDTO.setStaffId(staff.getId());
                        userDTO.setRoleList(list);
                        if (StrUtil.isNotBlank(staff.getStaffName())) {
                            userDTO.setNickName(staff.getStaffName());
                        }
                        if (StrUtil.isNotBlank(staff.getTelephone())) {
                            userDTO.setPhone(staff.getTelephone());
                        }
                        sysUserService.saveUser(userDTO);
                    }
                }
            }
        }
        return failMsgList;
    }

    /**
     * 批量新增人员
     * <p>
     * 该方法用于批量新增人员信息，并返回操作失败的错误信息列表。
     * </p>
     *
     * @param batchStaffXml 批量人员信息 XML 对象
     * @return 操作失败的错误信息列表
     */
    @Override
    public List<String> batchAddStaffs(BatchStaffXml batchStaffXml) {
        String type = null;
        List<String> failMsgList = new ArrayList<>();
        StaffXml staffXml1 = batchStaffXml.getStaffXmlList().get(0);
        if (staffXml1.getAction().equals("D")) {
            type = "0";//删除
        } else {
            type = "1";//新增或更新
        }
        List<SysStaff> list = new ArrayList<>();
        for (StaffXml staffXml : batchStaffXml.getStaffXmlList()) {
            SysDepartment sysDepartment = sysDepartmentMapper.selectOne(Wrappers.<SysDepartment>lambdaQuery().eq(SysDepartment::getOrganCode, staffXml.getOrganCode()).eq(SysDepartment::getDeptCode, staffXml.getDeptCode()));
            if (Objects.isNull(sysDepartment)) {
            	String msg = "batchAddStaffs-->" + BusinessEnum.U2_QUERY_DEPT.getMsg() + "。部门编码：" + staffXml.getDeptCode();
            	log.error(msg);
                failMsgList.add(msg);
                continue;
            }
            SysStaff sysStaff = new SysStaff();
            sysStaff.setDeptId(sysDepartment.getId());
            sysStaff.setOrganCode(staffXml.getOrganCode());
            sysStaff.setStaffNo(staffXml.getStaffNo());
            if (StrUtil.isNotBlank(staffXml.getIdentificationNo())) {
                sysStaff.setIdentificationNo(staffXml.getIdentificationNo());
            }
            if (StrUtil.isNotBlank(staffXml.getStaffName())) {
                sysStaff.setStaffName(staffXml.getStaffName());
            }
            if (staffXml.getGenderCode() != null) {
                sysStaff.setGenderCode(staffXml.getGenderCode());
            }
            if (StrUtil.isNotBlank(staffXml.getBirthdate())) {
                sysStaff.setBirthdate(DateUtil.parse(staffXml.getBirthdate(), "yyyy-MM-dd"));
            }
            if (StrUtil.isNotBlank(staffXml.getTechnicalQualificationsCode())) {
                sysStaff.setTechnicalQualificationsCode(staffXml.getTechnicalQualificationsCode());
            }
            if (StrUtil.isNotBlank(staffXml.getTechnicalQualificationsName())) {
                sysStaff.setTechnicalQualificationsName(staffXml.getTechnicalQualificationsName());
            }
            list.add(sysStaff);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            StaffDTO esbStaffDTO = new StaffDTO();
            esbStaffDTO.setType(type);
            esbStaffDTO.setSysStaffList(list);
            return batchSaveStaffs(esbStaffDTO, failMsgList);
        } else {
        	return failMsgList;
        }
    }

    /**
     * 获取人员角色和科室信息
     * <p>
     * 该方法用于根据工号获取人员的角色和科室信息。
     * </p>
     *
     * @param organCode 机构编码
     * @param staffNo   工号
     * @return 人员角色和科室信息
     */
    @Override
    public StaffRoleDeptVO getRoleAndDeptByUsername(String organCode, String staffNo) {
        SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOrganCode, organCode).eq(SysUser::getUsername, staffNo));
        if (Objects.isNull(sysUser)) {
        	log.error("getRoleAndDeptByUsername-->用户信息不存在");
            return null;
        }
        List<SysRoleVO> sysRoleVOS = sysRoleService.listRolesByUserId(sysUser.getId());
        SysStaff sysStaff = baseMapper.selectOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, staffNo));
        if (Objects.isNull(sysStaff)) {
        	log.error("getRoleAndDeptByUsername-->人员信息不存在");
            return null;
        }
        SysDepartment sysDepartment = sysDepartmentMapper.selectById(sysStaff.getDeptId());
        SysDepartmentVO sysDepartmentVO = BeanUtil.copyProperties(sysDepartment, SysDepartmentVO.class);
        List<SysDeptAttribute> list = sysDeptAttributeService.list(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, sysStaff.getDeptId()));
        if (null != list && list.size() > 0) {
            List<String> collect = list.stream().map(SysDeptAttribute::getDeptAttribute).collect(Collectors.toList());
            sysDepartmentVO.setDeptAttributeList(collect);
        }
        StaffRoleDeptVO staffRoleDeptVO = new StaffRoleDeptVO();
        staffRoleDeptVO.setSysStaff(sysStaff);
        staffRoleDeptVO.setSysRoleVOS(sysRoleVOS);
        staffRoleDeptVO.setSysDepartmentVO(sysDepartmentVO);
        return staffRoleDeptVO;
    }

    /**
     * 通过手机号查询人员基本信息
     *
     * @param phone:
     * @return R
     */
    @Override
    public SysStaffVO getStaffByPhone(String phone, String openId) {
        SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, phone));
        if (Objects.isNull(sysUser)) {
        	log.error("getStaffByPhone-->该手机号未找到对应用户信息，请联系管理员配置");
            return null;
        }
        SysStaff sysStaff = baseMapper.selectById(sysUser.getStaffId());
        if (Objects.isNull(sysStaff)) {
        	log.error("getStaffByPhone-->人员信息不存在");
            return null;
        }
        SysDepartment sysDepartment = sysDepartmentMapper.selectById(sysStaff.getDeptId());
        SysStaffVO sysStaffVO = BeanUtil.copyProperties(sysStaff, SysStaffVO.class);
        if (Objects.nonNull(sysDepartment)) {
            sysStaffVO.setDeptCode(sysDepartment.getDeptCode());
            sysStaffVO.setDeptName(sysDepartment.getDeptName());
        }
        return sysStaffVO;
    }

    /*
     *
     * @Description: 人员新增修改参数校验
     * @author edison
     * @date 2022/5/18
     * @param: sysOrgan
     * @param: type 0-新增 1-修改 2-删除
     * @return
     */
    private Boolean sysStaffCheck(SysStaff sysStaff, String type) {
        Long count = 0L;
        Long count2 = 0L;
        Long count3 = 0L;
        if (type.equals("0")) {
            //新增机构
            count = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffNo, sysStaff.getStaffNo()));
            count2 = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffName, sysStaff.getStaffName()));
            count3 = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getIdentificationNo, sysStaff.getIdentificationNo()));
        } else if (type.equals("1")) {
            //修改机构
            count = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffNo, sysStaff.getStaffNo()).ne(SysStaff::getId, sysStaff.getId()));
            count2 = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffName, sysStaff.getStaffName()).ne(SysStaff::getId, sysStaff.getId()));
            count3 = baseMapper.selectCount(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getIdentificationNo, sysStaff.getIdentificationNo()).ne(SysStaff::getId, sysStaff.getId()));
        }
        
        String msg = "";
        if (count > 0) {
        	msg = "sysStaffCheck-->人员工号不能重复";
        	log.error(msg);
        	return false;
        }
        if (count2 > 0) {
        	msg = "sysStaffCheck-->人员名称不能重复";           
        	log.error(msg);
        	return false;
        }
        if (count3 > 0) {
        	msg = "sysStaffCheck-->人员身份证号不能重复";
        	log.error(msg);
        	return false;
        }
    	    	
        return true;
    }
}