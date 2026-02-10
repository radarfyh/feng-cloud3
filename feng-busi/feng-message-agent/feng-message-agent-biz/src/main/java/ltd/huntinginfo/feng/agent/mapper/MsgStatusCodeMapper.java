package ltd.huntinginfo.feng.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.agent.api.entity.MsgStatusCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MsgStatusCodeMapper extends BaseMapper<MsgStatusCode> {
    
    @Select("SELECT * FROM msg_status_code WHERE status_code = #{statusCode} AND status = 1")
    MsgStatusCode selectByStatusCode(@Param("statusCode") String statusCode);
    
    @Select("SELECT * FROM msg_status_code WHERE category = #{category} AND status = 1 ORDER BY sort_order ASC, status_code ASC")
    List<MsgStatusCode> selectByCategory(@Param("category") String category);
    
    @Select("SELECT * FROM msg_status_code WHERE parent_code = #{parentCode} AND status = 1 ORDER BY sort_order ASC")
    List<MsgStatusCode> selectByParentCode(@Param("parentCode") String parentCode);
    
    @Select("SELECT * FROM msg_status_code WHERE is_final = 1 AND status = 1")
    List<MsgStatusCode> selectAllFinalStatus();
}