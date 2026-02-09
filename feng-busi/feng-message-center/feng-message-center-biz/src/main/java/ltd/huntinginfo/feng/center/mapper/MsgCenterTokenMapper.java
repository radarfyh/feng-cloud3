package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.center.api.entity.MsgCenterToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;

@Mapper
public interface MsgCenterTokenMapper extends BaseMapper<MsgCenterToken> {
    
    @Select("SELECT * FROM msg_center_token WHERE app_key = #{appKey} AND status = 1 ORDER BY create_time DESC LIMIT 1")
    MsgCenterToken selectLatestByAppKey(@Param("appKey") String appKey);
    
    @Select("SELECT * FROM msg_center_token WHERE expire_time <= #{expireTime} AND status = 1")
    List<MsgCenterToken> selectExpiringTokens(@Param("expireTime") Date expireTime);
    
    @Update("UPDATE msg_center_token SET status = 0 WHERE app_key = #{appKey}")
    int disableAllTokens(@Param("appKey") String appKey);
    
    @Update("UPDATE msg_center_token SET total_requests = total_requests + 1, last_request_time = NOW(), last_request_api = #{apiName} WHERE id = #{id}")
    int updateRequestInfo(@Param("id") String id, @Param("apiName") String apiName);
    
    @Update("UPDATE msg_center_token SET success_requests = success_requests + 1 WHERE id = #{id}")
    int incrementSuccessCount(@Param("id") String id);
}