package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 收件箱表Mapper接口
 */
@Mapper
public interface UmpMsgInboxMapper extends BaseMapper<UmpMsgInbox> {

    /**
     * 根据消息ID和接收者查询收件箱记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 收件箱记录
     */
    UmpMsgInbox selectByMsgAndReceiver(@Param("msgId") String msgId, 
                                       @Param("receiverId") String receiverId, 
                                       @Param("receiverType") String receiverType);

    /**
     * 分页查询接收者的收件箱
     *
     * @param page 分页参数
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态（可选）
     * @param receiveStatus 接收状态（可选）
     * @return 分页结果
     */
    IPage<UmpMsgInbox> selectPageByReceiver(IPage<UmpMsgInbox> page, 
                                            @Param("receiverId") String receiverId, 
                                            @Param("receiverType") String receiverType,
                                            @Param("readStatus") Integer readStatus,
                                            @Param("receiveStatus") String receiveStatus);

    /**
     * 根据消息ID查询收件箱记录列表
     *
     * @param msgId 消息ID
     * @return 收件箱记录列表
     */
    List<UmpMsgInbox> selectByMsgId(@Param("msgId") String msgId);

    /**
     * 批量更新收件箱记录的接收状态
     *
     * @param ids 收件箱记录ID列表
     * @param receiveStatus 接收状态
     * @param receiveTime 接收时间
     * @return 更新条数
     */
    int batchUpdateReceiveStatus(@Param("ids") List<String> ids, 
                                @Param("receiveStatus") String receiveStatus, 
                                @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 批量更新收件箱记录的阅读状态
     *
     * @param ids 收件箱记录ID列表
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    int batchUpdateReadStatus(@Param("ids") List<String> ids, 
                             @Param("readStatus") Integer readStatus, 
                             @Param("readTime") LocalDateTime readTime);

    /**
     * 更新推送状态和次数
     *
     * @param id 收件箱记录ID
     * @param pushStatus 推送状态
     * @param pushCount 推送次数
     * @param lastPushTime 最后推送时间
     * @return 更新条数
     */
    int updatePushStatus(@Param("id") String id, 
                        @Param("pushStatus") String pushStatus, 
                        @Param("pushCount") Integer pushCount, 
                        @Param("lastPushTime") LocalDateTime lastPushTime);

    /**
     * 查询待推送的收件箱记录
     *
     * @param limit 限制数量
     * @return 待推送的收件箱记录列表
     */
    List<UmpMsgInbox> selectPendingPush(@Param("limit") int limit);

    /**
     * 统计接收者的未读消息数量
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    Integer countUnreadByReceiver(@Param("receiverId") String receiverId, 
                                  @Param("receiverType") String receiverType);

    /**
     * 获取接收者的消息统计
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计结果
     */
    Map<String, Object> selectReceiverStatistics(@Param("receiverId") String receiverId, 
                                                @Param("receiverType") String receiverType,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
}