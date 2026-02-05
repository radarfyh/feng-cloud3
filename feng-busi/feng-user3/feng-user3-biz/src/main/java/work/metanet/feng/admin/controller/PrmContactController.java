package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.metanet.feng.admin.api.dto.ContactDTO;
import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.vo.ContactDetailVO;
import work.metanet.feng.admin.api.vo.ContactSimpleVO;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.admin.service.PrmContactService;

import java.util.List;

/**
 * 联系人控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/prmContact")
@Tag(name = "联系人模块")
public class PrmContactController {

    private final PrmContactService contactService;

    @Operation(summary = "联系人分页列表")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:contact:query')")
    public R<IPage<ContactDetailVO>> page(Page<PrmContact> page, PrmContact contact) {
        return R.ok(contactService.pageContact(page, contact));
    }
    
    @Operation(summary = "联系人列表（简略）")
    @GetMapping("/list")
    public R<List<ContactSimpleVO>> list(PrmContact contact) {
        return R.ok(contactService.listContact(contact));
    }

    @Operation(summary = "联系人详情")
    @GetMapping("/{contactId}")
    public R<ContactDetailVO> detail(@PathVariable Integer contactId) {
        return R.ok(contactService.getContactDetail(contactId));
    }

    @SysLog("新增联系人")
    @Operation(summary = "新增联系人")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('crm:contact:add')")
    public R add(@Validated(ValidGroup.Save.class) @RequestBody ContactDTO dto) {
        if (contactService.saveContact(dto))
        	return R.ok();
        else
        	return R.failed("新增联系人失败");
    }

    @SysLog("修改联系人")
    @Operation(summary = "修改联系人")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('crm:contact:update')")
    public R update(@Validated(ValidGroup.Update.class) @RequestBody ContactDTO dto) {
        if (contactService.updateContact(dto))
        	return R.ok();
        else 
        	return R.failed("修改联系人失败");
    }

    @SysLog("删除联系人")
    @Operation(summary = "删除联系人")
    @DeleteMapping("/{contactId}")
    @PreAuthorize("@pms.hasPermission('crm:contact:delete')")
    public R remove(@PathVariable Integer contactId) {
        if (contactService.removeContact(contactId))
        	return R.ok();
        else
        	return R.failed("删除联系人失败");
    }

    @Operation(summary = "根据客户ID查询联系人")
    @GetMapping("/byCustomer/{customerId}")
    public R<List<ContactSimpleVO>> byCustomer(@PathVariable Integer customerId) {
        return R.ok(contactService.listByCustomerId(customerId));
    }
}