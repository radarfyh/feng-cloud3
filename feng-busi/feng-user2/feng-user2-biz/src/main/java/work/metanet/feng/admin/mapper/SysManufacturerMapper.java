package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysManufacturer;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 厂商表(SysManufacturer)表数据库访问层
 *
 * @author edison
 * @since 2022-06-09 15:03:38
 */
@Mapper
public interface SysManufacturerMapper extends FengBaseMapper<SysManufacturer> {

}