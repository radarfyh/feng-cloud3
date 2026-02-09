package ltd.huntinginfo.feng.admin.mapper.dict;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 统一用户信息表 Mapper 接口
 */
@Mapper
public interface UniqueUserMapper extends BaseMapper<UniqueUser> {
    // 使用BaseMapper提供的默认方法，不额外定义方法
}
