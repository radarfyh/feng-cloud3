package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息主表控制器
 * 提供消息的CRUD、状态管理、统计查询等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/main")
@RequiredArgsConstructor
@Tag(name = "消息主表管理", description = "消息主表的增删改查、状态管理和统计查询")
public class UmpMsgMainController {

    private final UmpMsgMainService umpMsgMainService;

    @Operation(summary = "创建消息", description = "创建一条新的消息")
    @PostMapping("/create")
    public R<String> createMessage(@Valid @RequestBody MessageSendDTO sendDTO) {
        try {
            String msgId = umpMsgMainService.createMessage(sendDTO);
            return R.ok("消息创建成功", msgId);
        } catch (Exception e) {
            log.error("创建消息失败", e);
            return R.failed("消息创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "创建代理消息", description = "创建代理平台转发的消息")
    @PostMapping("/create/agent")
    public R<String> createAgentMessage(
            @Valid @RequestBody MessageSendDTO sendDTO,
            @RequestParam String agentAppKey,
            @RequestParam String agentMsgId) {
        try {
            String msgId = umpMsgMainService.createAgentMessage(sendDTO, agentAppKey, agentMsgId);
            return R.ok("代理消息创建成功", msgId);
        } catch (Exception e) {
            log.error("创建代理消息失败", e);
            return R.failed("代理消息创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据消息编码查询消息", description = "根据消息编码查询消息详情")
    @GetMapping("/code/{msgCode}")
    public R<MessageDetailVO> getMessageByCode(
            @Parameter(description = "消息编码", required = true) 
            @PathVariable String msgCode) {
        MessageDetailVO message = umpMsgMainService.getMessageByCode(msgCode);
        if (message == null) {
            return R.failed("消息不存在");
        }
        return R.ok(message);
    }

    @Operation(summary = "分页查询消息", description = "根据条件分页查询消息列表")
    @PostMapping("/page")
    public R<Page<MessagePageVO>> queryMessagePage(@Valid @RequestBody MessageQueryDTO queryDTO) {
        Page<MessagePageVO> page = umpMsgMainService.queryMessagePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "更新消息状态", description = "更新单条消息的状态")
    @PutMapping("/status/{msgId}")
    public R<Boolean> updateMessageStatus(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId,
            @Parameter(description = "目标状态", required = true) 
            @RequestParam String status) {
        boolean success = umpMsgMainService.updateMessageStatus(msgId, status);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "批量更新消息状态", description = "批量更新多条消息的状态")
    @PutMapping("/status/batch")
    public R<Integer> batchUpdateMessageStatus(
            @RequestBody List<String> msgIds,
            @RequestParam String status) {
        int updatedCount = umpMsgMainService.batchUpdateMessageStatus(msgIds, status);
        String ret = "成功更新" + String.valueOf(updatedCount) + "条消息";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "标记消息为已发送", description = "将消息状态标记为已发送")
    @PutMapping("/sent/{msgId}")
    public R<Boolean> markAsSent(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        boolean success = umpMsgMainService.markAsSent(msgId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记消息为已分发", description = "将消息状态标记为已分发")
    @PutMapping("/distributed/{msgId}")
    public R<Boolean> markAsDistributed(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        boolean success = umpMsgMainService.markAsDistributed(msgId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "更新已读统计", description = "更新消息的已读人数统计")
    @PutMapping("/statistics/read/{msgId}")
    public R<Boolean> updateReadStatistics(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId,
            @Parameter(description = "已读人数", required = true) 
            @RequestParam int readCount) {
        boolean success = umpMsgMainService.updateReadStatistics(msgId, readCount);
        return success ? R.ok(true) : R.failed("更新失败");
    }

    @Operation(summary = "处理过期消息", description = "扫描并处理所有过期的消息")
    @PostMapping("/process/expired")
    public R<Integer> processExpiredMessages() {
        int processedCount = umpMsgMainService.processExpiredMessages();
        return R.ok(processedCount, "已处理" + processedCount + "条过期消息");
    }

    @Operation(summary = "获取消息统计", description = "获取指定时间段内的消息统计信息")
    @GetMapping("/statistics")
    public R<MessageStatisticsVO> getMessageStatistics(
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "应用标识") 
            @RequestParam(required = false) String appKey) {
        
        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        MessageStatisticsVO statistics = umpMsgMainService.getMessageStatistics(startTime, endTime, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "查询未读消息", description = "查询指定接收者的未读消息")
    @GetMapping("/unread")
    public R<List<MessageDetailVO>> getUnreadMessages(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @Parameter(description = "限制数量", example = "20") 
            @RequestParam(defaultValue = "20") int limit) {
        
        List<MessageDetailVO> messages = umpMsgMainService.getUnreadMessages(receiverId, receiverType, limit);
        return R.ok(messages);
    }

    @Operation(summary = "检查消息有效性", description = "检查消息是否存在且未删除")
    @GetMapping("/exists/{msgId}")
    public R<Boolean> existsAndValid(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        boolean exists = umpMsgMainService.existsAndValid(msgId);
        return R.ok(exists);
    }

    @Operation(summary = "删除消息", description = "逻辑删除消息")
    @DeleteMapping("/{msgId}")
    public R<Boolean> deleteMessage(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        UmpMsgMain message = umpMsgMainService.getById(msgId);
        if (message == null) {
            return R.failed("消息不存在");
        }
        
        message.setDelFlag(1);
        boolean success = umpMsgMainService.updateById(message);
        return success ? R.ok(true) : R.failed("删除失败");
    }
}
