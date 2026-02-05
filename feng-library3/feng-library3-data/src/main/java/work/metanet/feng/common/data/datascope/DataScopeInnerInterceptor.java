package work.metanet.feng.common.data.datascope;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据权限拦截器，用于在查询之前处理数据权限。
 * <p>
 * 该类实现了 DataScopeInterceptor 接口，在查询前根据用户权限动态构建 SQL 语句。
 * </p>
 */
public class DataScopeInnerInterceptor implements DataScopeInterceptor {

    @Setter
    private DataScopeHandle dataScopeHandle;

    /**
     * 在执行查询前修改 SQL，添加数据权限过滤。
     * <p>
     * 该方法会根据传入的参数，判断是否需要进行数据过滤，并在 SQL 中动态添加 WHERE 子句来执行数据权限过滤。
     * </p>
     *
     * @param executor        执行器
     * @param ms             映射语句
     * @param parameter      参数
     * @param rowBounds      分页参数
     * @param resultHandler  结果处理器
     * @param boundSql       绑定 SQL
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                             ResultHandler resultHandler, BoundSql boundSql) {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);

        String originalSql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();

        // 查找参数中是否包含 DataScope 类型的对象
        DataScope dataScope = findDataScopeObject(parameterObject);
        if (dataScope == null) {
            return; // 如果没有数据权限对象，直接返回原始 SQL
        }

        // 如果不需要数据过滤，直接返回原始 SQL
        if (dataScopeHandle.shouldFilterData(dataScope.getDeptList())) {
            return;
        }

        List<Integer> deptIds = dataScope.getDeptList();
        String username = dataScope.getUsername();

        // 1. 无数据权限限制，则返回 0 条数据
        if (CollUtil.isEmpty(deptIds) && StrUtil.isBlank(username)) {
            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE 1 = 2",
                    dataScope.getFunc().getType(), originalSql);
        }
        // 2. 如果为本人权限，则添加用户名过滤条件
        else if (StrUtil.isNotBlank(username)) {
            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE temp_data_scope.%s = '%s'",
                    dataScope.getFunc().getType(), originalSql, dataScope.getScopeUserName(), username);
        }
        // 3. 否则，按照部门权限过滤
        else {
            String join = CollUtil.join(deptIds, ",");
            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE temp_data_scope.%s IN (%s)",
                    dataScope.getFunc().getType(), originalSql, dataScope.getScopeDeptName(), join);
        }

        mpBs.sql(originalSql); // 更新 SQL
    }

    /**
     * 查找参数是否包含 DataScope 对象
     * <p>
     * 检查参数对象是否为 DataScope 类型，或其包含 DataScope 类型的字段。
     * </p>
     *
     * @param parameterObj 参数对象
     * @return 返回 DataScope 对象，如果找不到则返回 null
     */
    private DataScope findDataScopeObject(Object parameterObj) {
        if (parameterObj instanceof DataScope) {
            return (DataScope) parameterObj;
        }
        else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof DataScope) {
                    return (DataScope) val;
                }
            }
        }
        return null;
    }

}
