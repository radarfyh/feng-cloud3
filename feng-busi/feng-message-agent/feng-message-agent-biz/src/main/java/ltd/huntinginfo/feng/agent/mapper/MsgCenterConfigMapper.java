package ltd.huntinginfo.feng.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.agent.api.entity.MsgCenterConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MsgCenterConfigMapper extends BaseMapper<MsgCenterConfig> {
    
    @Select("SELECT * FROM msg_center_config WHERE config_key = #{configKey} AND status = 1")
    MsgCenterConfig selectByConfigKey(@Param("configKey") String configKey);
    
    @Select("SELECT * FROM msg_center_config WHERE category = #{category} AND status = 1 ORDER BY config_key")
    List<MsgCenterConfig> selectByCategory(@Param("category") String category);
    
    @Select("SELECT config_value FROM msg_center_config WHERE config_key = #{configKey} AND status = 1")
    String selectConfigValue(@Param("configKey") String configKey);
}