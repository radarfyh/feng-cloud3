package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.dto.TableFileDTO;
import work.metanet.feng.admin.api.entity.SysTableField;
import work.metanet.feng.admin.api.vo.DataElementIdentifierInfoVO;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.admin.mapper.SysTableFieldMapper;
import work.metanet.feng.admin.mapper.SysTableMapper;
import work.metanet.feng.admin.service.SysTableFieldService;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 数据字段(SysTableField)表服务实现类
 */
@Service
@AllArgsConstructor
public class SysTableFieldServiceImpl extends ServiceImpl<SysTableFieldMapper, SysTableField> implements SysTableFieldService {
    @Resource
    private SysTableMapper tableMapper;
    @Resource
    private SysTableFieldMapper tableFieldMapper;

    @Override
    @Transactional
    public boolean batchSave(TableFileDTO tableFileRequestDTO) {
        List<SysTableField> sysTableFieldList = new LinkedList<>();
        Integer tableId = tableFileRequestDTO.getTable().getId();
        for (TableFieldVO tableField : tableFileRequestDTO.getTableField()) {
            SysTableField sysTableField = new SysTableField();

            BeanUtils.copyProperties(tableField, sysTableField);
            sysTableField.setTableId(tableId);
            sysTableField.setCreateTime(LocalDateTime.now());
            sysTableFieldList.add(sysTableField);
        }
        this.saveBatch(sysTableFieldList);
        return true;
    }

    @Override
    public boolean batchUpdate(TableFileDTO tableFileRequestDTO) {
        List<SysTableField> sysTableFieldList = new LinkedList<>();
        for (TableFieldVO tableField : tableFileRequestDTO.getTableField()) {
            SysTableField sysTableField = new SysTableField();
            BeanUtils.copyProperties(tableField, sysTableField);
            sysTableField.setUpdateTime(LocalDateTime.now());
            sysTableFieldList.add(sysTableField);
        }
        return this.updateBatchById(sysTableFieldList);
    }

    @Override
    public List<TableFieldVO> getAccurateQueryItems(String fieldNameChinese, String datasourceId) {
        if ("null".equalsIgnoreCase(fieldNameChinese)) {
            fieldNameChinese = null;
        } else {
            fieldNameChinese = "%" + fieldNameChinese + "%";
        }
        return tableFieldMapper.getAccurateQueryItems(fieldNameChinese, datasourceId);
    }
    
    @Override
    public List<DataElementIdentifierInfoVO> getDataElementIdentifierInfo(String datasourceId, String tableName, 
    		String fieldName, String fieldId, String identifier) {
        return tableFieldMapper.getDataElementIdentifierInfo(datasourceId, tableName, fieldName, fieldId, identifier);
    }
}
