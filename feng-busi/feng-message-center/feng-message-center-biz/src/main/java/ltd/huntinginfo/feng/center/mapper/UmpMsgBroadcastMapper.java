package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广播信息筒表Mapper接口
 */
@Mapper
public interface UmpMsgBroadcastMapper extends BaseMapper<UmpMsgBroadcast> {

    /**
     * 根据消息ID查询广播记录
     *
     * @param msgId 消息ID
     * @return 广播记录
     */
    UmpMsgBroadcast selectByMsgId(@Param("msgId") String msgId);

    /**
     * 分页查询广播记录
     *
     * @param page 分页参数
     * @param broadcastType 广播类型（可选）
     * @param status 状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<UmpMsgBroadcast> selectBroadcastPage(IPage<UmpMsgBroadcast> page,
                                              @Param("broadcastType") String broadcastType,
                                              @Param("status") String status,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态查询广播记录
     *
     * @param status 状态
     * @param limit 限制数量
     * @return 广播记录列表
     */
    List<UmpMsgBroadcast> selectByStatus(@Param("status") String status, @Param("limit") int limit);

    /**
     * 更新广播统计信息
     *
     * @param broadcastId 广播ID
     * @param distributedCount 已分发数量
     * @param receivedCount 已接收数量
     * @param readCount 已读人数
     * @param status 状态
     * @param updateTime 更新时间
     * @return 更新条数
     */
    int updateBroadcastStatistics(@Param("broadcastId") String broadcastId,
                                 @Param("distributedCount") Integer distributedCount,
                                 @Param("receivedCount") Integer receivedCount,
                                 @Param("readCount") Integer readCount,
                                 @Param("status") String status,
                                 @Param("updateTime") LocalDateTime updateTime);

    /**
     * 批量更新广播状态
     *
     * @param ids 广播ID列表
     * @param status 目标状态
     * @param updateTime 更新时间
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") String status,
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 获取广播统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param broadcastType 广播类型（可选）
     * @return 统计结果
     */
    Map<String, Object> selectBroadcastStatistics(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("broadcastType") String broadcastType);

    /**
     * 查询待分发的广播记录
     *
     * @param limit 限制数量
     * @return 待分发的广播记录列表
     */
    List<UmpMsgBroadcast> selectPendingDistribute(@Param("limit") int limit);
}
