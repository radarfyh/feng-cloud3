package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.center.api.entity.MsgAuthLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgAuthLogMapper extends BaseMapper<MsgAuthLog> {
    // 使用BaseMapper默认方法
}