package work.metanet.feng.common.datasource.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author edison
 * @date 2020/12/11
 * <p>
 * jdbc-url
 */
@Getter
@AllArgsConstructor
public enum DsJdbcUrlEnum {

    /**
     * mysql 数据库
     */
    MYSQL("1", "mysql", "jdbc:mysql://%s:%s/%s?characterEncoding=utf8" + "&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true" + "&useLegacyDatetimeCode=false&allowMultiQueries=true&allowPublicKeyRetrieval=true", "select 1", "mysql8 链接"),
    /**
     * pg 数据库
     */
    PG("3", "pg", "jdbc:postgresql://%s:%s/%s", "select 1", "postgresql 链接"),

    /**
     * SQL SERVER
     */
    MSSQL("4", "mssql", "jdbc:sqlserver://%s:%s;database=%s;characterEncoding=UTF-8", "select 1", "sqlserver 链接"),

    /**
     * mongodb
     */
    MONGODB("5", "mongodb", "mongodb://%s:%s@%s:%s/%s?ssl=true", "SELECT * FROM users", "mongodb 连接"),

    /**
     * ElasticSearch
     */
    ELASTICSEARCH("6", "ElasticSearch", "jdbc:es://http://%s:%s", "select * from rep  LIMIT 5", "ElasticSearch 连接");

	private final String type;
	
    private final String dbName;

    private final String url;

    private final String validationQuery;

    private final String description;

    public static DsJdbcUrlEnum getByName(String dbName) {
        return Arrays.stream(DsJdbcUrlEnum.values()).filter(dsJdbcUrlEnum -> dbName.equals(dsJdbcUrlEnum.getDbName())).findFirst().get();
    }
    
    public static DsJdbcUrlEnum getByType(String dsType) {
        return Arrays.stream(DsJdbcUrlEnum.values()).filter(dsJdbcUrlEnum -> dsType.equals(dsJdbcUrlEnum.getType())).findFirst().get();
    }

}
