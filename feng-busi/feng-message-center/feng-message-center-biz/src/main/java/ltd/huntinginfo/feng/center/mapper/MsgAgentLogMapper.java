package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import ltd.huntinginfo.feng.center.api.entity.MsgAgentLog;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Date;
import java.util.List;

@Mapper
public interface MsgAgentLogMapper extends BaseMapper<MsgAgentLog> {
    
    @Select("SELECT * FROM msg_agent_log WHERE msg_id = #{msgId} ORDER BY create_time DESC")
    List<MsgAgentLog> selectByMsgId(@Param("msgId") String msgId);
    
    @Select("SELECT * FROM msg_agent_log WHERE app_key = #{appKey} AND create_time >= #{startTime} AND create_time <= #{endTime} ORDER BY create_time DESC")
    List<MsgAgentLog> selectByAppKeyAndTimeRange(@Param("appKey") String appKey, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    @Select("SELECT * FROM msg_agent_log WHERE log_type = #{logType} AND log_level = #{logLevel} AND create_time >= #{startTime} ORDER BY create_time DESC")
    List<MsgAgentLog> selectByTypeAndLevel(@Param("logType") String logType, @Param("logLevel") String logLevel, @Param("startTime") Date startTime);
    
    @Select("SELECT COUNT(*) FROM msg_agent_log WHERE msg_id = #{msgId} AND log_type = #{logType}")
    Integer countByMsgIdAndType(@Param("msgId") String msgId, @Param("logType") String logType);
}