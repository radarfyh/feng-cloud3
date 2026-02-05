package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.entity.SysTable;
import work.metanet.feng.admin.api.vo.TableFieldVO;

import java.util.List;

/**
 * 数据表服务接口
 */
public interface SysTableService extends IService<SysTable> {
    public List<TableFieldVO> getTableFields(String datasourceId, String tableName) throws Exception;
    public boolean removeByIds(List<String> list);
}
