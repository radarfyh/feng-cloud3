package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统日志表Mapper接口
 */
@Mapper
public interface UmpSystemLogMapper extends BaseMapper<UmpSystemLog> {

    /**
     * 根据请求ID查询日志
     *
     * @param requestId 请求ID
     * @return 日志实体
     */
    UmpSystemLog selectByRequestId(@Param("requestId") String requestId);

    /**
     * 分页查询日志列表
     *
     * @param page 分页参数
     * @param logType 日志类型（可选）
     * @param logLevel 日志级别（可选）
     * @param appKey 应用标识（可选）
     * @param operator 操作者（可选）
     * @param operation 操作名称（可选）
     * @param apiPath API路径（可选）
     * @param responseCode 响应代码（可选）
     * @param authStatus 认证状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param keyword 关键词（可选）
     * @return 分页结果
     */
    IPage<UmpSystemLog> selectLogPage(IPage<UmpSystemLog> page,
                                     @Param("logType") String logType,
                                     @Param("logLevel") String logLevel,
                                     @Param("appKey") String appKey,
                                     @Param("operator") String operator,
                                     @Param("operation") String operation,
                                     @Param("apiPath") String apiPath,
                                     @Param("responseCode") String responseCode,
                                     @Param("authStatus") Integer authStatus,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("keyword") String keyword);

    /**
     * 根据日志类型查询日志列表
     *
     * @param logType 日志类型
     * @param limit 限制数量（可选）
     * @return 日志列表
     */
    List<UmpSystemLog> selectByLogType(@Param("logType") String logType,
                                      @Param("limit") Integer limit);

    /**
     * 根据日志级别查询日志列表
     *
     * @param logLevel 日志级别
     * @param limit 限制数量（可选）
     * @return 日志列表
     */
    List<UmpSystemLog> selectByLogLevel(@Param("logLevel") String logLevel,
                                       @Param("limit") Integer limit);

    /**
     * 根据应用标识查询日志列表
     *
     * @param appKey 应用标识
     * @param limit 限制数量（可选）
     * @return 日志列表
     */
    List<UmpSystemLog> selectByAppKey(@Param("appKey") String appKey,
                                     @Param("limit") Integer limit);

    /**
     * 根据操作者查询日志列表
     *
     * @param operator 操作者
     * @param limit 限制数量（可选）
     * @return 日志列表
     */
    List<UmpSystemLog> selectByOperator(@Param("operator") String operator,
                                       @Param("limit") Integer limit);

    /**
     * 获取日志统计信息
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param logType 日志类型（可选）
     * @param appKey 应用标识（可选）
     * @return 统计结果
     */
    Map<String, Object> selectLogStatistics(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("logType") String logType,
                                           @Param("appKey") String appKey);

    /**
     * 获取错误日志统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 错误统计结果
     */
    List<Map<String, Object>> selectErrorLogStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 获取性能统计信息
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 性能统计结果
     */
    Map<String, Object> selectPerformanceStatistics(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取API调用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @return API调用统计
     */
    List<Map<String, Object>> selectApiCallStatistics(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);

    /**
     * 获取操作者统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @return 操作者统计
     */
    List<Map<String, Object>> selectOperatorStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("limit") Integer limit);

    /**
     * 获取应用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @return 应用统计
     */
    List<Map<String, Object>> selectAppStatistics(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("limit") Integer limit);

    /**
     * 清理过期日志
     *
     * @param beforeTime 清理此时间之前的日志
     * @return 清理数量
     */
    int cleanExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量删除日志
     *
     * @param ids 日志ID列表
     * @return 删除数量
     */
    int batchDeleteLogs(@Param("ids") List<String> ids);

    /**
     * 获取日志趋势统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔（DAY-按天 HOUR-按小时）
     * @param logType 日志类型（可选）
     * @param logLevel 日志级别（可选）
     * @return 趋势统计
     */
    List<Map<String, Object>> selectLogTrendStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("interval") String interval,
                                                      @Param("logType") String logType,
                                                      @Param("logLevel") String logLevel);

    /**
     * 获取错误日志详情
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @return 错误日志列表
     */
    List<UmpSystemLog> selectErrorLogs(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("limit") Integer limit);
}