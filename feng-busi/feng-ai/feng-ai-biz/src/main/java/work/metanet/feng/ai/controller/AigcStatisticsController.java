package work.metanet.feng.ai.controller;

import cn.hutool.core.lang.Dict;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.ai.mapper.AigcAppMapper;
import work.metanet.feng.ai.mapper.AigcKnowledgeMapper;
import work.metanet.feng.ai.mapper.AigcMessageMapper;
import work.metanet.feng.ai.service.AigcStatisticsSerivce;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制类（APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RequestMapping("/statistic")
@RestController
@AllArgsConstructor
@Tag(name = "生成式AI统计模块")
public class AigcStatisticsController {

    private final AigcStatisticsSerivce statService;

    @GetMapping("/requestBy30")
    @Operation(summary = "查询30请求统计表")
    public R request30Chart() {
        return R.ok(statService.request30Chart());
    }

    @GetMapping("/tokenBy30")
    @Operation(summary = "查询30令牌统计")
    public R token30Chart() {
        return R.ok(statService.token30Chart());
    }

    @GetMapping("/token")
    @Operation(summary = "查询令牌统计")
    public R tokenChart() {
        return R.ok(statService.tokenChart());
    }

    @GetMapping("/request")
    @Operation(summary = "查询请求统计")
    public R requestChart() {
        return R.ok(statService.requestChart());
    }

    @GetMapping("/home")
    @Operation(summary = "查询所有统计数据")
    public R home() {

        return R.ok(statService.home());
    }

}
