package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.center.api.entity.MsgAppPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MsgAppPermissionMapper extends BaseMapper<MsgAppPermission> {
    
    @Select("SELECT * FROM msg_app_permission WHERE app_key = #{appKey} AND status = 1 AND del_flag = '0'")
    List<MsgAppPermission> selectActivePermissions(String appKey);
}