package ltd.huntinginfo.feng.center.api.entity;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息中心接口调用统计信息
 */
@Data
public class CenterApiStats {
    
    /**
     * 应用标识
     */
    private String appKey;
    
    /**
     * 统计开始时间
     */
    private Date startTime;
    
    /**
     * 统计结束时间
     */
    private Date endTime;
    
    /**
     * 总请求次数
     */
    private long totalRequests = 0;
    
    /**
     * 成功请求次数
     */
    private long successRequests = 0;
    
    /**
     * 失败请求次数
     */
    private long failedRequests = 0;
    
    /**
     * 总响应时间（毫秒）
     */
    private long totalDuration = 0;
    
    /**
     * 最小响应时间（毫秒）
     */
    private long minDuration = Long.MAX_VALUE;
    
    /**
     * 最大响应时间（毫秒）
     */
    private long maxDuration = 0;
    
    /**
     * 按API统计的详细信息
     */
    private Map<String, ApiDetailStats> apiDetails = new HashMap<>();
    
    /**
     * 按状态码统计
     */
    private Map<String, StatusCodeStats> statusCodeStats = new HashMap<>();
    
    /**
     * 按小时统计的请求量
     */
    private Map<Integer, Integer> hourlyRequests = new HashMap<>();
    
    public CenterApiStats() {
        this.startTime = new Date();
    }
    
    public CenterApiStats(String appKey) {
        this();
        this.appKey = appKey;
    }
    
    /**
     * 记录一次API调用（三个参数版本，兼容旧代码）
     */
    public void recordApiCall(String apiName, boolean success, long duration) {
        recordApiCall(apiName, success, duration, null);
    }
    
    /**
     * 记录一次API调用（四个参数版本）
     */
    public void recordApiCall(String apiName, boolean success, long duration, String statusCode) {
        // 更新总体统计
        totalRequests++;
        if (success) {
            successRequests++;
        } else {
            failedRequests++;
        }
        totalDuration += duration;
        minDuration = Math.min(minDuration, duration);
        maxDuration = Math.max(maxDuration, duration);
        
        // 更新API详情统计
        ApiDetailStats apiDetail = apiDetails.computeIfAbsent(apiName, k -> new ApiDetailStats(apiName));
        apiDetail.recordCall(success, duration, statusCode);
        
        // 更新状态码统计
        if (statusCode != null) {
            StatusCodeStats codeStats = statusCodeStats.computeIfAbsent(statusCode, k -> new StatusCodeStats(statusCode));
            codeStats.recordCall(success);
        }
        
        // 更新小时统计
        int hour = new Date().getHours();
        hourlyRequests.put(hour, hourlyRequests.getOrDefault(hour, 0) + 1);
        
        // 更新时间
        this.endTime = new Date();
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        return totalRequests == 0 ? 0 : (double) successRequests / totalRequests * 100;
    }
    
    /**
     * 获取平均响应时间
     */
    public double getAvgDuration() {
        return totalRequests == 0 ? 0 : (double) totalDuration / totalRequests;
    }
    
    /**
     * 获取失败率
     */
    public double getFailureRate() {
        return totalRequests == 0 ? 0 : (double) failedRequests / totalRequests * 100;
    }
    
    /**
     * 获取吞吐量（请求/分钟）
     */
    public double getThroughputPerMinute() {
        long durationMs = endTime.getTime() - startTime.getTime();
        if (durationMs == 0) return 0;
        
        double minutes = durationMs / 60000.0;
        return totalRequests / minutes;
    }
    
    /**
     * 获取接口调用趋势（最近24小时）
     */
    public Map<Integer, Integer> getHourlyRequestTrend() {
        Map<Integer, Integer> trend = new HashMap<>();
        
        // 初始化24小时
        for (int i = 0; i < 24; i++) {
            trend.put(i, hourlyRequests.getOrDefault(i, 0));
        }
        
        return trend;
    }
    
