package work.metanet.feng.admin.controller;

import cn.hutool.json.JSONObject;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 安全策略管理控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/securityPolicy")
@Tag(name = "安全策略管理模块")
public class SecurityPolicyController {


    private final RedisTemplate redisTemplate;

    @SysLog("更新安全策略")
    @PostMapping("/saveSecurityPolicy")
    @Operation(summary = "更新安全策略")
    public R saveSecurityPolicy(@RequestBody JSONObject jsonObject) {
        String key = String.format("%s:%s", "security_policy", "check_policy");
        redisTemplate.opsForValue().set(key, jsonObject);
        return R.ok();
    }

    @GetMapping("/getSecurityPolicy")
    @Operation(summary = "获取安全策略")
    public R getSecurityPolicy() {
        String key = String.format("%s:%s", "security_policy", "check_policy");
        return R.ok(redisTemplate.opsForValue().get(key));
    }
}
