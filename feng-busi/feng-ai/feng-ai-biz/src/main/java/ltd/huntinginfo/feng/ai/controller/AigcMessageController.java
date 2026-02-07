package ltd.huntinginfo.feng.ai.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge;
import ltd.huntinginfo.feng.ai.api.entity.AigcMessage;
import ltd.huntinginfo.feng.ai.service.AigcMessageService;
import ltd.huntinginfo.feng.common.core.util.QueryPage;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.ServletUtil;
import ltd.huntinginfo.feng.common.data.mybatis.MybatisPage;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.swagger.annotation.ApiLog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AI消息控制类（APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RequestMapping("/message")
@RestController
@AllArgsConstructor
@Tag(name = "生成式AI消息模块")
public class AigcMessageController {
	
	static final Integer MAX_MESSAGE_COUNT = 20;

    private final AigcMessageService aigcMessageService;

    @GetMapping("/page")
    @Operation(summary = "分页查询消息")
    public R<IPage<AigcMessage>> list(Page<AigcMessage> page, AigcMessage data) {
        LambdaQueryWrapper<AigcMessage> queryWrapper = Wrappers.<AigcMessage>lambdaQuery()
                .like(!StrUtil.isBlank(data.getMessage()), AigcMessage::getMessage, data.getMessage())
                .like(!StrUtil.isBlank(data.getUsername()), AigcMessage::getUsername, data.getUsername())
                .eq(!StrUtil.isBlank(data.getRole()), AigcMessage::getRole, data.getRole())
                .orderByDesc(AigcMessage::getCreateTime);
        IPage<AigcMessage> iPage = aigcMessageService.page(page, queryWrapper);
        return R.ok(iPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询")
    public R<AigcMessage> getById(@PathVariable Integer id) {
    	AigcMessage message = aigcMessageService.getById(id);
        return R.ok(message);
    }
    
    /**
     * get messages with conversationId，查询前20条
     */
    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "按会话ID查询消息")
    public R<List<AigcMessage>> getByConversationId(@PathVariable Integer conversationId) {
        List<AigcMessage> list = aigcMessageService.getMessages(conversationId, MAX_MESSAGE_COUNT);
        return R.ok(list);
    }
    
    @DeleteMapping("/{id}")
    @SysLog("删除消息")
    @Operation(summary = "删除消息")
    @PreAuthorize("@pms.hasPermission('aigc:message:delete')")
    public R<Boolean> delete(@PathVariable Integer id) {
        return R.ok(aigcMessageService.removeById(id));
    }
    
    @DeleteMapping("/conversation/{conversationId}")
    @SysLog("清空会话的消息 ")
    @PreAuthorize("@pms.hasPermission('aigc:conversation:clear')")
    @Operation(summary = "清空会话窗口数据")
    public R<?> clearMessage(@PathVariable Integer conversationId) {
    	aigcMessageService.clearMessage(conversationId);
        return R.ok();
    }
    /**
     * add message
     */
    @PostMapping
    @SysLog("新增消息")
    @Operation(summary = "新增消息")
    @PreAuthorize("@pms.hasPermission('aigc:message:add')")
    public R<AigcMessage> add(@RequestBody AigcMessage message) {
        // 创建时设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setUpdateTime(null);
        message.setIp(ServletUtil.getIpAddr());
        
        if (StrUtil.isBlank(message.getChatId())) {
        	message.setChatId(StrUtil.uuid());
        }
        return R.ok(aigcMessageService.addMessage(message));
    }
    
    /**
     * update message
     */
    @SysLog("修改消息")
    @PutMapping
    @Operation(summary = "修改消息")
    @PreAuthorize("@pms.hasPermission('aigc:message:update')")
    public R<AigcMessage> update(@RequestBody AigcMessage message) {
        message.setIp(ServletUtil.getIpAddr());
        // 保留原创建时间，不允许修改
        AigcMessage existing = aigcMessageService.getById(message.getId());
    	message.setCreateTime(existing.getCreateTime());
        // 设置修改时间为当前
    	message.setUpdateTime(LocalDateTime.now());
        return R.ok(aigcMessageService.addMessage(message));
    }
}
