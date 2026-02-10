package ltd.huntinginfo.feng.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.agent.api.entity.MsgAgentMapping;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;

@Mapper
public interface MsgAgentMappingMapper extends BaseMapper<MsgAgentMapping> {
    
    @Select("SELECT * FROM msg_agent_mapping WHERE msg_id = #{msgId} AND del_flag = '0'")
    MsgAgentMapping selectByMsgId(@Param("msgId") String msgId);
    
    @Select("SELECT * FROM msg_agent_mapping WHERE biz_id = #{bizId} AND app_key = #{appKey} AND del_flag = '0'")
    MsgAgentMapping selectByBizIdAndAppKey(@Param("bizId") String bizId, @Param("appKey") String appKey);
    
    @Select("SELECT * FROM msg_agent_mapping WHERE xxbm = #{xxbm} AND del_flag = '0'")
    MsgAgentMapping selectByXxbm(@Param("xxbm") String xxbm);
    
    @Select("SELECT * FROM msg_agent_mapping WHERE app_key = #{appKey} AND status = #{status} AND del_flag = '0'")
    List<MsgAgentMapping> selectByAppKeyAndStatus(@Param("appKey") String appKey, @Param("status") String status);
    
    @Select("SELECT * FROM msg_agent_mapping WHERE next_retry_time <= #{currentTime} AND retry_count < max_retry_count AND status IN ('CALLBACK_SENT', 'CALLBACK_FAILED') AND del_flag = '0'")
    List<MsgAgentMapping> selectNeedRetryMessages(@Param("currentTime") Date currentTime);
    
    // 删除这个方法，使用 Service 层的查询条件构建
    // IPage<MsgAgentMapping> selectByTimeRange(Page<MsgAgentMapping> page, @Param("appKey") String appKey, 
    //                                            @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    @Update("UPDATE msg_agent_mapping SET status = #{status}, status_code = #{statusCode}, status_detail = #{statusDetail}, update_time = NOW() WHERE msg_id = #{msgId}")
    int updateStatus(@Param("msgId") String msgId, @Param("status") String status, 
                    @Param("statusCode") String statusCode, @Param("statusDetail") String statusDetail);
    
    @Update("UPDATE msg_agent_mapping SET retry_count = retry_count + 1, next_retry_time = #{nextRetryTime}, status_detail = #{statusDetail}, update_time = NOW() WHERE msg_id = #{msgId}")
    int updateRetryInfo(@Param("msgId") String msgId, @Param("nextRetryTime") Date nextRetryTime, 
                       @Param("statusDetail") String statusDetail);
}