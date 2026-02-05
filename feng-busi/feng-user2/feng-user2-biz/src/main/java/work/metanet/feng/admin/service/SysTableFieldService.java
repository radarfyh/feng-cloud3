package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.dto.TableFileDTO;
import work.metanet.feng.admin.api.entity.SysTableField;
import work.metanet.feng.admin.api.vo.DataElementIdentifierInfoVO;
import work.metanet.feng.admin.api.vo.TableFieldVO;

import java.util.List;

/**
 * 数据字段表服务接口
 */
public interface SysTableFieldService extends IService<SysTableField> {
    public boolean batchSave(TableFileDTO tableFileRequestDTO);

    public boolean batchUpdate(TableFileDTO tableFileRequestDTO);

    public List<TableFieldVO> getAccurateQueryItems(String fieldNameChinese,String datasourceId);

    public List<DataElementIdentifierInfoVO> getDataElementIdentifierInfo(String datasourceId, String tableName, String fieldName, String fieldId, String identifier);
}
