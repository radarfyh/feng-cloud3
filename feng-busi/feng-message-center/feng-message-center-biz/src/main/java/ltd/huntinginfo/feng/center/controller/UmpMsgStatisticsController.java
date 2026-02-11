package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.dto.MsgStatisticsQueryDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgStatistics;
import ltd.huntinginfo.feng.center.api.vo.*;
import ltd.huntinginfo.feng.center.service.UmpMsgStatisticsService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 消息统计表控制器
 * 提供消息统计的查询、分析和报表接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/message/statistics")
@RequiredArgsConstructor
@Tag(name = "消息统计管理", description = "消息统计的查询、分析和报表")
public class UmpMsgStatisticsController {

    private final UmpMsgStatisticsService umpMsgStatisticsService;

    @Operation(summary = "更新统计记录", description = "更新或创建消息统计记录")
    @PostMapping("/upsert")
    public R<Boolean> upsertStatistics(
            @RequestParam LocalDate statDate,
            @RequestParam String appKey,
            @RequestParam String msgType,
            @RequestParam(required = false) Integer sendCount,
            @RequestParam(required = false) Integer sendSuccessCount,
            @RequestParam(required = false) Integer sendFailedCount,
            @RequestParam(required = false) Integer receiveCount,
            @RequestParam(required = false) Integer readCount,
            @RequestParam(required = false) Integer errorCount,
            @RequestParam(required = false) Integer retryCount,
            @RequestParam(required = false) Integer processTime,
            @RequestParam(required = false) Integer receiveTime,
            @RequestParam(required = false) Integer readTime) {
        try {
            boolean success = umpMsgStatisticsService.upsertStatistics(
                    statDate, appKey, msgType,
                    sendCount, sendSuccessCount, sendFailedCount,
                    receiveCount, readCount, errorCount, retryCount,
                    processTime, receiveTime, readTime);
            return success ? R.ok(true, "统计记录更新成功") : R.failed("统计记录更新失败");
        } catch (Exception e) {
            log.error("更新统计记录失败", e);
            return R.failed("统计记录更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据日期和应用查询统计", description = "根据统计日期和应用标识查询统计记录")
    @GetMapping("/date-app")
    public R<MsgStatisticsDetailVO> getByDateAndApp(
            @RequestParam LocalDate statDate,
            @RequestParam String appKey) {
        MsgStatisticsDetailVO statistics = umpMsgStatisticsService.getByDateAndApp(statDate, appKey);
        if (statistics == null) {
            return R.failed("统计记录不存在");
        }
        return R.ok(statistics);
    }

    @Operation(summary = "分页查询统计记录", description = "根据条件分页查询统计记录")
    @PostMapping("/page")
    public R<Page<MsgStatisticsPageVO>> queryStatisticsPage(@Valid @RequestBody MsgStatisticsQueryDTO queryDTO) {
        Page<MsgStatisticsPageVO> page = umpMsgStatisticsService.queryStatisticsPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据日期范围查询统计记录", description = "根据日期范围查询统计记录")
    @GetMapping("/date-range")
    public R<List<MsgStatisticsDetailVO>> getByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByDateRange(
                startDate, endDate, appKey, msgType);
        return R.ok(statistics);
    }

    @Operation(summary = "根据应用标识查询统计记录", description = "根据应用标识查询统计记录")
    @GetMapping("/app/{appKey}")
    public R<List<MsgStatisticsDetailVO>> getByAppKey(
            @Parameter(description = "应用标识", required = true) 
            @PathVariable String appKey,
            @RequestParam(required = false) Integer limit) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByAppKey(appKey, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "根据消息类型查询统计记录", description = "根据消息类型查询统计记录")
    @GetMapping("/msg-type/{msgType}")
    public R<List<MsgStatisticsDetailVO>> getByMsgType(
            @Parameter(description = "消息类型", required = true) 
            @PathVariable String msgType,
            @RequestParam(required = false) Integer limit) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByMsgType(msgType, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "获取统计汇总信息", description = "获取消息统计的汇总信息")
    @GetMapping("/summary")
    public R<MsgStatisticsSummaryVO> getStatisticsSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType) {
        MsgStatisticsSummaryVO summary = umpMsgStatisticsService.getStatisticsSummary(
                startDate, endDate, appKey, msgType);
        return R.ok(summary);
    }

    @Operation(summary = "获取应用统计排名", description = "获取应用统计排名")
    @GetMapping("/ranking/app")
    public R<List<AppStatisticsRankingVO>> getAppRanking(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<AppStatisticsRankingVO> ranking = umpMsgStatisticsService.getAppRanking(startDate, endDate, limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取消息类型统计排名", description = "获取消息类型统计排名")
    @GetMapping("/ranking/msg-type")
    public R<List<MsgTypeStatisticsRankingVO>> getMsgTypeRanking(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<MsgTypeStatisticsRankingVO> ranking = umpMsgStatisticsService.getMsgTypeRanking(startDate, endDate, limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取统计趋势数据", description = "获取统计趋势数据")
    @GetMapping("/trend")
    public R<List<StatisticsTrendVO>> getStatisticsTrend(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType,
            @RequestParam(defaultValue = "DAY") String interval) {
        List<StatisticsTrendVO> trend = umpMsgStatisticsService.getStatisticsTrend(
                startDate, endDate, appKey, msgType, interval);
        return R.ok(trend);
    }

    @Operation(summary = "获取性能统计信息", description = "获取性能统计信息")
    @GetMapping("/performance")
    public R<PerformanceStatisticsVO> getPerformanceStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey) {
        PerformanceStatisticsVO statistics = umpMsgStatisticsService.getPerformanceStatistics(
                startDate, endDate, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "获取错误统计信息", description = "获取错误统计信息")
    @GetMapping("/error")
    public R<List<ErrorStatisticsVO>> getErrorStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey) {
        List<ErrorStatisticsVO> statistics = umpMsgStatisticsService.getErrorStatistics(startDate, endDate, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "生成每日统计报表", description = "生成每日统计报表")
    @PostMapping("/generate/daily")
    public R<Boolean> generateDailyReport(
            @RequestParam(required = false) LocalDate statDate) {
        LocalDate targetDate = statDate != null ? statDate : LocalDate.now().minusDays(1);
        boolean success = umpMsgStatisticsService.generateDailyReport(targetDate);
        return success ? R.ok(true, "每日统计报表生成成功") : R.failed("每日统计报表生成失败");
    }

    @Operation(summary = "批量创建统计记录", description = "批量创建统计记录")
    @PostMapping("/batch/create")
    public R<Boolean> batchCreateStatistics(@RequestBody List<UmpMsgStatistics> statisticsList) {
        boolean success = umpMsgStatisticsService.batchCreateStatistics(statisticsList);
        return success ? R.ok(true, "批量创建统计记录成功") : R.failed("批量创建统计记录失败");
    }

    @Operation(summary = "检查统计记录是否存在", description = "检查统计记录是否存在")
    @GetMapping("/exists")
    public R<Boolean> existsStatistics(
            @RequestParam LocalDate statDate,
            @RequestParam String appKey,
            @RequestParam String msgType) {
        boolean exists = umpMsgStatisticsService.existsStatistics(statDate, appKey, msgType);
        return R.ok(exists);
    }

    @Operation(summary = "清理过期统计记录", description = "清理指定天数前的过期统计记录")
    @DeleteMapping("/clean/expired")
    public R<Integer> cleanExpiredStatistics(
            @RequestParam(defaultValue = "365") Integer days) {
        int cleanedCount = umpMsgStatisticsService.cleanExpiredStatistics(days);
        String ret = "成功清理" + String.valueOf(cleanedCount) + "条过期统计记录";
        return R.ok(cleanedCount, ret);
    }

    @Operation(summary = "获取统计图表数据", description = "获取统计图表数据")
    @GetMapping("/chart")
    public R<Map<String, Object>> getChartData(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(defaultValue = "SEND") String chartType) {
        Map<String, Object> chartData = umpMsgStatisticsService.getChartData(
                startDate, endDate, appKey, chartType);
        return R.ok(chartData);
    }

    @Operation(summary = "导出统计报表", description = "导出统计报表数据")
    @GetMapping("/export")
    public void exportStatisticsReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType,
            HttpServletResponse response) {
        try {
            List<MsgStatisticsExportVO> exportData = umpMsgStatisticsService.exportStatisticsReport(
                    startDate, endDate, appKey, msgType);
            
            // 构建CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("统计日期,应用标识,消息类型,发送数量,发送成功数量,发送失败数量,接收数量,阅读数量,错误数量,重试数量,平均处理时间(ms),平均接收时间(ms),平均阅读时间(ms),创建时间\n");
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (MsgStatisticsExportVO data : exportData) {
                csvContent.append(data.getStatDate().format(dateFormatter)).append(",");
                csvContent.append(data.getAppKey() != null ? data.getAppKey() : "").append(",");
                csvContent.append(data.getMsgType() != null ? data.getMsgType() : "").append(",");
                csvContent.append(data.getSendCount()).append(",");
                csvContent.append(data.getSendSuccessCount()).append(",");
                csvContent.append(data.getSendFailedCount()).append(",");
                csvContent.append(data.getReceiveCount()).append(",");
                csvContent.append(data.getReadCount()).append(",");
                csvContent.append(data.getErrorCount()).append(",");
                csvContent.append(data.getRetryCount()).append(",");
                csvContent.append(data.getAvgProcessTime()).append(",");
                csvContent.append(data.getAvgReceiveTime()).append(",");
                csvContent.append(data.getAvgReadTime()).append(",");
                csvContent.append(data.getCreateTime() != null ? data.getCreateTime().format(dateTimeFormatter) : "").append("\n");
            }
            
            // 设置响应头
            String filename = "message_statistics_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            response.setContentType("text/csv");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            
            // 写入响应
            response.getWriter().write(csvContent.toString());
            response.getWriter().flush();
            
            log.info("统计报表导出成功，共导出{}条记录", exportData.size());
        } catch (IOException e) {
            log.error("导出统计报表失败", e);
            throw new RuntimeException("导出统计报表失败", e);
        }
    }

    @Operation(summary = "获取实时统计概览", description = "获取实时统计概览")
    @GetMapping("/overview/real-time")
    public R<RealTimeStatisticsOverviewVO> getRealTimeOverview() {
        RealTimeStatisticsOverviewVO overview = umpMsgStatisticsService.getRealTimeOverview();
        return R.ok(overview);
    }

    @Operation(summary = "获取统计看板数据", description = "获取统计看板数据")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> getDashboardData(
            @RequestParam(defaultValue = "30") Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        // 获取汇总数据
        MsgStatisticsSummaryVO summary = umpMsgStatisticsService.getStatisticsSummary(startDate, endDate, null, null);
        
        // 获取应用排名
        List<AppStatisticsRankingVO> appRanking = umpMsgStatisticsService.getAppRanking(startDate, endDate, 5);
        
        // 获取消息类型排名
        List<MsgTypeStatisticsRankingVO> msgTypeRanking = umpMsgStatisticsService.getMsgTypeRanking(startDate, endDate, 5);
        
        // 获取趋势数据
        List<StatisticsTrendVO> trendData = umpMsgStatisticsService.getStatisticsTrend(startDate, endDate, null, null, "DAY");
        
        // 获取实时概览
        RealTimeStatisticsOverviewVO realTimeOverview = umpMsgStatisticsService.getRealTimeOverview();
        
        // 构建看板数据
        Map<String, Object> dashboardData = Map.of(
            "summary", summary,
            "appRanking", appRanking,
            "msgTypeRanking", msgTypeRanking,
            "trendData", trendData,
            "realTimeOverview", realTimeOverview
        );
        
        return R.ok(dashboardData);
    }
}