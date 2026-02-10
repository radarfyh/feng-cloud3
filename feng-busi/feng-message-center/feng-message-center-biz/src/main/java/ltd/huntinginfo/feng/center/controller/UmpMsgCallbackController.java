package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgCallbackService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 回调记录表控制器
 * 提供回调记录的创建、查询、状态管理等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/callback")
@RequiredArgsConstructor
@Tag(name = "回调记录管理", description = "回调记录的增删改查、状态管理和统计查询")
public class UmpMsgCallbackController {

    private final UmpMsgCallbackService umpMsgCallbackService;

    @Operation(summary = "创建回调记录", description = "创建一条新的回调记录")
    @PostMapping("/create")
    public R<String> createCallback(
            @RequestParam String msgId,
            @RequestParam String receiverId,
            @RequestParam String callbackUrl,
            @RequestParam(required = false) String callbackMethod,
            @RequestBody Map<String, Object> callbackData,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false) String callbackId) {
        try {
            String id = umpMsgCallbackService.createCallback(
                    msgId, receiverId, callbackUrl, callbackMethod, callbackData, signature, callbackId);
            String ret = "回调记录创建成功";
            return R.ok(id, ret);
        } catch (Exception e) {
            log.error("创建回调记录失败", e);
            return R.failed("回调记录创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量创建回调记录", description = "批量创建回调记录")
    @PostMapping("/create/batch")
    public R<Integer> batchCreateCallbacks(@RequestBody List<Map<String, Object>> callbacks) {
        try {
            int count = umpMsgCallbackService.batchCreateCallbacks(callbacks);
            String ret = "成功创建" + String.valueOf(count) + "条回调记录";
            return R.ok(count, ret);
        } catch (Exception e) {
            log.error("批量创建回调记录失败", e);
            return R.failed("批量创建回调记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据消息和接收者查询回调记录", description = "根据消息ID和接收者ID查询回调记录列表")
    @GetMapping("/message/{msgId}/receiver/{receiverId}")
    public R<List<CallbackDetailVO>> getCallbacksByMsgAndReceiver(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId,
            @Parameter(description = "接收者ID", required = true) 
            @PathVariable String receiverId) {
        List<CallbackDetailVO> callbacks = umpMsgCallbackService.getCallbacksByMsgAndReceiver(msgId, receiverId);
        return R.ok(callbacks);
    }

    @Operation(summary = "分页查询回调记录", description = "根据条件分页查询回调记录列表")
    @PostMapping("/page")
    public R<Page<CallbackPageVO>> queryCallbackPage(@Valid @RequestBody CallbackQueryDTO queryDTO) {
        Page<CallbackPageVO> page = umpMsgCallbackService.queryCallbackPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取回调记录详情", description = "根据回调记录ID获取详情")
    @GetMapping("/{callbackId}")
    public R<CallbackDetailVO> getCallbackDetail(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId) {
        CallbackDetailVO detail = umpMsgCallbackService.getCallbackDetail(callbackId);
        if (detail == null) {
            return R.failed("回调记录不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "更新回调状态", description = "更新回调记录的状态")
    @PutMapping("/status/{callbackId}")
    public R<Boolean> updateCallbackStatus(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId,
            @Parameter(description = "状态", required = true) 
            @RequestParam String status,
            @Parameter(description = "HTTP状态码") 
            @RequestParam(required = false) Integer httpStatus,
            @Parameter(description = "响应内容") 
            @RequestParam(required = false) String responseBody,
            @Parameter(description = "错误信息") 
            @RequestParam(required = false) String errorMessage) {
        boolean success = umpMsgCallbackService.updateCallbackStatus(
                callbackId, status, httpStatus, responseBody, errorMessage);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "标记回调为处理中", description = "将回调记录标记为处理中状态")
    @PutMapping("/processing/{callbackId}")
    public R<Boolean> markAsProcessing(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId) {
        boolean success = umpMsgCallbackService.markAsProcessing(callbackId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记回调为成功", description = "将回调记录标记为成功状态")
    @PutMapping("/success/{callbackId}")
    public R<Boolean> markAsSuccess(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId,
            @Parameter(description = "HTTP状态码", required = true) 
            @RequestParam Integer httpStatus,
            @Parameter(description = "响应内容") 
            @RequestParam(required = false) String responseBody) {
        boolean success = umpMsgCallbackService.markAsSuccess(callbackId, httpStatus, responseBody);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记回调为失败", description = "将回调记录标记为失败状态")
    @PutMapping("/failed/{callbackId}")
    public R<Boolean> markAsFailed(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId,
            @Parameter(description = "HTTP状态码") 
            @RequestParam(required = false) Integer httpStatus,
            @Parameter(description = "错误信息", required = true) 
            @RequestParam String errorMessage) {
        boolean success = umpMsgCallbackService.markAsFailed(callbackId, httpStatus, errorMessage);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "重试失败回调", description = "重试失败的回调记录")
    @PutMapping("/retry/{callbackId}")
    public R<Boolean> retryFailedCallback(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId,
            @Parameter(description = "重试延迟分钟数", example = "5") 
            @RequestParam(defaultValue = "5") int retryDelayMinutes) {
        boolean success = umpMsgCallbackService.retryFailedCallback(callbackId, retryDelayMinutes);
        return success ? R.ok(true) : R.failed("重试失败");
    }

    @Operation(summary = "批量重试失败回调", description = "批量重试失败的回调记录")
    @PutMapping("/retry/batch")
    public R<Integer> batchRetryFailedCallbacks(
            @RequestBody List<String> callbackIds,
            @Parameter(description = "重试延迟分钟数", example = "5") 
            @RequestParam(defaultValue = "5") int retryDelayMinutes) {
        int retriedCount = umpMsgCallbackService.batchRetryFailedCallbacks(callbackIds, retryDelayMinutes);
        String ret = "成功重试" + String.valueOf(retriedCount) + "条回调记录";
        return R.ok(retriedCount, ret);
    }

    @Operation(summary = "处理待发送回调", description = "处理待发送的回调记录，通常由定时任务调用")
    @PostMapping("/process/pending")
    public R<Integer> processPendingCallbacks(
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgCallbackService.processPendingCallbacks(limit);
        String ret = "已处理" + String.valueOf(processedCount) + "条回调记录";
        return R.ok(processedCount, ret);
    }

    @Operation(summary = "处理待重试回调", description = "处理待重试的回调记录，通常由定时任务调用")
    @PostMapping("/process/retry")
    public R<Integer> processRetryCallbacks(
            @Parameter(description = "最大重试次数") 
            @RequestParam(required = false) Integer maxRetryCount,
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgCallbackService.processRetryCallbacks(maxRetryCount, limit);
        String ret = "已处理" + String.valueOf(processedCount) + "条重试回调记录";
        return R.ok(processedCount, ret);
    }

    @Operation(summary = "获取回调统计", description = "获取指定时间段内的回调统计信息")
    @GetMapping("/statistics")
    public R<CallbackStatisticsVO> getCallbackStatistics(
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "消息ID") 
            @RequestParam(required = false) String msgId) {
        
        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        CallbackStatisticsVO statistics = umpMsgCallbackService.getCallbackStatistics(
                startTime, endTime, msgId);
        return R.ok(statistics);
    }

    @Operation(summary = "删除回调记录", description = "删除回调记录")
    @DeleteMapping("/{callbackId}")
    public R<Boolean> deleteCallback(
            @Parameter(description = "回调记录ID", required = true) 
            @PathVariable String callbackId) {
        boolean success = umpMsgCallbackService.deleteCallback(callbackId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "根据消息ID删除回调记录", description = "根据消息ID删除相关的回调记录")
    @DeleteMapping("/message/{msgId}")
    public R<Long> deleteByMsgId(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        long deletedCount = umpMsgCallbackService.deleteByMsgId(msgId);
        String ret = "成功删除" + String.valueOf(deletedCount) + "条回调记录";
        return R.ok(deletedCount, ret);
    }
}