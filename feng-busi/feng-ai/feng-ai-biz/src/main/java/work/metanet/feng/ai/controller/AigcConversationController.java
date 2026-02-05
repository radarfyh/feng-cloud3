package work.metanet.feng.ai.controller;

import work.metanet.feng.ai.api.entity.AigcConversation;
import work.metanet.feng.ai.service.AigcConversationService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;

/**
 * AI会话控制类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@RequestMapping("/conversation")
@AllArgsConstructor
@Tag(name = "生成式AI会话模块")
public class AigcConversationController {

    private final AigcConversationService aigcConversationService;

    /**
     * conversation list, filter by user
     */
    @GetMapping("/list")
    @Operation(summary = "查询会话列表")
    public R<?> conversations() {
        return R.ok(aigcConversationService.conversations(SecurityUtils.getUser().getId()));
    }

    /**
     * conversation page
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询会话数据")
    public R<IPage<AigcConversation>> list(Page<AigcConversation> page, AigcConversation data) {
        return R.ok(aigcConversationService.conversationPages(data, page));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "按ID查询会话数据")
    public R<AigcConversation> findById(@PathVariable String id) {
    	AigcConversation conversation = aigcConversationService.getById(id);
        return R.ok(conversation);
    }
    
    @PostMapping
    @SysLog("添加会话窗口")
    @PreAuthorize("@pms.hasPermission('aigc:conversation:add')")
    @Operation(summary = "新增会话")
    public R<?> addConversation(@RequestBody AigcConversation conversation) {
        if (conversation.getUserId() == null || conversation.getUserId() <= 0) {
        	conversation.setUserId(SecurityUtils.getUser().getId());
        }
        if (StrUtil.isBlank(conversation.getUsername())) {
        	conversation.setUsername(SecurityUtils.getUser().getUsername());
        }
        if (StrUtil.isBlank(conversation.getCreateBy())) {
        	conversation.setCreateBy(SecurityUtils.getUser().getUsername());
        }
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        conversation.setCreateTime(now);
        conversation.setUpdateTime(null);
        return R.ok(aigcConversationService.addConversation(conversation));
    }

    @PutMapping
    @SysLog("更新会话窗口")
    @PreAuthorize("@pms.hasPermission('aigc:conversation:update')")
    @Operation(summary = "更新会话")
    public R<?> updateConversation(@RequestBody AigcConversation conversation) {
        if (conversation.getId() == null) {
            return R.failed("conversation id is null");
        }
        // 保留原创建时间，不允许修改
        AigcConversation existing = aigcConversationService.getById(conversation.getId());
        conversation.setCreateTime(existing.getCreateTime());
        conversation.setCreateBy(existing.getCreateBy());
        if (StrUtil.isBlank(conversation.getUpdateBy())) {
        	conversation.setUpdateBy(SecurityUtils.getUser().getUsername());
        }
        // 设置修改时间为当前
        conversation.setUpdateTime(LocalDateTime.now());
        aigcConversationService.updateConversation(conversation);
        return R.ok();
    }

    @DeleteMapping("/{conversationId}")
    @SysLog("删除会话窗口")
    @PreAuthorize("@pms.hasPermission('aigc:conversation:delete')")
    @Operation(summary = "删除会话")
    public R<?> delConversation(@PathVariable Integer conversationId) {
    	aigcConversationService.delConversation(conversationId);
        return R.ok();
    }

}
