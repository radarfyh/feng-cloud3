package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 回调记录表Mapper接口
 */
@Mapper
public interface UmpMsgCallbackMapper extends BaseMapper<UmpMsgCallback> {

    /**
     * 根据消息ID和接收者ID查询回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @return 回调记录列表
     */
    List<UmpMsgCallback> selectByMsgAndReceiver(@Param("msgId") String msgId, 
                                               @Param("receiverId") String receiverId);

    /**
     * 分页查询回调记录
     *
     * @param page 分页参数
     * @param msgId 消息ID（可选）
     * @param receiverId 接收者ID（可选）
     * @param status 状态（可选）
     * @param callbackUrl 回调地址（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<UmpMsgCallback> selectCallbackPage(IPage<UmpMsgCallback> page,
                                            @Param("msgId") String msgId,
                                            @Param("receiverId") String receiverId,
                                            @Param("status") String status,
                                            @Param("callbackUrl") String callbackUrl,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询待重试的回调记录
     *
     * @param maxRetryCount 最大重试次数
     * @param limit 限制数量
     * @return 待重试的回调记录列表
     */
    List<UmpMsgCallback> selectPendingRetry(@Param("maxRetryCount") Integer maxRetryCount, 
                                           @Param("limit") int limit);

    /**
     * 查询待发送的回调记录
     *
     * @param limit 限制数量
     * @return 待发送的回调记录列表
     */
    List<UmpMsgCallback> selectPendingSend(@Param("limit") int limit);

    /**
     * 更新回调状态
     *
     * @param id 回调记录ID
     * @param status 状态
     * @param httpStatus HTTP状态码（可选）
     * @param responseBody 响应内容（可选）
     * @param errorMessage 错误信息（可选）
     * @param sendTime 发送时间（可选）
     * @param responseTime 响应时间（可选）
     * @param costTime 耗时（可选）
     * @return 更新条数
     */
    int updateCallbackStatus(@Param("id") String id,
                            @Param("status") String status,
                            @Param("httpStatus") Integer httpStatus,
                            @Param("responseBody") String responseBody,
                            @Param("errorMessage") String errorMessage,
                            @Param("sendTime") LocalDateTime sendTime,
                            @Param("responseTime") LocalDateTime responseTime,
                            @Param("costTime") Integer costTime);

    /**
     * 更新重试信息
     *
     * @param id 回调记录ID
     * @param retryCount 重试次数
     * @param nextRetryTime 下次重试时间
     * @param status 状态
     * @return 更新条数
     */
    int updateRetryInfo(@Param("id") String id,
                       @Param("retryCount") Integer retryCount,
                       @Param("nextRetryTime") LocalDateTime nextRetryTime,
                       @Param("status") String status);

    /**
     * 批量更新回调状态
     *
     * @param ids 回调记录ID列表
     * @param status 目标状态
     * @param updateTime 更新时间
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") String status,
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 获取回调统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param msgId 消息ID（可选）
     * @return 统计结果
     */
    Map<String, Object> selectCallbackStatistics(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("msgId") String msgId);

    /**
     * 查询失败的回调记录
     *
     * @param retryCount 重试次数阈值
     * @param limit 限制数量
     * @return 失败的回调记录列表
     */
    List<UmpMsgCallback> selectFailedCallbacks(@Param("retryCount") Integer retryCount,
                                              @Param("limit") int limit);
}