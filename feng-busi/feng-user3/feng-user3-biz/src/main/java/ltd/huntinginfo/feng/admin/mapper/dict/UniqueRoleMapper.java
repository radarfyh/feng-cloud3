package ltd.huntinginfo.feng.admin.mapper.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 统一角色信息表 Mapper 接口
 */
@Mapper
public interface UniqueRoleMapper extends BaseMapper<UniqueRole> {
    // 使用BaseMapper提供的默认方法，不额外定义方法
}
