package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import work.metanet.feng.admin.api.entity.SysMetaDataElement;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 单字段描述数据库访问层
 */
@Mapper
public interface SysMetaDataElementMapper extends FengBaseMapper<SysMetaDataElement> {

}