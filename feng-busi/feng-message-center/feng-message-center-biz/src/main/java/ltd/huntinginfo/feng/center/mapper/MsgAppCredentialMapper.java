package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.center.api.entity.MsgAppCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MsgAppCredentialMapper extends BaseMapper<MsgAppCredential> {
    
    @Select("SELECT * FROM msg_app_credential WHERE app_key = #{appKey} AND del_flag = '0'")
    MsgAppCredential selectByAppKey(@Param("appKey") String appKey);
    
    @Select("SELECT * FROM msg_app_credential WHERE sys_code = #{sysCode} AND del_flag = '0' AND status = 1")
    MsgAppCredential selectBySysCode(@Param("sysCode") String sysCode);
    
    @Select("SELECT COUNT(*) FROM msg_app_credential WHERE sys_code = #{sysCode} AND del_flag = '0'")
    Integer countBySysCode(@Param("sysCode") String sysCode);
}