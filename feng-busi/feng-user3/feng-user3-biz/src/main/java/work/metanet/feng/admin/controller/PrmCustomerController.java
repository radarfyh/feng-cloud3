package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.metanet.feng.admin.api.dto.CustomerDTO;
import work.metanet.feng.admin.api.entity.PrmCustomer;
import work.metanet.feng.admin.api.vo.CustomerDetailVO;
import work.metanet.feng.admin.api.vo.PrmCustomerVO;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.admin.service.PrmCustomerService;

import java.util.List;

import jakarta.validation.Valid;

/**
 * 客户管理控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Tag(name = "客户管理")
@RestController
@RequestMapping("/prmCustomer")
@RequiredArgsConstructor
@Validated
public class PrmCustomerController {

    private final PrmCustomerService customerService;

    @Operation(summary = "客户分页列表")
    @PostMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:customer:query')")
    public R<IPage<PrmCustomerVO>> page(Page page, @RequestBody @Valid CustomerDTO dto) {
        return R.ok(customerService.customerPage(page, dto));
    }
    
    @Operation(summary = "客户列表")
    @PostMapping("/list")
    public R<List<PrmCustomerVO>> list(@RequestBody @Valid CustomerDTO dto) {
        return R.ok(customerService.customerList(dto));
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{customerId}")
    public R<CustomerDetailVO> detail(@PathVariable Integer customerId) {
        return R.ok(customerService.getCustomerDetail(customerId));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('crm:customer:add')")
    public R add(@RequestBody @Validated(ValidGroup.Save.class) @Valid CustomerDTO dto) {
    	PrmCustomer customer = new PrmCustomer();
    	BeanUtil.copyProperties(dto, customer);
        if (customerService.saveCustomer(customer))
        	return R.ok();
        else 
        	return R.failed("新增失败，名称：" + dto.getName());
    }

    @Operation(summary = "修改客户")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('crm:customer:update')")
    public R update(@RequestBody @Validated(ValidGroup.Update.class) @Valid CustomerDTO dto) {
    	PrmCustomer customer = new PrmCustomer();
    	BeanUtil.copyProperties(dto, customer);
        if (customerService.saveCustomer(customer))
        	return R.ok();
        else 
        	return R.failed("修改失败，ID：" + dto.getId());
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{customerId}")
    @PreAuthorize("@pms.hasPermission('crm:customer:delete')")
    public R remove(@PathVariable Integer customerId) {
        if (customerService.removeCustomer(customerId))
        	return R.ok();
        else {
        	return R.failed("删除失败，ID：" + customerId.toString());
        }
    }

    @Operation(summary = "客户关系网络")
    @GetMapping("/network/{customerId}")
    public R<CustomerDetailVO> network(@PathVariable Integer customerId) {
        return R.ok(customerService.getCustomerNetwork(customerId));
    }
}