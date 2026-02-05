package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysDeptAttribute;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 科室属性关联表(SysDeptAttribute)表数据库访问层
 *
 * @author edison
 * @since 2022-11-01 11:39:30
 */
@Mapper
public interface SysDeptAttributeMapper extends FengBaseMapper<SysDeptAttribute> {

}
