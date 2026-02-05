package work.metanet.feng.ai.mapper;

public class ColumnFilterMapper extends PgVectorFilterMapper {

    String formatKey(String key, Class<?> valueType) {
        return String.format("%s::%s", key, SQL_TYPE_MAP.get(valueType));
    }

    String formatKeyAsString(String key) {
        return key;
    }

}
