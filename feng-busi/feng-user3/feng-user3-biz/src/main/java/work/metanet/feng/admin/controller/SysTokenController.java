package work.metanet.feng.admin.controller;

import work.metanet.feng.admin.api.feign.RemoteTokenService;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 令牌管理控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/token")
@Tag(name = "令牌管理模块")
public class SysTokenController{

    private final RemoteTokenService remoteTokenService;

    /**
     * 分页token 信息
     *
     * @param params 参数集
     * @return token集合
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public R getTokenPage(@RequestParam Map<String, Object> params) {
        // 获取请求的
        return remoteTokenService.getTokenPage(params, SecurityConstants.FROM_IN);
    }

    /**
     * 删除token
     * @param token token
     * @return
     */
    @SysLog("删除用户token")
    @DeleteMapping("/{token}")
    @PreAuthorize("@pms.hasPermission('token_del')")
    @Operation(summary = "删除令牌")
    public R<Boolean> removeTokenById(@PathVariable String token) {
        return remoteTokenService.removeTokenById(token, SecurityConstants.FROM_IN);
    }

    /**
     * 校验令牌获取用户信息
     *
     * @param token
     * @return
     */
    @GetMapping("/query-token")
    @Operation(summary = "查询令牌")
    public R<Map<String, Object>> queryToken(@RequestParam("token") String token) {
        return remoteTokenService.queryToken(token, SecurityConstants.FROM_IN);
    }

}
