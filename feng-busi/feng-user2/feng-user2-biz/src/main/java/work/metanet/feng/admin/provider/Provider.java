package work.metanet.feng.admin.provider;

import com.alibaba.druid.pool.DruidDataSource;

import work.metanet.feng.admin.api.dto.TableDesc;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.admin.api.vo.TableFieldVO;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public abstract class Provider {
    public void handleDatasource(SysDatasource datasource, String type) throws Exception{}
    public String getTablesSql(SysDatasource datasource) throws Exception{
        return null;
    }
    public Connection getConnection(SysDatasource datasource) throws Exception{
        return null;
    }
    public String checkStatus(SysDatasource datasource) throws Exception {
        return null;
    }

    public void addToPool(SysDatasource datasource) throws Exception{}

    public void setCredential(SysDatasource datasource, DruidDataSource druidDataSource) throws Exception{}
    public Connection getConnectionFromPool(SysDatasource datasource) throws Exception {
        return null;
    }
    public String getViewSql(SysDatasource datasource) throws Exception {
        return null;
    }
    abstract public List<TableDesc> getTables(SysDatasource datasource) throws Exception ;

    abstract public List<TableFieldVO> getTableFields(SysDatasource datasource, String tableName) throws Exception;

    abstract public List<Map<String,String>> getData(SysDatasource datasource, String sql) throws Exception ;

    abstract public void closeAllDatasource();
}
