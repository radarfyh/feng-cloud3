package work.metanet.feng.admin.controller;

import work.metanet.feng.admin.api.dto.SysAffiliationOrganDTO;
import work.metanet.feng.admin.api.entity.SysAffiliationOrgan;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.admin.service.SysAffiliationOrganService;
import work.metanet.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联盟机构关联表(SysAffiliationOrgan)表控制层
 *
 * @author edison
 * @since 2023-08-02
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("sysAffiliationOrgan")
@Tag(name = "联盟机构模块")
public class SysAffiliationOrganController {
    /**
     * 服务对象
     */
    private final SysAffiliationOrganService sysAffiliationOrganService;

    /**
     * 根据联盟id查询机构列表
     *
     * @return Response对象
     */
    @GetMapping("/getOrganListByAffiliationId")
    @Operation(summary = "根据联盟id查询机构列表")
    public R<List<SysOrgan>> getOrganListByAffiliationId(@RequestParam("affiliationId") Integer affiliationId) {
        return R.ok(this.sysAffiliationOrganService.getOrganListByAffiliationId(affiliationId));
    }

//    /**
//     * 配置机构关联联盟
//     *
//     * @param sysAffiliationOrganDTO 实体对象
//     * @return 新增结果
//     */
//    @PostMapping
//    @Operation(summary = "配置机构关联联盟")
//    public R configSysAffiliationOrgan(@RequestBody SysAffiliationOrganDTO sysAffiliationOrganDTO) {
//        return sysAffiliationOrganService.configSysAffiliationOrgan(sysAffiliationOrganDTO);
//    }

    /**
     * 新增数据
     *
     * @param sysAffiliationOrganDTO 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    public R insert(@RequestBody SysAffiliationOrganDTO sysAffiliationOrganDTO) {
        List<SysAffiliationOrgan> sysAffiliationOrganList = null;
        sysAffiliationOrganDTO.getOrganIdList().forEach(organId -> {
            SysAffiliationOrgan sysAffiliationOrgan = new SysAffiliationOrgan();
            sysAffiliationOrgan.setAffiliationId(sysAffiliationOrganDTO.getAffiliationId());
            sysAffiliationOrgan.setOrganId(organId);
            sysAffiliationOrganList.add(sysAffiliationOrgan);
        });
        return R.ok(this.sysAffiliationOrganService.saveBatch(sysAffiliationOrganList));
    }


    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    public R delete(@RequestParam("idList") List<Integer> idList) {
        return R.ok(this.sysAffiliationOrganService.removeByIds(idList));
    }

}
