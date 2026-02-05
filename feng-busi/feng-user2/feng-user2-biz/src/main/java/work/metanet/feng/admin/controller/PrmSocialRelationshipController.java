package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.metanet.feng.admin.api.dto.SocialRelationshipDTO;
import work.metanet.feng.admin.api.entity.PrmSocialRelationship;
import work.metanet.feng.admin.api.vo.SocialRelationshipVO;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.ValidGroup;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.admin.service.PrmSocialRelationshipService;

import java.util.List;

import javax.validation.Valid;

/**
 * 联系人关系网络控制器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/prmSocialRelationship")
@Tag(name = "联系人关系网络模块")
public class PrmSocialRelationshipController {

    private final PrmSocialRelationshipService relationshipService;

    @Operation(summary = "分页查询关系网络")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('crm:relation:query')")
    public R<IPage<SocialRelationshipVO>> page(Page<PrmSocialRelationship> page, SocialRelationshipDTO dto) {
        return R.ok(relationshipService.pageRelationship(page, dto));
    }
    
    @Operation(summary = "查询关系网络列表")
    @GetMapping("/list")
    public R<List<SocialRelationshipVO>> list(SocialRelationshipDTO dto) {
        return R.ok(relationshipService.listRelationship(dto));
    }

    @Operation(summary = "获取关系详情")
    @GetMapping("/{id}")
    public R<SocialRelationshipVO> detail(@PathVariable Integer id) {
        return R.ok(relationshipService.getRelationshipDetail(id));
    }

    @SysLog("新增联系人关系")
    @Operation(summary = "新增联系人关系")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('crm:relation:add')")
    public R<Void> add(@Validated(ValidGroup.Save.class) @RequestBody SocialRelationshipDTO dto) {
        relationshipService.saveRelationship(dto);
        return R.ok();
    }

    @SysLog("修改联系人关系")
    @Operation(summary = "修改联系人关系")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('crm:relation:update')")
    public R<Void> update(@Validated(ValidGroup.Update.class) @RequestBody SocialRelationshipDTO dto) {
        relationshipService.updateRelationship(dto);
        return R.ok();
    }

    @SysLog("删除联系人关系")
    @Operation(summary = "删除联系人关系")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('crm:relation:delete')")
    public R<Void> remove(@PathVariable Integer id) {
        relationshipService.removeRelationship(id);
        return R.ok();
    }

    @Operation(summary = "查询联系人A的所有关系")
    @GetMapping("/by-contact-a/{contactAId}")
    public R<List<SocialRelationshipVO>> byContactA(@PathVariable Integer contactAId) {
        return R.ok(relationshipService.listByContactA(contactAId));
    }

    @Operation(summary = "查询联系人B的所有关系")
    @GetMapping("/by-contact-b/{contactBId}")
    public R<List<SocialRelationshipVO>> byContactB(@PathVariable Integer contactBId) {
        return R.ok(relationshipService.listByContactB(contactBId));
    }
}