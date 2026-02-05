package work.metanet.feng.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.DepartmentAttributeDTO;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysDeptAttribute;
import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.service.SysDepartmentService;
import work.metanet.feng.admin.service.SysDeptAttributeService;
import work.metanet.feng.admin.service.SysDeptRelationService;
import work.metanet.feng.admin.xml.BatchDepartmentXml;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.klock.annotation.Klock;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门表(SysDepartment)表控制层
 *
 * @author edison
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysDepartment")
@Tag(name = "部门模块")
public class SysDepartmentController {
    /**
     * 服务对象
     */
    private final SysDepartmentService sysDepartmentService;

    private final SysDeptRelationService relationService;

    private final SysDeptAttributeService sysDeptAttributeService;

    /**
     * 分页查询所有数据
     *
     * @param page          分页对象
     * @param sysDepartment 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysDepartment sysDepartment) {
        Page sysDepartmentPage = this.sysDepartmentService.page(page, Wrappers.<SysDepartment>lambdaQuery()
        		.like(StringUtils.isNotBlank(sysDepartment.getDeptName()), SysDepartment::getDeptName, sysDepartment.getDeptName())
        		.eq(StringUtils.isNotBlank(sysDepartment.getDeptCode()), SysDepartment::getDeptCode, sysDepartment.getDeptCode())
        		.eq(StringUtils.isNotBlank(sysDepartment.getOrganCode()), SysDepartment::getOrganCode, sysDepartment.getOrganCode())
        		.eq(StringUtils.isNotBlank(sysDepartment.getParentCode()), SysDepartment::getParentCode, sysDepartment.getParentCode())
        		.eq(StringUtils.isNotBlank(sysDepartment.getSubjectCode()), SysDepartment::getSubjectCode, sysDepartment.getSubjectCode())
        		.eq(StringUtils.isNotBlank(sysDepartment.getSubjectName()), SysDepartment::getSubjectName, sysDepartment.getSubjectName())
        		.eq(StringUtils.isNotBlank(sysDepartment.getDeptCategoryCode()), SysDepartment::getDeptCategoryCode, sysDepartment.getDeptCategoryCode())
        		.eq(StringUtils.isNotBlank(sysDepartment.getDeptCategoryName()), SysDepartment::getDeptCategoryName, sysDepartment.getDeptCategoryName())
        		.eq(StringUtils.isNotBlank(sysDepartment.getBusinessSubjection()), SysDepartment::getBusinessSubjection, sysDepartment.getBusinessSubjection())
        		.eq(StringUtils.isNotBlank(sysDepartment.getBranchCode()), SysDepartment::getBranchCode, sysDepartment.getBranchCode())
        		.orderByDesc(SysDepartment::getSort, SysDepartment::getCreateTime));
        
        List<SysDepartment> records = sysDepartmentPage.getRecords();
        List<SysDepartmentVO> sysDepartmentVOS = BeanUtil.copyToList(records, SysDepartmentVO.class);
        for (SysDepartmentVO sysDepartmentVO : sysDepartmentVOS) {
            List<SysDeptAttribute> list = sysDeptAttributeService.list(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, sysDepartmentVO.getId()));
            if (null != list && list.size() > 0) {
                List<String> deptAttributeList = list.stream().map(SysDeptAttribute::getDeptAttribute).collect(Collectors.toList());
                sysDepartmentVO.setDeptAttributeList(deptAttributeList);
            }
        }
        sysDepartmentPage.setRecords(sysDepartmentVOS);
        return R.ok(sysDepartmentPage);
    }


    /**
     * 条件查询部门列表
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "条件查询部门列表")
    public R<List<SysDepartmentVO>> list(SysDepartment sysDepartment) {
        List<SysDepartment> sysDepartments = this.sysDepartmentService.list(Wrappers.<SysDepartment>lambdaQuery().like(StringUtils.isNotBlank(sysDepartment.getDeptName()), SysDepartment::getDeptName, sysDepartment.getDeptName()).eq(StringUtils.isNotBlank(sysDepartment.getDeptCode()), SysDepartment::getDeptCode, sysDepartment.getDeptCode()).eq(StringUtils.isNotBlank(sysDepartment.getOrganCode()), SysDepartment::getOrganCode, sysDepartment.getOrganCode()).eq(StringUtils.isNotBlank(sysDepartment.getParentCode()), SysDepartment::getParentCode, sysDepartment.getParentCode()).eq(StringUtils.isNotBlank(sysDepartment.getSubjectCode()), SysDepartment::getSubjectCode, sysDepartment.getSubjectCode()).eq(StringUtils.isNotBlank(sysDepartment.getSubjectName()), SysDepartment::getSubjectName, sysDepartment.getSubjectName()).eq(StringUtils.isNotBlank(sysDepartment.getDeptCategoryCode()), SysDepartment::getDeptCategoryCode, sysDepartment.getDeptCategoryCode()).eq(StringUtils.isNotBlank(sysDepartment.getDeptCategoryName()), SysDepartment::getDeptCategoryName, sysDepartment.getDeptCategoryName()).eq(StringUtils.isNotBlank(sysDepartment.getBusinessSubjection()), SysDepartment::getBusinessSubjection, sysDepartment.getBusinessSubjection()).eq(StringUtils.isNotBlank(sysDepartment.getBranchCode()), SysDepartment::getBranchCode, sysDepartment.getBranchCode()));
        List<SysDepartmentVO> sysDepartmentVOS = BeanUtil.copyToList(sysDepartments, SysDepartmentVO.class);
        for (SysDepartmentVO sysDepartmentVO : sysDepartmentVOS) {
            List<SysDeptAttribute> list = sysDeptAttributeService.list(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, sysDepartmentVO.getId()));
            if (null != list && list.size() > 0) {
                List<String> deptAttributeList = list.stream().map(SysDeptAttribute::getDeptAttribute).collect(Collectors.toList());
                sysDepartmentVO.setDeptAttributeList(deptAttributeList);
            }
        }
        return R.ok(sysDepartmentVOS);
    }

    /**
     * 通过主键查询单条部门数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条部门数据")
    public R selectOne(@PathVariable Serializable id) {
        SysDepartment sysDepartment = this.sysDepartmentService.getById(id);
        SysDepartmentVO sysDepartmentVO = BeanUtil.copyProperties(sysDepartment, SysDepartmentVO.class);
        List<SysDeptAttribute> list = sysDeptAttributeService.list(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, sysDepartmentVO.getId()));
        if (null != list && list.size() > 0) {
            List<String> deptAttributeList = list.stream().map(SysDeptAttribute::getDeptAttribute).collect(Collectors.toList());
            sysDepartmentVO.setDeptAttributeList(deptAttributeList);
        }
        return R.ok(sysDepartmentVO);
    }

    /**
     * 通过主键id查询单条部门数据
     *
     * @param deptId 主键
     * @return 单条数据
     */
    @GetMapping("/getDepartmentById")
    @Operation(summary = "通过主键id查询单条部门数据")
    public R getDepartmentById(@RequestParam("deptId") Integer deptId) {
        return R.ok(this.sysDepartmentService.getById(deptId));
    }

