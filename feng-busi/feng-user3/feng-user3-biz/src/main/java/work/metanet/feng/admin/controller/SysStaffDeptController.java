package work.metanet.feng.admin.controller;

import work.metanet.feng.admin.api.dto.SysStaffDeptDTO;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.service.SysStaffDeptService;
import work.metanet.feng.admin.xml.BatchStaffDeptXml;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 人员科室关联表(SysStaffDept)表控制层
 *
 * @author edison
 * @since 2023-12-26 
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysStaffDept")
@Tag(name = "人员部门模块")
public class SysStaffDeptController {
    /**
     * 服务对象
     */
    private final SysStaffDeptService sysStaffDeptService;

    /**
     * 外部接口无需token，通过人员工号查询科室列表
     *
     * @return Response对象
     */
    @GetMapping("/getDeptsByPraNo")
    @Operation(summary = "外部接口无需token，通过人员工号查询科室列表")
    public R<List<SysDepartment>> getDeptsByPraNo(@RequestParam("organCode") String organCode, @RequestParam("staffNo") String staffNo) {
        return R.ok(this.sysStaffDeptService.getDeptsByStaffNo(organCode, staffNo));
    }


    /**
     * 通过人员id查询科室列表
     *
     * @return Response对象
     */
    @GetMapping("/getDeptsByStaffId")
    @Operation(summary = "通过人员id查询科室列表")
    public R<List<SysDepartment>> getDeptsByStaffId(@RequestParam("staffId") Integer staffId) {
        return R.ok(this.sysStaffDeptService.getDeptsByStaffId(staffId));
    }

    /**
     * 通过人员工号查询科室列表
     *
     * @return Response对象
     */
    @GetMapping("/getDeptsByStaffNo")
    @Operation(summary = "通过人员工号查询科室列表")
    public R<List<SysDepartment>> getDeptsByStaffNo(@RequestParam("organCode") String organCode, @RequestParam("staffNo") String staffNo) {
        return R.ok(this.sysStaffDeptService.getDeptsByStaffNo(organCode, staffNo));
    }

    /**
     * 通过人员id配置科室列表
     *
     * @param sysStaffDeptDTO 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "通过人员id配置科室列表")
    public R configDept(@RequestBody SysStaffDeptDTO sysStaffDeptDTO) {
        return sysStaffDeptService.configDept(sysStaffDeptDTO);
    }

    /**
     * ESB批量导入人员科室关联
     *
     * @param esbStaffDeptXml
     * @return
     */
    @Operation(summary = "批量导入人员科室关联")
    @PostMapping("/batch")
    public R addStaffDepts(@RequestBody BatchStaffDeptXml batchStaffDeptXml) {
        return sysStaffDeptService.addStaffDepts(batchStaffDeptXml);
    }
}
