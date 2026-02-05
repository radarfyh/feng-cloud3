package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.admin.api.entity.SysTable;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.admin.mapper.SysTableFieldMapper;
import work.metanet.feng.admin.mapper.SysTableMapper;
import work.metanet.feng.admin.provider.Provider;
import work.metanet.feng.admin.provider.ProviderFactory;
import work.metanet.feng.admin.service.SysDatasourceService;
import work.metanet.feng.admin.service.SysTableService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 数据表(SysTable)表服务实现类
 */
@Service
@AllArgsConstructor
public class SysTableServiceImpl extends ServiceImpl<SysTableMapper, SysTable> implements SysTableService {
    @Resource
    private SysDatasourceService sysDatasourceService;
    @Resource
    private SysTableMapper tableMapper;
    @Resource
    private SysTableFieldMapper tableFieldMapper;
    @Override
    public List<TableFieldVO> getTableFields(String datasourceId, String tableName) throws Exception {
        SysDatasource datasource = sysDatasourceService.getById(datasourceId);
        Provider datasourceProvider = ProviderFactory.getProvider();
        List<TableFieldVO> list = datasourceProvider.getTableFields(datasource,tableName);
        return list;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(List<String> list) {
        tableMapper.deleteBatchIds(list);
        tableFieldMapper.deleteByTableId(list);
        return true;
    }
}
