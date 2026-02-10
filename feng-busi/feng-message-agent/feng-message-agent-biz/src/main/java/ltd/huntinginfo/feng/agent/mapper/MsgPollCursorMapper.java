package ltd.huntinginfo.feng.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.agent.api.entity.MsgPollCursor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;

@Mapper
public interface MsgPollCursorMapper extends BaseMapper<MsgPollCursor> {
    
    @Select("SELECT * FROM msg_poll_cursor WHERE app_key = #{appKey} AND cursor_key = #{cursorKey} AND del_flag = '0'")
    MsgPollCursor selectByAppKeyAndCursorKey(@Param("appKey") String appKey, @Param("cursorKey") String cursorKey);
    
    @Select("SELECT * FROM msg_poll_cursor WHERE sys_code = #{sysCode} AND status = 1 AND del_flag = '0'")
    List<MsgPollCursor> selectActiveBySysCode(@Param("sysCode") String sysCode);
    
    @Select("SELECT * FROM msg_poll_cursor WHERE status = 1 AND (last_poll_time IS NULL OR DATE_ADD(last_poll_time, INTERVAL poll_interval SECOND) <= NOW()) AND del_flag = '0'")
    List<MsgPollCursor> selectReadyToPoll();
    
    @Update("UPDATE msg_poll_cursor SET ybid = #{ybid}, last_poll_time = NOW(), poll_count = poll_count + 1, error_count = 0, last_success_time = NOW() WHERE id = #{id}")
    int updatePollSuccess(@Param("id") String id, @Param("ybid") String ybid);
    
    @Update("UPDATE msg_poll_cursor SET last_poll_time = NOW(), poll_count = poll_count + 1, error_count = error_count + 1, last_error = #{errorMsg} WHERE id = #{id}")
    int updatePollError(@Param("id") String id, @Param("errorMsg") String errorMsg);
    
    @Update("UPDATE msg_poll_cursor SET message_count = message_count + #{count}, last_message_time = NOW() WHERE id = #{id}")
    int incrementMessageCount(@Param("id") String id, @Param("count") Integer count);
}