package work.metanet.feng.common.data.mybatis;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.StringUtils;
import work.metanet.feng.common.data.config.FengMybatisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * 打印可执行的 SQL 日志
 * 该类用于拦截 SQL 执行，打印经过格式化的 SQL 语句及其执行时间。
 * 
 * @author edison
 */
@Slf4j
@RequiredArgsConstructor
public class DruidSqlLogFilter extends FilterEventAdapter {

    // SQL 格式化选项
    private static final SQLUtils.FormatOption FORMAT_OPTION = new SQLUtils.FormatOption(false, false);

    private final FengMybatisProperties properties;

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        // 记录开始时间
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        // 记录结束时间
        statement.setLastExecuteTimeNano();
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);

        // 判断是否显示 SQL
        if (!properties.isShowSql() || !log.isInfoEnabled()) {
            return;
        }

        // 获取执行的 SQL 语句
        String sql = statement.getBatchSql();
        if (StringUtils.isEmpty(sql)) {
            return;
        }

        // 获取 SQL 参数
        int parametersSize = statement.getParametersSize();
        List<Object> parameters = new ArrayList<>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            parameters.add(getJdbcParameter(statement.getParameter(i)));
        }

        // 获取数据库类型
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String formattedSql = SQLUtils.format(sql, DbType.of(dbType), parameters, FORMAT_OPTION);

        // 打印 SQL 和执行时间
        printSql(formattedSql, statement);
    }

    /**
     * 获取 JDBC 参数并处理 Java8 时间类型
     * 
     * @param jdbcParam JDBC 参数对象
     * @return 转换后的参数值
     */
    private static Object getJdbcParameter(JdbcParameter jdbcParam) {
        if (jdbcParam == null) {
            return null;
        }
        Object value = jdbcParam.getValue();
        
        // 处理 Java8 时间类型
        if (value instanceof TemporalAccessor) {
            try {
                return value.toString();  // 转换为字符串
            } catch (Exception e) {
                log.warn("Failed to convert TemporalAccessor: {}", value);
                return value;
            }
        }
        return value;
    }

    /**
     * 打印 SQL 语句和执行时间
     * 
     * @param sql 格式化后的 SQL 语句
     * @param statement 执行的 Statement 对象
     */
    private static void printSql(String sql, StatementProxy statement) {
        // 使用 StringBuilder 优化字符串拼接
        StringBuilder sqlLogger = new StringBuilder("\n\n======= Sql Logger ======================\n");
        sqlLogger.append("{}\n")
                .append("======= Sql Execute Time: {} =======\n");

        log.info(sqlLogger.toString(), sql.trim(), format(statement.getLastExecuteTimeNano()));
    }

    /**
     * 格式化执行时间，单位为 ms 和 s，保留三位小数
     * 
     * @param nanos 执行时间（纳秒）
     * @return 格式化后的时间字符串
     */
    private static String format(long nanos) {
        if (nanos < 1) {
            return "0ms";
        }
        double millis = (double) nanos / (1000 * 1000);
        if (millis > 1000) {
            return String.format("%.3fs", millis / 1000);
        } else {
            return String.format("%.3fms", millis);
        }
    }
}
