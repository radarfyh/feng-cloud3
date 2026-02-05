package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机构表(SysOrgan)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysOrganMapper extends FengBaseMapper<SysOrgan> {

    List<SysOrgan> getOrganListByUser(@Param("userId") Integer userId);
}