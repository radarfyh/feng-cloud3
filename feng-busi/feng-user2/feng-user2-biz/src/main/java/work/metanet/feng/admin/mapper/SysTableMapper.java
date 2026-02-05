package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import work.metanet.feng.admin.api.entity.SysTable;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 数据表数据库访问层
 */
@Mapper
public interface SysTableMapper extends FengBaseMapper<SysTable> {

}
