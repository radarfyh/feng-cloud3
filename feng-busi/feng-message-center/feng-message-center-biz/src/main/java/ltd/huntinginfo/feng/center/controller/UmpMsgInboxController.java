package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxPageVO;
import ltd.huntinginfo.feng.center.api.vo.ReceiverStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.common.core.util.R;
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
import java.util.Map;

/**
 * 收件箱表控制器
 * 提供收件箱的查询、状态更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/inbox")
@RequiredArgsConstructor
@Tag(name = "收件箱管理", description = "收件箱的查询、状态管理和统计")
public class UmpMsgInboxController {

    private final UmpMsgInboxService umpMsgInboxService;

    @Operation(summary = "分页查询收件箱", description = "根据条件分页查询收件箱记录")
    @PostMapping("/page")
    public R<Page<InboxPageVO>> queryInboxPage(@Valid @RequestBody InboxQueryDTO queryDTO) {
        Page<InboxPageVO> page = umpMsgInboxService.queryInboxPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询收件箱详情", description = "根据收件箱记录ID查询详情")
    @GetMapping("/{inboxId}")
    public R<InboxDetailVO> getInboxDetail(
            @Parameter(description = "收件箱记录ID", required = true) 
            @PathVariable String inboxId) {
        InboxDetailVO detail = umpMsgInboxService.getInboxDetail(inboxId);
        if (detail == null) {
            return R.failed("收件箱记录不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "根据消息和接收者查询", description = "根据消息ID和接收者信息查询收件箱记录")
    @GetMapping("/message/{msgId}")
    public R<InboxDetailVO> getByMsgAndReceiver(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId,
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        InboxDetailVO detail = umpMsgInboxService.getByMsgAndReceiver(msgId, receiverId, receiverType);
        if (detail == null) {
            return R.failed("收件箱记录不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "创建收件箱记录", description = "创建一条新的收件箱记录")
    @PostMapping("/create")
    public R<String> createInboxRecord(
            @RequestParam String msgId,
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam String receiverName,
            @RequestParam(required = false) String distributeMode) {
        try {
            String inboxId = umpMsgInboxService.createInboxRecord(msgId, receiverId, receiverType, 
                    receiverName, distributeMode);
            return R.ok("收件箱记录创建成功", inboxId);
        } catch (Exception e) {
            log.error("创建收件箱记录失败", e);
            return R.failed("收件箱记录创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量创建收件箱记录", description = "批量创建收件箱记录")
    @PostMapping("/create/batch")
    public R<Integer> batchCreateInboxRecords(
            @RequestParam String msgId,
            @RequestBody List<Map<String, Object>> receivers,
            @RequestParam(required = false) String distributeMode) {
        try {
            int count = umpMsgInboxService.batchCreateInboxRecords(msgId, receivers, distributeMode);
            return R.ok(count, "批量创建收件箱记录成功");
        } catch (Exception e) {
            log.error("批量创建收件箱记录失败", e);
            return R.failed("批量创建收件箱记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "标记为已接收", description = "将收件箱记录标记为已接收状态")
    @PutMapping("/received/{inboxId}")
    public R<Boolean> markAsReceived(
            @Parameter(description = "收件箱记录ID", required = true) 
            @PathVariable String inboxId) {
        boolean success = umpMsgInboxService.markAsReceived(inboxId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记为已读", description = "将收件箱记录标记为已读状态")
    @PutMapping("/read/{inboxId}")
    public R<Boolean> markAsRead(
            @Parameter(description = "收件箱记录ID", required = true) 
            @PathVariable String inboxId) {
        boolean success = umpMsgInboxService.markAsRead(inboxId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "批量标记为已读", description = "批量将收件箱记录标记为已读状态")
    @PutMapping("/read/batch")
    public R<Integer> batchMarkAsRead(@RequestBody List<String> inboxIds) {
        int updatedCount = umpMsgInboxService.batchMarkAsRead(inboxIds);
        return R.ok(updatedCount, "成功标记" + updatedCount + "条记录");
    }

    @Operation(summary = "根据接收者标记为已读", description = "根据接收者标记消息为已读")
    @PutMapping("/read/receiver")
    public R<Integer> markAsReadByReceiver(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @RequestBody(required = false) List<String> msgIds) {
        int updatedCount = umpMsgInboxService.markAsReadByReceiver(receiverId, receiverType, msgIds);
        return R.ok(updatedCount, "成功标记" + updatedCount + "条记录");
    }

    @Operation(summary = "统计未读消息数量", description = "根据接收者统计未读消息数量")
    @GetMapping("/unread/count")
    public R<Integer> countUnreadMessages(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        Integer count = umpMsgInboxService.countUnreadMessages(receiverId, receiverType);
        return R.ok(count != null ? count : 0);
    }

    @Operation(summary = "处理待推送消息", description = "处理待推送的消息，通常由定时任务调用")
    @PostMapping("/process/push")
    public R<Integer> processPendingPush(
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgInboxService.processPendingPush(limit);
        return R.ok(processedCount, "已处理" + processedCount + "条推送");
    }

    @Operation(summary = "获取接收者统计", description = "获取接收者的消息统计信息")
    @GetMapping("/statistics/receiver")
    public R<ReceiverStatisticsVO> getReceiverStatistics(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        ReceiverStatisticsVO statistics = umpMsgInboxService.getReceiverStatistics(receiverId, receiverType, 
                startTime, endTime);
        return R.ok(statistics);
    }

    @Operation(summary = "删除收件箱记录", description = "删除收件箱记录")
    @DeleteMapping("/{inboxId}")
    public R<Boolean> deleteInboxRecord(
            @Parameter(description = "收件箱记录ID", required = true) 
            @PathVariable String inboxId) {
        boolean success = umpMsgInboxService.deleteInboxRecord(inboxId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "根据消息ID删除", description = "根据消息ID删除相关的收件箱记录")
    @DeleteMapping("/message/{msgId}")
    public R<Integer> deleteByMsgId(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        Integer deletedCount = umpMsgInboxService.deleteByMsgId(msgId);
        return R.ok(deletedCount, "成功删除" + deletedCount + "条记录");
    }
}