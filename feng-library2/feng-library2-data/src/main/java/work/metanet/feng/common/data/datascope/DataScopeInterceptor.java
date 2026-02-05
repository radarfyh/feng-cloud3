package work.metanet.feng.common.data.datascope;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

/**
 * 数据权限拦截器接口，用于定义数据权限相关的拦截逻辑。
 * <p>
 * 该接口扩展了 MyBatis-Plus 的 InnerInterceptor，允许用户在执行查询之前进行数据权限的自动处理。
 * 各个实现类可以根据业务需求，重写接口方法，提供自定义的数据权限拦截行为。
 * </p>
 */
public interface DataScopeInterceptor extends InnerInterceptor {

    /**
     * 在查询前对 SQL 进行数据权限相关的处理。可以通过重写此方法实现数据权限的动态注入。
     * <p>
     * 示例：实现类可以根据用户的角色、部门等信息来自动修改查询 SQL，增加数据权限过滤条件。
     * </p>
     *
     * @param executor 执行器
     * @param ms      映射语句
     * @param parameter 查询参数
     * @param rowBounds 分页参数
     * @param resultHandler 结果处理器
     * @param boundSql 绑定的 SQL
     */
    void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                      ResultHandler resultHandler, BoundSql boundSql);
}

