package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import work.metanet.feng.admin.api.entity.SysMetaData;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 多字段描述数据库访问层
 */
@Mapper
public interface SysMetaDataMapper extends FengBaseMapper<SysMetaData> {

}