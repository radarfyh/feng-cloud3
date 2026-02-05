package work.metanet.feng.common.data.datascope;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 扩展支持COUNT查询数量
 * <p>
 * 该方法用于根据给定的数据权限范围计算并查询记录数量，支持根据不同的权限范围进行过滤。
 * </p>
 * <p>
 * 该方法通过构建 SQL 语句实现对数据表记录数的查询，使用 MyBatis-Plus 提供的 SqlMethod.SELECT_COUNT 来构造 COUNT 查询 SQL。
 * </p>
 */
public class SelectCountByScope extends AbstractMethod {
	private static final long serialVersionUID = 1L;

	/**
     * 构造方法，设置 SQL 方法名称
     */
    public SelectCountByScope() {
        super("selectCountByScope");
    }

    /**
     * 注入自定义的 SQL 查询逻辑，用于生成 COUNT 查询的 SQL 语句。
     * 
     * @param mapperClass 映射的 Mapper 类
     * @param modelClass 实体类
     * @param tableInfo 数据库表信息
     * @return MappedStatement 包含 SQL 语句和相关配置信息
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 使用 SqlMethod.SELECT_COUNT 来生成 COUNT 查询 SQL 语句
        SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;

        // 拼接 SQL 查询语句
        String sql = String.format(sqlMethod.getSql(), 
                                   this.sqlFirst(), 
                                   this.sqlSelectColumns(tableInfo, true), 
                                   tableInfo.getTableName(), 
                                   this.sqlWhereEntityWrapper(true, tableInfo), 
                                   this.sqlOrderBy(tableInfo), 
                                   this.sqlComment());

        // 创建 SqlSource 对象
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);

        // 返回自定义的 MappedStatement，指定返回值类型为 Integer
        return this.addSelectMappedStatementForOther(mapperClass, "selectCountByScope", sqlSource, Integer.class);
    }
}
