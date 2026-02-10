package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.TopicQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TopicDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TopicPageVO;
import ltd.huntinginfo.feng.center.api.vo.TopicStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgTopicService;
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
 * 消息主题表控制器
 * 提供消息主题的创建、查询、更新等接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/topic")
@RequiredArgsConstructor
@Tag(name = "消息主题管理", description = "消息主题的增删改查和状态管理")
public class UmpMsgTopicController {

    private final UmpMsgTopicService umpMsgTopicService;

    @Operation(summary = "创建主题", description = "创建新的消息主题")
    @PostMapping("/create")
    public R<String> createTopic(
            @RequestParam String topicCode,
            @RequestParam String topicName,
            @RequestParam String topicType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String defaultMsgType,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestBody(required = false) Map<String, Object> routingRules,
            @RequestParam(required = false) Integer maxSubscribers) {
        try {
            String topicId = umpMsgTopicService.createTopic(
                    topicCode, topicName, topicType, description, defaultMsgType,
                    defaultPriority, routingRules, maxSubscribers);
            String ret = "主题创建成功";
            return R.ok(topicId, ret);
        } catch (Exception e) {
            log.error("创建主题失败", e);
            return R.failed("主题创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新主题", description = "更新消息主题信息")
    @PutMapping("/{topicId}")
    public R<Boolean> updateTopic(
            @Parameter(description = "主题ID", required = true) 
            @PathVariable String topicId,
            @RequestParam(required = false) String topicName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String defaultMsgType,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestBody(required = false) Map<String, Object> routingRules,
            @RequestParam(required = false) Integer maxSubscribers,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpMsgTopicService.updateTopic(
                    topicId, topicName, description, defaultMsgType, defaultPriority,
                    routingRules, maxSubscribers, status);
            return success ? R.ok(true) : R.failed("主题更新失败");
        } catch (Exception e) {
            log.error("更新主题失败", e);
            return R.failed("主题更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据主题代码查询主题", description = "根据主题代码查询主题详情")
    @GetMapping("/code/{topicCode}")
    public R<TopicDetailVO> getTopicByCode(
            @Parameter(description = "主题代码", required = true) 
            @PathVariable String topicCode) {
        TopicDetailVO topic = umpMsgTopicService.getTopicByCode(topicCode);
        if (topic == null) {
            return R.failed("主题不存在");
        }
        return R.ok(topic);
    }

    @Operation(summary = "分页查询主题", description = "根据条件分页查询主题列表")
    @PostMapping("/page")
    public R<Page<TopicPageVO>> queryTopicPage(@Valid @RequestBody TopicQueryDTO queryDTO) {
        Page<TopicPageVO> page = umpMsgTopicService.queryTopicPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询可用主题列表", description = "查询所有可用的消息主题")
    @GetMapping("/available")
    public R<List<TopicDetailVO>> getAvailableTopics() {
        List<TopicDetailVO> topics = umpMsgTopicService.getAvailableTopics();
        return R.ok(topics);
    }

    @Operation(summary = "启用主题", description = "启用消息主题")
    @PutMapping("/enable/{topicId}")
    public R<Boolean> enableTopic(
            @Parameter(description = "主题ID", required = true) 
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.enableTopic(topicId);
        return success ? R.ok(true) : R.failed("启用失败");
    }

    @Operation(summary = "禁用主题", description = "禁用消息主题")
    @PutMapping("/disable/{topicId}")
    public R<Boolean> disableTopic(
            @Parameter(description = "主题ID", required = true) 
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.disableTopic(topicId);
        return success ? R.ok(true) : R.failed("禁用失败");
    }

    @Operation(summary = "批量启用主题", description = "批量启用消息主题")
    @PutMapping("/enable/batch")
    public R<Integer> batchEnableTopics(@RequestBody List<String> topicIds) {
        int updatedCount = umpMsgTopicService.batchEnableTopics(topicIds);
        String ret = "成功启用" + String.valueOf(updatedCount) + "个主题";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "批量禁用主题", description = "批量禁用消息主题")
    @PutMapping("/disable/batch")
    public R<Integer> batchDisableTopics(@RequestBody List<String> topicIds) {
        int updatedCount = umpMsgTopicService.batchDisableTopics(topicIds);
        String ret = "成功禁用" + String.valueOf(updatedCount) + "个主题";
        return R.ok(updatedCount, ret);
    }

    @Operation(summary = "更新主题订阅者数量", description = "更新主题的订阅者数量")
    @PutMapping("/subscriber-count/{topicId}")
    public R<Integer> updateTopicSubscriberCount(
            @Parameter(description = "主题ID", required = true) 
            @PathVariable String topicId,
            @Parameter(description = "增量", required = true) 
            @RequestParam int increment) {
        Integer newCount = umpMsgTopicService.updateTopicSubscriberCount(topicId, increment);
        if (newCount == null) {
            return R.failed("主题不存在");
        }
        String ret = "订阅者数量更新成功";
        return R.ok(newCount, ret);
    }

    @Operation(summary = "检查主题可用性", description = "检查主题是否可用")
    @GetMapping("/available/{topicCode}")
    public R<Boolean> isTopicAvailable(
            @Parameter(description = "主题代码", required = true) 
            @PathVariable String topicCode) {
        boolean available = umpMsgTopicService.isTopicAvailable(topicCode);
        return R.ok(available);
    }

    @Operation(summary = "获取主题统计", description = "获取主题的统计信息")
    @GetMapping("/statistics")
    public R<TopicStatisticsVO> getTopicStatistics() {
        TopicStatisticsVO statistics = umpMsgTopicService.getTopicStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "删除主题", description = "删除消息主题")
    @DeleteMapping("/{topicId}")
    public R<Boolean> deleteTopic(
            @Parameter(description = "主题ID", required = true) 
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.deleteTopic(topicId);
        return success ? R.ok(true) : R.failed("删除失败");
    }

    @Operation(summary = "批量删除主题", description = "批量删除消息主题")
    @DeleteMapping("/batch")
    public R<Integer> batchDeleteTopics(@RequestBody List<String> topicIds) {
        int deletedCount = umpMsgTopicService.batchDeleteTopics(topicIds);
        String ret = "成功删除" + String.valueOf(deletedCount) + "个主题";
        return R.ok(deletedCount, ret);
    }
}