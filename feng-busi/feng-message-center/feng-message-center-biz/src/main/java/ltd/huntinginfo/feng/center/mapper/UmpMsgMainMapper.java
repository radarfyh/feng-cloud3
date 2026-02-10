package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息主表Mapper接口
 */
@Mapper
public interface UmpMsgMainMapper extends BaseMapper<UmpMsgMain> {

    /**
     * 根据消息编码查询消息
     *
     * @param msgCode 消息编码
     * @return 消息实体
     */
    @Select("SELECT * FROM ump_msg_main WHERE msg_code = #{msgCode} AND del_flag = 0")
    UmpMsgMain selectByMsgCode(@Param("msgCode") String msgCode);

    /**
     * 根据发送应用标识分页查询消息
     *
     * @param page 分页参数
     * @param senderAppKey 发送应用标识
     * @return 分页结果
     */
    IPage<UmpMsgMain> selectPageBySender(IPage<UmpMsgMain> page, @Param("senderAppKey") String senderAppKey);

    /**
     * 根据状态查询消息列表
     *
     * @param status 消息状态
     * @param limit 限制数量
     * @return 消息列表
     */
    @Select("SELECT * FROM ump_msg_main WHERE status = #{status} AND del_flag = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<UmpMsgMain> selectByStatus(@Param("status") String status, @Param("limit") int limit);

    /**
     * 查询过期但未标记为过期的消息
     *
     * @param expireTime 过期时间
     * @return 过期消息列表
     */
    @Select("SELECT * FROM ump_msg_main WHERE expire_time < #{expireTime} AND status NOT IN ('EXPIRED', 'FAILED') AND del_flag = 0")
    List<UmpMsgMain> selectExpiredMessages(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 批量更新消息状态
     *
     * @param ids 消息ID列表
     * @param status 目标状态
     * @param updateTime 更新时间
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") String status, 
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 更新消息的已读统计
     *
     * @param msgId 消息ID
     * @param readCount 已读人数
     * @param updateTime 更新时间
     * @return 更新条数
     */
    @Select("UPDATE ump_msg_main SET read_count = #{readCount}, update_time = #{updateTime} WHERE id = #{msgId} AND del_flag = 0")
    int updateReadCount(@Param("msgId") String msgId, @Param("readCount") Integer readCount, 
                       @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询消息统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param appKey 应用标识（可选）
     * @return 统计结果
     */
    List<Map<String, Object>> selectMessageStatistics(@Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime, 
                                                     @Param("appKey") String appKey);
}
