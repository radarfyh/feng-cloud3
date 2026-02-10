package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.SubscriptionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionPageVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpTopicSubscriptionService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主题订阅表控制器
 * 提供主题订阅的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/subscription")
@RequiredArgsConstructor
@Tag(name = "主题订阅管理", description = "消息主题订阅的增删改查和状态管理")
public class UmpTopicSubscriptionController {

    private final UmpTopicSubscriptionService umpTopicSubscriptionService;

    @Operation(summary = "创建订阅", description = "创建新的主题订阅")
    @PostMapping("/create")
    public R<String> createSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        try {
            String subscriptionId = umpTopicSubscriptionService.createSubscription(
                    topicCode, appKey, subscriptionConfig, callbackUrl, pushMode);
            return R.ok(subscriptionId, "订阅创建成功");
        } catch (Exception e) {
            log.error("创建订阅失败", e);
            return R.failed("订阅创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新订阅", description = "更新主题订阅信息")
    @PutMapping("/{subscriptionId}")
    public R<Boolean> updateSubscription(
            @Parameter(description = "订阅ID", required = true) 
            @PathVariable String subscriptionId,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        try {
            boolean success = umpTopicSubscriptionService.updateSubscription(
                    subscriptionId, subscriptionConfig, callbackUrl, pushMode);
            return success ? R.ok(true) : R.failed("订阅更新失败");
        } catch (Exception e) {
            log.error("更新订阅失败", e);
            return R.failed("订阅更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "订阅主题", description = "订阅消息主题")
    @PostMapping("/subscribe")
    public R<Boolean> subscribeTopic(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        try {
            boolean success = umpTopicSubscriptionService.subscribeTopic(
                    topicCode, appKey, subscriptionConfig, callbackUrl, pushMode);
            return success ? R.ok(true) : R.failed("订阅失败");
        } catch (Exception e) {
            log.error("订阅主题失败", e);
            return R.failed("订阅失败: " + e.getMessage());
        }
    }

    @Operation(summary = "取消订阅", description = "取消主题订阅")
    @PostMapping("/unsubscribe")
    public R<Boolean> unsubscribeTopic(
            @RequestParam String topicCode,
            @RequestParam String appKey) {
        try {
            boolean success = umpTopicSubscriptionService.unsubscribeTopic(topicCode, appKey);
            return success ? R.ok(true) : R.failed("取消订阅失败");
        } catch (Exception e) {
            log.error("取消订阅失败", e);
            return R.failed("取消订阅失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询订阅", description = "根据主题代码和应用标识查询订阅详情")
    @GetMapping("/detail")
    public R<SubscriptionDetailVO> getSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey) {
        SubscriptionDetailVO subscription = umpTopicSubscriptionService.getSubscription(topicCode, appKey);
        if (subscription == null) {
            return R.failed("订阅不存在");
        }
        return R.ok(subscription);
    }

    @Operation(summary = "分页查询订阅", description = "根据条件分页查询订阅列表")
    @PostMapping("/page")
    public R<Page<SubscriptionPageVO>> querySubscriptionPage(@Valid @RequestBody SubscriptionQueryDTO queryDTO) {
        Page<SubscriptionPageVO> page = umpTopicSubscriptionService.querySubscriptionPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询主题订阅列表", description = "根据主题代码查询订阅列表")
    @GetMapping("/topic/{topicCode}")
    public R<List<SubscriptionDetailVO>> getSubscriptionsByTopic(
            @Parameter(description = "主题代码", required = true) 
            @PathVariable String topicCode,
            @RequestParam(required = false) Integer status) {
        List<SubscriptionDetailVO> subscriptions = umpTopicSubscriptionService.getSubscriptionsByTopic(topicCode, status);
        return R.ok(subscriptions);
    }

    @Operation(summary = "查询应用订阅列表", description = "根据应用标识查询订阅列表")
    @GetMapping("/app/{appKey}")
    public R<List<SubscriptionDetailVO>> getSubscriptionsByApp(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey,
            @RequestParam(required = false) Integer status) {
        List<SubscriptionDetailVO> subscriptions = umpTopicSubscriptionService.getSubscriptionsByApp(appKey, status);
        return R.ok(subscriptions);
    }

    @Operation(summary = "激活订阅", description = "激活主题订阅")
    @PutMapping("/activate/{subscriptionId}")
    public R<Boolean> activateSubscription(
            @Parameter(description = "订阅ID", required = true) 
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.activateSubscription(subscriptionId);
        return success ? R.ok(true) : R.failed("激活失败");
    }

    @Operation(summary = "停用订阅", description = "停用主题订阅")
    @PutMapping("/deactivate/{subscriptionId}")
    public R<Boolean> deactivateSubscription(
            @Parameter(description = "订阅ID", required = true) 
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.deactivateSubscription(subscriptionId);
        return success ? R.ok(true) : R.failed("停用失败");
    }

    @Operation(summary = "批量激活订阅", description = "批量激活主题订阅")
    @PutMapping("/activate/batch")
    public R<Integer> batchActivateSubscriptions(@RequestBody List<String> subscriptionIds) {
        int updatedCount = umpTopicSubscriptionService.batchActivateSubscriptions(subscriptionIds);
        String ret = "成功激活" + String.valueOf(updatedCount) + "个订阅";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量停用订阅", description = "批量停用主题订阅")
    @PutMapping("/deactivate/batch")
    public R<Integer> batchDeactivateSubscriptions(@RequestBody List<String> subscriptionIds) {
        int updatedCount = umpTopicSubscriptionService.batchDeactivateSubscriptions(subscriptionIds);
        String ret = "成功停用" + String.valueOf(updatedCount) + "个订阅";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "更新订阅统计", description = "更新订阅的消息统计信息")
    @PutMapping("/stats")
    public R<Integer> updateSubscriptionStats(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestParam int increment,
            @RequestParam(required = false) LocalDateTime lastMessageTime) {
        Integer newCount = umpTopicSubscriptionService.updateSubscriptionStats(
                topicCode, appKey, increment, lastMessageTime);
        if (newCount == null) {
            return R.failed("订阅不存在");
        }
        return R.ok(newCount, "统计信息更新成功");
    }

    @Operation(summary = "检查订阅是否存在", description = "检查主题订阅是否存在")
    @GetMapping("/exists")
    public R<Boolean> existsSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        boolean exists = umpTopicSubscriptionService.existsSubscription(topicCode, appKey, activeOnly);
        return R.ok(exists);
    }

    @Operation(summary = "获取订阅统计", description = "获取主题订阅的统计信息")
    @GetMapping("/statistics")
    public R<SubscriptionStatisticsVO> getSubscriptionStatistics(
            @RequestParam(required = false) String topicCode,
            @RequestParam(required = false) String appKey) {
        SubscriptionStatisticsVO statistics = umpTopicSubscriptionService.getSubscriptionStatistics(topicCode, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "获取活跃订阅数量", description = "获取主题的活跃订阅数量")
    @GetMapping("/count/active/{topicCode}")
    public R<Integer> countActiveSubscriptions(
            @Parameter(description = "主题代码", required = true) 
            @PathVariable String topicCode) {
        Integer count = umpTopicSubscriptionService.countActiveSubscriptions(topicCode);
        return R.ok(count);
    }

    @Operation(summary = "删除订阅", description = "删除主题订阅")
    @DeleteMapping("/{subscriptionId}")
    public R<Boolean> deleteSubscription(
            @Parameter(description = "订阅ID", required = true) 
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.deleteSubscription(subscriptionId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "批量删除订阅", description = "批量删除主题订阅")
    @DeleteMapping("/batch")
    public R<Integer> batchDeleteSubscriptions(@RequestBody List<String> subscriptionIds) {
        int deletedCount = umpTopicSubscriptionService.batchDeleteSubscriptions(subscriptionIds);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个订阅";
        return R.ok(deletedCount, ret);
    }
}