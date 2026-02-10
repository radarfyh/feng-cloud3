package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.BroadcastReceiveRecordQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpBroadcastReceiveRecordService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 广播消息接收记录表控制器
 * 提供广播接收记录的查询、状态更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/broadcast/receive-record")
@RequiredArgsConstructor
@Tag(name = "广播接收记录管理", description = "广播接收记录的增删改查和状态管理")
public class UmpBroadcastReceiveRecordController {

    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;

    @Operation(summary = "创建或更新接收记录", description = "创建或更新广播接收记录")
    @PostMapping("/upsert")
    public R<Boolean> upsertReceiveRecord(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType) {
        try {
            boolean success = umpBroadcastReceiveRecordService.upsertReceiveRecord(
                    broadcastId, receiverId, receiverType);
            return success ? R.ok(true) : R.failed("操作失败");
        } catch (Exception e) {
            log.error("创建或更新接收记录失败", e);
            return R.failed("创建或更新接收记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量创建或更新接收记录", description = "批量创建或更新广播接收记录")
    @PostMapping("/upsert/batch")
    public R<Integer> batchUpsertReceiveRecords(
            @RequestParam String broadcastId,
            @RequestBody List<Map<String, Object>> receivers) {
        try {
            int count = umpBroadcastReceiveRecordService.batchUpsertReceiveRecords(broadcastId, receivers);
            String ret = "成功处理" + String.valueOf(count) + "条接收记录";
            return R.ok(count, ret);
        } catch (Exception e) {
            log.error("批量创建或更新接收记录失败", e);
            return R.failed("批量创建或更新接收记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询接收记录", description = "根据复合主键查询广播接收记录")
    @GetMapping("/detail")
    public R<BroadcastReceiveRecordDetailVO> getReceiveRecord(
            @Parameter(description = "广播ID", required = true) 
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        BroadcastReceiveRecordDetailVO detail = umpBroadcastReceiveRecordService.getReceiveRecord(
                broadcastId, receiverId, receiverType);
        if (detail == null) {
            return R.failed("接收记录不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "分页查询接收记录", description = "根据条件分页查询广播接收记录")
    @PostMapping("/page")
    public R<Page<BroadcastReceiveRecordPageVO>> queryReceiveRecordPage(@Valid @RequestBody BroadcastReceiveRecordQueryDTO queryDTO) {
        Page<BroadcastReceiveRecordPageVO> page = umpBroadcastReceiveRecordService.queryReceiveRecordPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "更新接收状态", description = "更新广播接收记录的接收状态")
    @PutMapping("/receive-status")
    public R<Boolean> updateReceiveStatus(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam String receiveStatus) {
        boolean success = umpBroadcastReceiveRecordService.updateReceiveStatus(
                broadcastId, receiverId, receiverType, receiveStatus);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "标记为已送达", description = "将广播接收记录标记为已送达")
    @PutMapping("/delivered")
    public R<Boolean> markAsDelivered(
            @Parameter(description = "广播ID", required = true) 
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        boolean success = umpBroadcastReceiveRecordService.markAsDelivered(
                broadcastId, receiverId, receiverType);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "批量标记为已送达", description = "批量将广播接收记录标记为已送达")
    @PutMapping("/delivered/batch")
    public R<Integer> batchMarkAsDelivered(
            @RequestParam String broadcastId,
            @RequestBody List<String> receiverIds,
            @RequestParam String receiverType) {
        int updatedCount = umpBroadcastReceiveRecordService.batchMarkAsDelivered(
                broadcastId, receiverIds, receiverType);
        String ret = "成功标记" + String.valueOf(updatedCount) + "条记录为已送达";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "更新阅读状态", description = "更新广播接收记录的阅读状态")
    @PutMapping("/read-status")
    public R<Boolean> updateReadStatus(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam Integer readStatus) {
        boolean success = umpBroadcastReceiveRecordService.updateReadStatus(
                broadcastId, receiverId, receiverType, readStatus);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "标记为已读", description = "将广播接收记录标记为已读")
    @PutMapping("/read")
    public R<Boolean> markAsRead(
            @Parameter(description = "广播ID", required = true) 
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        boolean success = umpBroadcastReceiveRecordService.markAsRead(
                broadcastId, receiverId, receiverType);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "批量标记为已读", description = "批量将广播接收记录标记为已读")
    @PutMapping("/read/batch")
    public R<Integer> batchMarkAsRead(
            @RequestParam String broadcastId,
            @RequestBody List<String> receiverIds,
            @RequestParam String receiverType) {
        int updatedCount = umpBroadcastReceiveRecordService.batchMarkAsRead(
                broadcastId, receiverIds, receiverType);
        String ret = "成功标记" + String.valueOf(updatedCount) + "条记录为已读";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "根据接收者标记为已读", description = "根据接收者标记广播为已读")
    @PutMapping("/read/receiver")
    public R<Integer> markAsReadByReceiver(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @RequestBody(required = false) List<String> broadcastIds) {
        int updatedCount = umpBroadcastReceiveRecordService.markAsReadByReceiver(
                receiverId, receiverType, broadcastIds);
        String ret = "成功标记" + String.valueOf(updatedCount) + "条记录";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "获取广播接收统计", description = "获取广播的接收统计信息")
    @GetMapping("/statistics/{broadcastId}")
    public R<BroadcastReceiveRecordStatisticsVO> getBroadcastReceiveStatistics(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId) {
        BroadcastReceiveRecordStatisticsVO statistics = umpBroadcastReceiveRecordService.getBroadcastReceiveStatistics(broadcastId);
        return R.ok(statistics);
    }

    @Operation(summary = "查询广播未读接收者", description = "查询广播的未读接收者列表")
    @GetMapping("/unread-receivers/{broadcastId}")
    public R<List<Map<String, Object>>> getUnreadReceivers(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId,
            @Parameter(description = "限制数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        List<Map<String, Object>> receivers = umpBroadcastReceiveRecordService.getUnreadReceivers(broadcastId, limit);
        return R.ok(receivers);
    }

    @Operation(summary = "查询接收者的广播记录", description = "查询指定接收者的广播接收记录")
    @GetMapping("/receiver")
    public R<List<BroadcastReceiveRecordDetailVO>> getReceiverBroadcasts(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @Parameter(description = "阅读状态") 
            @RequestParam(required = false) Integer readStatus,
            @Parameter(description = "限制数量", example = "20") 
            @RequestParam(defaultValue = "20") int limit) {
        List<BroadcastReceiveRecordDetailVO> broadcasts = umpBroadcastReceiveRecordService.getReceiverBroadcasts(
                receiverId, receiverType, readStatus, limit);
        return R.ok(broadcasts);
    }

    @Operation(summary = "删除接收记录", description = "删除广播接收记录")
    @DeleteMapping
    public R<Boolean> deleteReceiveRecord(
            @Parameter(description = "广播ID", required = true) 
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType) {
        boolean success = umpBroadcastReceiveRecordService.deleteReceiveRecord(
                broadcastId, receiverId, receiverType);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "根据广播ID删除接收记录", description = "根据广播ID删除相关的接收记录")
    @DeleteMapping("/broadcast/{broadcastId}")
    public R<Long> deleteByBroadcastId(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId) {
        long deletedCount = umpBroadcastReceiveRecordService.deleteByBroadcastId(broadcastId);
        String ret = "成功删除" + String.valueOf(deletedCount) + "条记录";
        return R.ok(deletedCount, ret);
    }
}