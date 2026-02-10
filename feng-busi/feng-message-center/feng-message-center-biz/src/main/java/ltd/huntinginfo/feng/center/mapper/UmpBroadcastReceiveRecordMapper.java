package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广播消息接收记录表Mapper接口
 */
@Mapper
public interface UmpBroadcastReceiveRecordMapper extends BaseMapper<UmpBroadcastReceiveRecord> {

    /**
     * 根据复合主键查询接收记录
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 接收记录
     */
    UmpBroadcastReceiveRecord selectByPrimaryKey(@Param("broadcastId") String broadcastId,
                                                @Param("receiverId") String receiverId,
                                                @Param("receiverType") String receiverType);

    /**
     * 分页查询广播接收记录
     *
     * @param page 分页参数
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID（可选）
     * @param receiverType 接收者类型（可选）
     * @param receiveStatus 接收状态（可选）
     * @param readStatus 阅读状态（可选）
     * @return 分页结果
     */
    IPage<UmpBroadcastReceiveRecord> selectReceiveRecordPage(IPage<UmpBroadcastReceiveRecord> page,
                                                            @Param("broadcastId") String broadcastId,
                                                            @Param("receiverId") String receiverId,
                                                            @Param("receiverType") String receiverType,
                                                            @Param("receiveStatus") String receiveStatus,
                                                            @Param("readStatus") Integer readStatus);

    /**
     * 根据广播ID查询接收记录列表
     *
     * @param broadcastId 广播ID
     * @return 接收记录列表
     */
    List<UmpBroadcastReceiveRecord> selectByBroadcastId(@Param("broadcastId") String broadcastId);

    /**
     * 根据接收者查询广播接收记录
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态（可选）
     * @param limit 限制数量
     * @return 接收记录列表
     */
    List<UmpBroadcastReceiveRecord> selectByReceiver(@Param("receiverId") String receiverId,
                                                    @Param("receiverType") String receiverType,
                                                    @Param("readStatus") Integer readStatus,
                                                    @Param("limit") int limit);

    /**
     * 批量插入或更新接收记录
     *
     * @param records 接收记录列表
     * @return 插入/更新条数
     */
    int batchUpsert(@Param("records") List<UmpBroadcastReceiveRecord> records);

    /**
     * 更新接收状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param receiveStatus 接收状态
     * @param receiveTime 接收时间
     * @return 更新条数
     */
    int updateReceiveStatus(@Param("broadcastId") String broadcastId,
                           @Param("receiverId") String receiverId,
                           @Param("receiverType") String receiverType,
                           @Param("receiveStatus") String receiveStatus,
                           @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 更新阅读状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    int updateReadStatus(@Param("broadcastId") String broadcastId,
                        @Param("receiverId") String receiverId,
                        @Param("receiverType") String receiverType,
                        @Param("readStatus") Integer readStatus,
                        @Param("readTime") LocalDateTime readTime);

    /**
     * 批量更新阅读状态
     *
     * @param broadcastId 广播ID
     * @param receiverIds 接收者ID列表
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    int batchUpdateReadStatus(@Param("broadcastId") String broadcastId,
                             @Param("receiverIds") List<String> receiverIds,
                             @Param("receiverType") String receiverType,
                             @Param("readStatus") Integer readStatus,
                             @Param("readTime") LocalDateTime readTime);

    /**
     * 统计广播接收记录
     *
     * @param broadcastId 广播ID
     * @return 统计结果
     */
    Map<String, Object> countByBroadcast(@Param("broadcastId") String broadcastId);

    /**
     * 查询广播未读接收者
     *
     * @param broadcastId 广播ID
     * @param limit 限制数量
     * @return 未读接收者列表
     */
    List<Map<String, Object>> selectUnreadReceivers(@Param("broadcastId") String broadcastId,
                                                   @Param("limit") int limit);
}