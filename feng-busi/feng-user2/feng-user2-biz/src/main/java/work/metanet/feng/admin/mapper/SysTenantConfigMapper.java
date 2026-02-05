package work.metanet.feng.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import work.metanet.feng.admin.api.entity.SysTenantConfig;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

@Mapper
public interface SysTenantConfigMapper extends FengBaseMapper<SysTenantConfig> {

}
