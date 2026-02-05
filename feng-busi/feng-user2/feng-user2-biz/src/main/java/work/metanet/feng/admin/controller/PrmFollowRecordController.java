package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmFollowRecord;
import work.metanet.feng.admin.api.vo.FollowRecordVO;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.admin.service.PrmFollowRecordService;

import java.util.List;

/**
 * 客户跟踪记录控制器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/prmFollowRecord")
@Tag(name = "客户跟踪记录模块")
public class PrmFollowRecordController {

    private final PrmFollowRecordService followRecordService;

    @Operation(summary = "分页查询跟踪记录")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:follow:query')")
    public R<IPage<FollowRecordVO>> page(Page<PrmFollowRecord> page, FollowRecordDTO vo) {
        return R.ok(followRecordService.pageFollowRecord(page, vo));
    }
    
    @Operation(summary = "查询跟踪记录列表")
    @GetMapping("/list")
    public R<List<FollowRecordVO>> list(FollowRecordDTO vo) {
        return R.ok(followRecordService.listFollowRecord(vo));
    }

    @Operation(summary = "获取跟踪记录详情")
    @GetMapping("/{id}")
    public R<FollowRecordVO> detail(@PathVariable Integer id) {
        return R.ok(followRecordService.getFollowRecordDetail(id));
    }

    @SysLog("新增跟踪记录")
    @Operation(summary = "新增跟踪记录")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('crm:follow:add')")
    public R<Void> add(@Validated(ValidGroup.Save.class) @RequestBody FollowRecordDTO dto) {
        followRecordService.saveFollowRecord(dto);
        return R.ok();
    }

    @SysLog("修改跟踪记录")
    @Operation(summary = "修改跟踪记录")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('crm:follow:update')")
    public R<Void> update(@Validated(ValidGroup.Update.class) @RequestBody FollowRecordDTO dto) {
        followRecordService.updateFollowRecord(dto);
        return R.ok();
    }

    @SysLog("删除跟踪记录")
    @Operation(summary = "删除跟踪记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('crm:follow:delete')")
    public R<Void> remove(@PathVariable Integer id) {
        followRecordService.removeFollowRecord(id);
        return R.ok();
    }

    @Operation(summary = "查询客户的所有跟踪记录")
    @GetMapping("/by-customer/{customerId}")
    public R<List<FollowRecordVO>> byCustomer(@PathVariable Integer customerId) {
        return R.ok(followRecordService.listByCustomerId(customerId));
    }

    @Operation(summary = "查询联系人的所有跟踪记录")
    @GetMapping("/by-contact/{contactId}")
    public R<List<FollowRecordVO>> byContact(@PathVariable Integer contactId) {
        return R.ok(followRecordService.listByContactId(contactId));
    }
}