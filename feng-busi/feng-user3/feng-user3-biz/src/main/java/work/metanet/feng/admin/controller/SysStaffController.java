package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.vo.StaffRoleDeptVO;
import work.metanet.feng.admin.api.vo.SysStaffVO;
import work.metanet.feng.admin.service.SysStaffService;
import work.metanet.feng.admin.service.SysUserService;
import work.metanet.feng.admin.xml.BatchStaffXml;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.klock.annotation.Klock;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 人员信息表(SysStaff)表控制层
 *
 * @author edison
 * @since 2023-05-11
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysStaff")
@Tag(name = "人员模块")
public class SysStaffController {
    /**
     * 服务对象
     */
    private final SysStaffService sysStaffService;

    private final SysUserService sysUserService;

    /**
     * 分页查询所有数据
     *
     * @param page            分页对象
     * @param sysStaff 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R getStaffVosPage(Page page, SysStaff sysStaff) {
        return R.ok(this.sysStaffService.getStaffVoPage(page, sysStaff));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysStaff>> list(SysStaff sysStaff) {
        return R.ok(this.sysStaffService.list(Wrappers.<SysStaff>lambdaQuery().eq(StringUtils.isNotBlank(sysStaff.getOrganCode()), SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(null != sysStaff.getDeptId(), SysStaff::getDeptId, sysStaff.getDeptId()).eq(StringUtils.isNotBlank(sysStaff.getStaffNo()), SysStaff::getStaffNo, sysStaff.getStaffNo()).like(StringUtils.isNotBlank(sysStaff.getStaffName()), SysStaff::getStaffName, sysStaff.getStaffName()).eq(StringUtils.isNotBlank(sysStaff.getIdentificationNo()), SysStaff::getIdentificationNo, sysStaff.getIdentificationNo()).eq(StringUtils.isNotBlank(sysStaff.getTelephone()), SysStaff::getTelephone, sysStaff.getTelephone())));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据")
    public R selectOne(@PathVariable Serializable id) {
        return R.ok(this.sysStaffService.getById(id));
    }

    /**
     * 通过人员工号和机构编码获取人员详情
     *
     * @param username
     * @return 单条数据
     */
    @Inner
    @GetMapping("/getStaffByUsername")
    @Operation(summary = "通过人员工号和机构编码获取人员详情【内部接口】")
    public R<SysStaff> getStaffByUsername(@RequestParam("username") String username, @RequestParam("organCode") String organCode) {
        return R.ok(this.sysStaffService.getOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, username)));
    }


    /**
     * 通过人员工号和机构编码获取人员详情
     *
     * @param username
     * @return 单条数据
     */
    @GetMapping("/selectStaffByUsername")
    @Operation(summary = "通过人员工号和机构编码获取人员详情")
    public R<SysStaff> selectStaffByUsername(@RequestParam("username") String username, @RequestParam("organCode") String organCode) {
        SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username).eq(SysUser::getOrganCode, organCode));
        if (Objects.nonNull(sysUser) && null != sysUser.getStaffId()) {
            return R.ok(this.sysStaffService.getOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getId, sysUser.getStaffId())));
        }
        return R.ok(this.sysStaffService.getOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, username)));
    }

    /**
     * 通过工号获取角色和科室信息，【外部接口，免token获取】
     *
     * @param staffNo
     * @return 单条数据
     */
    @GetMapping("/getRoleAndDeptByUsername")
    @Operation(summary = "通过工号获取角色和科室信息")
    public R<StaffRoleDeptVO> getRoleAndDeptByUsername(@RequestParam("organCode") String organCode, @RequestParam("staffNo") String staffNo) {
    	StaffRoleDeptVO staffRoleDeptVO = sysStaffService.getRoleAndDeptByUsername(organCode, staffNo);
    	if (staffRoleDeptVO == null) {
    		return R.failed(BusinessEnum.U2_QUERY_STAFF.getMsg());
    	}
    	
    	return R.ok(staffRoleDeptVO);
    }

    /**
     * 新增数据
     *
     * @param sysStaff 实体对象
     * @return 新增结果
     */
    @SysLog("新增人员")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('staff_add')")
    @Operation(summary = "新增人员")
    public R saveSysStaff(@Validated(ValidGroup.Save.class) @RequestBody SysStaff sysStaff) {
        R r = checkSysStaff(sysStaff, "0");
        if (r.getCode() != 0) return r;
        return R.ok(sysStaffService.saveSysStaff(sysStaff));
    }

    /**
     * 修改人员
     *
     * @param sysStaff 实体对象
     * @return 修改结果
     */
    @SysLog("修改人员")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('staff_edit')")
    @Operation(summary = "修改人员")
    public R updateSysStaffById(@Validated(ValidGroup.Update.class) @RequestBody SysStaff sysStaff) {
        R r = checkSysStaff(sysStaff, "1");
        if (r.getCode() != 0) return r;
        return R.ok(sysStaffService.updateSysStaffById(sysStaff));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @SysLog("删除人员")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('staff_delt')")
    @Operation(summary = "删除人员")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysStaffService.removeByIds(idList));
    }

    /**
     * 通过手机号查询人员基本信息
     *
     * @param phone
     * @return
     */
    @Operation(summary = "通过手机号查询人员基本信息")
    @GetMapping("/getStaffByPhone")
    public R<SysStaffVO> getStaffByPhone(@RequestParam("phone") String phone, @RequestParam("openId") String openId) {
    	SysStaffVO sysStaffVO = sysStaffService.getStaffByPhone(phone, openId);
    	if (sysStaffVO == null) {
    		return R.failed(BusinessEnum.U2_QUERY_STAFF.getMsg());
    	}
        return R.ok(sysStaffVO);
    }

    /**
     * ESB批量导入人员列表
     *
     * @param esbStaffXml
     * @return
     */
    @Klock
    @Operation(summary = "批量导入人员列表")
    @PostMapping("/batch")
    public R addEsbStaffs(@RequestBody BatchStaffXml batchStaffXml) {
        return R.ok(sysStaffService.batchAddStaffs(batchStaffXml));
    }

    /**
     * 人员参数校验
     *
     * @param sysStaff: 菜单
     * @param type:            0-新增 1-修改
     * @return: work.metanet.feng.common.core.util.R
     **/
    private R checkSysStaff(SysStaff sysStaff, String type) {
        Long count = 0L;
        Long count2 = 0L;
        if ("0".equals(type)) {
            count = sysStaffService.count(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffNo, sysStaff.getStaffNo()));
            count2 = sysStaffService.count(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getIdentificationNo, sysStaff.getIdentificationNo()));
        } else {
            count = sysStaffService.count(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getStaffNo, sysStaff.getStaffNo()).ne(SysStaff::getId, sysStaff.getId()));
            count2 = sysStaffService.count(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, sysStaff.getOrganCode()).eq(SysStaff::getIdentificationNo, sysStaff.getIdentificationNo()).ne(SysStaff::getId, sysStaff.getId()));
        }
        if (count > 0) {
            return R.failed("人员工号已存在");
        }
        if (count2 > 0) {
            return R.failed("身份证号已存在");
        }
        return R.ok();
    }
}