    /**
     * 获取最常用的API
     */
    public String getMostFrequentApi() {
        return apiDetails.entrySet().stream()
            .max((e1, e2) -> Long.compare(e1.getValue().getCallCount(), e2.getValue().getCallCount()))
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * 获取最慢的API
     */
    public String getSlowestApi() {
        return apiDetails.entrySet().stream()
            .max((e1, e2) -> Double.compare(e1.getValue().getAvgDuration(), e2.getValue().getAvgDuration()))
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * 获取统计摘要
     */
    public String getSummary() {
        return String.format(
            "应用: %s, 总请求: %d, 成功: %d (%.2f%%), 失败: %d (%.2f%%), 平均响应时间: %.2fms, 吞吐量: %.2f req/min",
            appKey, totalRequests, successRequests, getSuccessRate(), 
            failedRequests, getFailureRate(), getAvgDuration(), getThroughputPerMinute()
        );
    }
    
    /**
     * 重置统计信息
     */
    public void reset() {
        totalRequests = 0;
        successRequests = 0;
        failedRequests = 0;
        totalDuration = 0;
        minDuration = Long.MAX_VALUE;
        maxDuration = 0;
        apiDetails.clear();
        statusCodeStats.clear();
        hourlyRequests.clear();
        startTime = new Date();
        endTime = null;
    }
    
    /**
     * 合并另一个统计对象
     */
    public void merge(CenterApiStats other) {
        if (other == null) return;
        
        this.totalRequests += other.totalRequests;
        this.successRequests += other.successRequests;
        this.failedRequests += other.failedRequests;
        this.totalDuration += other.totalDuration;
        this.minDuration = Math.min(this.minDuration, other.minDuration);
        this.maxDuration = Math.max(this.maxDuration, other.maxDuration);
        
        // 合并API详情
        for (Map.Entry<String, ApiDetailStats> entry : other.apiDetails.entrySet()) {
            ApiDetailStats existing = this.apiDetails.get(entry.getKey());
            if (existing != null) {
                existing.merge(entry.getValue());
            } else {
                this.apiDetails.put(entry.getKey(), entry.getValue().copy());
            }
        }
        
        // 合并状态码统计
        for (Map.Entry<String, StatusCodeStats> entry : other.statusCodeStats.entrySet()) {
            StatusCodeStats existing = this.statusCodeStats.get(entry.getKey());
            if (existing != null) {
                existing.merge(entry.getValue());
            } else {
                this.statusCodeStats.put(entry.getKey(), entry.getValue().copy());
            }
        }
        
        // 合并小时统计
        for (Map.Entry<Integer, Integer> entry : other.hourlyRequests.entrySet()) {
            this.hourlyRequests.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        
        // 更新时间范围
        if (other.startTime.before(this.startTime)) {
            this.startTime = other.startTime;
        }
        if (other.endTime != null) {
            if (this.endTime == null || other.endTime.after(this.endTime)) {
                this.endTime = other.endTime;
            }
        }
    }
    
    /**
     * API详细统计信息
     */
    @Data
    public static class ApiDetailStats {
        private String apiName;
        private long callCount = 0;
        private long successCount = 0;
        private long failedCount = 0;
        private long totalDuration = 0;
        private long minDuration = Long.MAX_VALUE;
        private long maxDuration = 0;
        private Date lastCallTime;
        private Date firstCallTime;
        
        public ApiDetailStats() {}
        
        public ApiDetailStats(String apiName) {
            this.apiName = apiName;
        }
        
        /**
         * 记录调用（三个参数版本）
         */
        public void recordCall(boolean success, long duration) {
            recordCall(success, duration, null);
        }
        
        /**
         * 记录调用（四个参数版本）
         */
        public void recordCall(boolean success, long duration, String statusCode) {
            callCount++;
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
            
            totalDuration += duration;
            minDuration = Math.min(minDuration, duration);
            maxDuration = Math.max(maxDuration, duration);
            
            Date now = new Date();
            lastCallTime = now;
            if (firstCallTime == null) {
                firstCallTime = now;
            }
        }
        
        public double getSuccessRate() {
            return callCount == 0 ? 0 : (double) successCount / callCount * 100;
        }
        
        public double getAvgDuration() {
            return callCount == 0 ? 0 : (double) totalDuration / callCount;
        }
        
        public double getFailureRate() {
            return callCount == 0 ? 0 : (double) failedCount / callCount * 100;
        }
        
        public ApiDetailStats copy() {
            ApiDetailStats copy = new ApiDetailStats();
            copy.apiName = this.apiName;
            copy.callCount = this.callCount;
            copy.successCount = this.successCount;
            copy.failedCount = this.failedCount;
            copy.totalDuration = this.totalDuration;
            copy.minDuration = this.minDuration;
            copy.maxDuration = this.maxDuration;
            copy.lastCallTime = this.lastCallTime;
            copy.firstCallTime = this.firstCallTime;
            return copy;
        }
        
        public void merge(ApiDetailStats other) {
            if (other == null || !this.apiName.equals(other.apiName)) {
                return;
            }
            
            this.callCount += other.callCount;
            this.successCount += other.successCount;
            this.failedCount += other.failedCount;
            this.totalDuration += other.totalDuration;
            this.minDuration = Math.min(this.minDuration, other.minDuration);
            this.maxDuration = Math.max(this.maxDuration, other.maxDuration);
            
            if (other.lastCallTime != null) {
                if (this.lastCallTime == null || other.lastCallTime.after(this.lastCallTime)) {
                    this.lastCallTime = other.lastCallTime;
                }
            }
            
            if (other.firstCallTime != null) {
                if (this.firstCallTime == null || other.firstCallTime.before(this.firstCallTime)) {
                    this.firstCallTime = other.firstCallTime;
                }
            }
        }
        
        @Override
        public String toString() {
            return String.format(
                "%s: 调用次数=%d, 成功率=%.2f%%, 平均响应时间=%.2fms",
                apiName, callCount, getSuccessRate(), getAvgDuration()
            );
        }
    }
    
    /**
     * 状态码统计信息
     */
    @Data
    public static class StatusCodeStats {
        private String statusCode;
        private long callCount = 0;
        private long successCount = 0;
        private long failedCount = 0;
        
        public StatusCodeStats() {}
        
        public StatusCodeStats(String statusCode) {
            this.statusCode = statusCode;
        }
        
        public void recordCall(boolean success) {
            callCount++;
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }
        
        public double getSuccessRate() {
            return callCount == 0 ? 0 : (double) successCount / callCount * 100;
        }
        
        public StatusCodeStats copy() {
            StatusCodeStats copy = new StatusCodeStats();
            copy.statusCode = this.statusCode;
            copy.callCount = this.callCount;
            copy.successCount = this.successCount;
            copy.failedCount = this.failedCount;
            return copy;
        }
        
        public void merge(StatusCodeStats other) {
            if (other == null || !this.statusCode.equals(other.statusCode)) {
                return;
            }
            
            this.callCount += other.callCount;
            this.successCount += other.successCount;
            this.failedCount += other.failedCount;
        }
        
        @Override
        public String toString() {
            return String.format(
                "状态码 %s: 调用次数=%d, 成功率=%.2f%%",
                statusCode, callCount, getSuccessRate()
            );
        }
    }
}
