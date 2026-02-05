package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import work.metanet.feng.admin.api.dto.QueryConditionDTO;
import work.metanet.feng.admin.api.entity.SysTableField;
import work.metanet.feng.admin.api.vo.DataElementIdentifierInfoVO;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据字段表数据库访问层
 */
@Mapper
public interface SysTableFieldMapper extends FengBaseMapper<SysTableField> {
    public void deleteByTableId(List<String> idList);

    public List<QueryConditionDTO> getDynamicQueryFieldsInfo(List<String> fieldIdList);

    public List<QueryConditionDTO> getTableAssociatedFields(List<String> tableIdList);

    public List<TableFieldVO> getAccurateQueryItems(@Param("fieldNameChinese") String fieldNameChinese, @Param("datasourceId") String datasourceId);

    public List<DataElementIdentifierInfoVO> getDataElementIdentifierInfo(@Param("datasourceId") String datasourceId,
                                                                          @Param("tableName") String tableName,
                                                                          @Param("fieldName") String fieldName,
                                                                          @Param("fieldId") String fieldId,
                                                                          @Param("identifier") String identifier);
}
