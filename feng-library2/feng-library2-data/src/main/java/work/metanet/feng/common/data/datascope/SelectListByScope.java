package work.metanet.feng.common.data.datascope;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 扩展支持数据权限查询，生成带有权限控制的 SELECT 查询 SQL
 * <p>
 * 该方法基于数据权限范围，生成带有过滤条件的查询语句（SELECT *）。支持自定义查询字段、排序以及 WHERE 条件。
 * </p>
 */
public class SelectListByScope extends AbstractMethod {

	private static final long serialVersionUID = 1L;

	/**
     * 构造方法，设置 SQL 方法名称
     */
    public SelectListByScope() {
        super("selectListByScope");
    }

    /**
     * 注入自定义的 SQL 查询逻辑，用于生成带有数据权限的 SELECT 查询 SQL 语句。
     *
     * @param mapperClass 映射的 Mapper 类
     * @param modelClass 实体类
     * @param tableInfo 数据库表信息
     * @return MappedStatement 包含 SQL 语句和相关配置信息
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 使用 SqlMethod.SELECT_LIST 来生成查询列表的 SQL 语句
        SqlMethod sqlMethod = SqlMethod.SELECT_LIST;

        // 拼接 SQL 查询语句，包含条件、排序、选择的列等
        String sql = String.format(sqlMethod.getSql(),
                this.sqlFirst(), // 插入 SQL 语句前缀
                this.sqlSelectColumns(tableInfo, true), // 选择查询的列
                tableInfo.getTableName(), // 表名
                this.sqlWhereEntityWrapper(true, tableInfo), // WHERE 条件
                this.sqlOrderBy(tableInfo), // 排序
                this.sqlComment()); // SQL 注释

        // 创建 SqlSource 对象，表示 SQL 语句的源
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);

        // 返回自定义的 MappedStatement，执行查询操作
        return this.addSelectMappedStatementForTable(mapperClass, sqlSource, tableInfo);
    }
}