    /**
     * 通过部门编码查询部门数据
     *
     * @param deptCode
     * @return 单条数据
     */
    @GetMapping("/getDepartmentByDeptCode")
    @Operation(summary = "通过部门编码查询")
    public R getDepartmentByDeptCode(@RequestParam("deptCode") String deptCode) {
        return R.ok(this.sysDepartmentService.getOne(Wrappers.<SysDepartment>lambdaQuery().eq(SysDepartment::getDeptCode, deptCode)));
    }


    /**
     * 根据机构编码查询部门菜单树
     *
     * @return 树形菜单
     */
    @Operation(summary = "根据机构编码查询部门树")
    @GetMapping(value = "/tree")
    public R getTree(@RequestParam("organCode") String organCode, @RequestParam("isExtra") String isExtra) {
        return R.ok(sysDepartmentService.selectTree(organCode, isExtra));
    }

    /**
     * 新增数据
     *
     * @param departmentDTO 实体对象
     * @return 新增结果
     */
    @SysLog("新增部门")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('dept_add')")
    @Operation(summary = "新增部门")
    public R saveDept(@Validated(ValidGroup.Save.class) @RequestBody DepartmentAttributeDTO departmentDTO) {
        return sysDepartmentService.saveDept(departmentDTO);
    }

    /**
     * 编辑部门
     *
     * @param departmentDTO 实体对象
     * @return 修改结果
     */
    @SysLog("编辑部门")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('dept_edit')")
    @Operation(summary = "编辑部门")
    public R update(@Validated(ValidGroup.Update.class) @RequestBody DepartmentAttributeDTO departmentDTO) {
        departmentDTO.setUpdateTime(LocalDateTime.now());
        return sysDepartmentService.updateDepartmentById(departmentDTO);
    }

    /**
     * 删除数据
     *
     * @param id 主键id
     * @return 删除结果
     */
    @SysLog("删除科室，下级科室，关联的科室关系，科室属性，员工，用户")
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('dept_del')")
    @Operation(summary = "删除科室，下级科室，关联的科室关系，科室属性，员工，用户")
    public R removeDepartmentById(@RequestParam("id") Integer id) {
        return sysDepartmentService.removeDepartmentById(id);
    }

    /**
     * 查收子级列表  (前端暂时未用)
     *
     * @return 返回子级
     */
    @GetMapping(value = "/getDescendantList/{deptId}")
    @Operation(summary = "查询下级部门")
    public R getDescendantList(@PathVariable Integer deptId) {
        return R.ok(relationService.list(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, deptId)));
    }

    /**
     * 批量导入部门列表
     *
     * @param batchDepartmentXml
     * @return
     */
    @SysLog("批量导入部门")
    @Klock
    @Operation(summary = "批量导入部门")
    @PostMapping("/batch")
    public R addBatchDepartments(@RequestBody BatchDepartmentXml batchDepartmentXml) {
        return sysDepartmentService.batchAddDepartments(batchDepartmentXml);
    }
}