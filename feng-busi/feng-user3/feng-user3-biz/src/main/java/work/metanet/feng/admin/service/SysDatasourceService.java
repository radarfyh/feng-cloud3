package work.metanet.feng.admin.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.dto.DataSourceType;
import work.metanet.feng.admin.api.dto.DatasourceSqlDTO;
import work.metanet.feng.admin.api.dto.SysDatasourceDTO;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.admin.api.vo.TableVO;
import work.metanet.feng.common.core.util.R;

/**
 * 数据源表(SysDatasource)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysDatasourceService extends IService<SysDatasource> {

    /*
     *
     * @Description: 保存数据源并且加密
     * @author edison
     * @date 2022/5/19
     * @param: sysDatasource
     * @return
     */
    R saveDsByEnc(SysDatasource sysDatasource);


    /**
     * 更新动态数据的数据源列表
     *
     * @param sysDatasource
     * @return
     */
    void addDynamicDataSource(SysDatasource sysDatasource);

    /**
     * 校验数据源配置是否有效
     *
     * @param sysDatasource 数据源信息
     * @return 有效/无效
     */
    R checkDataSource(SysDatasource sysDatasource);

    /*
     *
     * @Description: 更新数据源
     * @author edison
     * @date 2022/5/19
     * @param: sysDatasource
     * @return
     */
    R updateDsByEnc(SysDatasource sysDatasource);

    /*
     *
     * @Description: 通过数据源名称删除
     * @author edison
     * @date 2022/5/19
     * @param: id
     * @return
     */
    Boolean removeByDsId(Integer id);

    /*
     *
     * @Description: 动态执行SQL
     * @author edison
     * @date 2022/5/19
     * @param: datasourceSqlDTO
     * @return
     */
    R dynamicSql(DatasourceSqlDTO datasourceSqlDTO);

    /**
     * 根据填写参数测试数据源连接
     *
     * @param sysDatasourceDTO
     * @return
     */
    R testDatasource(SysDatasourceDTO sysDatasourceDTO);

    /**
     * 根据数据源id测试数据源连接
     *
     * @param id
     * @return
     */
    R testDatasourceById(Integer id);
    
    Collection<DataSourceType> types();
    
    List<TableVO> getTables(String id) throws Exception;
}