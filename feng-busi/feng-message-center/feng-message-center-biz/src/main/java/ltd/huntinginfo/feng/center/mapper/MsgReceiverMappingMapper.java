package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.center.api.entity.MsgReceiverMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MsgReceiverMappingMapper extends BaseMapper<MsgReceiverMapping> {
    
    @Select("SELECT * FROM msg_receiver_mapping WHERE app_key = #{appKey} AND biz_receiver_id = #{bizReceiverId} AND del_flag = '0'")
    MsgReceiverMapping selectByBizReceiver(@Param("appKey") String appKey, @Param("bizReceiverId") String bizReceiverId);
    
    @Select("SELECT * FROM msg_receiver_mapping WHERE app_key = #{appKey} AND jsrzjhm = #{jsrzjhm} AND del_flag = '0' AND status = 1")
    MsgReceiverMapping selectByJsrzjhm(@Param("appKey") String appKey, @Param("jsrzjhm") String jsrzjhm);
    
    @Select("SELECT * FROM msg_receiver_mapping WHERE app_key = #{appKey} AND jsdwdm = #{jsdwdm} AND del_flag = '0' AND status = 1")
    MsgReceiverMapping selectByJsdwdm(@Param("appKey") String appKey, @Param("jsdwdm") String jsdwdm);
    
    @Select("SELECT * FROM msg_receiver_mapping WHERE app_key = #{appKey} AND biz_receiver_type = #{bizReceiverType} AND del_flag = '0' AND status = 1")
    List<MsgReceiverMapping> selectByReceiverType(@Param("appKey") String appKey, @Param("bizReceiverType") String bizReceiverType);
    
    @Select("SELECT * FROM msg_receiver_mapping WHERE sys_code = #{sysCode} AND del_flag = '0' AND status = 1")
    List<MsgReceiverMapping> selectBySysCode(@Param("sysCode") String sysCode);
}