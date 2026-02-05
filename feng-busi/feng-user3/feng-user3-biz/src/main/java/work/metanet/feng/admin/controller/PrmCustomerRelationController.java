package work.metanet.feng.admin.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.service.PrmCustomerRelationService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.admin.api.dto.CustomerRelationDTO;
import work.metanet.feng.admin.api.entity.PrmCustomerRelation;
import work.metanet.feng.admin.api.vo.CustomerRelationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import java.util.List;

/**
 * 客户关系管理控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequestMapping("/prmCustomerRelation")
@Tag(name = "客户关系管理")
public class PrmCustomerRelationController {

    private final PrmCustomerRelationService relationService;

    public PrmCustomerRelationController(PrmCustomerRelationService relationService) {
        this.relationService = relationService;
    }
    
    @Operation(summary = "分页查询关系网络")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:relation:query')")
    public R<IPage<CustomerRelationVO>> page(Page<PrmCustomerRelation> page, CustomerRelationDTO dto) {
        return R.ok(relationService.pageRelationship(page, dto));
    }
    
    @Operation(summary = "查询关系网络列表")
    @GetMapping("/list")
    public R<List<CustomerRelationVO>> list(CustomerRelationDTO dto) {
        return R.ok(relationService.listRelationship(dto));
    }
    
    @PostMapping
    @Operation(summary = "创建客户关系")
    @PreAuthorize("@pms.hasPermission('crm:customer-relation:add')")
    public R<CustomerRelationVO> saveRelation(@RequestBody @Validated CustomerRelationDTO dto) {
    	CustomerRelationVO ret = relationService.saveRelation(dto);
    	if(ret == null) {
    		return R.failed("创建客户关系失败");
    	}
        return R.ok(ret);
    }
    
    @PutMapping
    @Operation(summary = "更新客户关系")
    @PreAuthorize("@pms.hasPermission('crm:customer-relation:update')")
    public R<CustomerRelationVO> updateRelation(@RequestBody @Validated CustomerRelationDTO dto) {
    	CustomerRelationVO ret = relationService.saveRelation(dto);
    	if(ret == null) {
    		return R.failed("创建客户关系失败");
    	}
        return R.ok(ret);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户关系")
    @PreAuthorize("@pms.hasPermission('crm:customer-relation:delete')")
    public R removeRelation(@PathVariable Integer id) {
        relationService.removeRelation(id);
        return R.ok();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "查询客户的所有关系")
    public R<List<CustomerRelationVO>> listRelations(@PathVariable Integer customerId) {
        return R.ok(relationService.listRelations(customerId));
    }

    @GetMapping("/between")
    @Operation(summary = "查询两个客户间的关系")
    public R<List<CustomerRelationVO>> getRelationBetweenCustomers(
            @RequestParam Integer customerId,
            @RequestParam Integer relatedCustomerId) {
        return R.ok(relationService.getRelationBetweenCustomers(customerId, relatedCustomerId));
    }

    @PostMapping("/recalculate/{customerId}")
    @Operation(summary = "重新计算关系强度")
    public R recalculateStrength(@PathVariable Integer customerId) {
        relationService.recalculateStrength(customerId);
        return  R.ok();
    }
}