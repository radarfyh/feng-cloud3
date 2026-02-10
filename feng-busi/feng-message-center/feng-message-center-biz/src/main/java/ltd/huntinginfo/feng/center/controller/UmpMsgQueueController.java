package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
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
 * 消息队列表控制器
 * 提供队列任务的创建、查询、状态管理等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/queue")
@RequiredArgsConstructor
@Tag(name = "消息队列管理", description = "队列任务的增删改查、状态管理和统计查询")
public class UmpMsgQueueController {

    private final UmpMsgQueueService umpMsgQueueService;

    @Operation(summary = "创建队列任务", description = "创建一条新的队列任务")
    @PostMapping("/create")
    public R<String> createQueueTask(
            @RequestParam String queueType,
            @RequestParam String queueName,
            @RequestParam String msgId,
            @RequestBody Map<String, Object> taskData,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime executeTime,
            @RequestParam(required = false) Integer maxRetry) {
        try {
            String taskId = umpMsgQueueService.createQueueTask(
                    queueType, queueName, msgId, taskData, priority, executeTime, maxRetry);
            String ret = "队列任务创建成功";
            return R.ok(taskId, ret);
        } catch (Exception e) {
            log.error("创建队列任务失败", e);
            return R.failed("队列任务创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量创建队列任务", description = "批量创建队列任务")
    @PostMapping("/create/batch")
    public R<Integer> batchCreateQueueTasks(@RequestBody List<Map<String, Object>> tasks) {
        try {
            int count = umpMsgQueueService.batchCreateQueueTasks(tasks);
            String ret = "成功创建" + String.valueOf(count) + "条队列任务";
            return R.ok(count, ret);
        } catch (Exception e) {
            log.error("批量创建队列任务失败", e);
            return R.failed("批量创建队列任务失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据消息ID查询队列任务", description = "根据消息ID查询队列任务列表")
    @GetMapping("/message/{msgId}")
    public R<List<MsgQueueDetailVO>> getQueueTasksByMsgId(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getQueueTasksByMsgId(msgId);
        return R.ok(tasks);
    }

    @Operation(summary = "分页查询队列任务", description = "根据条件分页查询队列任务列表")
    @PostMapping("/page")
    public R<Page<MsgQueuePageVO>> queryQueuePage(@Valid @RequestBody MsgQueueQueryDTO queryDTO) {
        Page<MsgQueuePageVO> page = umpMsgQueueService.queryQueuePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取待执行任务", description = "获取待执行的队列任务")
    @GetMapping("/pending")
    public R<List<MsgQueueDetailVO>> getPendingTasks(
            @Parameter(description = "队列类型") 
            @RequestParam(required = false) String queueType,
            @Parameter(description = "队列名称") 
            @RequestParam(required = false) String queueName,
            @Parameter(description = "限制数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getPendingTasks(queueType, queueName, limit);
        return R.ok(tasks);
    }

    @Operation(summary = "获取任务详情", description = "根据任务ID获取队列任务详情")
    @GetMapping("/{taskId}")
    public R<MsgQueueDetailVO> getQueueTaskDetail(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId) {
        MsgQueueDetailVO detail = umpMsgQueueService.getQueueTaskDetail(taskId);
        if (detail == null) {
            return R.failed("队列任务不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "更新任务状态", description = "更新队列任务的状态")
    @PutMapping("/status/{taskId}")
    public R<Boolean> updateTaskStatus(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId,
            @Parameter(description = "状态", required = true) 
            @RequestParam String status,
            @Parameter(description = "工作者ID") 
            @RequestParam(required = false) String workerId,
            @Parameter(description = "结果代码") 
            @RequestParam(required = false) String resultCode,
            @Parameter(description = "结果消息") 
            @RequestParam(required = false) String resultMessage,
            @Parameter(description = "错误堆栈") 
            @RequestParam(required = false) String errorStack) {
        boolean success = umpMsgQueueService.updateTaskStatus(
                taskId, status, workerId, resultCode, resultMessage, errorStack);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "标记任务为处理中", description = "将队列任务标记为处理中状态")
    @PutMapping("/processing/{taskId}")
    public R<Boolean> markAsProcessing(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId,
            @Parameter(description = "工作者ID", required = true) 
            @RequestParam String workerId) {
        boolean success = umpMsgQueueService.markAsProcessing(taskId, workerId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记任务为成功", description = "将队列任务标记为成功状态")
    @PutMapping("/success/{taskId}")
    public R<Boolean> markAsSuccess(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId,
            @Parameter(description = "工作者ID", required = true) 
            @RequestParam String workerId,
            @Parameter(description = "结果消息") 
            @RequestParam(required = false) String resultMessage) {
        boolean success = umpMsgQueueService.markAsSuccess(taskId, workerId, resultMessage);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记任务为失败", description = "将队列任务标记为失败状态")
    @PutMapping("/failed/{taskId}")
    public R<Boolean> markAsFailed(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId,
            @Parameter(description = "工作者ID", required = true) 
            @RequestParam String workerId,
            @Parameter(description = "错误消息", required = true) 
            @RequestParam String errorMessage,
            @Parameter(description = "错误堆栈") 
            @RequestParam(required = false) String errorStack) {
        boolean success = umpMsgQueueService.markAsFailed(taskId, workerId, errorMessage, errorStack);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "重试失败任务", description = "重试失败的队列任务")
    @PutMapping("/retry/{taskId}")
    public R<Boolean> retryFailedTask(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId,
            @Parameter(description = "重试延迟分钟数", example = "5") 
            @RequestParam(defaultValue = "5") int retryDelayMinutes) {
        boolean success = umpMsgQueueService.retryFailedTask(taskId, retryDelayMinutes);
        return success ? R.ok(true) : R.failed("重试失败");
    }

    @Operation(summary = "批量重试失败任务", description = "批量重试失败的队列任务")
    @PutMapping("/retry/batch")
    public R<Integer> batchRetryFailedTasks(
            @RequestBody List<String> taskIds,
            @Parameter(description = "重试延迟分钟数", example = "5") 
            @RequestParam(defaultValue = "5") int retryDelayMinutes) {
        int retriedCount = umpMsgQueueService.batchRetryFailedTasks(taskIds, retryDelayMinutes);
        String ret = "成功重试" + String.valueOf(retriedCount) + "条任务";
        return R.ok(retriedCount, ret);
    }

    @Operation(summary = "处理待执行任务", description = "处理待执行的队列任务，通常由定时任务调用")
    @PostMapping("/process/pending")
    public R<Integer> processPendingTasks(
            @Parameter(description = "队列类型") 
            @RequestParam(required = false) String queueType,
            @Parameter(description = "队列名称") 
            @RequestParam(required = false) String queueName,
            @Parameter(description = "工作者ID", required = true) 
            @RequestParam String workerId,
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgQueueService.processPendingTasks(queueType, queueName, workerId, limit);
        String ret = "已处理" + String.valueOf(processedCount) + "条队列任务";
        return R.ok(processedCount, ret);
    }

    @Operation(summary = "获取队列统计", description = "获取指定时间段内的队列统计信息")
    @GetMapping("/statistics")
    public R<MsgQueueStatisticsVO> getQueueStatistics(
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "队列类型") 
            @RequestParam(required = false) String queueType) {
        
        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        MsgQueueStatisticsVO statistics = umpMsgQueueService.getQueueStatistics(
                startTime, endTime, queueType);
        return R.ok(statistics);
    }

    @Operation(summary = "处理超时任务", description = "处理超时的队列任务，通常由定时任务调用")
    @PostMapping("/process/timeout")
    public R<Integer> processTimeoutTasks(
            @Parameter(description = "超时时间（分钟）", example = "30") 
            @RequestParam(defaultValue = "30") int timeoutMinutes,
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgQueueService.processTimeoutTasks(timeoutMinutes, limit);
        String ret = "已处理" + String.valueOf(processedCount) + "条超时任务";
        return R.ok(processedCount, ret);
    }

    @Operation(summary = "删除队列任务", description = "删除队列任务")
    @DeleteMapping("/{taskId}")
    public R<Boolean> deleteQueueTask(
            @Parameter(description = "任务ID", required = true) 
            @PathVariable String taskId) {
        boolean success = umpMsgQueueService.deleteQueueTask(taskId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "根据消息ID删除队列任务", description = "根据消息ID删除相关的队列任务")
    @DeleteMapping("/message/{msgId}")
    public R<Long> deleteByMsgId(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        long deletedCount = umpMsgQueueService.deleteByMsgId(msgId);
        String ret = "成功删除" + String.valueOf(deletedCount) + "条任务";
        return R.ok(deletedCount, ret);
    }
}