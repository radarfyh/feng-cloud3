package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.metanet.feng.admin.api.dto.SocialDetailsDTO;
import work.metanet.feng.admin.api.entity.SysSocialDetails;
import work.metanet.feng.admin.api.vo.SocialDetailsVO;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.admin.service.SysSocialDetailsService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 社交账号控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysSocialDetials")
@Tag(name = "社交账号模块")
public class SysSocialDetailsController {

    private final SysSocialDetailsService socialDetailsService;

    @Operation(summary = "分页查询社交账号")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:social:query')")
    public R<IPage<SocialDetailsVO>> page(Page<SysSocialDetails> page, SocialDetailsDTO dto) {
        return R.ok(socialDetailsService.pageSocialDetails(page, dto));
    }
    
    @Operation(summary = "查询社交账号列表")
    @GetMapping("/list")
    public R<List<SocialDetailsVO>> list(SocialDetailsDTO dto) {
        return R.ok(socialDetailsService.listSocialDetails(dto));
    }

    @Operation(summary = "查询客户社交账号")
    @GetMapping("/by-customer/{customerId}")
    public R<List<SocialDetailsVO>> byCustomer(@PathVariable Integer customerId) {
        return R.ok(socialDetailsService.listByCustomer(customerId));
    }

    @Operation(summary = "查询联系人社交账号")
    @GetMapping("/by-contact/{contactId}")
    public R<List<SocialDetailsVO>> byContact(@PathVariable Integer contactId) {
        return R.ok(socialDetailsService.listByContact(contactId));
    }

    @Operation(summary = "查询员工社交账号")
    @GetMapping("/by-staff/{staffId}")
    public R<List<SocialDetailsVO>> byStaff(@PathVariable Integer staffId) {
        return R.ok(socialDetailsService.listByStaff(staffId));
    }

    @SysLog("新增社交账号")
    @Operation(summary = "新增社交账号")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('crm:social:add')")
    public R<Void> add(@Valid @RequestBody SocialDetailsDTO dto) {
        socialDetailsService.saveSocialDetails(dto);
        return R.ok();
    }

    @SysLog("修改社交账号")
    @Operation(summary = "修改社交账号")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('crm:social:update')")
    public R<Void> update(@Validated(ValidGroup.Update.class) @RequestBody SocialDetailsDTO dto) {
        socialDetailsService.updateSocialDetails(dto);
        return R.ok();
    }

    @SysLog("删除社交账号")
    @Operation(summary = "删除社交账号")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('crm:social:delete')")
    public R<Void> remove(@PathVariable Integer id) {
        socialDetailsService.removeSocialDetails(id);
        return R.ok();
    }

    @Operation(summary = "获取社交账号详情")
    @GetMapping("/{id}")
    public R<SocialDetailsVO> detail(@PathVariable Integer id) {
        return R.ok(socialDetailsService.getSocialDetails(id));
    }
}