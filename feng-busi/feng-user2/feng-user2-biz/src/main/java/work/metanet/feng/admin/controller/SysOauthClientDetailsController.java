package work.metanet.feng.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.SysOauthClientDetailsDTO;
import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.admin.service.SysOauthClientDetailsService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 终端信息表(SysOauthClientDetails)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Tag(name = "终端认证模块")
@Slf4j
public class SysOauthClientDetailsController {
    /**
     * 服务对象
     */
    private final SysOauthClientDetailsService clientDetailsService;

    /**
     * 通过ID查询
     * @param clientId clientId
     * @return SysOauthClientDetails
     */
    @GetMapping("/{clientId}")
    @Operation(summary = "按ID查询，返回列表")
    public R getByClientId(@PathVariable String clientId) {
    	List<SysOauthClientDetails> clients = clientDetailsService
    			.list(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId));
    	log.debug("getByClientId --> clients: {}", clients);
    	
        return R.ok(clients);
    }

    /**
     * 简单分页查询
     * @param page 分页对象
     * @param sysOauthClientDetails 系统终端
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public R getOauthClientDetailsPage(Page page, SysOauthClientDetails sysOauthClientDetails) {
    	Page resultPage = clientDetailsService.queryPage(page, sysOauthClientDetails);
    	log.debug("getOauthClientDetailsPage --> resultPage: {}", resultPage);
    	
        return R.ok();
    }

    /**
     * 添加
     * @param clientDetailsDTO 实体
     * @return success/false
     */
    @SysLog("添加终端")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('client_add')")
    @Operation(summary = "新增认证终端")
    public R add(@Valid @RequestBody SysOauthClientDetailsDTO clientDetailsDTO) {
        return R.ok(clientDetailsService.saveClient(clientDetailsDTO));
    }

    /**
     * 删除
     * @param clientId ID
     * @return success/false
     */
    @SysLog("删除终端")
    @DeleteMapping("/{clientId}")
    @PreAuthorize("@pms.hasPermission('client_del')")
    @Operation(summary = "删除认证终端")
    public R removeById(@PathVariable String clientId) {
        return R.ok(clientDetailsService.removeByClientId(clientId));
    }

    /**
     * 编辑
     * @param clientDetailsDTO 实体
     * @return success/false
     */
    @SysLog("编辑终端")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('client_edit')")
    @Operation(summary = "修改认证终端")
    public R update(@Valid @RequestBody SysOauthClientDetailsDTO clientDetailsDTO) {
        return R.ok(clientDetailsService.updateClientById(clientDetailsDTO));
    }

    @Inner(false)
    @GetMapping("/getClientDetailsById/{clientId}")
    @Operation(summary = "按ID查询，返回第一条")
    public R getClientDetailsById(@PathVariable String clientId) {
    	SysOauthClientDetails client = clientDetailsService.getOne(
                Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false);
    	log.debug("getClientDetailsById --> client: {}", client);
        return R.ok(client);
    }

    /**
     * 查询全部客户端
     * @return
     */
    @Inner(false)
    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public R listClients() {
    	List<SysOauthClientDetails> clients = clientDetailsService.list();
    	log.debug("listClients --> clients: {}", clients);
    	
        return R.ok(clients);
    }

    /**
     * 同步缓存字典
     * @return R
     */
    @SysLog("同步终端")
    @PutMapping("/sync")
    @Operation(summary = "同步缓存")
    public R sync() {
        return clientDetailsService.syncClientCache();
    }
}