package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import ltd.huntinginfo.feng.center.api.entity.MsgSendQueue;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;

@Mapper
public interface MsgSendQueueMapper extends BaseMapper<MsgSendQueue> {
    
    @Select("SELECT * FROM msg_send_queue WHERE queue_status = 'PENDING' AND execute_time <= NOW() ORDER BY priority ASC, execute_time ASC LIMIT #{limit}")
    List<MsgSendQueue> selectPendingTasks(@Param("limit") Integer limit);
    
    @Select("SELECT * FROM msg_send_queue WHERE msg_id = #{msgId} AND queue_type = #{queueType} ORDER BY create_time DESC LIMIT 1")
    MsgSendQueue selectLatestByMsgIdAndType(@Param("msgId") String msgId, @Param("queueType") String queueType);
    
    @Select("SELECT * FROM msg_send_queue WHERE app_key = #{appKey} AND queue_status = #{queueStatus} AND execute_time <= #{endTime} ORDER BY execute_time ASC")
    List<MsgSendQueue> selectByAppKeyAndStatus(@Param("appKey") String appKey, @Param("queueStatus") String queueStatus, @Param("endTime") Date endTime);
    
    @Update("UPDATE msg_send_queue SET queue_status = #{queueStatus}, result_code = #{resultCode}, result_message = #{resultMessage}, execute_end_time = NOW() WHERE id = #{id}")
    int updateQueueStatus(@Param("id") String id, @Param("queueStatus") String queueStatus, 
                         @Param("resultCode") String resultCode, @Param("resultMessage") String resultMessage);
    
    @Update("UPDATE msg_send_queue SET current_retry = current_retry + 1, execute_time = #{nextExecuteTime}, queue_status = 'PENDING' WHERE id = #{id}")
    int retryQueueTask(@Param("id") String id, @Param("nextExecuteTime") Date nextExecuteTime);
}