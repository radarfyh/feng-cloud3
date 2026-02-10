package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息队列表Mapper接口
 */
@Mapper
public interface UmpMsgQueueMapper extends BaseMapper<UmpMsgQueue> {

    /**
     * 根据消息ID查询队列任务
     *
     * @param msgId 消息ID
     * @return 队列任务列表
     */
    List<UmpMsgQueue> selectByMsgId(@Param("msgId") String msgId);

    /**
     * 分页查询队列任务
     *
     * @param page 分页参数
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param status 状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<UmpMsgQueue> selectQueuePage(IPage<UmpMsgQueue> page,
                                      @Param("queueType") String queueType,
                                      @Param("queueName") String queueName,
                                      @Param("status") String status,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询待执行的任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit 限制数量
     * @return 待执行任务列表
     */
    List<UmpMsgQueue> selectPendingTasks(@Param("queueType") String queueType,
                                        @Param("queueName") String queueName,
                                        @Param("limit") int limit);

    /**
     * 更新任务状态
     *
     * @param id 任务ID
     * @param status 状态
     * @param workerId 工作者ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param resultCode 结果代码（可选）
     * @param resultMessage 结果消息（可选）
     * @param errorStack 错误堆栈（可选）
     * @return 更新条数
     */
    int updateTaskStatus(@Param("id") String id,
                        @Param("status") String status,
                        @Param("workerId") String workerId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("resultCode") String resultCode,
                        @Param("resultMessage") String resultMessage,
                        @Param("errorStack") String errorStack);

    /**
     * 更新任务重试次数
     *
     * @param id 任务ID
     * @param currentRetry 当前重试次数
     * @param executeTime 下次执行时间
     * @return 更新条数
     */
    int updateRetryCount(@Param("id") String id,
                        @Param("currentRetry") Integer currentRetry,
                        @Param("executeTime") LocalDateTime executeTime);

    /**
     * 批量更新任务状态
     *
     * @param ids 任务ID列表
     * @param status 目标状态
     * @param updateTime 更新时间
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") String status,
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 获取队列统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param queueType 队列类型（可选）
     * @return 统计结果
     */
    Map<String, Object> selectQueueStatistics(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("queueType") String queueType);

    /**
     * 查询超时任务
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @param limit 限制数量
     * @return 超时任务列表
     */
    List<UmpMsgQueue> selectTimeoutTasks(@Param("timeoutMinutes") int timeoutMinutes,
                                        @Param("limit") int limit);

    /**
     * 查询失败任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit 限制数量
     * @return 失败任务列表
     */
    List<UmpMsgQueue> selectFailedTasks(@Param("queueType") String queueType,
                                       @Param("queueName") String queueName,
                                       @Param("limit") int limit);
}