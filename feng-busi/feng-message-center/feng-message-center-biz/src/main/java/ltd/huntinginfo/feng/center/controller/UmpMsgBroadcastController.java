package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
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
 * 广播信息筒表控制器
 * 提供广播记录的创建、查询、状态管理等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/broadcast")
@RequiredArgsConstructor
@Tag(name = "广播信息筒管理", description = "广播记录的增删改查、状态管理和统计查询")
public class UmpMsgBroadcastController {

    private final UmpMsgBroadcastService umpMsgBroadcastService;

    @Operation(summary = "创建广播记录", description = "创建一条新的广播记录")
    @PostMapping("/create")
    public R<String> createBroadcast(
            @RequestParam String msgId,
            @RequestParam String broadcastType,
            @RequestBody Map<String, Object> targetScope,
            @RequestParam(required = false) String targetDescription) {
        try {
            String broadcastId = umpMsgBroadcastService.createBroadcast(
                    msgId, broadcastType, targetScope, targetDescription);
            String ret = "广播记录创建成功";
            return R.ok(broadcastId, ret);
        } catch (Exception e) {
            log.error("创建广播记录失败", e);
            return R.failed("广播记录创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据消息ID查询广播记录", description = "根据消息ID查询广播记录详情")
    @GetMapping("/message/{msgId}")
    public R<BroadcastDetailVO> getBroadcastByMsgId(
            @Parameter(description = "消息ID", required = true) 
            @PathVariable String msgId) {
        BroadcastDetailVO broadcast = umpMsgBroadcastService.getBroadcastByMsgId(msgId);
        if (broadcast == null) {
            return R.failed("广播记录不存在");
        }
        return R.ok(broadcast);
    }

    @Operation(summary = "分页查询广播记录", description = "根据条件分页查询广播记录列表")
    @PostMapping("/page")
    public R<Page<BroadcastPageVO>> queryBroadcastPage(@Valid @RequestBody BroadcastQueryDTO queryDTO) {
        Page<BroadcastPageVO> page = umpMsgBroadcastService.queryBroadcastPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "更新广播统计信息", description = "更新广播的统计信息")
    @PutMapping("/statistics/{broadcastId}")
    public R<Boolean> updateBroadcastStatistics(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId,
            @RequestParam(required = false) Integer distributedCount,
            @RequestParam(required = false) Integer receivedCount,
            @RequestParam(required = false) Integer readCount) {
        boolean success = umpMsgBroadcastService.updateBroadcastStatistics(
                broadcastId, distributedCount, receivedCount, readCount);
        return success ? R.ok(true) : R.failed("更新失败");
    }

    @Operation(summary = "更新广播状态", description = "更新单条广播记录的状态")
    @PutMapping("/status/{broadcastId}")
    public R<Boolean> updateBroadcastStatus(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId,
            @Parameter(description = "目标状态", required = true) 
            @RequestParam String status) {
        boolean success = umpMsgBroadcastService.updateBroadcastStatus(broadcastId, status);
        return success ? R.ok(true) : R.failed("状态更新失败");
    }

    @Operation(summary = "批量更新广播状态", description = "批量更新多条广播记录的状态")
    @PutMapping("/status/batch")
    public R<Integer> batchUpdateBroadcastStatus(
            @RequestBody List<String> broadcastIds,
            @RequestParam String status) {
        int updatedCount = umpMsgBroadcastService.batchUpdateBroadcastStatus(broadcastIds, status);
        String ret = "成功更新" + String.valueOf(updatedCount) + "条广播记录";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "标记广播为分发中", description = "将广播记录状态标记为分发中")
    @PutMapping("/distributing/{broadcastId}")
    public R<Boolean> markAsDistributing(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId) {
        boolean success = umpMsgBroadcastService.markAsDistributing(broadcastId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "标记广播为完成", description = "将广播记录状态标记为完成")
    @PutMapping("/completed/{broadcastId}")
    public R<Boolean> markAsCompleted(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId) {
        boolean success = umpMsgBroadcastService.markAsCompleted(broadcastId);
        return success ? R.ok(true) : R.failed("标记失败");
    }

    @Operation(summary = "处理待分发的广播", description = "处理待分发的广播记录，通常由定时任务调用")
    @PostMapping("/process/distribute")
    public R<Integer> processPendingDistribute(
            @Parameter(description = "每次处理数量", example = "100") 
            @RequestParam(defaultValue = "100") int limit) {
        int processedCount = umpMsgBroadcastService.processPendingDistribute(limit);
        String ret = "已处理" + String.valueOf(processedCount) + "条广播记录";
        return R.ok(processedCount, ret);
    }

    @Operation(summary = "获取广播统计", description = "获取指定时间段内的广播统计信息")
    @GetMapping("/statistics")
    public R<BroadcastStatisticsVO> getBroadcastStatistics(
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "广播类型") 
            @RequestParam(required = false) String broadcastType) {
        
        // 设置默认时间范围（最近30天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        BroadcastStatisticsVO statistics = umpMsgBroadcastService.getBroadcastStatistics(
                startTime, endTime, broadcastType);
        return R.ok(statistics);
    }

    @Operation(summary = "根据接收者查询广播", description = "查询指定接收者相关的广播记录")
    @GetMapping("/receiver")
    public R<List<BroadcastDetailVO>> getBroadcastsByReceiver(
            @Parameter(description = "接收者ID", required = true) 
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true) 
            @RequestParam String receiverType,
            @Parameter(description = "限制数量", example = "20") 
            @RequestParam(defaultValue = "20") int limit) {
        
        List<BroadcastDetailVO> broadcasts = umpMsgBroadcastService.getBroadcastsByReceiver(
                receiverId, receiverType, limit);
        return R.ok(broadcasts);
    }

    @Operation(summary = "删除广播记录", description = "删除广播记录")
    @DeleteMapping("/{broadcastId}")
    public R<Boolean> deleteBroadcast(
            @Parameter(description = "广播ID", required = true) 
            @PathVariable String broadcastId) {
        boolean success = umpMsgBroadcastService.removeById(broadcastId);
        return success ? R.ok(true) : R.failed("删除失败");
    }
}