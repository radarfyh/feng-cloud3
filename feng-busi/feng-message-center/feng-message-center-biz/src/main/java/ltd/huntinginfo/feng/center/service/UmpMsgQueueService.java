package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息队列表服务接口
 */
public interface UmpMsgQueueService extends IService<UmpMsgQueue> {

    /**
     * 创建队列任务
     *
     * @param queueType 队列类型
     * @param queueName 队列名称
     * @param msgId 消息ID
     * @param taskData 任务数据
     * @param priority 优先级
     * @param executeTime 执行时间
     * @param maxRetry 最大重试次数
     * @return 任务ID
     */
    String createQueueTask(String queueType, String queueName, String msgId,
                          Map<String, Object> taskData, Integer priority,
                          LocalDateTime executeTime, Integer maxRetry);

    /**
     * 批量创建队列任务
     *
     * @param tasks 任务列表
     * @return 成功创建数量
     */
    int batchCreateQueueTasks(List<Map<String, Object>> tasks);

    /**
     * 根据消息ID查询队列任务
     *
     * @param msgId 消息ID
     * @return 队列任务列表
     */
    List<MsgQueueDetailVO> getQueueTasksByMsgId(String msgId);

    /**
     * 分页查询队列任务
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<MsgQueuePageVO> queryQueuePage(MsgQueueQueryDTO queryDTO);

    /**
     * 获取待执行的任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit 限制数量
     * @return 待执行任务列表
     */
    List<MsgQueueDetailVO> getPendingTasks(String queueType, String queueName, int limit);

    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情VO
     */
    MsgQueueDetailVO getQueueTaskDetail(String taskId);

    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 状态
     * @param workerId 工作者ID
     * @param resultCode 结果代码（可选）
     * @param resultMessage 结果消息（可选）
     * @param errorStack 错误堆栈（可选）
     * @return 是否成功
     */
    boolean updateTaskStatus(String taskId, String status, String workerId,
                            String resultCode, String resultMessage, String errorStack);

    /**
     * 标记任务为处理中
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @return 是否成功
     */
    boolean markAsProcessing(String taskId, String workerId);

    /**
     * 标记任务为成功
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param resultMessage 结果消息（可选）
     * @return 是否成功
     */
    boolean markAsSuccess(String taskId, String workerId, String resultMessage);

    /**
     * 标记任务为失败
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param errorMessage 错误消息
     * @param errorStack 错误堆栈（可选）
     * @return 是否成功
     */
    boolean markAsFailed(String taskId, String workerId, String errorMessage, String errorStack);

    /**
     * 重试失败任务
     *
     * @param taskId 任务ID
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 是否成功
     */
    boolean retryFailedTask(String taskId, int retryDelayMinutes);

    /**
     * 批量重试失败任务
     *
     * @param taskIds 任务ID列表
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 成功重试数量
     */
    int batchRetryFailedTasks(List<String> taskIds, int retryDelayMinutes);

    /**
     * 处理待执行任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param workerId 工作者ID
     * @param limit 每次处理数量
     * @return 处理的任务数量
     */
    int processPendingTasks(String queueType, String queueName, String workerId, int limit);

    /**
     * 获取队列统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param queueType 队列类型（可选）
     * @return 统计信息VO
     */
    MsgQueueStatisticsVO getQueueStatistics(LocalDateTime startTime,
                                           LocalDateTime endTime,
                                           String queueType);

    /**
     * 处理超时任务
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @param limit 每次处理数量
     * @return 处理的超时任务数量
     */
    int processTimeoutTasks(int timeoutMinutes, int limit);

    /**
     * 删除队列任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteQueueTask(String taskId);

    /**
     * 根据消息ID删除队列任务
     *
     * @param msgId 消息ID
     * @return 删除的任务数量
     */
    long deleteByMsgId(String msgId);
}