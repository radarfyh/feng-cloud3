package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 回调记录表服务接口
 */
public interface UmpMsgCallbackService extends IService<UmpMsgCallback> {

    /**
     * 创建回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param callbackUrl 回调地址
     * @param callbackMethod 回调方法
     * @param callbackData 回调数据
     * @param signature 回调签名（可选）
     * @param callbackId 回调ID（可选）
     * @return 回调记录ID
     */
    String createCallback(String msgId, String receiverId, String callbackUrl,
                         String callbackMethod, Map<String, Object> callbackData,
                         String signature, String callbackId);

    /**
     * 批量创建回调记录
     *
     * @param callbacks 回调记录列表
     * @return 成功创建数量
     */
    int batchCreateCallbacks(List<Map<String, Object>> callbacks);

    /**
     * 根据消息ID和接收者ID查询回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @return 回调记录列表
     */
    List<CallbackDetailVO> getCallbacksByMsgAndReceiver(String msgId, String receiverId);

    /**
     * 分页查询回调记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<CallbackPageVO> queryCallbackPage(CallbackQueryDTO queryDTO);

    /**
     * 获取回调记录详情
     *
     * @param callbackId 回调记录ID
     * @return 回调记录详情VO
     */
    CallbackDetailVO getCallbackDetail(String callbackId);

    /**
     * 更新回调状态
     *
     * @param callbackId 回调记录ID
     * @param status 状态
     * @param httpStatus HTTP状态码（可选）
     * @param responseBody 响应内容（可选）
     * @param errorMessage 错误信息（可选）
     * @return 是否成功
     */
    boolean updateCallbackStatus(String callbackId, String status, Integer httpStatus,
                               String responseBody, String errorMessage);

    /**
     * 标记回调为处理中
     *
     * @param callbackId 回调记录ID
     * @return 是否成功
     */
    boolean markAsProcessing(String callbackId);

    /**
     * 标记回调为成功
     *
     * @param callbackId 回调记录ID
     * @param httpStatus HTTP状态码
     * @param responseBody 响应内容
     * @return 是否成功
     */
    boolean markAsSuccess(String callbackId, Integer httpStatus, String responseBody);

    /**
     * 标记回调为失败
     *
     * @param callbackId 回调记录ID
     * @param httpStatus HTTP状态码（可选）
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    boolean markAsFailed(String callbackId, Integer httpStatus, String errorMessage);

    /**
     * 重试失败回调
     *
     * @param callbackId 回调记录ID
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 是否成功
     */
    boolean retryFailedCallback(String callbackId, int retryDelayMinutes);

    /**
     * 批量重试失败回调
     *
     * @param callbackIds 回调记录ID列表
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 成功重试数量
     */
    int batchRetryFailedCallbacks(List<String> callbackIds, int retryDelayMinutes);

    /**
     * 处理待发送的回调
     *
     * @param limit 每次处理数量
     * @return 处理的回调数量
     */
    int processPendingCallbacks(int limit);

    /**
     * 处理待重试的回调
     *
     * @param maxRetryCount 最大重试次数
     * @param limit 每次处理数量
     * @return 处理的重试回调数量
     */
    int processRetryCallbacks(Integer maxRetryCount, int limit);

    /**
     * 获取回调统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param msgId 消息ID（可选）
     * @return 统计信息VO
     */
    CallbackStatisticsVO getCallbackStatistics(LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             String msgId);

    /**
     * 删除回调记录
     *
     * @param callbackId 回调记录ID
     * @return 是否成功
     */
    boolean deleteCallback(String callbackId);

    /**
     * 根据消息ID删除回调记录
     *
     * @param msgId 消息ID
     * @return 删除的记录数量
     */
    long deleteByMsgId(String msgId);
}