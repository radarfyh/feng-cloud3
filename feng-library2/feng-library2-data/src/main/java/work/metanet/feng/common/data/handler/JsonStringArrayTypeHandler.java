package work.metanet.feng.common.data.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mybatis 数组与字符串之间的互转
 * <p>
 * 通过此类型处理器将 String 数组转换为字符串，以便存储到数据库中，并能将数据库中的字符串转换回 String 数组。
 * </p>
 */
@MappedTypes(value = { String[].class })
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class JsonStringArrayTypeHandler extends BaseTypeHandler<String[]> {

    /**
     * 设置非空参数，将 String 数组转换为以逗号分隔的字符串，并设置到 PreparedStatement 中
     *
     * @param ps PreparedStatement 对象
     * @param i 参数索引
     * @param parameter String 数组参数
     * @param jdbcType 数据库中的类型
     * @throws SQLException 如果设置参数失败，抛出 SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            // 将 String 数组转换为以逗号分隔的字符串
            ps.setString(i, ArrayUtil.join(parameter, StrUtil.COMMA));
        }
    }

    /**
     * 从 ResultSet 中获取列值并将其转换为 String 数组
     *
     * @param rs ResultSet 对象
     * @param columnName 列名
     * @return 转换后的 String 数组
     * @throws SQLException 如果获取列值失败，抛出 SQL 异常
     */
    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String reString = rs.getString(columnName);
        // 转换字符串为 String 数组，若字符串为空，返回空数组
        return reString == null ? new String[0] : Convert.toStrArray(reString);
    }

    /**
     * 从 ResultSet 中获取列值并将其转换为 String 数组
     *
     * @param rs ResultSet 对象
     * @param columnIndex 列索引
     * @return 转换后的 String 数组
     * @throws SQLException 如果获取列值失败，抛出 SQL 异常
     */
    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String reString = rs.getString(columnIndex);
        // 转换字符串为 String 数组，若字符串为空，返回空数组
        return reString == null ? new String[0] : Convert.toStrArray(reString);
    }

    /**
     * 从 CallableStatement 中获取列值并将其转换为 String 数组
     *
     * @param cs CallableStatement 对象
     * @param columnIndex 列索引
     * @return 转换后的 String 数组
     * @throws SQLException 如果获取列值失败，抛出 SQL 异常
     */
    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String reString = cs.getString(columnIndex);
        // 转换字符串为 String 数组，若字符串为空，返回空数组
        return reString == null ? new String[0] : Convert.toStrArray(reString);
    }
}
